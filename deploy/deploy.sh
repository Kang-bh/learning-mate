#!/bin/bash

# Check if Nginx is installed
if ! command -v nginx &> /dev/null; then
    echo "Nginx is not installed. Installing Nginx..."
    sudo apt update && sudo apt install -y nginx
    if [ $? -ne 0 ]; then
        echo "Failed to install Nginx. Exiting."
        exit 1
    fi
    echo "Nginx installed successfully."
fi

# 현재 실행 중인 컨테이너 확인 (정확한 이름 매칭)
IS_GREEN=$(docker ps --format '{{.Names}}' | grep -w green)
UPSTREAM_CONF="/etc/nginx/upstream.conf"

MAX_RETRIES=30   # Limit the number of retries (5 minutes with 10s intervals)
RETRY_COUNT=0

BLUE_PORT=8081
GREEN_PORT=8082

if [ -z "$IS_GREEN" ]; then # blue -> green
    echo "### BLUE => GREEN ###"
    TARGET_CONTAINER="green"
    OLD_CONTAINER="blue"
else # green -> blue
    echo "### GREEN => BLUE ###"
    TARGET_CONTAINER="blue"
    OLD_CONTAINER="green"
fi

echo "1. Stopping and removing old $TARGET_CONTAINER container if exists"
docker-compose stop $TARGET_CONTAINER || true
docker-compose rm -f $TARGET_CONTAINER || true

echo "2. Get $TARGET_CONTAINER image"
docker-compose pull $TARGET_CONTAINER

echo "3. Start $TARGET_CONTAINER container"
docker-compose up -d $TARGET_CONTAINER --build

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    echo "4. $TARGET_CONTAINER health check..."
    sleep 5

    # 컨테이너 상태 확인
    CONTAINER_STATUS=$(docker inspect -f '{{.State.Status}}' $TARGET_CONTAINER 2>/dev/null)

    if [ "$CONTAINER_STATUS" != "running" ]; then
        echo "$TARGET_CONTAINER container is not running. Status: $CONTAINER_STATUS"
        RETRY_COUNT=$((RETRY_COUNT+1))
        continue
    fi

    RESPONSE=$(docker exec $TARGET_CONTAINER curl --max-time 5 --silent --write-out "HTTPSTATUS:%{http_code}" --output /dev/null http://localhost:8080/actuator/health)
    HTTP_STATUS=$(echo $RESPONSE | sed -e 's/.*HTTPSTATUS://')
    echo "Response code: $HTTP_STATUS"

    if [[ "$HTTP_STATUS" =~ ^[0-9]+$ ]] && [ "$HTTP_STATUS" -eq 200 ]; then
        echo "Health check success"
        break
    fi

    RETRY_COUNT=$((RETRY_COUNT+1))
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "Health check failed after maximum retries. Rolling back..."
    docker-compose stop $TARGET_CONTAINER
    docker-compose rm -f $TARGET_CONTAINER
    exit 1
fi

echo "5. Updating nginx upstream to $TARGET_CONTAINER"
echo "upstream active_upstream { server $TARGET_CONTAINER:8080; }" | sudo tee $UPSTREAM_CONF

echo "6. Reloading nginx"
sudo nginx -t && sudo nginx -s reload

echo "7. Stopping and removing $OLD_CONTAINER container"
docker-compose stop $OLD_CONTAINER || true
docker-compose rm -f $OLD_CONTAINER || true

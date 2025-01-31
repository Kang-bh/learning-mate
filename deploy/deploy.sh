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

    echo "1. Stopping and removing old green container if exists"
    docker-compose stop green || true
    docker-compose rm -f green || true

    echo "2. Get green image"
    docker-compose pull green

    echo "3. Start green container"
    docker-compose up -d green --build

    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        echo "4. Green health check..."
        sleep 5

        RESPONSE=$(docker exec green curl --max-time 5 --silent --write-out "HTTPSTATUS:%{http_code}" --output /dev/null http://localhost:8080/actuator/health)
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
        docker-compose stop green
        docker-compose rm -f green
        exit 1
    fi


    echo "5. Updating nginx upstream to GREEN"
    echo "upstream active_upstream { server green:8080; }" | sudo tee $UPSTREAM_CONF

    echo "6. Reloading nginx"
    sudo nginx -t && sudo nginx -s reload

    echo "7. Stopping and removing blue container"
    docker-compose stop blue || true
    docker-compose rm -f blue || true

else # green -> blue
    echo "### GREEN => BLUE ###"

    echo "1. Stopping and removing old blue container if exists"
    docker-compose stop blue || true
    docker-compose rm -f blue || true

    echo "2. Get blue image"
    docker-compose pull blue

    echo "3. Start blue container"
    docker-compose up -d blue --build

    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        echo "4. Green health check..."
        sleep 5

        RESPONSE=$(docker exec green curl --max-time 5 --silent --write-out "HTTPSTATUS:%{http_code}" --output /dev/null http://localhost:8080/actuator/health)
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
        docker-compose stop green
        docker-compose rm -f green
        exit 1
    fi


    echo "5. Updating nginx upstream to BLUE"
    echo "upstream active_upstream { server blue:8080; }" | sudo tee $UPSTREAM_CONF

    echo "6. Reloading nginx"
    sudo nginx -t && sudo nginx -s reload

    echo "7. Stopping and removing green container"
    docker-compose stop green || true
    docker-compose rm -f green || true
fi

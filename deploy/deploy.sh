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
DEFAULT_CONF="/etc/nginx/nginx.conf"

MAX_RETRIES=30   # Limit the number of retries (5 minutes with 10s intervals)
RETRY_COUNT=0

BLUE_PORT=8081
GREEN_PORT=8082

if [ -z "$IS_GREEN" ]; then # blue case
    echo "### BLUE => GREEN ###"

    echo "1. Stopping and removing old green container if exists"
    docker-compose stop green || true
    docker-compose rm -f green || true

    echo "2. Get green image"
    docker-compose pull green

    echo "3. Start green container"
    docker-compose up -d green

    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        echo "4. Green health check..."
        sleep 5

        RESPONSE=$(curl --max-time 5 --silent --write-out "HTTPSTATUS:%{http_code}" --output /dev/null http://127.0.0.1:$GREEN_PORT/health)
        HTTP_STATUS=$(echo $RESPONSE | sed -e 's/.*HTTPSTATUS://')
        echo "Response code: $HTTP_STATUS"

        if [ "$HTTP_STATUS" -eq 200 ]; then
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

    echo "5. Reloading nginx"
    sudo cp /etc/nginx/nginx.green.conf /etc/nginx/nginx.conf
    sudo nginx -t && sudo nginx -s reload

    echo "6. Stopping and removing blue container"
    docker-compose stop blue || true
    docker-compose rm -f blue || true

else
    echo "### GREEN => BLUE ###"

    echo "1. Stopping and removing old blue container if exists"
    docker-compose stop blue || true
    docker-compose rm -f blue || true

    echo "2. Get blue image"
    docker-compose pull blue

    echo "3. Start blue container"
    docker-compose up -d blue

    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        echo "4. Blue health check..."
        sleep 5

        RESPONSE=$(curl --max-time 5 --silent --write-out "HTTPSTATUS:%{http_code}" --output /dev/null http://127.0.0.1:$BLUE_PORT/health)
        HTTP_STATUS=$(echo $RESPONSE | sed -e 's/.*HTTPSTATUS://')
        echo "Response code: $HTTP_STATUS"

        if [ "$HTTP_STATUS" -eq 200 ]; then
            echo "Health check success"
            break
        fi

        RETRY_COUNT=$((RETRY_COUNT+1))
    done

    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
        echo "Health check failed after maximum retries. Rolling back..."
        docker-compose stop blue
        docker-compose rm -f blue
        exit 1
    fi

    echo "5. Reloading nginx"
    sudo cp /etc/nginx/nginx.blue.conf /etc/nginx/nginx.conf
    sudo nginx -t && sudo nginx -s reload

    echo "6. Stopping and removing green container"
    docker-compose stop green || true
    docker-compose rm -f green || true
fi
#!/bin/bash

# 현재 실행 중인 컨테이너 확인
IS_GREEN=$(docker ps --format '{{.Names}}' | grep -w green)
UPSTREAM_CONF="/etc/nginx/upstream.conf"

MAX_RETRIES=30   # 최대 재시도 횟수
RETRY_COUNT=0
SLEEP_INTERVAL=5 # health check 간격

BLUE_PORT=8081
GREEN_PORT=8082

if [ -z "$IS_GREEN" ]; then # blue -> green
    echo "### BLUE => GREEN ###"

    echo "1. Stopping and removing old green container (if exists)"
    docker-compose stop green || true
    docker-compose rm -f green || true

    echo "2. Pulling latest green image"
    docker-compose pull green

    echo "3. Starting new green container"
    docker-compose up -d green --build

    # 새로운 컨테이너가 정상적으로 실행될 때까지 대기
    echo "4. Waiting for green container to be up..."
    until docker ps --format '{{.Names}}' | grep -w green; do
        sleep 2
        echo "Waiting..."
    done

    echo "5. Performing Green health check..."
    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        sleep $SLEEP_INTERVAL
        RESPONSE=$(docker inspect --format='{{.State.Health.Status}}' green 2>/dev/null)

        if [ "$RESPONSE" == "healthy" ]; then
            echo "Health check success!"
            break
        fi

        echo "Health check attempt $RETRY_COUNT failed, retrying..."
        RETRY_COUNT=$((RETRY_COUNT+1))
    done

    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
        echo "Health check failed after maximum retries. Rolling back..."
        docker-compose stop green
        docker-compose rm -f green
        exit 1
    fi

    echo "6. Updating Nginx upstream to GREEN"
    echo "upstream active_upstream { server green:8080; }" | sudo tee $UPSTREAM_CONF

    echo "7. Gracefully reloading Nginx"
    sudo systemctl reload nginx || sudo nginx -s reload

    echo "8. Stopping and removing blue container"
    docker-compose stop blue || true
    docker-compose rm -f blue || true

else # green -> blue
    echo "### GREEN => BLUE ###"

    echo "1. Stopping and removing old blue container (if exists)"
    docker-compose stop blue || true
    docker-compose rm -f blue || true

    echo "2. Pulling latest blue image"
    docker-compose pull blue

    echo "3. Starting new blue container"
    docker-compose up -d blue --build

    echo "4. Waiting for blue container to be up..."
    until docker ps --format '{{.Names}}' | grep -w blue; do
        sleep 2
        echo "Waiting..."
    done

    echo "5. Performing Blue health check..."
    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        sleep $SLEEP_INTERVAL
        RESPONSE=$(docker inspect --format='{{.State.Health.Status}}' blue 2>/dev/null)

        if [ "$RESPONSE" == "healthy" ]; then
            echo "Health check success!"
            break
        fi

        echo "Health check attempt $RETRY_COUNT failed, retrying..."
        RETRY_COUNT=$((RETRY_COUNT+1))
    done

    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
        echo "Health check failed after maximum retries. Rolling back..."
        docker-compose stop blue
        docker-compose rm -f blue
        exit 1
    fi

    echo "6. Updating Nginx upstream to BLUE"
    echo "upstream active_upstream { server blue:8080; }" | sudo tee $UPSTREAM_CONF

    echo "7. Gracefully reloading Nginx"
    sudo systemctl reload nginx || sudo nginx -s reload

    echo "8. Stopping and removing green container"
    docker-compose stop green || true
    docker-compose rm -f green || true
fi
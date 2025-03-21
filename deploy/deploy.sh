#!/bin/bash

# Blue-Green Deployment 스크립트

# 변수 정의
IS_GREEN=$(docker ps | grep green) # 현재 실행 중인 컨테이너 확인
MAX_RETRIES=30                     # Health Check 최대 재시도 횟수 (5분간 10초 간격)
RETRY_COUNT=0                      # 현재 재시도 횟수
BLUE_PORT=8081                     # Blue 컨테이너 포트
GREEN_PORT=8082                    # Green 컨테이너 포트

# Nginx 설정 파일 경로
BLUE_CONF="/etc/nginx/nginx.blue.conf"
GREEN_CONF="/etc/nginx/nginx.green.conf"
NGINX_CONF="/etc/nginx/nginx.conf"

# 함수 정의: Health Check 수행
function health_check() {
    local port=$1
    local retries=$2
    local count=0

    while [ $count -lt $retries ]; do
        echo "Health check on port $port..."
        RESPONSE=$(curl --max-time 5 --silent --write-out "HTTPSTATUS:%{http_code}" --output /dev/null http://127.0.0.1:$port/health)
        HTTP_STATUS=$(echo $RESPONSE | sed -e 's/.*HTTPSTATUS://')

        if [ "$HTTP_STATUS" -eq 200 ]; then
            echo "Health check successful! HTTP Status: $HTTP_STATUS"
            return 0
        fi

        echo "Health check failed. HTTP Status: $HTTP_STATUS. Retrying..."
        count=$((count + 1))
        sleep 10
    done

    echo "Health check failed after $retries retries."
    return 1
}

# 함수 정의: 롤백 처리
function rollback() {
    echo "Rolling back to previous state..."

    if [ -z "$IS_GREEN" ]; then
        echo "Rollback to BLUE"
        docker-compose stop green
        docker-compose up -d blue
        sudo cp $BLUE_CONF $NGINX_CONF
        sudo nginx -t && sudo nginx -s reload
    else
        echo "Rollback to GREEN"
        docker-compose stop blue
        docker-compose up -d green
        sudo cp $GREEN_CONF $NGINX_CONF
        sudo nginx -t && sudo nginx -s reload
    fi

    echo "Rollback complete."
}

# Blue-Green Deployment 시작
if [ -z "$IS_GREEN" ]; then # 현재 Blue 상태인 경우

    echo "### BLUE => GREEN ###"

    echo "1. Pull Green image"
    docker-compose pull green

    echo "2. Start Green container"
    docker-compose up -d green

    echo "3. Perform Health Check for Green container"
    if health_check $GREEN_PORT $MAX_RETRIES; then
        echo "Green container is healthy."

        echo "4. Update Nginx configuration to Green"
        sudo cp $GREEN_CONF $NGINX_CONF
        sudo nginx -t && sudo nginx -s reload

        echo "5. Stop Blue container"
        docker-compose stop blue
    else
        echo "Green container health check failed!"
        rollback # Health Check 실패 시 롤백 처리
        exit 1   # 스크립트 종료 (에러 반환)
    fi

else # 현재 Green 상태인 경우

    echo "### GREEN => BLUE ###"

    echo "1. Pull Blue image"
    docker-compose pull blue

    echo "2. Start Blue container"
    docker-compose up -d blue

    echo "3. Perform Health Check for Blue container"
    if health_check $BLUE_PORT $MAX_RETRIES; then
        echo "Blue container is healthy."

        echo "4. Update Nginx configuration to Blue"
        sudo cp $BLUE_CONF $NGINX_CONF
        sudo nginx -t && sudo nginx -s reload

        echo "5. Stop Green container"
        docker-compose stop green
    else
        echo "Blue container health check failed!"
        rollback # Health Check 실패 시 롤백 처리
        exit 1   # 스크립트 종료 (에러 반환)
    fi

fi

echo "Deployment completed successfully!"

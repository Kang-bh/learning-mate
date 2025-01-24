#!/bin/bash

IS_GREEN=$(docker ps | grep green)
DEFAULT_CONF= "/etc/nginx/nginx.conf"


MAX_RETRIES=30   # Limit the number of retries (5 minutes with 10s intervals)
RETRY_COUNT=0

BLUE_PORT=8081
GREEN_PORT=8082

if [ -z $IS_GREEN  ];then # blue인 경우

  echo "### BLUE => GREEN ###"

  echo "1. get green image"
  docker-compose pull green # green으로 이미지를 내려받습니다.

  echo "2. green container up"
  docker-compose up -d green # green 컨테이너 실행

   while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
      echo "3. green health check..."
      sleep 5

      RESPONSE=$(curl --max-time 5 --silent --write-out "HTTPSTATUS:%{http_code}" --output /dev/null http://127.0.0.1:$GREEN_PORT/health)
      HTTP_STATUS=$(echo $RESPONSE | sed -e 's/.*HTTPSTATUS://')
      echo "Response code: $HTTP_STATUS"

      if [ $? -eq 0 ]; then
        echo "health check success"
        break
      fi

      RETRY_COUNT=$((RETRY_COUNT+1))
    done
  echo "4. reload nginx"
  sudo cp /etc/nginx/nginx.green.conf /etc/nginx/nginx.conf
  sudo nginx -t && sudo nginx -s reload

  echo "5. blue container down"
  docker-compose stop blue

else
  echo "### GREEN => BLUE ###"

  echo "1. get blue image"
  docker-compose pull blue

  echo "2. blue container up"
  docker-compose up -d blue


  while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    echo "3. blue health check..."
    sleep 5

    RESPONSE=$(curl --max-time 5 --silent --write-out "HTTPSTATUS:%{http_code}" --output /dev/null http://127.0.0.1:$BLUE_PORT/health)
    HTTP_STATUS=$(echo $RESPONSE | sed -e 's/.*HTTPSTATUS://')
    echo "Response code: $HTTP_STATUS"

    if [ $? -eq 0 ]; then
      echo "health check success"
      break
    fi

    RETRY_COUNT=$((RETRY_COUNT+1))
  done

  echo "4. reload nginx"
  sudo cp /etc/nginx/nginx.blue.conf /etc/nginx/nginx.conf
  sudo nginx -t && sudo nginx -s reload

  echo "5. green container down"
  docker-compose stop green

fi
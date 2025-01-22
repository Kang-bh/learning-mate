#!/bin/bash

EC2_HOST="15.164.2.37"
EC2_USER="ec2-user"
EC2_KEY_PATH="/Users/gangbyeongho/.ssh/learning-mate.pem"

# 로컬의 docker-compose-db.yml 파일 경로
DOCKER_COMPOSE_FILE="./docker-compose-db.yml"

# EC2에 docker-compose-db.yml 파일 전송
scp -i $EC2_KEY_PATH $DOCKER_COMPOSE_FILE $EC2_USER@$EC2_HOST:~/docker-compose-db.yml

# EC2에 접속하여 Docker Compose 실행
ssh -i $EC2_KEY_PATH $EC2_USER@$EC2_HOST << EOF
    if ! docker --version &> /dev/null; then
        echo "Docker가 설치되어 있지 않습니다. 설치를 시작합니다..."
        sudo yum update -y
        sudo yum install -y docker
        sudo systemctl start docker
        sudo systemctl enable docker
        sudo usermod -aG docker $USER
        echo "Docker 설치가 완료되었습니다."
    else
        echo "Docker가 이미 설치되어 있습니다. 버전: $(docker --version)"
    fi

    if ! command -v docker-compose &> /dev/null; then
        echo "Docker Compose가 설치되어 있지 않습니다. 설치를 시작합니다..."
        sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
        sudo chmod +x /usr/local/bin/docker-compose
        sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
        echo "Docker Compose 설치가 완료되었습니다."
    else
        echo "Docker Compose가 이미 설치되어 있습니다. 버전: $(docker-compose --version)"
    fi

    if command -v docker-compose &> /dev/null; then
        echo "설치된 Docker Compose 버전: $(docker-compose --version)"
    else
        echo "Docker Compose 설치 실패"
    fi

    echo "설치된 Docker 버전: $(docker --version)"
    echo "설치된 Docker Compose 버전: $(docker-compose --version)"

    if command -v docker-compose &> /dev/null; then
        docker-compose -f ~/docker-compose-db.yml up -d
    else
        echo "Docker Compose 실행 실패: 명령어를 찾을 수 없습니다."
    fi

    docker-compose -f ~/docker-compose-db.yml up -d

    echo "데이터베이스 컨테이너가 실행되기를 기다리는 중..."
    sleep 30

    rm -f ~/docker-compose-db.yml
    echo "docker-compose-db.yml 파일이 삭제되었습니다."
EOF

echo "Database deployment completed."

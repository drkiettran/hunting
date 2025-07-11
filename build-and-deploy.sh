#!/bin/bash

# Build all services
echo "Building all microservices..."

# Build common module first
cd common
mvn clean install -DskipTests
cd ..

# Build all services
services=("gateway-service" "user-management-service" "threat-intelligence-service" "detection-analytics-service" "alert-management-service" "investigation-service" "investigation-service" "intelligence-product-service")

for service in "${services[@]}"; do
    echo "Building $service..."
    cd $service
    mvn clean package -DskipTests
    cd ..
done

# Build frontend
echo "Building frontend..."
cd web-frontend
npm install
npm run build
cd ..

# Build Docker images
echo "Building Docker images..."
docker-compose build

# Start the system
echo "Starting Persistent Hunt System..."
docker-compose up -d

# Wait for services to be ready
echo "Waiting for services to start..."
sleep 30

# Check service health
echo "Checking service health..."
curl -f http://localhost:8080/actuator/health
curl -f http://localhost:8081/api/users/health
curl -f http://localhost:8082/api/threat-intelligence/health

echo "Deployment completed! Access the system at http://localhost:3000"
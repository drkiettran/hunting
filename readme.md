# README.md - Complete setup instructions
# Persistent Hunt System

A comprehensive microservices-based cybersecurity threat hunting and incident response platform.

## Architecture Overview

The system consists of the following microservices:

- **Gateway Service** (Port 8080) - API Gateway and authentication
- **User Management Service** (Port 8081) - User authentication and authorization
- **Threat Intelligence Service** (Port 8082) - Threat intelligence management
- **Detection Analytics Service** (Port 8083) - Analytics creation and execution
- **Alert Management Service** (Port 8084) - Alert processing and management
- **Investigation Service** (Port 8085) - Case and investigation workflow
- **Web Frontend** (Port 3000) - React-based user interface

## Technology Stack

- **Backend**: Spring Boot 3.2, Java 17
- **Frontend**: React 18, Tailwind CSS
- **Database**: MySQL 8.0
- **Cache**: Redis 7
- **Messaging**: Apache Kafka
- **Analytics Platform**: Elasticsearch
- **Security**: JWT, Spring Security
- **Containerization**: Docker, Docker Compose
- **Orchestration**: Kubernetes (optional)

## Prerequisites

- Java 17+
- Node.js 18+
- Maven 3.8+
- Docker and Docker Compose
- MySQL 8.0 (if running locally)

## Quick Start

### Using Docker Compose (Recommended)

1. Clone the repository
2. Run the build and deployment script:
   ```bash
   chmod +x build-and-deploy.sh
   ./build-and-deploy.sh
   ```
3. Access the application at http://localhost:3000

### Manual Setup

1. **Start Infrastructure Services**
   ```bash
   docker-compose up mysql redis kafka elasticsearch -d
   ```

2. **Build and Run Services**
   ```bash
   # Build common module
   cd common && mvn clean install
   
   # Build and run each service
   cd ../gateway-service && mvn spring-boot:run
   cd ../user-management-service && mvn spring-boot:run
   # ... repeat for other services
   ```

3. **Start Frontend**
   ```bash
   cd web-frontend
   npm install
   npm start
   ```

## Default Credentials

- **Admin User**: admin / admin123
- **Analyst User**: analyst1 / analyst123
- **MySQL**: root / password

## API Documentation

Each service exposes Swagger documentation at `/swagger-ui.html`:
- Gateway: http://localhost:8080/swagger-ui.html
- User Service: http://localhost:8081/swagger-ui.html
- etc.

## Configuration

### Environment Variables

Key environment variables for configuration:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/persistent_hunt_system
SPRING_DATASOURCE_USERNAME=huntuser
SPRING_DATASOURCE_PASSWORD=huntpass

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400

# Elasticsearch
PLATFORMS_ELASTIC_HOST=http://localhost:9200
PLATFORMS_ELASTIC_USERNAME=elastic
PLATFORMS_ELASTIC_PASSWORD=changeme
```

### Profiles

- `default` - Local development
- `docker` - Docker container deployment
- `kubernetes` - Kubernetes deployment

## Monitoring and Operations

### Health Checks

All services expose health endpoints:
- http://localhost:8080/actuator/health (Gateway)
- http://localhost:8081/api/users/health (User Service)
- etc.

### Metrics

Metrics are available at `/actuator/metrics` for each service.

### Logging

Centralized logging configuration with structured JSON output for production deployments.

## Development

### Adding New Features

1. Update the common module if new shared entities/DTOs are needed
2. Implement the feature in the relevant service
3. Update the frontend components
4. Add appropriate tests
5. Update API documentation

### Database Migrations

Use Flyway for database schema migrations:
- Place migration scripts in `src/main/resources/db/migration/`
- Follow naming convention: `V{version}__{description}.sql`

## Production Deployment

### Kubernetes

1. Apply the Kubernetes manifests:
   ```bash
   kubectl apply -f kubernetes-deployment.yaml
   ```

2. Configure ingress and TLS certificates
3. Set up monitoring with Prometheus and Grafana
4. Configure backup strategies for persistent data

### Security Considerations

- Use strong JWT secrets in production
- Configure proper CORS policies
- Enable SSL/TLS for all communications
- Implement rate limiting
- Use Kubernetes secrets for sensitive data
- Regular security updates

## Troubleshooting

### Common Issues

1. **Service startup failures**: Check database connectivity and Kafka availability
2. **Authentication issues**: Verify JWT configuration and user credentials
3. **Performance issues**: Check Redis cache and database query performance
4. **Frontend errors**: Verify API gateway routing and CORS configuration

### Logs

Check service logs:
```bash
docker-compose logs -f [service-name]
kubectl logs -f deployment/[service-name] -n persistent-hunt
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Submit a pull request

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
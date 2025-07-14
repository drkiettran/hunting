# Security Hardening Implementation Guide

## Overview

This comprehensive security hardening implementation addresses critical security requirements for your Persistent Hunting System:

- **JWT Token Security** - Strong secret management and secure token handling
- **CORS Configuration** - Strict cross-origin resource sharing policies
- **SSL/TLS Encryption** - End-to-end encryption for all communications
- **Rate Limiting** - Protection against brute force and DoS attacks
- **Security Headers** - Browser security protections
- **Password Policy** - Strong password requirements
- **Input Validation** - Protection against injection attacks

## Implementation Steps

### 1. Add Security Dependencies

Add the security dependencies to your `pom.xml` files in each service:

```bash
# Update common module
cd common && mvn clean install

# Update each service
cd ../gateway-service && mvn dependency:resolve
cd ../user-management-service && mvn dependency:resolve
cd ../threat-intelligence-service && mvn dependency:resolve
```

### 2. Generate Strong JWT Secret

**CRITICAL**: Never use the default JWT secret in production!

```bash
# Generate a strong JWT secret
openssl rand -base64 64

# Example output:
# Yj8K9mN2pQ5rS7tU1vW3xY4zA6bC8dE0fG1hI3jK5lM7nO9pQ2sT4uV6wX8yZ0aB2cD4eF6gH8iJ0kL2mN4oP6qR8s
```

### 3. Create SSL Certificates

Run the certificate generation script:

```bash
# Make the script executable
chmod +x generate-ssl-certs.sh

# Generate certificates
./generate-ssl-certs.sh

# Verify certificates
openssl x509 -in ssl/certs/hunting-system.crt -text -noout
```

### 4. Update Configuration Files

#### Gateway Service (`gateway-service/src/main/resources/application.yml`):

```yaml
spring:
  profiles:
    include: security
  security:
    oauth2:
      client:
        provider:
          hunting-system:
            authorization-uri: https://localhost:8443/oauth/authorize
            token-uri: https://localhost:8443/oauth/token

security:
  jwt:
    secret: ${JWT_SECRET}
  cors:
    allowed-origins:
      - ${FRONTEND_URL:https://localhost:3000}

server:
  port: 8443
  ssl:
    enabled: true
```

#### User Management Service (`user-management-service/src/main/resources/application.yml`):

```yaml
spring:
  profiles:
    include: security

security:
  jwt:
    secret: ${JWT_SECRET}
  password:
    min-length: 12

server:
  port: 8543
  ssl:
    enabled: true
```

#### All Services Common Configuration (`application-security.yml`):

Copy the complete security configuration from the SSL/TLS artifact.

### 5. Environment Variables Setup

Create environment files for different environments:

#### Development (`.env.dev`):
```bash
JWT_SECRET=dev-secret-that-is-at-least-256-bits-long-for-development-only
SSL_ENABLED=true
SSL_KEYSTORE_PASSWORD=changeme
FRONTEND_URL=https://localhost:3000
```

#### Production (`.env.prod`):
```bash
JWT_SECRET=your-production-jwt-secret-from-openssl-rand-base64-64
SSL_ENABLED=true
SSL_KEYSTORE_PASSWORD=your-secure-production-password
FRONTEND_URL=https://hunting.yourdomain.com
```

### 6. File Structure Implementation

Create the following security package structure in each service:

```
src/main/java/com/hunting/[service]/
├── security/
│   ├── JwtTokenUtil.java
│   ├── SecurityConfig.java
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtRequestFilter.java
│   ├── RateLimitService.java
│   ├── RateLimitFilter.java
│   ├── PasswordPolicyValidator.java
│   └── SecurityHeadersFilter.java
```

### 7. Database Security Updates

#### Update Database Connection for SSL:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/persistent_hunt_system?useSSL=true&requireSSL=true&verifyServerCertificate=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    properties:
      ssl-ca: /etc/ssl/certs/ca-cert.pem
      ssl-cert: /etc/ssl/certs/client-cert.pem
      ssl-key: /etc/ssl/certs/client-key.pem
```

#### Create Security Audit Table:

```sql
CREATE TABLE security_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    success BOOLEAN NOT NULL,
    timestamp DATETIME NOT NULL,
    details JSON,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_timestamp (timestamp),
    INDEX idx_success (success)
);
```

### 8. Docker Deployment with SSL

Update your Docker Compose configuration:

```bash
# Copy SSL certificates to container volume
mkdir -p ./ssl/certs
cp ssl/certs/* ./ssl/certs/

# Deploy with SSL
docker-compose -f docker-compose.yml -f docker-compose-ssl.yml up -d
```

### 9. Kubernetes Deployment

For Kubernetes deployment:

```bash
# Create secrets
kubectl create secret generic hunting-jwt-secret \
  --from-literal=jwt-secret="your-jwt-secret" \
  --namespace=persistent-hunt

kubectl create secret tls hunting-ssl-secret \
  --cert=ssl/certs/hunting-system.crt \
  --key=ssl/certs/hunting-system.key \
  --namespace=persistent-hunt

# Apply configuration
kubectl apply -f k8s-ssl-config.yaml
```

## Security Testing

### 1. JWT Token Testing

```bash
# Test JWT token generation
curl -X POST https://localhost:8443/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  --insecure

# Test with invalid token
curl -X GET https://localhost:8443/api/artifacts \
  -H "Authorization: Bearer invalid-token" \
  --insecure
```

### 2. CORS Testing

```bash
# Test CORS preflight
curl -X OPTIONS https://localhost:8443/api/artifacts \
  -H "Origin: https://malicious.com" \
  -H "Access-Control-Request-Method: GET" \
  --insecure

# Should return 403 or CORS error for unauthorized origins
```

### 3. SSL/TLS Testing

```bash
# Test SSL configuration
nmap --script ssl-enum-ciphers -p 8443 localhost

# Test certificate validity
openssl s_client -connect localhost:8443 -servername hunting.yourdomain.com

# Test protocol support
curl -v --tlsv1.2 https://localhost:8443/health --insecure
curl -v --tlsv1.3 https://localhost:8443/health --insecure
```

### 4. Rate Limiting Testing

```bash
# Test login rate limiting
for i in {1..10}; do
  curl -X POST https://localhost:8443/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"test","password":"wrong"}' \
    --insecure
done

# Should receive 429 Too Many Requests after 5 attempts
```

### 5. Security Headers Testing

```bash
# Test security headers
curl -I https://localhost:8443/api/artifacts \
  -H "Authorization: Bearer valid-token" \
  --insecure

# Verify headers:
# X-Content-Type-Options: nosniff
# X-Frame-Options: DENY
# X-XSS-Protection: 1; mode=block
# Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
```

## Security Monitoring

### 1. Security Metrics

Monitor these key security metrics:

```yaml
# Add to Prometheus/Grafana
- name: failed_login_attempts
  query: increase(security_failed_logins_total[5m])
  
- name: jwt_token_validation_failures
  query: increase(jwt_validation_failures_total[5m])
  
- name: rate_limit_violations
  query: increase(rate_limit_violations_total[5m])
  
- name: ssl_handshake_failures
  query: increase(ssl_handshake_failures_total[5m])
```

### 2. Security Alerts

Set up alerts for:

- **Multiple failed login attempts** from same IP
- **JWT token validation failures** spike
- **Rate limit violations** from specific IPs
- **SSL certificate expiration** warnings
- **Unusual API access patterns**

### 3. Audit Logging

Implement comprehensive audit logging:

```java
@EventListener
public class SecurityAuditListener {
    
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        // Log successful authentication
    }
    
    @EventListener
    public void handleAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        // Log failed authentication
    }
    
    @EventListener
    public void handleAuthorizationFailure(AuthorizationDeniedEvent event) {
        // Log authorization failures
    }
}
```

## Performance Considerations

### 1. JWT Token Caching

Cache JWT tokens to reduce validation overhead:

```java
@Service
public class JwtCacheService {
    
    @Cacheable(value = "jwt-validation", key = "#token")
    public boolean validateToken(String token) {
        // Cache validation results for 5 minutes
        return jwtTokenUtil.validateToken(token);
    }
}
```

### 2. Rate Limiting Optimization

Use Redis for distributed rate limiting:

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    ssl: true
    timeout: 2000ms
```

### 3. SSL Performance

Optimize SSL performance:

```yaml
server:
  ssl:
    session-cache-size: 10000
    session-timeout: 600s
```

## Troubleshooting

### Common Issues

#### 1. JWT Secret Too Short
**Error**: `JWT secret must be at least 256 bits`
**Solution**: Generate longer secret with `openssl rand -base64 64`

#### 2. SSL Handshake Failures
**Error**: `SSL handshake failed`
**Solutions**:
- Check certificate validity: `openssl x509 -in cert.crt -text -noout`
- Verify certificate chain
- Check TLS protocol compatibility

#### 3. CORS Preflight Failures
**Error**: `CORS policy blocked`
**Solutions**:
- Verify allowed origins configuration
- Check request headers
- Ensure proper CORS handling in gateway

#### 4. Rate Limiting False Positives
**Error**: `Too many requests`
**Solutions**:
- Adjust rate limits for legitimate traffic
- Implement IP whitelisting for trusted sources
- Use sliding window rate limiting

### Debug Commands

```bash
# Check SSL certificate
openssl x509 -in ssl/certs/hunting-system.crt -text -noout

# Test JWT token
echo "your-jwt-token" | base64 -d

# Check running processes
docker ps | grep hunting

# View security logs
tail -f logs/hunting-security.log

# Test network connectivity
curl -v https://localhost:8443/health --insecure
```

## Production Checklist

### Pre-Deployment Security Checklist

- [ ] **JWT Secret**: Generated with `openssl rand -base64 64`
- [ ] **SSL Certificates**: Valid and properly configured
- [ ] **CORS**: Restricted to production domains only
- [ ] **Rate Limiting**: Configured for expected traffic
- [ ] **Password Policy**: Enforced for all users
- [ ] **Security Headers**: All headers properly set
- [ ] **Audit Logging**: Comprehensive logging enabled
- [ ] **Dependency Scan**: No high-severity vulnerabilities
- [ ] **Penetration Testing**: Security assessment completed

### Post-Deployment Verification

- [ ] **SSL/TLS**: A+ rating on SSL Labs test
- [ ] **Security Headers**: Pass security header tests
- [ ] **Authentication**: Login/logout functioning properly
- [ ] **Authorization**: Role-based access working
- [ ] **Rate Limiting**: Protecting against abuse
- [ ] **Monitoring**: Security metrics being collected
- [ ] **Alerts**: Security alerts configured and tested

## Maintenance

### Regular Security Tasks

#### Weekly
- Review failed login attempts
- Check rate limiting violations
- Monitor SSL certificate expiration dates

#### Monthly
- Update security dependencies
- Review and rotate JWT secrets
- Audit user permissions and roles
- Run security scans

#### Quarterly
- Penetration testing
- Security policy review
- Update SSL certificates
- Review and update CORS policies

### Security Updates

To update security configurations:

```bash
# Update dependencies
mvn versions:use-latest-versions -DgenerateBackupPoms=false

# Run security scans
mvn org.owasp:dependency-check-maven:check

# Test security configuration
mvn test -Dtest=SecurityConfigTest
```

This security hardening implementation provides enterprise-grade security for your Persistent Hunting System. The configuration is designed to be production-ready while maintaining the flexibility needed for a cybersecurity platform.

Remember to regularly review and update these security measures as threats evolve and new vulnerabilities are discovered.
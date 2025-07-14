#!/bin/bash

# SSL Certificate Generation Script for Hunting System
set -e

CERT_DIR="./ssl/certs"
KEYSTORE_DIR="./ssl/keystore"
DOMAIN="hunting.yourdomain.com"
VALIDITY_DAYS=365

# Create directories
mkdir -p $CERT_DIR
mkdir -p $KEYSTORE_DIR

echo "Generating SSL certificates for Hunting System..."

# Generate CA private key
openssl genrsa -out $CERT_DIR/ca-key.pem 4096

# Generate CA certificate
openssl req -new -x509 -key $CERT_DIR/ca-key.pem -out $CERT_DIR/ca-cert.pem -days $VALIDITY_DAYS -subj "/C=US/ST=State/L=City/O=Hunting System/OU=Security/CN=Hunting CA"

# Generate server private key
openssl genrsa -out $CERT_DIR/hunting-system.key 4096

# Generate certificate signing request
openssl req -new -key $CERT_DIR/hunting-system.key -out $CERT_DIR/hunting-system.csr -subj "/C=US/ST=State/L=City/O=Hunting System/OU=Security/CN=$DOMAIN" -config <(
cat <<EOF
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
[req_distinguished_name]
[v3_req]
keyUsage = critical, digitalSignature, keyEncipherment, keyAgreement
extendedKeyUsage = serverAuth, clientAuth
subjectAltName = @alt_names
[alt_names]
DNS.1 = $DOMAIN
DNS.2 = localhost
DNS.3 = *.yourdomain.com
IP.1 = 127.0.0.1
IP.2 = ::1
EOF
)

# Generate server certificate signed by CA
openssl x509 -req -in $CERT_DIR/hunting-system.csr -CA $CERT_DIR/ca-cert.pem -CAkey $CERT_DIR/ca-key.pem -CAcreateserial -out $CERT_DIR/hunting-system.crt -days $VALIDITY_DAYS -extensions v3_req -extfile <(
cat <<EOF
[v3_req]
keyUsage = critical, digitalSignature, keyEncipherment, keyAgreement
extendedKeyUsage = serverAuth, clientAuth
subjectAltName = @alt_names
[alt_names]
DNS.1 = $DOMAIN
DNS.2 = localhost
DNS.3 = *.yourdomain.com
IP.1 = 127.0.0.1
IP.2 = ::1
EOF
)

# Generate PKCS12 keystore for Spring Boot
openssl pkcs12 -export -in $CERT_DIR/hunting-system.crt -inkey $CERT_DIR/hunting-system.key -out $KEYSTORE_DIR/hunting-system.p12 -name "hunting-system" -passout pass:changeme

# Generate truststore
keytool -import -file $CERT_DIR/ca-cert.pem -alias "hunting-ca" -keystore $KEYSTORE_DIR/truststore.p12 -storepass changeme -noprompt -storetype PKCS12

# Set proper permissions
chmod 644 $CERT_DIR/*.crt $CERT_DIR/*.pem
chmod 600 $CERT_DIR/*.key
chmod 644 $KEYSTORE_DIR/*.p12

echo "SSL certificates generated successfully!"
echo "Certificate files:"
echo "  - CA Certificate: $CERT_DIR/ca-cert.pem"
echo "  - Server Certificate: $CERT_DIR/hunting-system.crt"
echo "  - Server Private Key: $CERT_DIR/hunting-system.key"
echo "  - PKCS12 Keystore: $KEYSTORE_DIR/hunting-system.p12"
echo "  - Truststore: $KEYSTORE_DIR/truststore.p12"
echo ""
echo "IMPORTANT: Change the default keystore password 'changeme' in production!"
echo "Update your environment variables:"
echo "  SSL_KEYSTORE_PASSWORD=your-secure-password"
echo "  SSL_TRUSTSTORE_PASSWORD=your-secure-password"
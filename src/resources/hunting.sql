-- Persistent Hunt System - MySQL Database Schema
-- Generated from ERD PlantUML diagram

-- Create database
CREATE DATABASE IF NOT EXISTS persistent_hunt_system;
USE persistent_hunt_system;

-- Create enums as lookup tables
CREATE TABLE threat_types (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE severity_levels (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    numeric_value INT NOT NULL
);

CREATE TABLE alert_statuses (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE analyst_tiers (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE investigation_statuses (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE platforms (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type ENUM('ELASTIC', 'DATABRICKS', 'HYBRID') NOT NULL,
    endpoint VARCHAR(200),
    configuration TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_types (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Main entities
CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    role ENUM('ANALYST', 'PRODUCTION_STAFF', 'ADMIN') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    tier ENUM('TIER_1', 'TIER_2', 'TIER_3') NULL,
    department VARCHAR(100),
    clearance_level VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role_tier (role, tier)
);

CREATE TABLE threat_intelligence (
    id VARCHAR(50) PRIMARY KEY,
    source VARCHAR(100) NOT NULL,
    threat_type ENUM('MALWARE', 'PHISHING', 'APT', 'INSIDER_THREAT', 'VULNERABILITY', 'BOTNET') NOT NULL,
    ttp TEXT NOT NULL,
    description TEXT NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    discovered_date TIMESTAMP NOT NULL,
    reported_by VARCHAR(100) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_source (source),
    INDEX idx_threat_type (threat_type),
    INDEX idx_severity (severity),
    INDEX idx_discovered_date (discovered_date)
);

CREATE TABLE indicators (
    id VARCHAR(50) PRIMARY KEY,
    type ENUM('IP', 'DOMAIN', 'URL', 'FILE_HASH', 'EMAIL', 'USER_AGENT', 'CVE') NOT NULL,
    value VARCHAR(500) NOT NULL,
    description TEXT,
    confidence DECIMAL(3,2) CHECK (confidence >= 0 AND confidence <= 1),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_type_value (type, value),
    INDEX idx_confidence (confidence)
);

CREATE TABLE detection_analytics (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    query_text TEXT NOT NULL,
    platform ENUM('ELASTIC', 'DATABRICKS', 'HYBRID') NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    accuracy DECIMAL(5,2) CHECK (accuracy >= 0 AND accuracy <= 100),
    threat_intelligence_id VARCHAR(50),
    FOREIGN KEY (threat_intelligence_id) REFERENCES threat_intelligence(id) ON DELETE SET NULL,
    INDEX idx_platform (platform),
    INDEX idx_created_by (created_by),
    INDEX idx_is_active (is_active),
    INDEX idx_threat_intel (threat_intelligence_id)
);

CREATE TABLE cases (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    status ENUM('OPEN', 'IN_PROGRESS', 'PENDING_REVIEW', 'CLOSED') NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    risk_level ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_created_by (created_by),
    INDEX idx_created_date (created_date)
);

CREATE TABLE alerts (
    id VARCHAR(50) PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    status ENUM('NEW', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'FALSE_POSITIVE') NOT NULL,
    description TEXT NOT NULL,
    raw_data TEXT,
    assigned_to VARCHAR(100),
    false_positive BOOLEAN DEFAULT FALSE,
    analytic_id VARCHAR(50),
    FOREIGN KEY (analytic_id) REFERENCES detection_analytics(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_timestamp (timestamp),
    INDEX idx_severity (severity),
    INDEX idx_status (status),
    INDEX idx_assigned_to (assigned_to),
    INDEX idx_analytic_id (analytic_id)
);

CREATE TABLE investigations (
    id VARCHAR(50) PRIMARY KEY,
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_analyst VARCHAR(100) NOT NULL,
    tier ENUM('TIER_1', 'TIER_2', 'TIER_3') NOT NULL,
    status ENUM('OPEN', 'IN_PROGRESS', 'PENDING_REVIEW', 'CLOSED', 'ESCALATED') NOT NULL,
    end_date TIMESTAMP NULL,
    findings TEXT,
    recommendations TEXT,
    case_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_analyst) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_assigned_analyst (assigned_analyst),
    INDEX idx_tier (tier),
    INDEX idx_status (status),
    INDEX idx_case_id (case_id),
    INDEX idx_start_date (start_date)
);

CREATE TABLE tickets (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_to VARCHAR(100),
    due_date TIMESTAMP,
    case_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_assigned_to (assigned_to),
    INDEX idx_case_id (case_id),
    INDEX idx_due_date (due_date)
);

CREATE TABLE intelligence_products (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    type ENUM('THREAT_REPORT', 'ADVISORY', 'BULLETIN', 'ALERT', 'BRIEFING') NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_date TIMESTAMP NULL,
    classification ENUM('UNCLASSIFIED', 'CONFIDENTIAL', 'SECRET', 'TOP_SECRET') NOT NULL,
    FOREIGN KEY (author) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_type (type),
    INDEX idx_author (author),
    INDEX idx_published_date (published_date),
    INDEX idx_classification (classification)
);

CREATE TABLE artifacts (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type ENUM('LOG_FILE', 'QUERY', 'NOTEBOOK', 'REPORT', 'EVIDENCE', 'SCREENSHOT') NOT NULL,
    file_path VARCHAR(500),
    description TEXT,
    created_by VARCHAR(100) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    investigation_id VARCHAR(50),
    FOREIGN KEY (investigation_id) REFERENCES investigations(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_type (type),
    INDEX idx_created_by (created_by),
    INDEX idx_investigation_id (investigation_id),
    INDEX idx_created_date (created_date)
);

CREATE TABLE notes (
    id VARCHAR(50) PRIMARY KEY,
    content TEXT NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ticket_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_created_by (created_by),
    INDEX idx_created_date (created_date)
);

CREATE TABLE tags (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50),
    description TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE query_results (
    id VARCHAR(50) PRIMARY KEY,
    query_text TEXT NOT NULL,
    result_data TEXT,
    execution_time INTEGER,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    record_count INTEGER,
    platform_id VARCHAR(50),
    FOREIGN KEY (platform_id) REFERENCES platforms(id) ON DELETE SET NULL,
    INDEX idx_platform_id (platform_id),
    INDEX idx_timestamp (timestamp)
);

-- Junction tables for many-to-many relationships
CREATE TABLE alert_indicators (
    alert_id VARCHAR(50),
    indicator_id VARCHAR(50),
    relevance_score DECIMAL(3,2),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (alert_id, indicator_id),
    FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE,
    FOREIGN KEY (indicator_id) REFERENCES indicators(id) ON DELETE CASCADE
);

CREATE TABLE threat_intelligence_indicators (
    threat_intelligence_id VARCHAR(50),
    indicator_id VARCHAR(50),
    confidence DECIMAL(3,2),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (threat_intelligence_id, indicator_id),
    FOREIGN KEY (threat_intelligence_id) REFERENCES threat_intelligence(id) ON DELETE CASCADE,
    FOREIGN KEY (indicator_id) REFERENCES indicators(id) ON DELETE CASCADE
);

CREATE TABLE investigation_alerts (
    investigation_id VARCHAR(50),
    alert_id VARCHAR(50),
    analysis_notes TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (investigation_id, alert_id),
    FOREIGN KEY (investigation_id) REFERENCES investigations(id) ON DELETE CASCADE,
    FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE
);

CREATE TABLE product_sources (
    product_id VARCHAR(50),
    source_id VARCHAR(50),
    source_type ENUM('THREAT_INTELLIGENCE', 'INVESTIGATION', 'EXTERNAL') NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id, source_id),
    FOREIGN KEY (product_id) REFERENCES intelligence_products(id) ON DELETE CASCADE
);

CREATE TABLE artifact_tags (
    artifact_id VARCHAR(50),
    tag_id VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (artifact_id, tag_id),
    FOREIGN KEY (artifact_id) REFERENCES artifacts(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE TABLE product_tags (
    product_id VARCHAR(50),
    tag_id VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id, tag_id),
    FOREIGN KEY (product_id) REFERENCES intelligence_products(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Insert initial lookup data
INSERT INTO threat_types VALUES 
('MALWARE', 'Malware', 'Malicious software including viruses, trojans, and ransomware'),
('PHISHING', 'Phishing', 'Social engineering attacks via email, web, or other means'),
('APT', 'Advanced Persistent Threat', 'Sophisticated, long-term targeted attacks'),
('INSIDER_THREAT', 'Insider Threat', 'Threats from within the organization'),
('VULNERABILITY', 'Vulnerability', 'Software or system vulnerabilities'),
('BOTNET', 'Botnet', 'Networks of compromised computers');

INSERT INTO severity_levels VALUES 
('LOW', 'Low', 1),
('MEDIUM', 'Medium', 2),
('HIGH', 'High', 3),
('CRITICAL', 'Critical', 4);

INSERT INTO platforms VALUES
('elastic-prod', 'Elastic Production', 'ELASTIC', 'https://elastic.company.com', '{"cluster": "production"}', TRUE, NOW()),
('databricks-prod', 'Databricks Production', 'DATABRICKS', 'https://databricks.company.com', '{"workspace": "production"}', TRUE, NOW());

-- Create indexes for performance
CREATE INDEX idx_alerts_timestamp_severity ON alerts(timestamp, severity);
CREATE INDEX idx_investigations_case_status ON investigations(case_id, status);
CREATE INDEX idx_tickets_case_priority ON tickets(case_id, priority);
CREATE INDEX idx_artifacts_investigation_type ON artifacts(investigation_id, type);

-- Create views for common queries
CREATE VIEW active_cases AS
SELECT c.*, COUNT(DISTINCT i.id) as investigation_count, COUNT(DISTINCT t.id) as ticket_count
FROM cases c
LEFT JOIN investigations i ON c.id = i.case_id
LEFT JOIN tickets t ON c.id = t.case_id
WHERE c.status IN ('OPEN', 'IN_PROGRESS', 'PENDING_REVIEW')
GROUP BY c.id;

CREATE VIEW alert_summary AS
SELECT 
    DATE(timestamp) as alert_date,
    severity,
    status,
    COUNT(*) as alert_count,
    COUNT(CASE WHEN false_positive = TRUE THEN 1 END) as false_positive_count
FROM alerts
GROUP BY DATE(timestamp), severity, status;

CREATE VIEW analyst_workload AS
SELECT 
    u.username,
    u.tier,
    COUNT(DISTINCT c.id) as assigned_cases,
    COUNT(DISTINCT i.id) as assigned_investigations,
    COUNT(DISTINCT t.id) as assigned_tickets
FROM users u
LEFT JOIN cases c ON u.username = c.created_by
LEFT JOIN investigations i ON u.username = i.assigned_analyst
LEFT JOIN tickets t ON u.username = t.assigned_to
WHERE u.role = 'ANALYST' AND u.is_active = TRUE
GROUP BY u.id, u.username, u.tier;
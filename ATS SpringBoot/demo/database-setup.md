# ATS Database Setup Guide

This guide provides step-by-step instructions for setting up the PostgreSQL database for the Applicant Tracking System (ATS).

## Prerequisites

- PostgreSQL 12+ installed and running
- Database admin access (postgres user)
- pgAdmin or command-line access to PostgreSQL

## Setup Instructions

### 1. Create Database

Connect to PostgreSQL as admin user and create the database:

```sql
-- Connect as postgres user
CREATE DATABASE ats_db;
CREATE USER ats_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE ats_db TO ats_user;
```

### 2. Run Schema Script

Execute the schema.sql file to create all tables and initial data:

```bash
# Using psql command line
psql -U ats_user -d ats_db -f schema.sql

# Or using pgAdmin
# Import and execute the schema.sql file through the Query Tool
```

### 3. Configure Spring Boot Application

Update your `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ats_db
spring.datasource.username=ats_user
spring.datasource.password=your_secure_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### 4. Environment-Specific Configurations

#### Development (application-dev.properties)
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

#### Production (application-prod.properties)
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=WARN

# Use environment variables for sensitive data
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
```

## Database Schema Overview

### Core Tables

1. **users** - System users (admins, recruiters)
2. **companies** - Client companies posting jobs
3. **candidates** - Job applicants
4. **jobs** - Available positions
5. **applications** - Candidate applications to jobs
6. **application_notes** - Communication tracking

### Key Relationships

- Companies → Jobs (one-to-many)
- Candidates → Applications (one-to-many)
- Jobs → Applications (one-to-many)
- Applications → Application Notes (one-to-many)
- Users → Jobs (assigned recruiter)
- Users → Application Notes (created by)

## Default Users

The schema includes sample users for testing:

| Username | Email | Password | Role |
|----------|--------|----------|------|
| admin | admin@ats.com | password | ADMIN |
| recruiter1 | recruiter@ats.com | password | RECRUITER |

**Note:** Change these passwords in production!

## Verification Steps

1. **Check Tables Created:**
```sql
\dt  -- List all tables
```

2. **Verify Sample Data:**
```sql
SELECT * FROM users;
SELECT * FROM companies;
```

3. **Test Spring Boot Connection:**
```bash
mvn spring-boot:run
```

Look for successful database connection logs.

## Common Issues & Solutions

### Connection Refused
- Ensure PostgreSQL is running
- Check port (default 5432)
- Verify firewall settings

### Authentication Failed
- Check username/password
- Verify database user permissions
- Ensure database exists

### Table Creation Errors
- Check PostgreSQL version compatibility
- Verify user has CREATE privileges
- Review schema.sql for syntax errors

## Backup & Restore

### Create Backup
```bash
pg_dump -U ats_user -d ats_db -f ats_backup.sql
```

### Restore from Backup
```bash
psql -U ats_user -d ats_db -f ats_backup.sql
```

## Performance Tuning

The schema includes optimized indexes for common queries:

- User lookups by email/username
- Candidate searches by name/email
- Job filtering by company/status
- Application tracking by candidate/job
- Note queries by application

## Security Considerations

1. **Password Security:**
   - Use strong passwords
   - Hash passwords using BCrypt
   - Store credentials in environment variables

2. **Database Access:**
   - Limit database user permissions
   - Use connection pooling
   - Enable SSL for production

3. **Data Protection:**
   - Regular backups
   - Access logging
   - Data encryption at rest

## Monitoring

Consider setting up monitoring for:
- Connection pool usage
- Query performance
- Database size growth
- Failed login attempts

## Migration Strategy

For schema changes:
1. Create migration scripts
2. Test on development environment
3. Backup production database
4. Apply changes during maintenance window
5. Verify data integrity

This setup provides a robust foundation for the ATS application with proper indexing, constraints, and sample data for development.
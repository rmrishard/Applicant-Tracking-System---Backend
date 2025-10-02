-- ATS (Applicant Tracking System) Database Schema for PostgreSQL
-- This schema supports the complete ATS application including users, companies, jobs, candidates, applications, and notes

-- Create database (run separately if needed)
-- CREATE DATABASE ats_db;

-- Enable UUID extension for better unique identifiers (optional)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table for authentication and role management
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'RECRUITER' CHECK (role IN ('ADMIN', 'RECRUITER')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Companies table for client/hiring organizations
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    industry VARCHAR(100),
    location VARCHAR(100),
    website VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Candidates table for job applicants
CREATE TABLE candidates (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    location VARCHAR(100),
    linkedin_url VARCHAR(200),
    skills VARCHAR(1000),
    experience_years INTEGER,
    current_job_title VARCHAR(100),
    current_company VARCHAR(100),
    summary VARCHAR(500),
    resume_url VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Jobs table for open positions
CREATE TABLE jobs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    requirements VARCHAR(1000),
    location VARCHAR(100),
    job_type VARCHAR(20) NOT NULL DEFAULT 'FULL_TIME' CHECK (job_type IN ('FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP')),
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'ON_HOLD', 'FILLED', 'CANCELLED')),
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    min_salary DECIMAL(10,2),
    max_salary DECIMAL(10,2),
    deadline DATE,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    assigned_recruiter_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Applications table for tracking candidate applications to jobs
CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    candidate_id BIGINT NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'APPLIED' CHECK (status IN ('APPLIED', 'SCREENING', 'INTERVIEWING', 'OFFER', 'HIRED', 'REJECTED')),
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_contact_date TIMESTAMP,
    follow_up_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(candidate_id, job_id) -- Prevent duplicate applications
);

-- Application notes table for tracking communication and feedback
CREATE TABLE application_notes (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    created_by_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content VARCHAR(2000) NOT NULL,
    note_type VARCHAR(20) NOT NULL DEFAULT 'GENERAL' CHECK (note_type IN ('CALL', 'EMAIL', 'INTERVIEW', 'GENERAL', 'FOLLOW_UP')),
    scheduled_follow_up TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better query performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_candidates_email ON candidates(email);
CREATE INDEX idx_candidates_name ON candidates(first_name, last_name);
CREATE INDEX idx_jobs_company_id ON jobs(company_id);
CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_assigned_recruiter ON jobs(assigned_recruiter_id);
CREATE INDEX idx_applications_candidate_id ON applications(candidate_id);
CREATE INDEX idx_applications_job_id ON applications(job_id);
CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_follow_up ON applications(follow_up_date);
CREATE INDEX idx_application_notes_application_id ON application_notes(application_id);
CREATE INDEX idx_application_notes_created_by ON application_notes(created_by_id);

-- Function to update the updated_at column automatically
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers to automatically update the updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_companies_updated_at BEFORE UPDATE ON companies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_candidates_updated_at BEFORE UPDATE ON candidates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_jobs_updated_at BEFORE UPDATE ON jobs FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_applications_updated_at BEFORE UPDATE ON applications FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_application_notes_updated_at BEFORE UPDATE ON application_notes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Sample data for development (optional - remove in production)
-- Insert a default admin user (password should be hashed in real application)
INSERT INTO users (username, email, password, first_name, last_name, role)
VALUES ('admin', 'admin@ats.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', 'User', 'ADMIN');

-- Insert a sample recruiter
INSERT INTO users (username, email, password, first_name, last_name, role)
VALUES ('recruiter1', 'recruiter@ats.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John', 'Recruiter', 'RECRUITER');

-- Insert sample companies
INSERT INTO companies (name, description, industry, location, website)
VALUES
    ('TechCorp Inc', 'Leading technology solutions provider', 'Technology', 'San Francisco, CA', 'https://techcorp.com'),
    ('DataSoft Solutions', 'Data analytics and software development', 'Software', 'Austin, TX', 'https://datasoft.com'),
    ('Innovation Labs', 'Research and development company', 'Research', 'Boston, MA', 'https://innovationlabs.com');
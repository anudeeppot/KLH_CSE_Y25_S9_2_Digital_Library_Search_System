-- =======================================================
-- DIGITAL LIBRARY SEARCH SYSTEM - DATABASE SCHEMA
-- Compatible with MySQL 8.x and H2 Database
-- =======================================================

CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255),
    title VARCHAR(500) NOT NULL,
    author VARCHAR(500) NOT NULL,
    category VARCHAR(255),
    publication_year VARCHAR(50),
    isbn VARCHAR(100),
    document_type VARCHAR(100),
    keywords VARCHAR(1000),
    abstract_text TEXT,
    content LONGTEXT,
    citation_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doc_title ON documents(title);
CREATE INDEX idx_doc_category ON documents(category);
CREATE INDEX idx_doc_type ON documents(document_type);

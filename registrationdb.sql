-- Create database
CREATE DATABASE registrationdb;

-- Use the database
USE registrationdb;

-- Create table for registrations
CREATE TABLE registrations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    dob DATE NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact VARCHAR(20) NOT NULL
);
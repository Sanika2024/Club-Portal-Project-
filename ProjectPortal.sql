-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS ProjectPortal
    DEFAULT CHARACTER SET = 'utf8mb4';

USE ProjectPortal;


-- Step 2: Create Students table
CREATE TABLE IF NOT EXISTS Student (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100),
    skills VARCHAR(255),
    interests VARCHAR(255)
);


-- Step 3: Create Clubs table
CREATE TABLE IF NOT EXISTS Club (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100),
    requiredSkills VARCHAR(255),
    description VARCHAR(255),
    teamMembers VARCHAR(255)
);

-- Step 4: Create Admin (no table needed if hardcoded, but optional for future)
-- Optional if you want a dynamic admin table
-- CREATE TABLE IF NOT EXISTS Admin (
--     username VARCHAR(50) PRIMARY KEY,
--     password VARCHAR(100) NOT NULL
-- );

-- Step 5: Create Projects table (for student and club projects)
CREATE TABLE IF NOT EXISTS Project (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    createdBy VARCHAR(50),
    FOREIGN KEY (createdBy) REFERENCES Student(U_No)
);
ALTER TABLE Project
ADD COLUMN requiredSkills TEXT;

ALTER TABLE Project ADD COLUMN memberLimit INT DEFAULT 5;

CREATE TABLE ProjectMembers (
    projectId INT,
    studentId INT,
    FOREIGN KEY (projectId) REFERENCES Project(id),
    FOREIGN KEY (studentId) REFERENCES Student(id)
);
ALTER TABLE ProjectMembers ADD CONSTRAINT fk_project
  FOREIGN KEY (projectId) REFERENCES Project(id)
  ON DELETE CASCADE;


-- Step 6: Create JoinProject table (for students joining projects)
CREATE TABLE IF NOT EXISTS JoinProject (
    id INT AUTO_INCREMENT PRIMARY KEY,
    studentId INT,
    projectId INT,
    FOREIGN KEY (studentId) REFERENCES Student(id),
    FOREIGN KEY (projectId) REFERENCES Project(id)
);

-- Step 7: Create Events table (for club events)
CREATE TABLE IF NOT EXISTS Event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    clubId INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    date DATE,
    FOREIGN KEY (clubId) REFERENCES Club(id)
);

-- Step 8: Create Collaborations table (club-to-club collaborations)
CREATE TABLE ClubCollaborations (
    club1_id INT,
    club2_id INT,
    FOREIGN KEY (club1_id) REFERENCES Club(id),
    FOREIGN KEY (club2_id) REFERENCES Club(id)
);

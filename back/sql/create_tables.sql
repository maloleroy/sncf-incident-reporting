-- Create table for Incident
CREATE TABLE IF NOT EXISTS incidents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    location TEXT NOT NULL,
    category TEXT NOT NULL,
    system TEXT NOT NULL,
    precision1 TEXT NOT NULL,
    precision2 TEXT NOT NULL,
    precision3 TEXT NOT NULL,
    subSystem TEXT NOT NULL,
    failure TEXT NOT NULL
)
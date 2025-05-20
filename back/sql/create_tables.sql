-- Create table for IncidentLocation
CREATE TABLE IF NOT EXISTS incident_location (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    main TEXT NOT NULL,
    precision1 TEXT NOT NULL,
    precision2 TEXT NOT NULL,
    precision3 TEXT NOT NULL
);

-- Create table for Incident
CREATE TABLE IF NOT EXISTS incidents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    lastUpdate INTEGER NOT NULL,
    location_id INTEGER NOT NULL,
    subSystem TEXT NOT NULL,
    failure TEXT NOT NULL,
    comment TEXT NOT NULL,
    sealed BOOLEAN NOT NULL,
    t4Call BOOLEAN NOT NULL,
    FOREIGN KEY (location_id) REFERENCES incident_location (id)
);

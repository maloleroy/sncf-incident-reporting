UPDATE incidents
SET
    timestamp = ?,
    location = ?,
    precision1 = ?,
    category = ?,
    precision2 = ?,
    system = ?,
    precision3 = ?,
    subSystem = ?,
    failure = ?
WHERE uuid = ?

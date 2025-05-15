SELECT i.id as incident_id, i.lastUpdate, il.id as location_id, il.main, il.precision1, il.precision2, il.precision3,
        i.subSystem, i.failure, i.comment, i.sealed, i.t4Call
FROM incidents i
JOIN incident_location il ON i.location_id = il.id

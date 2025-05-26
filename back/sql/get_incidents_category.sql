SELECT category, precision2, system, precision3, subSystem, failure
FROM {train}
WHERE rames LIKE '%' || ? || '%' 
  AND localisation LIKE '%' || ? || '%'
  AND precision1 LIKE '%' || ? || '%';

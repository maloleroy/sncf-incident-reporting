SELECT localisation, categorie, organe, precision_n2, precision_n3, sous_organe, defaillance
FROM DASYE_eau_incidents
WHERE rames LIKE '%' || ? || '%';

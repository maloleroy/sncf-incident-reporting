SELECT localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance
FROM DASYE_eau_incidents
WHERE rames LIKE '%' || ? || '%';

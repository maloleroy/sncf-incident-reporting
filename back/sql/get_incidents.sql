SELECT localisation, categorie, organe, precision_n1, precision_n2, precision_n3, sous_organe, defaillance
FROM {train}
WHERE rames LIKE '%' || ? || '%';

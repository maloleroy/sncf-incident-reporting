SELECT localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance
FROM {train}
WHERE rames LIKE '%' || ? || '%' AND localisation LIKE '%' || ? || '%' AND categorie LIKE '%' || ? || '%';

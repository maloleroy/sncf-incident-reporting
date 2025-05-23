SELECT sous_organe, defaillance
FROM {train}
WHERE rames LIKE '%' || ? || '%' AND localisation LIKE '%' || ? || '%' AND categorie LIKE '%' || ? || '%' AND organe LIKE '%' || ? || '%' AND precision_n1 LIKE '%' || ? || '%' AND precision_n2 LIKE '%' || ? || '%' AND precision_n3 LIKE '%' || ? || '%' AND sous_organe LIKE '%' || ? || '%';

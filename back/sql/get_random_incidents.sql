SELECT * FROM (
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'DASYE_eau_incidents' AS trainType FROM DASYE_eau_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'DUPLEX_WC_chimique_incidents' AS trainType FROM DUPLEX_WC_chimique_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'DUPLEX_WC_eau_incidents' AS trainType FROM DUPLEX_WC_eau_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'NEODUPLEX_chimique_incidents' AS trainType FROM NEODUPLEX_chimique_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'OCEANE_LIKE_incidents' AS trainType FROM OCEANE_LIKE_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'OUIGO1_incidents' AS trainType FROM OUIGO1_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'OUIGO2_incidents' AS trainType FROM OUIGO2_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'PLT_incidents' AS trainType FROM PLT_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'POS_incidents' AS trainType FROM POS_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'RDOM_incidents' AS trainType FROM RDOM_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'RITA_incidents' AS trainType FROM RITA_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'TANGO_incidents' AS trainType FROM TANGO_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'TGV_R_TRI_FO_incidents' AS trainType FROM TGV_R_TRI_FO_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'TGV_R_TRI_incidents' AS trainType FROM TGV_R_TRI_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'TRAIN_2N2_3UA_LYRIA_incidents' AS trainType FROM TRAIN_2N2_3UA_LYRIA_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'TRAIN_2N2_3UA_incidents' AS trainType FROM TRAIN_2N2_3UA_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'TRAIN_2N2_3UFC_incidents' AS trainType FROM TRAIN_2N2_3UFC_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'TRAIN_2N2_3UF_incidents' AS trainType FROM TRAIN_2N2_3UF_incidents
    UNION ALL
    SELECT rames, localisation, precision_n1, categorie, precision_n2, organe, precision_n3, sous_organe, defaillance, 'TRAIN_2N2_3UH_incidents' AS trainType FROM TRAIN_2N2_3UH_incidents
) 
ORDER BY RANDOM()
LIMIT 10;

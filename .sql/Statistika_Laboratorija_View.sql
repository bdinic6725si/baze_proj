CREATE VIEW `Statistika_Laboratorija` AS
SELECT 
    l.Naziv              AS Naziv_Laboratorije,
    l.Tip                AS Tip_Laboratorije,
    COUNT(DISTINCT s.ID_Sesije)  AS Ukupno_Sesija,
    AVG(vu.Vidljivost)           AS Prosecna_Vidljivost,
    AVG(vu.Temperatura)          AS Prosecna_Temperatura
FROM Laboratorija l
JOIN Sesija s            ON s.ID_Lab      = l.ID_Lab
JOIN Vremenski_Uslovi vu ON vu.ID_Sesije  = s.ID_Sesije
GROUP BY l.ID_Lab, l.Naziv, l.Tip
HAVING COUNT(DISTINCT s.ID_Sesije) > 3;
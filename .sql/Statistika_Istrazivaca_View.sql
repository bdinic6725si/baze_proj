CREATE VIEW Statistika_Istrazivaca AS
SELECT
    i.ID_Istrazivaca,
    i.Naziv              AS Ime_Istrazivaca,
    i.Kvalifikacija,
    COUNT(DISTINCT de.ID_Eksperimenta)  AS Broj_Dizajniranih_Eksperimenata,
    COUNT(DISTINCT ie.ID_Izvodjenja)    AS Broj_Izvodjenja
FROM Istrazivac i
JOIN Dizajner_Eksperimenta de ON de.ID_Istrazivaca = i.ID_Istrazivaca
JOIN Izvodjac_Eksperimenta ie ON ie.ID_Istrazivaca = i.ID_Istrazivaca
GROUP BY i.ID_Istrazivaca, i.Naziv, i.Kvalifikacija
HAVING COUNT(DISTINCT ie.ID_Izvodjenja) > 1;

SELECT 
    o.ID_Opservacije,
    o.Kvalitet_Podataka,
    o.Izmerena_Magnituda,
    o.SNR,
    o.Broj_Ekspozicija,
    no.Naucni_Naziv        AS Nebeski_Objekat,
    no.Tip_Objekta,
    e.Naziv                AS Naziv_Eksperimenta,
    e.Tip                  AS Tip_Eksperimenta,
    l.Naziv                AS Naziv_Laboratorije,
    l.Tip                  AS Tip_Laboratorije,
    s.Datum                AS Datum_Sesije,
    o.Napomena
FROM Opservacija o
JOIN Sesija s              ON s.ID_Sesije            = o.ID_Sesije
JOIN Laboratorija l        ON l.ID_Lab               = s.ID_Lab
JOIN Izvodjenje iz         ON iz.ID_Izvodjenja       = s.ID_Izvodjenja
JOIN Eksperiment e         ON e.ID_Eksperimenta      = iz.ID_Eksperimenta
JOIN Op_O_Objektu ooo      ON ooo.ID_Opservacije     = o.ID_Opservacije
JOIN Nebeski_Objekat no    ON no.ID_Nebeskog_Objekta = ooo.ID_Nebeskog_Objekta
ORDER BY 
    FIELD(o.Kvalitet_Podataka, 'odlican', 'dobar', 'slab', 'neupotrebljiv'),
    o.SNR DESC;
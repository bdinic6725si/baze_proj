SELECT
    o.ID_Opservacije,
    o.Kvalitet_Podataka,
    o.Izmerena_Magnituda,
    o.SNR,
    o.Broj_Ekspozicija,

    no.Naucni_Naziv AS Nebeski_Objekat,
    no.Tip_Objekta,

    e.Naziv AS Naziv_Eksperimenta,
    e.Tip AS Tip_Eksperimenta,

    l.Naziv AS Naziv_Laboratorije,
    l.Tip AS Tip_Laboratorije,

    s.Datum AS Datum_Sesije,
    s.Tip_Sesije,

    vu.Temperatura,
    vu.Vidljivost,
    vu.Oblacnost

FROM Opservacija o

JOIN Sesija s
    ON s.ID_Sesije = o.ID_Sesije

JOIN Izvodjenje iz
    ON iz.ID_Izvodjenja = s.ID_Izvodjenja

JOIN Eksperiment e
    ON e.ID_Eksperimenta = iz.ID_Eksperimenta

JOIN Laboratorija l
    ON l.ID_Lab = s.ID_Lab

JOIN Op_O_Objektu oo
    ON oo.ID_Opservacije = o.ID_Opservacije

JOIN Nebeski_Objekat no
    ON no.ID_Nebeskog_Objekta = oo.ID_Nebeskog_Objekta

LEFT JOIN Vremenski_Uslovi vu
    ON vu.ID_Sesije = s.ID_Sesije

WHERE o.Kvalitet_Podataka IN ('odlican', 'dobar')

ORDER BY
    o.SNR DESC,
    s.Datum DESC;
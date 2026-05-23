CREATE PROCEDURE Zakazi_Sesiju(
    IN p_ID_Izvodjenja  INT,
    IN p_ID_Lab         INT,
    IN p_Datum          DATE,
    IN p_Pocetak        TIME,
    IN p_Kraj           TIME,
    IN p_Tip            VARCHAR(10)
)
BEGIN
    DECLARE v_Preklapanje INT DEFAULT 0;
    
    SELECT COUNT(*) INTO v_Preklapanje
    FROM Sesija
    WHERE ID_Lab = p_ID_Lab
      AND Datum = p_Datum
      AND Vreme_Pocetka  < p_Kraj
      AND Vreme_Zavrsetka > p_Pocetak;
    
    IF v_Preklapanje = 0 THEN
    
        START TRANSACTION;
        
            INSERT INTO Sesija(
                ID_Izvodjenja, ID_Lab, Datum,
                Vreme_Pocetka, Vreme_Zavrsetka, Tip_Sesije
            )
            VALUES(
                p_ID_Izvodjenja, p_ID_Lab, p_Datum,
                p_Pocetak, p_Kraj, p_Tip
            );
            
            UPDATE Izvodjenje
            SET Status = 'zapoceto'
            WHERE ID_Izvodjenja = p_ID_Izvodjenja;
        
        COMMIT;
        
        SELECT * FROM Sesija
        WHERE ID_Sesije = LAST_INSERT_ID();
        
    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Greška: Sesija se preklapa sa postojećom!';
        
    END IF;

END

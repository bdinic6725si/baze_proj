DELIMITER //

CREATE FUNCTION Trajanje_Sesije(p_ID_Sesije INT)
RETURNS INT
DETERMINISTIC

BEGIN
    DECLARE v_Pocetak   TIME;
    DECLARE v_Kraj      TIME;
    DECLARE v_Trajanje  INT;
    
    SELECT Vreme_Pocetka, Vreme_Zavrsetka
    INTO v_Pocetak, v_Kraj
    FROM Sesija
    WHERE ID_Sesije = p_ID_Sesije;
    
    SET v_Trajanje = TIMESTAMPDIFF(MINUTE, v_Pocetak, v_Kraj);
    
    -- Korekcija za sesije koje prelaze ponoć
    IF v_Trajanje < 0 THEN
        SET v_Trajanje = v_Trajanje + 1440;
    END IF;
    
    RETURN v_Trajanje;
END	//

DELIMITER ;

-- Funkcija koja racuna trajanje sesija
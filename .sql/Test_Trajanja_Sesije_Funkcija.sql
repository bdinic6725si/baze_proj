DELIMITER //

CREATE FUNCTION Test_Trajanje_Sesije()
RETURNS BOOLEAN
DETERMINISTIC
BEGIN
    DECLARE v_Test1 INT;
    DECLARE v_Test2 INT;
    DECLARE v_Test3 INT;
    DECLARE v_Test4 INT;
    DECLARE v_Test5 INT;
    
    SET v_Test1 = Trajanje_Sesije(1); 	-- 210 min
    SET v_Test2 = Trajanje_Sesije(2);   -- 300 min
    SET v_Test3 = Trajanje_Sesije(3);   -- 210 min
    SET v_Test4 = Trajanje_Sesije(4);   -- 480 min
    SET v_Test5 = Trajanje_Sesije(6);   -- 480 min
    
    IF  v_Test1 = 210
    AND v_Test2 = 300
    AND v_Test3 = 210
    AND v_Test4 = 480
    AND v_Test5 = 480
    THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;

END	//

DELIMITER ;

-- Funkcija koja testira da li su ispravno izracunate sesije i njihova trajanja
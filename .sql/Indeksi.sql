-- Svi Indeksi u MySQL Workbenchu koji se kreiraju proizvoljno su neklasterovani i MySQL jedino podržava BTREE strukturu indeksa

-- 1. Indeks za brzu pretragu istraživača po imenu
-- Optimizacija upita: Ubrzava pretragu istraživača po imenu
CREATE INDEX IX_Istrazivac_Naziv ON Istrazivac(Naziv);

-- 2. Indeks za brzu pretragu objekata po naučnom nazivu
-- Optimizacija upita: Ubrzava upite koji traže tačan objekat (npr. WHERE Naucni_Naziv = 'Andromeda').
CREATE INDEX IX_Neb_Obj_Naziv ON Nebeski_Objekat(Naucni_Naziv);

-- 3. Indeks za filtriranje nebeskih objekata po tipu
-- Optimizacija upita: Savršeno za izveštaje gde želimo da izdvojimo samo 'Pulsare' ili samo 'Zvezde'.
CREATE INDEX IX_Neb_Obj_Tip ON Nebeski_Objekat(Tip_Objekta);

-- 4. Indeks za vremenske opsege izvođenja eksperimenata
-- Optimizacija upita: Ubrzava pronalaženje eksperimenata iz određenog vremenskog perioda.
CREATE INDEX IX_Izvodjenje_Datum ON Izvodjenje(Datum);

-- 5. Indeks za filtriranje po statusu izvođenja
-- Optimizacija upita: Dramatično ubrzava upite koji traže npr. samo 'planirano' ili 'zavrseno_uspesno' izvođenja.
CREATE INDEX IX_Izvodjenje_Status ON Izvodjenje(Status);

-- 6. Indeks za pronalaženje najkvalitetnijih podataka iz opservacija (Signal-to-Noise Ratio)
-- Optimizacija upita: Bitan za analitiku kada tražimo opservacije visokog kvaliteta (npr. WHERE SNR > 5.0).
CREATE INDEX IX_Opservacija_SNR ON Opservacija(SNR);

-- 7. Indeks za brzu proveru dostupnosti resursa
-- Optimizacija upita: Omogućava sistemu da brzo izlista resurse koji se mogu odmah zadužiti.
CREATE INDEX IX_Resurs_Dostupnost ON Resurs(Dostupnost);

-- 8. Indeks za filtriranje tipa laboratorije
-- Optimizacija upita: Korisno za pronalaženje npr. svih "Optickih_Opservatorija" pri planiranju eksperimenta.
CREATE INDEX IX_Lab_Tip ON Laboratorija(Tip);

-- 9. Indeks za raspon datuma kod sesija
-- Optimizacija upita: Pomaže pri generisanju mesečnih ili godišnjih izveštaja o vremenu provedenom u opservatoriji.
CREATE INDEX IX_Sesija_Datum ON Sesija(Datum);

-- 10. Indeks za idealne astronomske vremenske uslove
-- Optimizacija upita: U astronomiji je ovo kritično. Oblačnost utiče na kvalitet podataka.
CREATE INDEX IX_Vreme_Oblacnost ON Vremenski_Uslovi(Oblacnost);
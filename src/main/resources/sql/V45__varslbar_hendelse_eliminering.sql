-- OPPRETTET ARBEIDSGIVER
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select deltaker_fnr from avtale where avtale.id = hendelselogg.avtale_id), 'Avtale er opprettet', AVTALE_ID, TIDSPUNKT, HENDELSE, true, 'ARBEIDSGIVER', 'DELTAKER'
FROM hendelselogg WHERE hendelse = 'OPPRETTET_AV_ARBEIDSGIVER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = hendelselogg.avtale_id), 'Avtale er opprettet', AVTALE_ID, TIDSPUNKT, HENDELSE, false, 'ARBEIDSGIVER', 'ARBEIDSGIVER'
FROM hendelselogg WHERE hendelse = 'OPPRETTET_AV_ARBEIDSGIVER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = hendelselogg.avtale_id), 'Avtale er opprettet', AVTALE_ID, TIDSPUNKT, HENDELSE, true, 'ARBEIDSGIVER', 'VEILEDER'
FROM hendelselogg WHERE hendelse = 'OPPRETTET_AV_ARBEIDSGIVER';
-----------------------------------------------------------------------

-- OPPRETTET VEILEDER
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select deltaker_fnr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er opprettet', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, true, 'VEILEDER', 'DELTAKER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'OPPRETTET';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er opprettet', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, true, 'VEILEDER', 'ARBEIDSGIVER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'OPPRETTET';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er opprettet', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'VEILEDER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'OPPRETTET';
------------------------------------------

-- GODKJENT_AV_ARBEIDSGIVER
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av arbeidsgiver', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, true, 'ARBEIDSGIVER', 'VEILEDER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_AV_ARBEIDSGIVER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select deltaker_fnr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av arbeidsgiver', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'ARBEIDSGIVER', 'DELTAKER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_AV_ARBEIDSGIVER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av arbeidsgiver', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'ARBEIDSGIVER', 'ARBEIDSGIVER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_AV_ARBEIDSGIVER';
-----------------------------------------

-- GODKJENT_AV_VEILEDER
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, true, 'VEILEDER', 'ARBEIDSGIVER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_AV_VEILEDER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select deltaker_fnr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, true, 'VEILEDER', 'DELTAKER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_AV_VEILEDER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'VEILEDER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_AV_VEILEDER';
-------------------------------------------

-- GODKJENT_AV_DELTAKER
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av deltaker', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'DELTAKER', 'ARBEIDSGIVER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_AV_DELTAKER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av deltaker', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, true, 'DELTAKER', 'VEILEDER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_AV_DELTAKER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av deltaker', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'DELTAKER', 'ARBEIDSGIVER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_AV_DELTAKER';
---------------------------------------------

-- GODKJENT_PAA_VEGNE_AV
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av NAV-veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, true, 'VEILEDER', 'ARBEIDSGIVER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_PAA_VEGNE_AV';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av NAV-veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'VEILEDER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_PAA_VEGNE_AV';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select deltaker_fnr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale er godkjent av NAV-veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'DELTAKER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENT_PAA_VEGNE_AV';
-------------------------------------------

-- GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select deltaker_fnr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtalens godkjenninger er opphevet av arbeidsgiver', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'DELTAKER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtalens godkjenninger er opphevet av arbeidsgiver', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'VEILEDER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtalens godkjenninger er opphevet av arbeidsgiver', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'ARBEIDSGIVER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER';
---------------------------------------------

-- GODKJENNINGER_OPPHEVET_AV_VEILEDER
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtalens godkjenninger er opphevet av NAV-veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'ARBEIDSGIVER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENNINGER_OPPHEVET_AV_VEILEDER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtalens godkjenninger er opphevet av NAV-veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'VEILEDER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENNINGER_OPPHEVET_AV_VEILEDER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select deltaker_fnr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtalens godkjenninger er opphevet av NAV-veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'DELTAKER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'GODKJENNINGER_OPPHEVET_AV_VEILEDER';
---------------------------------------------

-- DELT_MED_DELTAKER
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select deltaker_fnr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale delt med deltaker', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'DELTAKER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'DELT_MED_DELTAKER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale delt med deltaker', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'VEILEDER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'DELT_MED_DELTAKER';
--------------------------------------------

-- DELT_MED_ARBEIDSGIVER
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select deltaker_fnr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale delt med arbeidsgiver', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'DELTAKER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'DELT_MED_ARBEIDSGIVER';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale delt med arbeidsgiver', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'VEILEDER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'DELT_MED_ARBEIDSGIVER';
-------------------------------------------

-- AVBRUTT
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, veileder_nav_ident, 'Avtale avbrutt av veileder', ID, 'AVBRUTT', SIST_ENDRET, false, 'VEILEDER', 'VEILEDER'
FROM AVTALE WHERE AVBRUTT = true;

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, deltaker_fnr, 'Avtale avbrutt av veileder', ID, 'AVBRUTT', SIST_ENDRET, true, 'VEILEDER', 'DELTAKER'
FROM AVTALE WHERE AVBRUTT = true;

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, bedrift_nr, 'Avtale avbrutt av veileder', ID, 'AVBRUTT', SIST_ENDRET, true, 'VEILEDER', 'ARBEIDSGIVER'
FROM AVTALE WHERE AVBRUTT = true;
------------------------------------------

-- LÅST_OPP
INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select veileder_nav_ident from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale låst opp av veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, false, 'VEILEDER', 'VEILEDER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'LÅST_OPP';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale låst opp av veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, true, 'VEILEDER', 'ARBEIDSGIVER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'LÅST_OPP';

INSERT INTO varsel (ID, LEST, IDENTIFIKATOR, TEKST, AVTALE_ID, HENDELSE_TYPE, TIDSPUNKT, BJELLE, UTFØRT_AV, MOTTAKER)
SELECT gen_random_uuid (), true, (select bedrift_nr from avtale where avtale.id = varslbar_hendelse.avtale_id), 'Avtale låst opp av veileder', AVTALE_ID, VARSLBAR_HENDELSE_TYPE, TIDSPUNKT, true, 'VEILEDER', 'DELTAKER'
FROM VARSLBAR_HENDELSE WHERE VARSLBAR_HENDELSE_TYPE = 'LÅST_OPP';
------------------------------------------

-- GJENOPPRETTET

create table sms_varsel_ny as select sms_varsel.id, sms_varsel.status, sms_varsel.telefonnummer, sms_varsel.identifikator, sms_varsel.meldingstekst, v.tidspunkt, v.utført_av, v.varslbar_hendelse_type from sms_varsel inner join varslbar_hendelse v on v.id=sms_varsel.varslbar_hendelse;

alter table sms_varsel rename to sms_varsel_gammel;
alter table sms_varsel_ny rename to sms_varsel;
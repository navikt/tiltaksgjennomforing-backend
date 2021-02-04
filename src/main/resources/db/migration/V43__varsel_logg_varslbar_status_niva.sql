alter table bjelle_varsel add column varslbar_status varchar;
alter table hendelselogg add column beskrivelse varchar;
alter table hendelselogg add column hendelse_status varchar;

alter table bjelle_varsel rename to varsel

package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

// Nødvendig å gjøre denne migreringen i Java fordi constraint-navn ikke kan settes inn i alter table-spørring som tekststreng
public class V18__fjerne_constraints extends BaseJavaMigration {
    public void migrate(Context context) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));

        String målConstraint = jdbcTemplate.queryForObject("select constraint_name from information_schema.constraints where constraint_type='REFERENTIAL' and table_name='MAAL'", String.class);
        jdbcTemplate.execute("alter table maal drop constraint " + målConstraint);

        String oppgaveConstraint = jdbcTemplate.queryForObject("select constraint_name from information_schema.constraints where constraint_type='REFERENTIAL' and table_name='OPPGAVE'", String.class);
        jdbcTemplate.execute("alter table oppgave drop constraint " + oppgaveConstraint);

        String bjelleVarselConstraintAvtale = jdbcTemplate.queryForObject("select constraint_name from information_schema.constraints where constraint_type='REFERENTIAL' and table_name='BJELLE_VARSEL' and column_list='AVTALE_ID'", String.class);
        jdbcTemplate.execute("alter table bjelle_varsel drop constraint " + bjelleVarselConstraintAvtale);

        String bjelleVarselConstraintVarslbarHendelse = jdbcTemplate.queryForObject("select constraint_name from information_schema.constraints where constraint_type='REFERENTIAL' and table_name='BJELLE_VARSEL' and column_list='VARSLBAR_HENDELSE'", String.class);
        jdbcTemplate.execute("alter table bjelle_varsel drop constraint " + bjelleVarselConstraintVarslbarHendelse);

        String smsVarselConstraintVarslbarHendelse = jdbcTemplate.queryForObject("select constraint_name from information_schema.constraints where constraint_type='REFERENTIAL' and table_name='SMS_VARSEL'", String.class);
        jdbcTemplate.execute("alter table sms_varsel drop constraint " + smsVarselConstraintVarslbarHendelse);
    }
}
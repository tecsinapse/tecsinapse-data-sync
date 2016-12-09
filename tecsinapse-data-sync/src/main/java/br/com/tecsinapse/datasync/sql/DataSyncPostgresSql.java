package br.com.tecsinapse.datasync.sql;

import br.com.tecsinapse.datasync.enums.DataSyncEvent;
import br.com.tecsinapse.datasync.exception.DataSyncStartupException;

import java.util.Set;

public class DataSyncPostgresSql implements DataSyncSql {

    private static final String TRIGGER_PREFIX_NAME = "data_sync_%s_on_%s";
    private static final String FUNCTION_PREFIX_NAME = "implement_data_sync_queue_%s_when_%s";

    private static DataSyncPostgresSql instance = new DataSyncPostgresSql();

    public static DataSyncSql getInstance() {
        return instance;
    }

    @Override
    public String getSqlToCreateDataSyncTable() {
        return "CREATE TABLE IF NOT EXISTS data_sync (" +
               "    id bigint NOT NULL DEFAULT nextval('data_sync_seq')," +
               "    table_name varchar(150) NOT NULL," +
               "    action varchar(10) NOT NULL," +
               "    tentativas integer," +
               "    object_id bigint NOT NULL," +
               "    changed_fields varchar(500)," +
               "    CONSTRAINT data_sync_pkey PRIMARY KEY (id)" +
               ")";
    }

    @Override
    public String getSqlToCreateDataSyncSequenceIfNotExists() {
        return " DO $$" +
               " BEGIN" +
               "    IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'data_sync_seq' )" +
               "    THEN" +
               "        CREATE SEQUENCE data_sync_seq;" +
               "    END IF;" +
               " END$$";
    }

    @Override
    public String getSqlToCreateFunctionToExecuteWithTrigger(DataSyncEvent dataSyncEvent, String tableName, Set<String> fields) {
        switch (dataSyncEvent) {
            case INSERT:
                return getSqlFunctionToExecutoWhenInsert(tableName);
            case UPDATE:
                return getSqlFunctionToExecutoWhenUpdate(tableName, fields);
            case DELETE:
                return getSqlFunctionToExecutoWhenDelete(tableName);
            default:
                throw new DataSyncStartupException("DataSyncEvent doesn't supported to create function.");
        }
    }

    private String getSqlFunctionToExecutoWhenInsert(String tableName) {
        return " CREATE OR REPLACE FUNCTION " + getFunctionName(tableName, DataSyncEvent.INSERT) + "() RETURNS TRIGGER AS $queue_data_sync$" +
                " BEGIN" +
                "    INSERT INTO data_sync(TABLE_NAME, action, object_id) values (TG_TABLE_NAME, TG_OP, NEW.ID);" +
                "    RETURN NEW;" +
                " END;" +
                " $queue_data_sync$ language plpgsql;";
    }

    private String getSqlFunctionToExecutoWhenUpdate(String tableName, Set<String> fields) {

        return "CREATE OR REPLACE FUNCTION " + getFunctionName(tableName, DataSyncEvent.UPDATE) + "() RETURNS TRIGGER AS $fila_data_sync$" +
                " declare" +
//                "    fields varchar(500);" +
                " BEGIN" +
//                "    fields := '';" +

//                     validateChangedFields(fields) +

//                "    INSERT INTO data_sync(TABLE_NAME, action, object_id, changed_fields) values (TG_TABLE_NAME, TG_OP, NEW.ID, fields);" +
                "    INSERT INTO data_sync(table_name, action, object_id) values (TG_TABLE_NAME, TG_OP, NEW.ID);" +
                "    RETURN NEW;" +
                " END;" +
                "$fila_data_sync$ language plpgsql;";
    }

    private String validateChangedFields(Set<String> fields) {
        String result = "";

        for (String fieldName : fields) {
            result +=
                    " IF (NEW." + fieldName + " <> OLD." + fieldName + ") THEN" +
                    "    fields := fields || '" + fieldName + ",';" +
                    " end if;";
        }

        return result;
    }

    private String getSqlFunctionToExecutoWhenDelete(String tableName) {
        return " CREATE OR REPLACE FUNCTION " + getFunctionName(tableName, DataSyncEvent.DELETE) + "() RETURNS TRIGGER AS $queue_data_sync$" +
                " BEGIN" +
                "    INSERT INTO data_sync(TABLE_NAME, action, object_id) values (TG_TABLE_NAME, TG_OP, OLD.ID);" +
                "    RETURN OLD;" +
                " END;" +
                " $queue_data_sync$ language plpgsql;";
    }

    @Override
    public String getSqlToCreateTrigger(String tableName, DataSyncEvent dataSyncEvent) {
        return " create trigger " + getTriggerName(tableName, dataSyncEvent) +
               " after " + dataSyncEvent.getEvent() + " on " + tableName +
               " FOR EACH ROW EXECUTE procedure " + getFunctionName(tableName, dataSyncEvent) + "()";
    }

    @Override
    public String getSqlToDropTrigger(String tableName, DataSyncEvent dataSyncEvent) {
        return " drop trigger if exists " + getTriggerName(tableName, dataSyncEvent) + " on " + tableName;
    }

    @Override
    public String getTriggerName(String table, DataSyncEvent dataSyncEvent) {
        return String.format(TRIGGER_PREFIX_NAME, table, dataSyncEvent.getEvent());
    }

    @Override
    public String getFunctionName(String tableName, DataSyncEvent dataSyncEvent) {
        return String.format(FUNCTION_PREFIX_NAME, tableName, dataSyncEvent.getEvent());
    }

}

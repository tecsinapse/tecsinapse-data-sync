package br.com.tecsinapse.datasync.sql;

import br.com.tecsinapse.datasync.enums.DataSyncEvent;

import java.util.Set;

public interface DataSyncSql {

    String SQL_SCANNER = "select id, table_name, action, object_id from data_sync";

    String getSqlToCreateDataSyncTable();

    String getSqlToCreateDataSyncSequenceIfNotExists();

    String getSqlToCreateFunctionToExecuteWithTrigger(DataSyncEvent dataSyncEvent, String tableName, Set<String> fields);

    String getSqlToCreateTrigger(String tableName, DataSyncEvent dataSyncEvent);

    String getSqlToDropTrigger(String tableName, DataSyncEvent dataSyncEvent);

    String getTriggerName(String tableName, DataSyncEvent dataSyncEvent);

    String getFunctionName(String tableName, DataSyncEvent dataSyncEvent);

    static DataSyncSql getInstance() {
        //TODO identify database and return correct database sql.
        return DataSyncPostgresSql.getInstance();
    }

}

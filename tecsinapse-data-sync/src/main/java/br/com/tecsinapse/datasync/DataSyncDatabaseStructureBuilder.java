package br.com.tecsinapse.datasync;

import br.com.tecsinapse.datasync.enums.DataSyncEvent;
import br.com.tecsinapse.datasync.sql.DataSyncSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public class DataSyncDatabaseStructureBuilder {

    public static void createDataSyncTable(Connection connection) throws SQLException {
        createTable(connection);
    }

    public static void createTriggers(Connection connection, String tableName, Set<String> fields, DataSyncEvent... events) throws SQLException {
        for (DataSyncEvent event : events) {
            createFunction(connection, event, tableName, fields);

            dropTrigger(connection, tableName, event);

            final PreparedStatement createTrigger =
                    connection.prepareStatement(DataSyncSql.getInstance().getSqlToCreateTrigger(tableName, event));
            createTrigger.execute();
        }
    }

    private static void dropTrigger(Connection connection, String tableName, DataSyncEvent dataSyncEvent) throws SQLException {
        final PreparedStatement dropTrigger = connection.prepareStatement(DataSyncSql.getInstance().getSqlToDropTrigger(tableName, dataSyncEvent));
        dropTrigger.execute();
    }

    private static void createFunction(Connection connection, DataSyncEvent event, String tableName, Set<String> fields) throws SQLException {
        final PreparedStatement createFunction = connection.prepareStatement(
                DataSyncSql.getInstance().getSqlToCreateFunctionToExecuteWithTrigger(event, tableName, fields));
        createFunction.execute();
    }

    private static void createTable(Connection connection) throws SQLException {
        createSequence(connection);
        final PreparedStatement createTable = connection.prepareStatement(DataSyncSql.getInstance().getSqlToCreateDataSyncTable());
        createTable.execute();
    }

    private static void createSequence(Connection connection) throws SQLException {
        final PreparedStatement crateSequence = connection.prepareStatement(
                DataSyncSql.getInstance().getSqlToCreateDataSyncSequenceIfNotExists());
        crateSequence.execute();
    }

}

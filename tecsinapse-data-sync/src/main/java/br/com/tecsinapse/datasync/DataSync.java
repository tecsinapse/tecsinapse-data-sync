package br.com.tecsinapse.datasync;

import br.com.tecsinapse.datasync.annotations.SyncField;
import br.com.tecsinapse.datasync.annotations.SyncTable;
import br.com.tecsinapse.datasync.enums.DataSyncEvent;
import br.com.tecsinapse.datasync.exception.DataSyncStartupException;
import com.google.common.base.Strings;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataSync {

    private static final Logger LOG = LoggerFactory.getLogger(DataSync.class);

    void init(Connection connection) {
        try {
            DataSyncDatabaseStructureBuilder.createDataSyncTable(connection);
            loadEntitiesToSync(connection);
        } catch (SQLException e) {
            LOG.error("Error to build DataSync structure. Error: ", e);
            throw new DataSyncStartupException(e.getMessage());
        }

        LOG.info("DATASYNC STARTED!");
    }

    public static void loadEntitiesToSync(Connection connection) throws SQLException {
        Reflections reflections = new Reflections("br.com.tecsinapse");
        Set<Class<?>> mappedClasses = reflections.getTypesAnnotatedWith(SyncTable.class, true);

        for (Class<?> clazz : mappedClasses) {
            final String tableName = getTableName(clazz);
            final Set<String> mappedFields = extractSyncFieldNames(clazz);
            final DataSyncEvent[] events = clazz.getAnnotation(SyncTable.class).syncWhen();

            DataSyncDatabaseStructureBuilder.createTriggers(connection, tableName, mappedFields, events);
            DataSyncQuery.putQuery(tableName, mappedFields);

            System.out.println("SQL de Consulta: " + DataSyncQuery.getQuery(tableName));
            LOG.info("SQL de Consulta: " + DataSyncQuery.getQuery(tableName));
        }

    }

    private static String getTableName(Class<?> clazz) {
        final String tableName = clazz.getAnnotation(SyncTable.class).name();
        return Strings.isNullOrEmpty(tableName) ? clazz.getSimpleName().toLowerCase() : tableName;
    }

    private static Set<String> extractSyncFieldNames(Class<?> clazz) {
        return Stream.of(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(SyncField.class))
                .map(field -> {
                    final SyncField syncField = field.getDeclaredAnnotation(SyncField.class);
                    final String fieldColumn = Strings.isNullOrEmpty(syncField.name()) ? field.getName().toLowerCase() : syncField.name();
                    return fieldColumn + " as " + syncField.attribute();
                })
                .collect(Collectors.toSet());
    }

}

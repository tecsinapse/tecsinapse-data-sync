package br.com.tecsinapse.datasync;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DataSyncQuery {

    private static Map<String, String> queryByTableName = new HashMap<>();

    static void putQuery(String tableName, Set<String> mappedFields) {
        final String query = new StringBuilder("select ")
                .append(mappedFields.stream().collect(Collectors.joining(", ")))
                .append(" from ")
                .append(tableName)
                .toString();

        queryByTableName.put(tableName, query);
    }

    public static String getQuery(String tableName) {
        return queryByTableName.get(tableName);
    }

}

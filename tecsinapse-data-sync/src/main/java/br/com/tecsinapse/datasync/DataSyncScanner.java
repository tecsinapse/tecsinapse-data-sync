package br.com.tecsinapse.datasync;

import br.com.tecsinapse.datasync.model.DataSyncItem;
import br.com.tecsinapse.datasync.sql.DataSyncSql;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

public class DataSyncScanner {

    private static final Logger LOG = LoggerFactory.getLogger(DataSyncScanner.class);

    public void run(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(DataSyncSql.SQL_SCANNER)) {
            ResultSet rs = ps.executeQuery();

            Set<DataSyncItem> itens = new HashSet<>();

            while (rs.next()) {
                DataSyncItem item = new DataSyncItem();

                item.setId(rs.getLong("id"));
                item.setAction(rs.getString("action"));
                item.setObjectId(rs.getLong("object_id"));
                item.setTableName(rs.getString("table_name"));

                itens.add(item);
            }

            JSONArray json = generateJson(connection, itens);

            LOG.info(json.toString());
        }
    }

    private JSONArray generateJson(Connection connection, Set<DataSyncItem> itens) throws SQLException {
        JSONArray jsonArray = new JSONArray();

        final Map<String, List<DataSyncItem>> itensByTableName = itens.stream()
                .collect(groupingBy(DataSyncItem::getTableName));

        for (Map.Entry<String, List<DataSyncItem>> itemByTableEntry : itensByTableName.entrySet()) {
            String tableName = itemByTableEntry.getKey();
            List<DataSyncItem> tableItens = itemByTableEntry.getValue();
            JSONObject tableObject = new JSONObject();
            JSONArray tableItensJsonArray = new JSONArray();

            final String identifiers = tableItens.stream()
                    .map(DataSyncItem::getObjectId)
                    .map(String::valueOf)
                    .collect(joining(", "));

            //TODO: Configure what field is the ID using annotation
            final String whereInClause = String.format(" where id in (%s)", identifiers);
            final String query = DataSyncQuery.getQuery(tableName) + whereInClause;

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    tableItensJsonArray.add(createJsonObjectFromResultSet(rs));
                }
            }

            tableObject.put("tableName", tableName);
            tableObject.put("itens", tableItensJsonArray);
            jsonArray.add(tableObject);
        }

        return jsonArray;
    }

    private JSONObject createJsonObjectFromResultSet(ResultSet rs) throws SQLException {
        JSONObject itemJson = new JSONObject();
        final ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            final String columnLabel = metaData.getColumnLabel(i + 1);
            final String columnValue = rs.getString(columnLabel);
            itemJson.put(columnLabel, columnValue);
        }
        return itemJson;
    }

}

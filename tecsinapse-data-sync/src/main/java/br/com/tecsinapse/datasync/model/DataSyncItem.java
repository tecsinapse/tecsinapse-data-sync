package br.com.tecsinapse.datasync.model;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public class DataSyncItem {
    private long id;
    private String tableName;
    private String action;
    private long objectId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSyncItem)) return false;
        DataSyncItem that = (DataSyncItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("tableName", tableName)
                .add("action", action)
                .add("objectId", objectId)
                .toString();
    }
}

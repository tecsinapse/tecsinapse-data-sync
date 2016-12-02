package br.com.tecsinapse.datasync.enums;

public enum DataSyncEvent {

    INSERT, UPDATE, DELETE;

    public String getEvent() {
        return this.toString();
    }

}

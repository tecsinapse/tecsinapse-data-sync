package br.com.tecsinapse.datasync.annotations;

import br.com.tecsinapse.datasync.enums.DataSyncEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SyncTable {

    /**
     * Table name on database.
     * @return
     */
    String name() default "";

    DataSyncEvent[] syncWhen() default { DataSyncEvent.INSERT, DataSyncEvent.UPDATE, DataSyncEvent.DELETE };

}

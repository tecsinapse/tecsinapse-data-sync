package br.com.tecsinapse.datasync.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SyncField {

    /**
     * Field name on database.
     * @return
     */
    String name();

    String attribute();
}

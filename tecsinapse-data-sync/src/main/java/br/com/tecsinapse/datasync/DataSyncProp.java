package br.com.tecsinapse.datasync;

import br.com.tecsinapse.core.CachedConfigFactory;
import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(Config.LoadType.MERGE)
@Sources({"classpath:tecsinapse/datasync.properties", "classpath:tecsinapse/default-datasync.properties"})
public interface DataSyncProp extends Accessible {

    DataSyncProp INSTANCE = CachedConfigFactory.create(DataSyncProp.class);

    @Key("datasource")
    String datasource();

}

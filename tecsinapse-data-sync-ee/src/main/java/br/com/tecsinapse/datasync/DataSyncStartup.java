package br.com.tecsinapse.datasync;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class DataSyncStartup {

    @Inject
    private DataSync dataSync;

    @PostConstruct
    private void init() {
        dataSync.init();
    }

}

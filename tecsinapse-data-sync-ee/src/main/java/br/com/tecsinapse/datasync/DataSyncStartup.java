package br.com.tecsinapse.datasync;

import br.com.tecsinapse.datasync.util.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class DataSyncStartup {

    private static final Logger LOG = LoggerFactory.getLogger(DataSyncStartup.class);

    @Inject
    private DataSync dataSync;

    @PostConstruct
    private void init() {
        dataSync.init(ConnectionFactory.getConnection());
    }



}

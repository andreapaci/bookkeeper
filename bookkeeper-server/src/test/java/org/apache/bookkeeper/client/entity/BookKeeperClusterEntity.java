package org.apache.bookkeeper.client.entity;

import org.apache.bookkeeper.bookie.storage.ldb.DbLedgerStorage;
import org.apache.bookkeeper.common.allocator.PoolingPolicy;
import org.apache.bookkeeper.conf.ServerConfiguration;

public class BookKeeperClusterEntity {

    private ServerConfiguration conf;   //Configurazione del server
    private String ledgersRootPath;     //Root path dei ledgers di ZooKeeper che fanno storing dei metadati dei bookies e dei ledgers
    private String instanceId;          //ID di istanza del cluster dei metadati, viene usato per confrontare
    private boolean retreiveIstanceId;  //Se settato a true, viene parsato l'istanceId da Zookeeper
    private boolean force;              //Se questo check è attivo, viene bypassato il check dell'istance ID e viene fatto il nuke lo stesso

    public BookKeeperClusterEntity(ServerConfiguration conf, boolean baseConf, String ledgersRootPath, boolean retreiveIstanceId, String instanceId, boolean force) {
        this.conf = conf;
        this.ledgersRootPath = ledgersRootPath;
        this.instanceId = instanceId;
        this.retreiveIstanceId = retreiveIstanceId;
        this.force = force;

        //Configurazione di base per fare il setup della configurazione server, presa dai test di Apache
        if(baseConf && conf != null)
            baseConfig();

    }



    public BookKeeperClusterEntity(ServerConfiguration conf, boolean baseConf, String ledgersRootPath) {
        this.conf = conf;
        this.ledgersRootPath = ledgersRootPath;

        //Configurazione di base per fare il setup della configurazione server, presa dai test di Apache
        if(baseConf && conf != null)
            baseConfig();

    }


    //Configurazione base per il server
    private void baseConfig(){

        conf.setJournalFlushWhenQueueEmpty(true);
        conf.setJournalFormatVersionToWrite(5);
        conf.setAllowEphemeralPorts(true);
        conf.setBookiePort(0);
        conf.setGcWaitTime(1000);
        conf.setDiskUsageThreshold(0.999f);
        conf.setDiskUsageWarnThreshold(0.99f);
        conf.setAllocatorPoolingPolicy(PoolingPolicy.UnpooledHeap);
        conf.setProperty(DbLedgerStorage.WRITE_CACHE_MAX_SIZE_MB, 4);
        conf.setProperty(DbLedgerStorage.READ_AHEAD_CACHE_MAX_SIZE_MB, 4);

    }

    public void setInstanceId(String instanceId){
        this.instanceId = instanceId;
    }

    public ServerConfiguration getConf() {
        return conf;
    }

    public String getLedgersRootPath() {
        return ledgersRootPath;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public boolean isForce() {
        return force;
    }

    public boolean isRetreiveIstanceId() {
        return retreiveIstanceId;
    }

}

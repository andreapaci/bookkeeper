package org.apache.bookkeeper.client;

import org.apache.bookkeeper.client.entity.BookKeeperClusterEntity;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.meta.zk.ZKMetadataDriverBase;
import org.apache.bookkeeper.test.ZooKeeperCluster;
import org.apache.bookkeeper.test.ZooKeeperClusterUtil;
import org.apache.bookkeeper.util.BookKeeperConstants;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static java.nio.charset.StandardCharsets.UTF_8;


/*
 * Testing del nuke del cluster bookkeeper
 *  - Andrea Paci
 */


@RunWith(value = Parameterized.class)
public class BookKeeperAdminNukeTest {

    private BookKeeperClusterEntity cluster;
    private ZooKeeperCluster zk;
    private boolean expectedResult;

    @Before
    public void setUp() throws Exception {

        //E' necessario inizializzare un cluster prima di testare il suo nuke
        System.out.println("Inizializzazione");

        //Il cluster di metadati è gestito con ZooKeeper, instanziamo quindi un cluster Zookeeper
        try {

            zk = new ZooKeeperClusterUtil(5);
            zk.startCluster();



            System.out.println("Cluster Zookeeper inizializzato e running");

            //E' necessario specificare lo URI dove vengono salvati i ledgers
            if(cluster.getConf() != null) {
                cluster.getConf().setMetadataServiceUri(zk.getMetadataServiceUri());
                //Inizializazione del nuovo cluster
                BookKeeperAdmin.initNewCluster(cluster.getConf());

                byte[] data = zk.getZooKeeperClient().getData(
                        ZKMetadataDriverBase.resolveZkLedgersRootPath(cluster.getConf()) + "/" + BookKeeperConstants.INSTANCEID,
                        false, null);
                String readInstanceId = new String(data, UTF_8);

                if(cluster.isRetreiveIstanceId()) cluster.setInstanceId(readInstanceId);



            }




            System.out.println("Cluster inizializzato");
        }
        catch (Exception e) { System.out.println("Failure nell'inizializzazione del cluster"); e.printStackTrace();}


    }

    @After
    public void tearDown() throws Exception {

        System.out.println("Effettuo il teardown dell'enviroment");
        try{
            zk.getZooKeeperClient().close();
            zk.killCluster();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Teardown di Zookeeper effettuato");


    }



    @Parameterized.Parameters
    public static Collection<?> getParameters(){
        return Arrays.asList(new Object[][] {
                {new BookKeeperClusterEntity(null, true, "/ledgers", true, "6657388", true), false},
                {new BookKeeperClusterEntity(new ServerConfiguration(), true, "/ledgers", true, "6657388", true), true},
                {new BookKeeperClusterEntity(new ServerConfiguration(), false, null, true,  null, false), false},
                {new BookKeeperClusterEntity(new ServerConfiguration(), true,"/ledgers", false,"485902049", true), true},
                {new BookKeeperClusterEntity(new ServerConfiguration(), false,"/ledgers",false,  null, false), false},
                {new BookKeeperClusterEntity(new ServerConfiguration(), false,"/wrongPath",false,  null, true), false}

        });
    }

    public BookKeeperAdminNukeTest(BookKeeperClusterEntity cluster, boolean expectedResult) {
        this.cluster = cluster;
        this.expectedResult = expectedResult;

        System.out.println("Test inizializzato");


    }





    @Test
    public void nukeExistingCluster() {

        boolean result;
        System.out.println("Eseguo il test di nuke");
        try {
            result = BookKeeperAdmin.nukeExistingCluster(cluster.getConf(), this.cluster.getLedgersRootPath(), cluster.getInstanceId(), cluster.isForce());
                }
        catch(Exception e){
            e.printStackTrace();
            result = false;
        }

        System.out.println("Test finito");

        Assert.assertEquals(result, expectedResult);

    }
}
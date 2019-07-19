package utilities;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.MySqlPlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import common.GlobalConstants;
import dao.DaoFactory;
import dao.SportsDao;
import distributed.DistributedServices;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.test.FakeApplication;
import play.test.Helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for supporting testing with a non-H2 database.  In our case we'd be using MySQL.
 */
public class BaseTest {

    public static FakeApplication app;

    public boolean hazelcastStarted = false;

    public ApplicationContext context = new FileSystemXmlApplicationContext("test/spring-test.xml");

    /**
     * Start a fake application instance with the provided JDBC settings.
     *
     * @throws IOException
     */
    @BeforeClass
    public static void startApp() throws IOException {

        Map<String, String> additionalConfiguration = new HashMap<>();
        additionalConfiguration.put("db.default.driver", "com.mysql.jdbc.Driver");
//        additionalConfiguration.put("db.default.url", "jdbc:mysql://localhost:3306/ruckustest");
//        additionalConfiguration.put("db.default.user", "ruckus");
//        additionalConfiguration.put("db.default.password", "ruckus");
        additionalConfiguration.put("db.default.url", String.format("jdbc:mysql://%s:3306/%s",
                System.getProperty("test_db_host"),
                System.getProperty("test_db_database")));
        additionalConfiguration.put("db.default.user", System.getProperty("test_db_user"));
        additionalConfiguration.put("db.default.password", System.getProperty("test_db_pass"));

        app = Helpers.fakeApplication(additionalConfiguration);
        Helpers.start(app);
    }

    /**
     * Stops the fake application after all tests have been run.
     */
    @AfterClass
    public static void stopApp() {
        Helpers.stop(app);
    }

    /**
     * Drops and recreates the database schema before each test.
     *
     * @throws IOException
     */
    @Before
    public void dropCreateDb() throws IOException {

        String serverName = "default";

        EbeanServer server = Ebean.getServer(serverName);

        ServerConfig config = new ServerConfig();

        DdlGenerator ddl = new DdlGenerator();
        ddl.setup((SpiEbeanServer) server, new MySqlPlatform(), config);

        // Drop
        ddl.runScript(false, ddl.generateDropDdl());

        // Create
        ddl.runScript(false, ddl.generateCreateDdl());

        DistributedServices.setContext(context);

        new DaoFactory(context);

        new SportsDao().init();
    }

    public void startHazelcast() {
        // Set cluster port to localhost if it's not passed in
        String host_ip = System.getProperty(GlobalConstants.CONFIG_PUBLIC_HOST_IP);
        if (host_ip == null) {
            host_ip = System.getenv(GlobalConstants.ENV_HOST_IP) == null ? "127.0.0.1" : System.getenv(GlobalConstants.ENV_HOST_IP);
            System.setProperty(GlobalConstants.CONFIG_PUBLIC_HOST_IP, host_ip);
        }

        DistributedServices.get();

        hazelcastStarted = true;
    }
}

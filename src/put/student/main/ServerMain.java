package put.student.main;

import put.student.utils.PropertiesFactory;
import put.student.utils.RegistryFactory;

import java.rmi.registry.Registry;
import java.util.Properties;

/**
 * Created by tkuczma on 13.08.15.
 */
public class ServerMain {
    public static void main(String[] args) {

        try {
            Properties prop = new PropertiesFactory().getServerProperties();
            final int port = Integer.parseInt(prop.getProperty("port"));

            Registry registry = new RegistryFactory().createBindedRegistry(port);

            System.err.println("Server ready. Port: " + port);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

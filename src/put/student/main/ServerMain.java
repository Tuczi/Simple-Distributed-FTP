package put.student.main;

import put.student.utils.PropertiesFactory;
import put.student.utils.RegistryFactory;

import java.rmi.registry.Registry;
import java.util.Properties;

/**
 * Created by tkuczma on 13.08.15.
 *
 * Server main class.
 * Just call RegistryFactory.createBindedRegistry
 */
public class ServerMain {
    public static void main(String[] args) {
        try {
            PropertiesFactory prop = PropertiesFactory.getServerProperties();
            final int port = prop.getPort();

            Registry registry = new RegistryFactory().createBindedRegistry(port);

            System.err.println("Server ready. Port: " + port);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

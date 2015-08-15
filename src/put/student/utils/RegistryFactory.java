package put.student.utils;

import put.student.rmi.interfaces.ClientServerRMIInterface;
import put.student.rmi.interfaces.ServerServerRMIInterface;
import put.student.rmi.server.ClientServerRMI;
import put.student.rmi.server.ServerServerRMI;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by tkuczma on 13.08.15.
 *
 * Keeps RMI registry names as constatns.
 * Creates and binds registry.
 */
public class RegistryFactory {
    public static final String CLIENT_SERVER_RMI_NAME = "ClientServerRMIInterface";
    public static final String SERVER_SERVER_RMI_NAME = "ServerServerRMIInterface";

    public Registry createBindedRegistry(final int port) throws IOException, AlreadyBoundException, URISyntaxException {
        Registry registry = LocateRegistry.createRegistry(port);

        ClientServerRMI clientServerRMI = new ClientServerRMI();
        ClientServerRMIInterface clientServerRMIstub = (ClientServerRMIInterface) UnicastRemoteObject.exportObject(clientServerRMI, 0);
        registry.bind(CLIENT_SERVER_RMI_NAME, clientServerRMIstub);

        ServerServerRMI serverServerRMI = new ServerServerRMI();
        ServerServerRMIInterface serverServerRMIstub = (ServerServerRMIInterface) UnicastRemoteObject.exportObject(serverServerRMI, 0);
        registry.bind(SERVER_SERVER_RMI_NAME, serverServerRMIstub);

        return registry;
    }
}

package put.student.utils;

import put.student.rmi.interfaces.ClientServerRMIInterface;
import put.student.rmi.interfaces.ServerServerRMIInterface;
import put.student.rmi.server.ClientServerRMI;
import put.student.rmi.server.ServerServerRMI;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by tkuczma on 13.08.15.
 * <p>
 * Keeps RMI registry names as constatns.
 * Creates and binds registry.
 */
public class RegistryFactory {
    private static final String CLIENT_SERVER_RMI_NAME = "ClientServerRMIInterface";
    private static final String SERVER_SERVER_RMI_NAME = "ServerServerRMIInterface";

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

    public ServerServerRMIInterface[] createServerServerRMIList(String[] serverList) throws URISyntaxException, RemoteException, NotBoundException {
        ServerServerRMIInterface[] serverServerRMIList = new ServerServerRMIInterface[serverList.length];
        for (int i = 0; i < serverList.length; i++) {
            URI uri = URIUtil.getURI(serverList[i]);
            Registry registry = LocateRegistry.getRegistry(uri.getHost(), uri.getPort());
            serverServerRMIList[i] = (ServerServerRMIInterface) registry.lookup(SERVER_SERVER_RMI_NAME);
        }
        return serverServerRMIList;
    }
}

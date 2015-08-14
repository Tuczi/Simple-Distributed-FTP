package put.student.rmi.interfaces;

import put.student.rmi.model.Metadata;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by tkuczma on 13.08.15.
 */
public interface ClientServerRMIInterface extends Remote {
    Metadata getMeta(String id) throws IOException;

    Metadata putMeta(String id, long length) throws IOException, NotBoundException, URISyntaxException;

    byte[] get(String id, long part) throws IOException;

    void put(String id, long part, byte[] data) throws IOException, NotBoundException, URISyntaxException;
}

package put.student.rmi.interfaces;

import put.student.rmi.model.Metadata;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by tkuczma on 14.08.15.
 */
public interface ServerServerRMIInterface extends Remote{
    void putMeta(String id, long length) throws RemoteException;
    void put(String id, long part, byte[] data) throws IOException;
}

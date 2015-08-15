package put.student.rmi.interfaces;

import java.io.IOException;
import java.rmi.Remote;

/**
 * Created by tkuczma on 14.08.15.
 */
public interface ServerServerRMIInterface extends Remote {
    void putMeta(String id, long length) throws IOException;

    void put(String id, long part, byte[] data) throws IOException;
}

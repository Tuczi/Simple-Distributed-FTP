package put.student.rmi.server;

import put.student.rmi.interfaces.ServerServerRMIInterface;
import put.student.utils.PropertiesFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by tkuczma on 14.08.15.
 * <p>
 * RMI class used in server - server communication
 * (generally distributing file to replicas).
 */
public class ServerServerRMI implements ServerServerRMIInterface {
    private final File ROOT;
    private final long BLOCK_SIZE;

    public ServerServerRMI() throws IOException {
        PropertiesFactory prop = PropertiesFactory.getServerProperties();
        ROOT = prop.getROOT();
        BLOCK_SIZE = prop.getBLOCK_SIZE();
    }

    @Override
    public void putMeta(String id, long length) throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File(ROOT, id), "rw");
        file.setLength(length);
    }

    @Override
    public void put(String id, long part, byte[] data) throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File(ROOT, id), "rw");
        file.seek(BLOCK_SIZE * part);
        file.write(data);
    }

}

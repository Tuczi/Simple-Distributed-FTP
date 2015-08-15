package put.student.rmi.server;

import put.student.rmi.interfaces.ServerServerRMIInterface;
import put.student.utils.PropertiesFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.util.Properties;

/**
 * Created by tkuczma on 14.08.15.
 */
public class ServerServerRMI implements ServerServerRMIInterface {
    private final File ROOT;
    private final long BLOCK_SIZE;

    public ServerServerRMI() throws IOException {
        Properties prop = new PropertiesFactory().getServerProperties();
        ROOT = getROOTPath(prop);
        BLOCK_SIZE = Long.parseLong(prop.getProperty("blocksize"));
    }

    @Override
    public void putMeta(String id, long length) throws RemoteException {
        //TODO set file length
    }

    @Override
    public void put(String id, long part, byte[] data) throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File(ROOT, id), "rw");
        file.seek(BLOCK_SIZE * part);
        file.write(data);
    }

    // TODO refctor this (repeated code)
    private File getROOTPath(Properties prop) throws UnsupportedEncodingException {
        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
        return new File(new File(URLDecoder.decode(url.getFile(), "UTF-8")).getParent(), prop.getProperty("rootpath"));
    }
}

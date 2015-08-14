package put.student.rmi.server;

import com.sun.jndi.toolkit.url.Uri;
import put.student.rmi.interfaces.ClientServerRMIInterface;
import put.student.rmi.interfaces.ServerServerRMIInterface;
import put.student.rmi.model.Metadata;
import put.student.utils.PropertiesFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

/**
 * Created by tkuczma on 13.08.15.
 */
public class ClientServerRMI implements ClientServerRMIInterface {
    private final File ROOT;
    private final long BLOCK_SIZE;
    private final String[] serverList;

    private ServerServerRMIInterface[] serverServerRMIList = null;

    public ClientServerRMI() throws IOException {
        Properties prop = new PropertiesFactory().getServerProperties();
        ROOT = new File(prop.getProperty("rootpath"));
        BLOCK_SIZE = Long.parseLong(prop.getProperty("blocksize"));
        serverList = prop.getProperty("servers").split(" ");
    }

    @Override
    public Metadata getMeta(String id) throws IOException {
        Path file = Paths.get(new File(ROOT, id).getPath());
        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);

        return new Metadata(id, BLOCK_SIZE, attr.size(), serverList);
    }

    @Override
    public Metadata putMeta(String id, long length) throws IOException, NotBoundException {
        initServerServerRMIList();
        for(int i=0; i <serverServerRMIList.length;i++)
            serverServerRMIList[i].putMeta(id, length);

        return new Metadata(id, BLOCK_SIZE, length, serverList);
    }

    @Override
    public byte[] get(String id, long part) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(new File(ROOT, id)));

        byte[] buf = new byte[(int) BLOCK_SIZE];

        is.skip(BLOCK_SIZE * part);
        int bytesRead = is.read(buf);

        return buf;
    }

    @Override
    public void put(String id, long part, byte[] data) throws IOException, NotBoundException {
        RandomAccessFile file = new RandomAccessFile(new File(ROOT, id), "rw");

        file.seek(BLOCK_SIZE * part);
        file.write(data);

        initServerServerRMIList();
        for(int i=0; i <serverServerRMIList.length;i++)
            serverServerRMIList[i].put(id, part, data);
    }

    private void initServerServerRMIList() throws IOException, NotBoundException {
        if(serverServerRMIList != null)
            return;

        serverServerRMIList = new ServerServerRMIInterface[serverList.length];
        for (int i = 0; i < serverList.length; i++) {
            Uri uri = new Uri(serverList[i]);
            Registry registry = LocateRegistry.getRegistry(uri.getHost(), uri.getPort());
            serverServerRMIList[i] = (ServerServerRMIInterface) registry.lookup("ServerServerRMIInterface");
        }
    }
}

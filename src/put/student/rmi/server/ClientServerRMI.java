package put.student.rmi.server;

import put.student.rmi.interfaces.ClientServerRMIInterface;
import put.student.rmi.interfaces.ServerServerRMIInterface;
import put.student.rmi.model.Metadata;
import put.student.utils.PropertiesFactory;
import put.student.utils.URIUtil;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by tkuczma on 13.08.15.
 * <p>
 * RMI class used in client - server communication.
 * It contains method to exchange file metadata and fragments between client and servers.
 */
public class ClientServerRMI implements ClientServerRMIInterface {
    private final File ROOT;
    private final long BLOCK_SIZE;
    private final String[] serverList;

    private ServerServerRMIInterface[] serverServerRMIList = null;

    public ClientServerRMI() throws IOException {
        PropertiesFactory prop = PropertiesFactory.getServerProperties();
        ROOT = prop.getROOT();
        BLOCK_SIZE = prop.getBLOCK_SIZE();
        serverList = prop.getServerList();
    }

    @Override
    public Metadata getMeta(String id) throws IOException {
        Path file = Paths.get(new File(ROOT, id).getPath());
        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);

        return new Metadata(id, BLOCK_SIZE, attr.size(), serverList);
    }

    @Override
    public Metadata putMeta(String id, long length) throws IOException, NotBoundException, URISyntaxException {
        initServerServerRMIList();
        for (int i = 0; i < serverServerRMIList.length; i++)
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
    public void put(String id, long part, byte[] data) throws IOException, NotBoundException, URISyntaxException {
        RandomAccessFile file = new RandomAccessFile(new File(ROOT, id), "rw");

        file.seek(BLOCK_SIZE * part);//TODO check if it is redundant
        file.write(data);

        initServerServerRMIList();
        for (int i = 0; i < serverServerRMIList.length; i++)
            serverServerRMIList[i].put(id, part, data);
    }

    private void initServerServerRMIList() throws IOException, NotBoundException, URISyntaxException {
        if (serverServerRMIList != null)
            return;

        serverServerRMIList = new ServerServerRMIInterface[serverList.length];
        for (int i = 0; i < serverList.length; i++) {
            URI uri = URIUtil.getURI(serverList[i]);
            Registry registry = LocateRegistry.getRegistry(uri.getHost(), uri.getPort());
            serverServerRMIList[i] = (ServerServerRMIInterface) registry.lookup("ServerServerRMIInterface");
        }
    }
}

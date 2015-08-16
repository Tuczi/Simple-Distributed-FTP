package put.student.rmi.server;

import put.student.jobs.UploadJob;
import put.student.rmi.interfaces.ClientServerRMIInterface;
import put.student.rmi.interfaces.ServerServerRMIInterface;
import put.student.rmi.model.Metadata;
import put.student.utils.PropertiesFactory;
import put.student.utils.RegistryFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private final int threadsCount;
    private ServerServerRMIInterface[] serverServerRMIList = null;

    public ClientServerRMI() throws IOException {
        PropertiesFactory prop = PropertiesFactory.getServerProperties();
        ROOT = prop.getROOT();
        BLOCK_SIZE = prop.getBLOCK_SIZE();
        threadsCount = prop.getThreadsCunt();
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
    public void put(String id, long part, byte[] data) throws NotBoundException, URISyntaxException, RemoteException {
        initServerServerRMIList();
        ExecutorService exec = Executors.newFixedThreadPool(threadsCount);
        try {
            for (int i = 0; i < serverServerRMIList.length; i++)
                exec.submit(new UploadJob(serverServerRMIList[i], id, part, data));
        } finally {
            exec.shutdown();
        }
    }

    private void initServerServerRMIList() throws RemoteException, NotBoundException, URISyntaxException {
        if (serverServerRMIList != null)
            return;

        serverServerRMIList = new RegistryFactory().createServerServerRMIList(serverList);
    }
}

package put.student.rmi.proxy;

import put.student.jobs.DownloadJob;
import put.student.rmi.interfaces.ClientServerRMIInterface;
import put.student.rmi.model.Metadata;
import put.student.utils.PropertiesFactory;
import put.student.utils.RegistryFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tkuczma on 14.08.15.
 * <p>
 * Proxy class for ClientServerRMI.
 * Its implements logic for downloading and uploading files.
 */
public class ClientServerRMIProxy {
    private final String mainHost;
    private final int mainPort;
    private final int threadsCount;

    public ClientServerRMIProxy() throws IOException {
        PropertiesFactory prop = PropertiesFactory.getClientProperties();
        mainHost = prop.getMainHost();
        mainPort = prop.getMainPort();
        threadsCount = prop.getThreadsCunt();
    }

    public void getFile(final String from, final String to) throws IOException, NotBoundException, URISyntaxException {
        RegistryFactory registryFactory = new RegistryFactory();
        ClientServerRMIInterface mainClientServerRMI = registryFactory.getMainClientServerRMIInterface(mainHost, mainPort);
        Metadata meta = mainClientServerRMI.getMeta(from);
        ClientServerRMIInterface[] fileOwnerClientServerRMI = registryFactory.getFileOwnerClientServerRMI(meta);

        ExecutorService exec = Executors.newFixedThreadPool(threadsCount);
        RandomAccessFile file = new RandomAccessFile(new File(to), "rw");
        final int max = (int) Math.ceil((float) meta.getFileSize() / meta.getBlockSize());
        try {
            for (int part = 0; part < max; part++)
                exec.submit(new DownloadJob(fileOwnerClientServerRMI, meta, file, part));

        } finally {
            exec.shutdown();
        }
    }

    public void putFile(final String from, final String to) throws IOException, NotBoundException, URISyntaxException {
        RandomAccessFile file = new RandomAccessFile(new File(from), "r");
        RegistryFactory registryFactory = new RegistryFactory();
        ClientServerRMIInterface mainClientServerRMI = registryFactory.getMainClientServerRMIInterface(mainHost, mainPort);
        Metadata meta = mainClientServerRMI.putMeta(to, file.length());
        ClientServerRMIInterface fileOwnerClientServerRMI = registryFactory.getFileOwnerClientServerRMI(meta)[0];

        final int max = (int) (meta.getFileSize() / meta.getBlockSize());
        byte[] data = new byte[(int) meta.getBlockSize()];
        for (long part = 0; part < max; part++) {
            file.read(data);
            fileOwnerClientServerRMI.put(to, part, data);
        }

        //send last part
        final int lastPartSize = (int) (meta.getFileSize() % meta.getBlockSize());
        if (lastPartSize > 0) {
            data = new byte[lastPartSize];
            file.read(data);
            fileOwnerClientServerRMI.put(to, max, data);
        }
    }
}

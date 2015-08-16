package put.student.rmi.proxy;

import put.student.jobs.DownloadJob;
import put.student.rmi.interfaces.ClientServerRMIInterface;
import put.student.rmi.model.Metadata;
import put.student.utils.PropertiesFactory;
import put.student.utils.URIUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tkuczma on 14.08.15.
 * <p>
 * Proxy class for ClientServerRMI.
 * Its implements logic for downloading and uploading files.
 */
public class ClientServerRMIProxy {
    private String mainHost;
    private int mainPort;
    private int threadsCount;

    public ClientServerRMIProxy() throws IOException {
        PropertiesFactory prop = PropertiesFactory.getClientProperties();
        mainHost = prop.getMainHost();
        mainPort = prop.getMainPort();
        threadsCount = prop.getThreadsCunt();
    }

    public void getFile(final String from, final String to) throws IOException, NotBoundException, URISyntaxException {
        ClientServerRMIInterface mainClientServerRMI = getMainClientServerRMIInterface();
        Metadata meta = mainClientServerRMI.getMeta(from);
        ClientServerRMIInterface[] fileOwnerClientServerRMI = getFileOwnerClientServerRMI(meta);

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
        ClientServerRMIInterface mainClientServerRMI = getMainClientServerRMIInterface();
        Metadata meta = mainClientServerRMI.putMeta(to, file.length());
        ClientServerRMIInterface fileOwnerClientServerRMI = getFileOwnerClientServerRMI(meta)[0];

        final int max = (int) (meta.getFileSize() / meta.getBlockSize());
        byte[] data = new byte[(int) meta.getBlockSize()];
        for (long part = 0; part < max; part++) {
            //file.seek(meta.getBlockSize() * part);
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

    private ClientServerRMIInterface getMainClientServerRMIInterface() throws RemoteException, NotBoundException {
        Registry mainRegistry = LocateRegistry.getRegistry(mainHost, mainPort);
        return (ClientServerRMIInterface) mainRegistry.lookup("ClientServerRMIInterface");
    }

    private ClientServerRMIInterface[] getFileOwnerClientServerRMI(Metadata meta) throws RemoteException, NotBoundException, URISyntaxException {
        final int maxSize = (int) Math.ceil((float) meta.getFileSize() / meta.getBlockSize());
        ClientServerRMIInterface[] fileOwnerClientServerRMI = new ClientServerRMIInterface[Math.min(maxSize, meta.getOwnerList().length)];

        for (int i = 0; i < fileOwnerClientServerRMI.length; i++) {
            URI uri = URIUtil.getURI(meta.getOwnerList()[i]);
            Registry fileOwnerRegistry = LocateRegistry.getRegistry(uri.getHost(), uri.getPort());
            fileOwnerClientServerRMI[i] = (ClientServerRMIInterface) fileOwnerRegistry.lookup("ClientServerRMIInterface");
        }

        return fileOwnerClientServerRMI;
    }
}

package put.student.rmi.proxy;

import com.sun.jndi.toolkit.url.Uri;
import put.student.rmi.interfaces.ClientServerRMIInterface;
import put.student.rmi.model.Metadata;
import put.student.utils.PropertiesFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

/**
 * Created by tkuczma on 14.08.15.
 */
public class ClientServerRMIProxy {
    private String mainHost;
    private int mainPort;

    public ClientServerRMIProxy() throws IOException {
        Properties prop = new PropertiesFactory().getClientProperties();
        mainHost = prop.getProperty("mainhost");//TODO const
        mainPort = Integer.parseInt(prop.getProperty("mainport"));//TODO const
    }

    public void getFile(final String from, final String to) throws IOException, NotBoundException {
        ClientServerRMIInterface mainClientServerRMI = getMainClientServerRMIInterface();
        Metadata meta = mainClientServerRMI.getMeta(from);
        ClientServerRMIInterface[] fileOwnerClientServerRMI = getFileOwnerClientServerRMI(meta);

        RandomAccessFile file = new RandomAccessFile(new File(to), "rw");
        final int max = (int) Math.ceil((float)meta.getFileSize()/meta.getBlockSize());
        for (int part = 0; part < max; part++) {
            byte[] response = fileOwnerClientServerRMI[ part % fileOwnerClientServerRMI.length ].get(from, part);

            //file.seek(meta.getBlockSize() * part);
            file.write(response);
        }
    }

    public void putFile(final String from, final String to) throws IOException, NotBoundException, URISyntaxException {
        RandomAccessFile file = new RandomAccessFile(new File(from), "r");
        ClientServerRMIInterface mainClientServerRMI = getMainClientServerRMIInterface();
        Metadata meta = mainClientServerRMI.putMeta(to, file.length());
        ClientServerRMIInterface fileOwnerClientServerRMI = getFileOwnerClientServerRMI(meta)[0];

        final int max = (int) Math.ceil((float)meta.getFileSize()/meta.getBlockSize());
        byte[] data = new byte[(int) meta.getBlockSize()];
        for (long part = 0; part < max; part++) {
            //file.seek(meta.getBlockSize() * part);
            file.read(data);
            fileOwnerClientServerRMI.put(to, part, data);
        }
    }

    private ClientServerRMIInterface getMainClientServerRMIInterface() throws RemoteException, NotBoundException {
        Registry mainRegistry = LocateRegistry.getRegistry(mainHost, mainPort);
        return (ClientServerRMIInterface) mainRegistry.lookup("ClientServerRMIInterface");
    }

    private ClientServerRMIInterface[] getFileOwnerClientServerRMI(Metadata meta) throws RemoteException, NotBoundException, MalformedURLException {
        final int maxSize = (int) Math.ceil((float)meta.getFileSize()/meta.getBlockSize());
        ClientServerRMIInterface[] fileOwnerClientServerRMI = new ClientServerRMIInterface[Math.min(maxSize, meta.getOwnerList().length)];

        for (int i = 0; i < fileOwnerClientServerRMI.length; i++) {
            Uri uri = new Uri(meta.getOwnerList()[i]);
            Registry fileOwnerRegistry = LocateRegistry.getRegistry(uri.getHost(), uri.getPort());
            fileOwnerClientServerRMI[i] = (ClientServerRMIInterface) fileOwnerRegistry.lookup("ClientServerRMIInterface");
        }

        return fileOwnerClientServerRMI;
    }
}

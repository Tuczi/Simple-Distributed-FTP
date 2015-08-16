package put.student.jobs;

import put.student.rmi.interfaces.ClientServerRMIInterface;
import put.student.rmi.model.Metadata;

import java.io.RandomAccessFile;

/**
 * Created by tkuczma on 16.08.15.
 * <p>
 * Job used to download part of file using ThreadPoll in ClientServerRMIProxy
 */
public class DownloadJob implements java.util.concurrent.Callable<Object> {

    private final ClientServerRMIInterface[] fileOwnerClientServerRMI;
    private final RandomAccessFile file;
    private final int part;
    private Metadata meta;

    public DownloadJob(ClientServerRMIInterface[] fileOwnerClientServerRMI, Metadata meta, RandomAccessFile file, int part) {
        this.fileOwnerClientServerRMI = fileOwnerClientServerRMI;
        this.meta = meta;
        this.file = file;
        this.part = part;
    }

    @Override
    public Object call() throws Exception {
        byte[] response = fileOwnerClientServerRMI[part % fileOwnerClientServerRMI.length].get(meta.getId(), part);

        synchronized (file) {
            file.seek(meta.getBlockSize() * part);
            file.write(response);
        }

        return new Object();
    }

}

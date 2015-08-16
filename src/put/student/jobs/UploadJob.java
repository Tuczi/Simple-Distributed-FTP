package put.student.jobs;

import put.student.rmi.interfaces.ServerServerRMIInterface;

import java.util.concurrent.Callable;

/**
 * Created by tkuczma on 16.08.15.
 * <p>
 * Job used to upload part of file using ThreadPoll in ClientServerRMI
 */
public class UploadJob implements Callable<Object> {
    private final ServerServerRMIInterface serverServerRMI;
    private final String id;
    private final long part;
    private final byte[] data;

    public UploadJob(ServerServerRMIInterface serverServerRMI, String id, long part, byte[] data) {
        this.serverServerRMI = serverServerRMI;
        this.id = id;
        this.part = part;
        this.data = data;
    }

    @Override
    public Object call() throws Exception {
        serverServerRMI.put(id, part, data);
        return new Object();
    }

}

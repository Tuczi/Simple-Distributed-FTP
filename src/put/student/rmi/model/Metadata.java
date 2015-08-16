package put.student.rmi.model;

import java.io.Serializable;

/**
 * Created by tkuczma on 14.08.15.
 * <p>
 * File metadata model used in RMI communication.
 */
public class Metadata implements Serializable {
    private final String id;
    private final long blockSize;
    private final long fileSize;
    private final String[] ownerList;

    public Metadata(String id, long blockSize, long fileSize, String[] ownerList) {
        this.id = id;
        this.blockSize = blockSize;
        this.fileSize = fileSize;
        this.ownerList = ownerList;
    }

    public String getId() {
        return id;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String[] getOwnerList() {
        return ownerList;
    }

}

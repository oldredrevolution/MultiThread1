package com.shelly;

import com.shelly.AbstractMessage;

import java.io.File;
import java.sql.Timestamp;

/**
 * TODO 传递文件具体数据以及相关信息
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly
 * @Author: shelly
 * @CreateTime: 2023/11/26  19:18
 * @Description: FileMessage实现数据传输对象化，可以将文件信息和内容以对象的形式一起传输
 */
public class FileMessage extends AbstractMessage {
    private String description;
    private byte[] data;
    private boolean flag;

    public String getDescription() {
        return description;
    }

    public byte[] getData() {
        return data;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getFileName() {
        return fileName;
    }

    private Timestamp timestamp;
    private String fileName;
    private String fileId = null;

    public FileMessage(String name, String description, byte[] data, String fileName, Timestamp timestamp) {
        super(name);
        this.data = data;
        this.description = description;
        this.timestamp = timestamp;
        this.fileName = fileName;
        this.flag = false;
    }

    public FileMessage(String fileId, String fileName) {
        super(null);
        this.data = null;
        this.description = null;
        this.timestamp = null;
        this.fileName = fileName;
        this.flag = false;
        this.fileId = fileId;
    }

    public FileMessage(String fileId, byte[] data) {
        super(null);
        this.data = data;
        this.description = null;
        this.timestamp = null;
        this.fileName = null;
        this.flag = false;
        this.fileId = fileId;
    }

    public FileMessage(String fileId, String fileName, byte[] data) {
        super(null);
        this.data = data;
        this.description = null;
        this.timestamp = null;
        this.fileName = fileName;
        this.flag = false;
        this.fileId = fileId;
    }


    public void setFlag() {
        this.flag = true;
    }

    public boolean getFlag() {
        return flag;
    }

    public String getFileId() {
        return fileId;
    }
}

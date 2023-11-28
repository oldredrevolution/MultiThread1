package com.shelly;

/**
 * TODO
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly
 * @Author: shelly
 * @CreateTime: 2023/11/24  16:58
 * @Description: consist of this project
 */
public class OperationMessage extends AbstractMessage {
    private String operation;
    private boolean INSERT_USER = false;
    private boolean DELETE_USER = false;

    private boolean MODIFY_USER = false;
    private boolean SHOW_USER = false;
    private boolean CHANGE_PASSWORD = false;
    private boolean UPLOAD_FILE = false;
    private boolean SHOW_DOC = false;
    private boolean DOWNLOAD_FILE = false;

    public OperationMessage(String name, String passWord, String operation) {
        super(name, passWord);
        this.operation = operation;
    }

    public OperationMessage(String operation) {
        super(" ", " ");
        this.operation = operation;
    }

    public void setINSERT_USER() {
        this.INSERT_USER = true;
    }

    public boolean getINSERT_USER() {
        return INSERT_USER;
    }

    public String getOperation() {
        return operation;
    }

    public boolean getDELETE_USER() {
        return DELETE_USER;
    }

    public void setDELETE_USER() {
        this.DELETE_USER = true;
    }

    public void setMODIFY_USER() {
        this.MODIFY_USER = true;
    }

    public boolean getMODIFY_USER() {
        return MODIFY_USER;
    }

    public void setSHOW_USER() {
        this.SHOW_USER = true;
    }

    public boolean getSHOW_USER() {
        return SHOW_USER;
    }

    public void setCHANGE_PASSWORD() {
        this.CHANGE_PASSWORD = true;
    }

    public boolean getCHANGE_PASSWORD() {
        return CHANGE_PASSWORD;
    }
    public void setUPLOAD_FILE(){
        this.UPLOAD_FILE = true;
    }
    public  boolean getUPLOAD_FILE(){
        return UPLOAD_FILE;
    }
    public void setSHOW_DOC(){
        this.SHOW_DOC = true;
    }
    public boolean getSHOW_DOC(){
        return SHOW_DOC;
    }
    public void setDOWNLOAD_FILE(){
        DOWNLOAD_FILE = true;
    }
    public boolean getDOWNLOAD_FILE(){
        return DOWNLOAD_FILE;
    }
}

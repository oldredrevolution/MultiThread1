package com.shelly;

import java.io.Serializable;

/**
 * TODO
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly
 * @Author: shelly
 * @CreateTime: 2023/11/23  15:17
 * @Description: consist of this project
 */
public abstract class AbstractMessage implements Serializable {


    private String name;
    private String passWord;
    private String role;
    private static final long serialVersionUID = 1L;

    public AbstractMessage(String name, String passWord) {
        this.name = name;
        this.passWord = passWord;
        this.role = null;
    }
    public AbstractMessage(String name){
        this.name = name;
        this.passWord = null;
        this.role = null;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}

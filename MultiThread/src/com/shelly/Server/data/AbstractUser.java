package com.shelly.Server.data;


import java.io.Serializable;


/**
 * TODO 抽象用户类，为各用户子类提供模板
 *
 * @author gongjing
 * @date 2016/10/13
 */
public abstract class AbstractUser implements Serializable{
    private String name;
    private String passWord;
    private String role;

    public AbstractUser(String name, String password, String role) {
        this.name = name;
        this.passWord = password;
        this.role = role;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return passWord;
    }

    public void setPassword(String password) {
        this.passWord = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}

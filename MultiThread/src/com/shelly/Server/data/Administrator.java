package com.shelly.Server.data;

import java.sql.SQLException;
import java.util.Enumeration;

public class Administrator extends AbstractUser {
    Administrator(String name, String passWord, String role) {
        super(name, passWord, role);
    }
}



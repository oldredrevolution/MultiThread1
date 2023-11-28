package com.shelly.Server.utils;

import com.shelly.LoginMessage;
import com.shelly.Server.data.DataProcessing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TODO 实现客户端与服务端的交流（由服务端发送信息）
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly.Server.utils
 * @Author: shelly
 * @CreateTime: 2023/11/23  22:34
 * @Description: 包括但不限于确定客户端能否登录，向客户端传输文件
 */
public class ServerToClient {
    /**
     * TODO 判断是否能够登录
     *
     * @param ois 对象输出流
     * @param oos 对象输入流
     * @throws IOException            抛出一个IOException
     * @throws SQLException           抛出一个SQLException
     * @throws ClassNotFoundException 抛出一个ClassNotFoundException
     */
    public static boolean checkUser(ObjectInputStream ois,ObjectOutputStream oos) throws IOException, SQLException, ClassNotFoundException {
        LoginMessage loginRequest = (LoginMessage) ois.readObject();
        String sql = "SELECT * FROM user_info WHERE name = ? AND password = ?";
        PreparedStatement stat = DataProcessing.connection.prepareStatement(sql);
        stat.setString(1, loginRequest.getName());
        stat.setString(2, loginRequest.getPassWord());
        ResultSet res = stat.executeQuery();
        if (res.next()) {
            loginRequest.setRole(res.getString("role"));
            loginRequest.setLoginRequest();
            oos.writeObject(loginRequest);
            return true;
        } else {
            oos.writeObject(loginRequest);
            return false;
        }
    }
}

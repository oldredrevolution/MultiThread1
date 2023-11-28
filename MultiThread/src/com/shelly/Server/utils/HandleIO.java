package com.shelly.Server.utils;

import com.shelly.FileMessage;
import com.shelly.Server.data.DataProcessing;

import java.io.*;
import java.sql.*;

/**
 * TODO 处理文件相关操作
 *
 * @author shelly
 * @date 2023/11/20
 */
public class HandleIO {
    /**
     * TODO 将文件上传到数据库
     * @param mes 此变量内部包含存储文件的字节数组，以及文件相关信息
     * @return 上传成功返回true
     */

    public static boolean upLoadFileToMySQL(FileMessage mes) {
        try {
            // 获得文件相关信息
            String creator = mes.getName();
            Timestamp time = mes.getTimestamp();
            String description = mes.getDescription();
            String fileName = mes.getFileName();
            byte[] data = mes.getData();

            // 创建预编译SQL语句
            PreparedStatement statement = DataProcessing.connection.prepareStatement("INSERT INTO doc_info VALUES(?,?,?,?,?,?)");
            statement.setString(1, DataProcessing.setId());
            statement.setString(2, creator);
            statement.setTimestamp(3, time);
            statement.setString(4, description);
            statement.setString(5, fileName);
            // 将字节数组中的数据读入数据库
            statement.setBinaryStream(6, new ByteArrayInputStream(data));
            // executeUpdate方法返回受影响记录条数
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * TODO 从数据库中读取文件数据
     * @param id 档案编号
     * @return 返回存储数据的字节数组
     */
    public static byte[] readDataFromDataBase(String id){
        String sql = "SELECT * FROM doc_info WHERE id = ?";
        try {
            // 获得预编译的SQL语句
            PreparedStatement stat = DataProcessing.connection.prepareStatement(sql);
            stat.setString(1,id);

            // res存储结果集
            ResultSet res = stat.executeQuery();
            res.next();
            // 以字节数组的形式获得数据
            byte[] data = res.getBytes("file_content");
            return  data;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

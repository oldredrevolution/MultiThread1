package com.shelly.client.gui;

import com.shelly.FileMessage;
import com.shelly.OperationMessage;

import javax.swing.*;
import java.io.*;

/**
 * TODO 实现客户端与服务端的交流（由客户端发送信息）
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly.client.gui
 * @Author: shelly
 * @CreateTime: 2023/11/24  16:09
 * @Description: consist of this project
 */
public class utils {
    public static byte[] fileToByteArray(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            return data;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void downLoadFile(ObjectOutputStream oos, ObjectInputStream ois) throws IOException, ClassNotFoundException {
        oos.writeObject(new OperationMessage("DOWNLOAD_FILE"));
        OperationMessage o = (OperationMessage) ois.readObject();
        if (o.getDOWNLOAD_FILE()) {
            JTextField fileIdFiled = new JTextField();
            String fileID = "档案编号";
            JTextField fileNameFiled = new JTextField();
            String fileName = "档案名";
            Object[] message = {
                    fileID, fileIdFiled,
                    fileName, fileNameFiled
            };
            int option = JOptionPane.showConfirmDialog(null, message, "修改密码", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String id = fileIdFiled.getText();
                // 发送想要下载的档案编号为id，文件名为name的请求的请求
                oos.writeObject(new FileMessage(id, fileNameFiled.getText()));

                //检查目标文件夹是否存在
                File defaultDownLoaddir = new File("D:\\JavaWeek3path");
                if (!defaultDownLoaddir.exists()) {
                    defaultDownLoaddir.mkdir();
                }
                // 读取数据到相应的位置
                FileMessage o1 = (FileMessage) ois.readObject();
                File file = new File("D:\\JavaWeek3path\\" + o1.getFileName());

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                int size = 0;
                if (o1.getData().length > 0) {
                    fileOutputStream.write(o1.getData(), 0, o1.getData().length);
                    JOptionPane.showMessageDialog(null, "下载成功", "操作", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "该文件无内容", "操作", JOptionPane.INFORMATION_MESSAGE);
                }
                fileOutputStream.close();
            }

        }
    }
    public static void changePassWord(ObjectInputStream ois,ObjectOutputStream oos){
        JTextField newPassWordFiled = new JTextField();
        JPasswordField repeatPassWordFiled = new JPasswordField();
        JTextField userNameFiled = new JTextField();
        Object[] message = {
                "用户名：", userNameFiled,
                "新密码：", newPassWordFiled,
                "请再次输出：", repeatPassWordFiled
        };
        int option = JOptionPane.showConfirmDialog(null, message, "修改密码", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (newPassWordFiled.getText().equals(new String(repeatPassWordFiled.getPassword()))) {
                try {
                    oos.writeObject(new OperationMessage(userNameFiled.getText(), newPassWordFiled.getText(), "CHANGE_PASSWORD"));
                    OperationMessage o = (OperationMessage) ois.readObject();
                    if (o.getCHANGE_PASSWORD()) {
                        JOptionPane.showMessageDialog(null, "修改密码成功！", "操作：", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "修改密码失败！", "操作：", JOptionPane.INFORMATION_MESSAGE);

                    }
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}

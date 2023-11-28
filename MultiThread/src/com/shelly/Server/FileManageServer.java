package com.shelly.Server;

import com.shelly.FileMessage;
import com.shelly.OperationMessage;
import com.shelly.Server.data.AbstractUser;
import com.shelly.Server.data.DataProcessing;
import com.shelly.Doc;
import com.shelly.Server.utils.HandleIO;
import com.shelly.Server.utils.ServerToClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * TODO 启动服务端
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly.Server
 * @Author: shelly
 * @CreateTime: 2023/11/23  14:55
 * @Description: 启动服务端，无GUI
 */
public class FileManageServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9999);
        int flag = 0;
        DataProcessing.init();
        while (true) {
            // 获得一个与客户端交互的socket
            Socket socket = serverSocket.accept();
            // 生成一个新的线程与客户端交互
            new Thread(() -> {
                try {
                    Socket newSocket = socket;
                    // 检测是否成功登录
                    ObjectOutputStream oos = new ObjectOutputStream(newSocket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(newSocket.getInputStream());
                    while (true) {
                        if (ServerToClient.checkUser(ois, oos)) {
                            System.out.println("登录成功！");
                            break;
                        }

                    }
                    while (true) {
                        OperationMessage o = (OperationMessage) ois.readObject();
                        System.out.println(o.getOperation());

                        switch (o.getOperation()) {
                            case "INSERT_USER":
                                if (!DataProcessing.checkUserExist(o.getName())) {
                                    if (DataProcessing.insertUser(o.getName(), o.getPassWord(), o.getRole())) {
                                        o.setINSERT_USER();
                                        oos.writeObject(o);
                                        System.out.println("新增用户成功！");
                                    } else {
                                        System.out.println("新增用户失败！");
                                    }
                                } else {
                                    oos.writeObject(o);
                                    System.out.println("已存在该用户");
                                }
                                break;
                            case "DELETE_USER":
                                if (DataProcessing.checkUserExist(o.getName())) {
                                    if (DataProcessing.deleteUser(o.getName())) {
                                        o.setDELETE_USER();
                                        oos.writeObject(o);
                                        System.out.println("删除用户成功");
                                    } else {
                                        System.out.println("删除用户失败！");
                                    }
                                } else {
                                    oos.writeObject(o);
                                    System.out.println("该用户不存在！");
                                }
                                break;
                            case "MODIFY_USER":
                                if (DataProcessing.checkUserExist(o.getName())) {
                                    if (DataProcessing.updateUser(o.getName(), o.getPassWord(), o.getRole())) {
                                        o.setMODIFY_USER();
                                        oos.writeObject(o);
                                        System.out.println("修改用户信息成功");
                                    } else {
                                        System.out.println("修改用户信息失败");
                                    }
                                } else {
                                    oos.writeObject(o);
                                    System.out.println("该用户不存在！");
                                }
                                break;
                            case "SHOW_USER":
                                ArrayList<AbstractUser> userList = DataProcessing.listUser();
                                oos.writeObject(userList);
                                break;
                            case "SHOW_DOC":
                                ArrayList<Doc> docList = DataProcessing.listDoc();
                                oos.writeObject(docList);
                                break;
                            case "CHANGE_PASSWORD":
                                if (DataProcessing.checkUserExist(o.getName())) {
                                    if (DataProcessing.changePassWord(o.getName(), o.getPassWord())) {
                                        o.setCHANGE_PASSWORD();
                                        oos.writeObject(o);
                                        System.out.println("修改密码成功！");
                                    } else {
                                        System.out.println("修改密码失败！");
                                    }
                                } else {
                                    oos.writeObject(o);
                                    System.out.println("该用户不存在！");
                                }
                                break;
                            case "UPLOAD_FILE":
                                o.setUPLOAD_FILE();
                                oos.writeObject(o);
                                FileMessage fileMessage = (FileMessage) ois.readObject();
                                if (HandleIO.upLoadFileToMySQL(fileMessage)) {
                                    fileMessage.setFlag();
                                    oos.writeObject(fileMessage);
                                }
                                // 上传数据到数据库
                                break;
                            case "DOWNLOAD_FILE":
                                o.setDOWNLOAD_FILE();
                                oos.writeObject(o);
                                // 收到档案编号,档案名
                                FileMessage fileMessage1 = (FileMessage)ois.readObject();
                                // 传输数据（以字节数组的形式）
                                FileMessage fileMessage2 = new FileMessage(fileMessage1.getFileId(),
                                        fileMessage1.getFileName(),
                                        HandleIO.readDataFromDataBase(fileMessage1.getFileId()));
                                oos.writeObject(fileMessage2);
                                break;
                            default:
                                System.out.println("默认操作！");
                        }
                    }

                } catch (SocketException se) {
                    System.out.println("客户端链接被重置" + se.getMessage());
                } catch (IOException e) {
                    System.out.println("IO异常" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    System.out.println("Class Not Found!");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }).start();
            if (flag == 1) break;
        }
    }
}


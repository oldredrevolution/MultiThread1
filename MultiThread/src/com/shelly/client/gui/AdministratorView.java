package com.shelly.client.gui;

import com.shelly.FileMessage;
import com.shelly.OperationMessage;
import com.shelly.Server.data.AbstractUser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import com.shelly.Doc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


/**
 * TODO 管理员界面
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly.client.gui
 * @Author: shelly
 * @CreateTime: 2023/11/24  14:17
 * @Description: consist of this project
 */
public class AdministratorView {
    private Socket socket;

    // 窗口和面板
    private JFrame frame;
    private JPanel buttonPanel, contentPanel;

    // 按钮
    private JButton addUserButton, deleteUserButton,
            modifyUserButton, userListButton,
            downloadFileButton, fileListButton,
            updatePasswordButton, logoutButton;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    // 表格和滚动面板
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;

    public AdministratorView(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        this.socket = socket;
        this.oos = oos;
        this.ois = ois;
        // 创建窗口
        frame = new JFrame("管理员界面");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(500, 300, 800, 450);
        //frame.setSize(800, 450);

        // 创建面板
        buttonPanel = new JPanel();
        contentPanel = new JPanel(new BorderLayout());

        // 创建按钮
        addUserButton = new JButton("新增用户");
        deleteUserButton = new JButton("删除用户");
        modifyUserButton = new JButton("修改用户");
        userListButton = new JButton("用户列表");
        downloadFileButton = new JButton("下载档案");
        fileListButton = new JButton("档案列表");
        updatePasswordButton = new JButton("修改密码");
        logoutButton = new JButton("退出登录");

        // 设置按钮样式和布局
        Font font = new Font("微软雅黑", Font.PLAIN, 16);
        addButtonStyle(addUserButton, font);
        addButtonStyle(deleteUserButton, font);
        addButtonStyle(modifyUserButton, font);
        addButtonStyle(userListButton, font);
        addButtonStyle(downloadFileButton, font);
        addButtonStyle(fileListButton, font);
        addButtonStyle(updatePasswordButton, font);
        addButtonStyle(logoutButton, font);
        buttonPanel.setLayout(new GridLayout(8, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(addUserButton);
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(modifyUserButton);
        buttonPanel.add(userListButton);
        buttonPanel.add(downloadFileButton);
        buttonPanel.add(fileListButton);
        buttonPanel.add(updatePasswordButton);
        buttonPanel.add(logoutButton);

        // 添加表格和滚动面板
        model = new DefaultTableModel();
//        model.addColumn("用户名");
//        model.addColumn("密码");
//        model.addColumn("权限");
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        contentPanel.add(scrollPane, BorderLayout.WEST);

        // 将面板添加到窗口中
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(buttonPanel, BorderLayout.WEST);
        frame.getContentPane().add(contentPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        run(socket);

    }

    // 按钮样式
    private void addButtonStyle(JButton button, Font font) {
        button.setFont(font);
        button.setPreferredSize(new Dimension(200, 30));
    }


    /**
     * TODO 点击按钮之后的触发的动作
     *
     * @param socket 管理员界面的socket，实现客户端和服务器端的通信
     */
    public void run(Socket socket) {


        // 新增用户
        addUserButton.addActionListener(e -> {
            JTextField userNameFiled = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            JTextField roleFiled = new JTextField();
            Object[] message = {
                    "用户名：", userNameFiled,
                    "密码：", passwordField,
                    "权限：", roleFiled
            };
            int option = JOptionPane.showConfirmDialog(null, message, "新增用户", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String userName = userNameFiled.getText();
                String passWord = new String(passwordField.getPassword());
                String role = roleFiled.getText();
                OperationMessage insertUserMessage = new OperationMessage(userName, passWord, "INSERT_USER");
                insertUserMessage.setRole(role);
                try {
                    oos.writeObject(insertUserMessage);
                    OperationMessage o = (OperationMessage) ois.readObject();
                    if (o.getINSERT_USER()) {
                        JOptionPane.showMessageDialog(null, "新增用户成功！", "操作：", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "新增用户失败！", "操作", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // 删除用户
        deleteUserButton.addActionListener(e -> {
            // 点击删除用户按钮执行的操作
            JTextField usernameField = new JTextField();

            Object[] message = {
                    "要删除的用户名:", usernameField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "删除用户", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String userNameToDelete = usernameField.getText();
                OperationMessage deleteUserMessage = new OperationMessage(userNameToDelete, " ", "DELETE_USER");
                try {
                    oos.writeObject(deleteUserMessage);
                    OperationMessage o = (OperationMessage) ois.readObject();
                    if (o.getDELETE_USER()) {
                        JOptionPane.showMessageDialog(null, "删除用户成功！", "操作：", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "删除用户失败！", "操作：", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // 修改用户
        modifyUserButton.addActionListener(e -> {
            // 点击修改用户按钮执行的操作
            JTextField userNameFiled = new JTextField("用户名");
            JTextField userPassWordFiled = new JTextField("不需要修改密码请输入:no need");
            JTextField roleFiled = new JTextField("新权限");
            Object[] message = {
                    "用户名", userNameFiled,
                    "密 码", userPassWordFiled,
                    "新权限", roleFiled
            };
            int option = JOptionPane.showConfirmDialog(null, message, "新增用户", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String userName = userNameFiled.getText();
                String passWord = userPassWordFiled.getText();
                String role = roleFiled.getText();
                OperationMessage operationMessage = new OperationMessage(userName, passWord, "MODIFY_USER");
                operationMessage.setRole(role);
                try {
                    oos.writeObject(operationMessage);
                    OperationMessage o = (OperationMessage) ois.readObject();

                    if (o.getMODIFY_USER()) {
                        JOptionPane.showMessageDialog(null, "修改用户成功！", "操作：", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "修改用户失败！", "操作：", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //用户列表
        userListButton.addActionListener(e -> {
            // 点击用户列表按钮执行的操作
            model.setColumnCount(0);
            model.addColumn("用户名");
            model.addColumn("密码");
            model.addColumn("权限");
            try {
                // 用户列表行数
                int rows;
                // 发送请求获得全部用户数据
                oos.writeObject(new OperationMessage("SHOW_USER"));

                // 获得数据
                ArrayList<AbstractUser> userList = (ArrayList<AbstractUser>) ois.readObject();

                if (!userList.isEmpty()) {
                    // 获得全部用户的数据
                    rows = userList.size();
                    String[][] data = new String[rows][3];
                    // 遍历整个userList
                    Iterator<AbstractUser> iterator = userList.iterator();
                    int i = 0;
                    int j;
                    AbstractUser user;
                    while (iterator.hasNext()) {
                        j = 0;
                        user = iterator.next();
                        data[i][j++] = user.getName();
                        data[i][j++] = user.getPassword();
                        data[i][j] = user.getRole();
                        i++;
                    }

                    // 刷新原本数据
                    model.setRowCount(0);

                    // 添加新数据
                    for (String[] row : data) {
                        model.addRow(row);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "获取用户数据失败！", "操作：",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        // 下载文件
        downloadFileButton.addActionListener(e -> {
            // 点击下载档案按钮执行的操作
            try {
                utils.downLoadFile(oos, ois);
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        // 文件列表
        fileListButton.addActionListener(e -> {
            // 点击档案列表按钮执行的操作
            model.setColumnCount(0);
            model.addColumn("档案编号");
            model.addColumn("上传者");
            model.addColumn("上传时间");
            model.addColumn("档案描述");
            model.addColumn("档案名");
            try {
                // 档案列表行数
                int rows;
                // 发送请求获得全部档案数据
                oos.writeObject(new OperationMessage("SHOW_DOC"));

                // 获得数据
                ArrayList<Doc> docList = (ArrayList<Doc>) ois.readObject();
                if (!docList.isEmpty()) {
                    // 获得全部档案的数据
                    rows = docList.size();
                    String[][] data = new String[rows][5];
                    // 遍历整个docList
                    Iterator<Doc> iterator = docList.iterator();
                    int i = 0;
                    int j;
                    Doc doc;
                    while (iterator.hasNext()) {
                        j = 0;
                        doc = iterator.next();
                        data[i][j++] = doc.getId();
                        data[i][j++] = doc.getCreator();
                        data[i][j++] = doc.getTimestamp().toString();
                        data[i][j++] = doc.getDescription();
                        data[i][j] = doc.getFilename();
                        i++;
                    }

                    // 刷新原本数据
                    model.setRowCount(0);

                    // 添加新数据
                    for (String[] row : data) {
                        model.addRow(row);
                    }
                    JOptionPane.showMessageDialog(null, "获取用户数据成功！", "操作",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "获取用户数据失败！", "操作：",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        // 修改密码
        updatePasswordButton.addActionListener(e -> {
            // 点击修改密码按钮执行的操作
            JTextField userNameFiled = new JTextField();
            JPasswordField PassWordFiled = new JPasswordField();
            JPasswordField checkPassWordFiled = new JPasswordField();
            Object[] message = {
                    "用户名：", userNameFiled,
                    "新密码：", PassWordFiled,
                    "确认新密码：", checkPassWordFiled
            };
            int option = JOptionPane.showConfirmDialog(null, message, "修改密码", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String userName = userNameFiled.getText();
                String passWord = new String(PassWordFiled.getPassword());
                String checkPassWord = new String(checkPassWordFiled.getPassword());
                if (passWord.equals(checkPassWord)) {
                    try {
                        oos.writeObject(new OperationMessage(userName, passWord, "CHANGE_PASSWORD"));
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
        });

        // 退出登录
        logoutButton.addActionListener(e -> {
            // 点击退出登录按钮
            System.exit(0);
        });
    }
}

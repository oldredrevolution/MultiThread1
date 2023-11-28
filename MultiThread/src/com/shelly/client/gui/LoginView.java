package com.shelly.client.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.shelly.LoginMessage;

/**
 * TODO 登陆界面
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly.gui
 * @Author: shelly
 * @CreateTime: 2023/11/21  19:43
 * @Description: consist of this project
 */
public class LoginView extends JFrame {

    /**
     * TODO 界面布局
     */
    public LoginView() {
        // 主窗口设置
        setTitle("档案管理系统");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(600, 330, 565, 372);


        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        // 用户名标签
        JLabel userNameLabel = new JLabel("用户名：");
        userNameLabel.setBounds(128, 89, 80, 40);
        userNameLabel.setFont(new Font("楷体", Font.PLAIN, 20));

        // 密码标签
        JLabel passWordLabel = new JLabel("密码：");
        passWordLabel.setBounds(148, 142, 60, 37);
        passWordLabel.setFont(new Font("楷体", Font.PLAIN, 20));

        // 用户名文本域
        JTextField userNameField = new JTextField();
        userNameField.setBounds(222, 99, 176, 24);
        userNameField.setColumns(10);

        // 密码文本域
        JPasswordField passWordField = new JPasswordField();
        passWordField.setBounds(222, 150, 176, 24);

        // 确认按钮
        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e ->
                {
                    try {
                        Socket socket = new Socket(InetAddress.getLocalHost(), 9999);
                        operationLogin(userNameField.getText(), new String(passWordField.getPassword()), socket);
                    } catch (IOException | ClassNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
        );
        confirmButton.setFont(new Font("黑体", Font.PLAIN, 20));
        confirmButton.setBounds(173, 216, 85, 27);

        // 重置按钮
        JButton cancelButton = new JButton("重置");
        cancelButton.setFont(new Font("黑体", Font.PLAIN, 20));
        cancelButton.setBounds(313, 216, 85, 27);
        cancelButton.addActionListener(e -> {
            userNameField.setText("");
            passWordField.setText("");
        });
        // 整体布局
        contentPane.setLayout(null);
        contentPane.add(userNameLabel);
        contentPane.add(passWordLabel);
        contentPane.add(userNameField);
        contentPane.add(passWordField);
        contentPane.add(confirmButton);
        contentPane.add(cancelButton);
        setVisible(true);
    }

    public void operationLogin(String name, String passWord, Socket socket) throws IOException, ClassNotFoundException {

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(new LoginMessage(name, passWord));
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        LoginMessage o = (LoginMessage) ois.readObject();

        if (o.getLoginRequest()) {
            System.out.println("登陆成功！");
            this.dispose();
            switch (o.getRole()) {
                case "administrator":
                    new AdministratorView(socket, oos, ois);
                    break;
                case "browser":
                    new BrowserView(ois, oos);
                    break;
                default:
                    new OperatorView(socket, oos, ois, o.getName());
            }
        } else {
            JOptionPane.showMessageDialog(null, "账号或密码错误", "警告：", JOptionPane.WARNING_MESSAGE);
            oos.close();
            ois.close();
        }
    }
}



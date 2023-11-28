package com.shelly.client.gui;

import com.shelly.Doc;
import com.shelly.FileMessage;
import com.shelly.OperationMessage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * TODO 操作员界面
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly.client.gui
 * @Author: shelly
 * @CreateTime: 2023/11/24  14:18
 * @Description: consist of this project
 */
public class OperatorView extends JFrame {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private JPanel buttonPanel, contentPanel;
    private JButton upLoadFileButton, downLoadFileButton, fileListButton,
            changePassWordButton, exitButton;
    private DefaultTableModel model;
    private JTable table;
    private JScrollPane scrollPane;
    private String name;

    public OperatorView(Socket socket, ObjectOutputStream oos, ObjectInputStream ois, String name) {
        this.socket = socket;
        this.ois = ois;
        this.oos = oos;
        this.name = name;
        setTitle("操作员界面");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 300, 800, 450);
        //setVisible(true);

        // 创建面板
        buttonPanel = new JPanel();
        contentPanel = new JPanel(new BorderLayout());

        // 创建按钮
        upLoadFileButton = new JButton("上传档案");
        upLoadFileButton.addActionListener(e -> {

            System.out.print("请输入文件描述：");
            String description = ReadData.sc.nextLine();
            try {
                OperationMessage message = new OperationMessage("UPLOAD_FILE");
                oos.writeObject(message);
                OperationMessage o = (OperationMessage) ois.readObject();
                if (o.getUPLOAD_FILE()) {
                    if (upLoadFile(description, new Timestamp(System.currentTimeMillis()))) {
                        JOptionPane.showMessageDialog(null, "上传文件成功", "操作", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "上传文件失败", "操作", JOptionPane.INFORMATION_MESSAGE);

                    }
                } else {
                    JOptionPane.showMessageDialog(null, "上传文件失败", "操作", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        });

        downLoadFileButton = new JButton("下载档案");
        downLoadFileButton.addActionListener(e -> {
            try {
                utils.downLoadFile(oos, ois);
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });


        fileListButton = new JButton("档案列表");
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
                    JOptionPane.showMessageDialog(null, "获取档案信息成功！", "操作",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "获取档案信息失败！", "操作：",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });


        changePassWordButton = new JButton("修改个人密码");
        changePassWordButton.addActionListener(e -> utils.changePassWord(ois, oos));

        exitButton = new JButton("退出登录");
        exitButton.addActionListener(e -> System.exit(0));

        // 设置按钮样式和布局
        Font font = new Font("楷体", Font.PLAIN, 16);
        addButtonStyle(upLoadFileButton, font);
        addButtonStyle(downLoadFileButton, font);
        addButtonStyle(fileListButton, font);
        addButtonStyle(changePassWordButton, font);
        addButtonStyle(exitButton, font);
        buttonPanel.setLayout(new GridLayout(5, 1, 0, 10));
        buttonPanel.add(upLoadFileButton);
        buttonPanel.add(downLoadFileButton);
        buttonPanel.add(fileListButton);
        buttonPanel.add(changePassWordButton);
        buttonPanel.add(exitButton);

        // 添加表格和滚动面板
        model = new DefaultTableModel();
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        contentPanel.add(scrollPane, BorderLayout.WEST);

        // 将面板添加到窗口中
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        setVisible(true);

    }

    private void addButtonStyle(JButton button, Font font) {
        button.setFont(font);
        button.setPreferredSize(new Dimension(200, 30));
    }

    private boolean upLoadFile(String description, Timestamp time) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.showOpenDialog(null);
        File selectedFile = fc.getSelectedFile();
        byte[] data = utils.fileToByteArray(selectedFile);
        try {
            oos.writeObject(new FileMessage(name, description, data, selectedFile.getName(), time));
            FileMessage o = (FileMessage) ois.readObject();
            if (o.getFlag()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}

package com.shelly.client.gui;

import java.awt.*;

/**
 * TODO 启动客户端！
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly.client.gui
 * @Author: shelly
 * @CreateTime: 2023/11/24  14:16
 * @Description: consist of this project
 */
class ClientStart {
    public static void main(String[] args) {
        EventQueue.invokeLater(LoginView::new);
    }
}

package com.shelly;

/**
 * TODO 处理登录请求信息
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly
 * @Author: shelly
 * @CreateTime: 2023/11/24  16:58
 * @Description: 服务器端检索无误后，将loginRequest设为true并返回给客户端
 */
public class LoginMessage extends AbstractMessage {


    // 登录请求
    private boolean loginRequest;
    public LoginMessage(String name,String password){
        super(name,password);
        loginRequest = false;
    }
    public boolean getLoginRequest() {
        return loginRequest;
    }

    public void setLoginRequest() {
        this.loginRequest = true;
    }


}

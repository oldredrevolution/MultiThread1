package com.shelly.Server.data;

import com.shelly.Doc;

import java.sql.*;
import java.util.ArrayList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO 处理数据
 *
 * @BelongsProject: MultiThread
 * @BelongsPackage: com.shelly.data
 * @Author: shelly
 * @CreateTime: 2023/11/21  19:38
 * @Description: 对用户和文件的信息进行相应修改
 */

public class DataProcessing {
    //public static Scanner SC = new Scanner(System.in);

    private static boolean connectToDB = false;
    public static Connection connection;
    private static String fileNum;

    // 为设置档案编号的方法加锁，防止档案编号出现错误
    static Lock lock = new ReentrantLock();


    /**
     * TODO 初始化，连接本地数据库
     */
    public static void init() {
        String url = "jdbc:mysql://127.0.0.1:3306/filemanagersystem?useSSL=false";
        String user = "root";
        String passWord = "myyx7731526";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动加载失败！");
        }
        try {
            connection = DriverManager.getConnection(url, user, passWord);
        } catch (SQLException e) {
            System.out.println("数据库访问错误！");
        }
        getFileNum();
        connectToDB = true;
    }


    /**
     * TODO 获得数据库中档案记录条数
     */
    public static void getFileNum() {
        String sql = "SELECT * FROM doc_info";
        int i = 0;
        try {
            Statement statement = DataProcessing.connection.createStatement();
            ResultSet res = statement.executeQuery(sql);

            while (res.next()) {
                i++;
            }
            fileNum = String.valueOf(i);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO 为档案编号
     * @return 返回档案编号
     * @Description: 方法中使用了锁lock，避免多线程修改档案编号时出现错误
     */
    public static String setId() {
        try {
            lock.lock();
            int number = Integer.parseInt(fileNum);
            fileNum = String.valueOf(number+1);
            return fileNum;
        } finally {
            lock.unlock();
        }
    }


    /**
     * TODO 取出所有的用户
     *
     * @return Enumeration<AbstractUser>
     * @throws SQLException 抛出一个SQLException
     */
    public static ArrayList<AbstractUser> listUser() throws SQLException {
        if (!connectToDB) throw new SQLException("Not Connected to Database");
        ArrayList<AbstractUser> userList = new ArrayList<>();
        String sql = "SELECT * FROM user_info";
        Statement stat = DataProcessing.connection.createStatement();
        ResultSet res = stat.executeQuery(sql);
        while (res.next()) {
            switch (res.getString("role")) {
                case "administrator":
                    userList.add(new Administrator(res.getString("name"),
                            res.getString("password"),
                            "administrator"));
                    break;
                case "operator":
                    userList.add(new Operator(res.getString("name"),
                            res.getString("password"),
                            "operator"));
                    break;
                default:
                    userList.add(new Browser(res.getString("name"),
                            res.getString("password"),
                            "browser"));
            }
        }
        return userList;
    }

    /**
     * TODO 获得所有档案的信息，并存储在ArrayList中
     *
     * @return 返回一个ArrayList<Doc>
     * @throws SQLException 对数据库操作时可能抛出SQLException
     */
    public static ArrayList<Doc> listDoc() throws SQLException {
        if (!connectToDB) throw new SQLException("Not Connected to Database");
        ArrayList<Doc> docList = new ArrayList<>();
        String sql = "SELECT * FROM doc_info";
        Statement stat = DataProcessing.connection.createStatement();
        ResultSet res = stat.executeQuery(sql);
        while (res.next()) {
            docList.add(new Doc(res.getString("id"),
                    res.getString("creator"),
                    res.getTimestamp("timestamp"),
                    res.getString("description"),
                    res.getString("filename")));
        }
        return docList;
    }

    /**
     * TODO 修改用户信息
     *
     * @param name     用户名
     * @param password 密码
     * @param role     角色
     * @return 修改成功则返回true, 反之返回false
     * @throws SQLException 抛出一个SQLException
     */
    public static boolean updateUser(String name, String password, String role) throws SQLException {
        if (!connectToDB) throw new SQLException("Not Connected to the DataBase!");
        String sqlModifySingle = "UPDATE user_info SET role = ? WHERE name = ?";
        String sqlModifyDouble = "UPDATE user_info SET role = ?,password = ? WHERE name = ?";
        PreparedStatement stat;
        if (password.equals("no need")) {
            stat = DataProcessing.connection.prepareStatement(sqlModifySingle);
            stat.setString(1, role);
            stat.setString(2, name);
            int affectedRows = stat.executeUpdate();
            return affectedRows > 0;
        } else {
            stat = DataProcessing.connection.prepareStatement(sqlModifyDouble);
            stat.setString(1, role);
            stat.setString(2, password);
            stat.setString(3, name);
            int affectedRows = stat.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * TODO 修改密码
     *
     * @param name     被修改者用户名
     * @param passWord 被修改者新密码
     * @return 修改成功返回true，反之返回false
     * @throws SQLException 未成功链接数据库将会抛出SQLException异常
     */
    public static boolean changePassWord(String name, String passWord) throws SQLException {
        if (!connectToDB) throw new SQLException("Not Connect to the DataBase");
        String sql = "UPDATE user_info SET password = ? WHERE name = ?";
        PreparedStatement stat = DataProcessing.connection.prepareStatement(sql);
        stat.setString(1, passWord);
        stat.setString(2, name);
        int affectedRows = stat.executeUpdate();
        return affectedRows > 0;
    }

    /**
     * TODO 插入新用户
     *
     * @param name     用户名
     * @param password 密码
     * @param role     权限
     * @return 成功插入新用户则返回true
     * @throws SQLException 抛出一个SQLException
     */
    public static boolean insertUser(String name, String password, String role) throws SQLException {
        if (!connectToDB) throw new SQLException("Not Connected to the DataBase!");
        String sqlInsert = "INSERT INTO user_info VALUES(?,?,?)";
        PreparedStatement stat = DataProcessing.connection.prepareStatement(sqlInsert);
        stat.setString(1, name);
        stat.setString(2, password);
        stat.setString(3, role);
        int affectedRows = stat.executeUpdate();
        return affectedRows > 0;
    }

    /**
     * TODO 删除指定用户
     *
     * @param name 用户名
     * @return 如果删除成功则返回true，反之返回false
     * @throws SQLException 抛出一个SQLException
     */
    public static boolean deleteUser(String name) throws SQLException {
        if (!connectToDB) throw new SQLException("Not Connected to the DataBase!");
        String sqlDelete = "DELETE FROM user_info WHERE name = ?";
        PreparedStatement stat = DataProcessing.connection.prepareStatement(sqlDelete);
        stat.setString(1, name);
        int affectedRows = stat.executeUpdate();
        return affectedRows > 0;

    }

    /**
     * TODO 关闭数据库连接
     */
    public static void disconnectFromDataBase() {
        if (connectToDB) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("断开数据库失败！");
            } finally {
                connectToDB = false;
            }
        } else System.out.println("无数据库链接！");
    }

    /**
     * TODO 检测是否用户已经存在
     *
     * @param name 客户端希望插入的新用户名
     * @return 如果用户名已存在则返回true，反之返回false
     * @throws SQLException 此方法可能会抛出一个SQLException
     */
    public static boolean checkUserExist(String name) throws SQLException {
        if (!connectToDB) throw new SQLException("Not Connected to the DataBase!");
        String sql = "SELECT * FROM user_info WHERE name = ?";
        PreparedStatement stat = connection.prepareStatement(sql);
        stat.setString(1, name);
        ResultSet res = stat.executeQuery();
        if (res.next()) return true;
        else return false;
    }


    static enum ROLE_ENUM {
        /**
         * administrator
         */
        administrator("administrator"),
        /**
         * operator
         */
        operator("operator"),
        /**
         * browser
         */
        browser("browser");

        private String role;

        ROLE_ENUM(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }
    }

}
package ru.home.common.system;

public class UserInfo {

    private final static UserInfo SYSTEM_INFO = new UserInfo();
    private String ip;
    private String computerName;
    private String userName;

    public static UserInfo getSystemInfo() {
        return SYSTEM_INFO;
    }

    public static void updateSystemInfo() {
        SYSTEM_INFO.update();
    }

    private UserInfo() {
        super();
        update();
    }

    private void update() {
        this.ip = UserUtils.getIp();
        this.computerName = UserUtils.getComputerName();
        this.userName = UserUtils.getUserName();
    }

    public String getIp() {
        return ip;
    }

    public String getComputerName() {
        return computerName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "ip='" + ip + '\'' +
                ", computerName='" + computerName + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}

package globl;

import tools.AppUtils;

public class Data {

    private String workingPath;
    private String myExternalIp = "";
    private String myInternalIp = "";
    private int myServerPort = 25565;

    private boolean isServerRunning = false;

    public Data(String path) {
        this.workingPath = path;
    }

    public String getWorkingPath() {
        return workingPath;
    }

    public String getMyExternalIp() {
        return myExternalIp;
    }

    public void setMyExternalIp(String myExternalIp) {
        this.myExternalIp = myExternalIp;
    }

    public int getMyServerPort() {
        return myServerPort;
    }

    public void setMyServerPort(int myServerPort) {
        AppUtils.validatePort(myServerPort);
        this.myServerPort = myServerPort;
    }

    public boolean isServerRunning() { return isServerRunning; }
    public void setServerRunning(boolean serverRunning) { isServerRunning = serverRunning; }


}

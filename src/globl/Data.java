package globl;

import tools.AppUtils;
import tools.FileService;

import java.io.File;
import java.security.spec.ECField;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Data {

    // Base server Data
    private String workingPath;
    private String myExternalIp = "";
    // Properties
    private final Map<String, String> propertiesMap = new LinkedHashMap<>();
    //

    private boolean isServerRunning = false;

    public Data(String path) {
        this.workingPath = path;
        if (new File(workingPath + "\\server.properties").exists()) fillProperties();
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
        try {
            int serverPort = Integer.parseInt(propertiesMap.get("server-port"));
            return serverPort;
        } catch (Exception e) { return 25565; }
    }

    public void setMyServerPort(int myServerPort) {
        AppUtils.validatePort(myServerPort);
        propertiesMap.put("server-port", myServerPort + "");
    }

    public boolean isServerRunning() { return isServerRunning; }
    public void setServerRunning(boolean serverRunning) { isServerRunning = serverRunning; }

    public String getMyInternalIp() {
        String internalIP = propertiesMap.get("server-ip");
        if (internalIP == null) return "";
        return internalIP;
    }

    public void setMyInternalIp(String myInternalIp) {
            this.propertiesMap.put("server-ip", myInternalIp);
    }

    // PropertiesMap methods
    private void fillProperties() {
        List<String> lines = FileService.readFile(workingPath + "\\\\server.properties")
                .stream()
                .map(o -> (String) o)
                .toList();

        for (int i = 2; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            int eq = line.indexOf('=');
            if (eq == -1) {
                continue;
            }

            String key = line.substring(0, eq).trim();
            String value = line.substring(eq + 1).trim();

            propertiesMap.put(key, value);
        }
    }
    public Map<String, String> getPropertiesMap() {
        if (propertiesMap.isEmpty()) fillProperties();
        return propertiesMap;
    }
    public void setProperty(String key, String value) {
        if (propertiesMap.isEmpty()) fillProperties();
        propertiesMap.put(key,value);
    }
    public String getProperty(String key) {
        if (propertiesMap.isEmpty()) fillProperties();
        return propertiesMap.get(key);
    }

}

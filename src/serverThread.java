import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class serverThread extends Thread {

    private final JTextArea consoleWindow;
    private final String serverPath;
    private Process process;

    private List<String> onlinePlayers = new ArrayList<>();

    public serverThread(JTextArea consoleWindow, String serverPath) {
        this.consoleWindow = consoleWindow;
        this.serverPath = serverPath;
    }

    public List<String> getOnlinePlayers() { return onlinePlayers; }

    @Override
    public void run() {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", serverPath + "\\server.jar", "--nogui");
        pb.directory(new File(serverPath));
        pb.redirectErrorStream(true);

        try {
            process = pb.start();
            Main.data.setServerRunning(true);

            // currently only windows supported
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                long pid = process.pid();
                ServerMonitor monitor =
                        new ServerMonitor(pid, Main.mainWindow.usageStatusLabel, this.process);
                Thread monitorThread = new Thread(monitor);
                monitorThread.start();
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    consoleWindow.append(line + "\n");
                    if (line.contains(": Done (")) Main.mainWindow.GUIstateServerStarted();
                    if (line.contains("joined") || line.contains("left")) { // "<" check if it's not written by player
                        String[] parts = line.split(" ");
                        if (parts[2].equalsIgnoreCase("thread/INFO]:")) {
                            if (parts[4].equalsIgnoreCase("joined")) {
                                onlinePlayers.add(parts[3]);
                            } else {
                                onlinePlayers.remove(parts[3]);
                            }
                            // call update on GUI:
                            Main.mainWindow.updateOnlinePlayers(this.onlinePlayers);
                        }
                    }
                }
            } catch (Exception e) {
                consoleWindow.append("\n--ERROR---------------------\\x1B[31m \n" + e.getMessage() + "\n \\x1B[0m----------------------------\n");
            }
            int exitCode = process.waitFor();
            //
        } catch (Exception e) {
            consoleWindow.append("\n--ERROR---------------------\\x1B[31m \n" + e.getMessage() + "\n \\x1B[0m----------------------------\n");
        }
        System.out.println("szerver leállt");
        Main.data.setServerRunning(false);
        Main.mainWindow.GUIstateServerStopped();
    }

    public void stopThread() {
        Main.data.setServerRunning(false);
        Main.mainWindow.GUIstateServerStopped();
        this.interrupt();
    }

    public void sendCommand(String cmd) {
        if (process == null) return;

        try {
            OutputStream os = process.getOutputStream();
            os.write((cmd + "\n").getBytes());
            os.flush();
        } catch (Exception e) {
            consoleWindow.append("\n--ERROR COMMAND---------------------\n" + e.getMessage() + "\n-----------------------------------\n");
            if (cmd.equalsIgnoreCase("stop")) stopThread();
        }
    }


}
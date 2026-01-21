import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerMonitor implements Runnable {

    private final long pid;
    private final JLabel label;
    private volatile boolean running = true;
    Process process;

    public ServerMonitor(long pid, JLabel label, Process proccess) {
        this.pid = pid;
        this.label = label;
        this.process = proccess;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {

        while (Main.data.isServerRunning()) {

            double cpu = getCpuUsage(this.process);
            long ramMB = getRamUsageMB(pid);

            SwingUtilities.invokeLater(() -> {
                label.setText(
                        String.format("<html><span style=\"font-size:7px;color:gray;\">CPU: %.0f%%   RAM: %d MB</span></html>", cpu, ramMB)
                );
            });

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {}
        }

        SwingUtilities.invokeLater(() -> label.setText(""));
    }

    // =========================
    // CPU (Windows)
    // =========================
    private long lastCpuTime = 0;
    private long lastTimestamp = 0;

    private double getCpuUsage(Process process) {
        ProcessHandle handle = process.toHandle();
        ProcessHandle.Info info = handle.info();

        long cpuTime = info.totalCpuDuration()
                .map(d -> d.toNanos())
                .orElse(0L);

        long now = System.nanoTime();

        if (lastTimestamp == 0) {
            lastTimestamp = now;
            lastCpuTime = cpuTime;
            return 0.0;
        }

        long cpuDiff = cpuTime - lastCpuTime;
        long timeDiff = now - lastTimestamp;

        lastCpuTime = cpuTime;
        lastTimestamp = now;

        int cores = Runtime.getRuntime().availableProcessors();

        double usage = (double) cpuDiff / timeDiff / cores * 100.0;

        return Math.max(0.0, Math.min(100.0, usage));
    }



    // =========================
    // RAM (Windows)
    // =========================
    private long getRamUsageMB(long pid) {
        try {
            Process p = new ProcessBuilder(
                    "powershell",
                    "-Command",
                    "(Get-Process -Id " + pid + ").WorkingSet64"
            ).start();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );

            String line = br.readLine();
            if (line == null) return 0;

            return Long.parseLong(line.trim()) / 1024 / 1024;

        } catch (Exception e) {
            return 0;
        }
    }

}

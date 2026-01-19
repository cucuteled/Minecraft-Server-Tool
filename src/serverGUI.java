import globl.global;
import tools.AppUtils;

import javax.print.DocFlavor;
import javax.print.attribute.standard.JobKOctets;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.security.spec.ECField;
import java.util.Map;

public class serverGUI {

    private JFrame frame = new JFrame(new File(Main.data.getWorkingPath()).getName());
    private final String serverPath = Main.data.getWorkingPath();

    public serverGUI() {
            // Set window propeties;
            frame.setResizable(false);
            frame.setIconImage(global.appIMG);
            frame.setLayout(null);
            // Set Sceen
            if (AppUtils.isEulaAccepted(Main.data.getWorkingPath())) {
                showMainWindow();
            } else {
                showFirstSetup();
            }
            // Show window
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
    }

    private void showFirstSetup() {
        frame.getContentPane().removeAll();
        frame.setSize(360,130);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //
        if (new File(serverPath + "\\eula.txt").exists()) {
            // if Eula already exists
        } else {
            JLabel label = new JLabel("<html><h1>Starting server for the first time... Please wait..</h1></html>");
            label.setBounds(10,-15,340,120);
            frame.add(label);
            frame.repaint();
            frame.revalidate();
            // First run
            ProcessBuilder pb = new ProcessBuilder( "java", "-jar", serverPath + "\\server.jar", "--nogui" );
            pb.directory(new File(serverPath));
            pb.redirectErrorStream(true);

            try {
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(
                     new InputStreamReader(process.getInputStream()))) {

                     String line;
                     while ((line = reader.readLine()) != null) {
                        System.out.println("[SERVER] " + line);
                     }
                } catch (Exception e) {
                    System.out.println(e);
                }
                int exitCode = process.waitFor();
                //
            } catch (Exception e) {
                System.out.println(e);
            }
            label.setBounds(110,-35,340,120);
            label.setText("<html><h1>Success!</h1></html>");
            label.repaint();
            label.revalidate();
        }
        //

        frame.setSize(360, 250);
        //

        JButton nextButton = new JButton("Next");
        nextButton.setVisible(false);
        nextButton.setBounds(60,120,240,30);

        JCheckBox acceptEula = new JCheckBox("Accept EULA");
        acceptEula.setBounds(60, 50, 100, 30);
        acceptEula.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                nextButton.setVisible(acceptEula.isSelected());
            }
        });
        frame.add(acceptEula);
        nextButton.addActionListener(e -> {
            if (acceptEula.isSelected() && AppUtils.acceptEula(serverPath + "\\eula.txt")) {
                showIPSettingsPanel();
                frame.setVisible(false);
            } else {
                acceptEula.setSelected(false);
                nextButton.setVisible(false);
                JOptionPane.showMessageDialog(
                        null,
                        "Unable to accept.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });
        frame.add(nextButton);

        JLabel openEULA = new JLabel("#open eula");
        openEULA.setForeground(Color.BLUE);
        openEULA.setCursor(new Cursor(Cursor.HAND_CURSOR));
        openEULA.setBounds(180,50,100,30);
        openEULA.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    String cmd = "cmd /c start \"\" \"" + serverPath + "\\eula.txt\"";
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception d) {}
            }
        });
        frame.add(openEULA);
        //
        frame.repaint();
        frame.revalidate();
    }

    private void showIPSettingsPanel() {
        JFrame ipSettingsPanel = new JFrame("IP Settings");
        ipSettingsPanel.setSize(300,360);
        ipSettingsPanel.setLocationRelativeTo(null);
        ipSettingsPanel.setResizable(false);
        ipSettingsPanel.setIconImage(global.appIMG);
        ipSettingsPanel.setLayout(null);
        ipSettingsPanel.setVisible(true);
        //
        JLabel label = new JLabel("<html><h2>IP & Port Settings</h2></html>");
        label.setBounds(20,10,200,40);
        ipSettingsPanel.add(label);

        boolean isExternalIpAvailable = true;
        // Retrieve ip
        try {
            Main.data.setMyExternalIp(AppUtils.getExternalIp());
        } catch (Exception e) {
            isExternalIpAvailable = false;
            Main.data.setMyExternalIp(AppUtils.getInternalIp());
            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        // Set Port
        JLabel portlabel = new JLabel("Port: ");
        portlabel.setBounds(20,55, 60, 30);
        ipSettingsPanel.add(portlabel);

        JTextField inputPort = new JTextField();
        inputPort.setText(Main.data.getMyServerPort() + "");
        inputPort.setBounds(60,55,50,30);

        JLabel inputPortErrorLabel = new JLabel("");
        inputPortErrorLabel.setText("");
        inputPortErrorLabel.setBounds(20,90,300,30);
        ipSettingsPanel.add(inputPortErrorLabel);

        inputPort.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
                if (inputPort.getText().length() >= 5) { e.consume(); }
            }
        });

        //todo:: interal server let be modifiable 220height

        // Check Port is Open
        //
        JLabel checkPortOpen =  new JLabel("<html><a href=\"#\">Check is Port Open?</a></html>");
        checkPortOpen.setBounds(20,150,200,30);
        checkPortOpen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkPortOpen.setVisible(isExternalIpAvailable);

        JLabel openPortGuide = new JLabel("<html><a href=\"#\"> #How to open port?</a></html");
        openPortGuide.setBounds(150,150,200,30);
        openPortGuide.setCursor(new Cursor(Cursor.HAND_CURSOR));
        openPortGuide.setVisible(false);

        openPortGuide.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    String cmd = "cmd /c start \"\" \"" + global.openPortGuidePath;
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception d) {}
            }
        });

        ipSettingsPanel.add(openPortGuide);

        checkPortOpen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                checkPortOpen.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                checkPortOpen.setText("<html><img src=\"" + global.loadingIMGPath + "\" width=\"15\" height=\"15\"></html>");
                checkPortOpen.revalidate();
                checkPortOpen.repaint();
                //
                Thread thread = new Thread(() -> {
                    if (AppUtils.isPortOpen(Main.data.getMyExternalIp(), Main.data.getMyServerPort(), Main.data.isServerRunning())) {
                        checkPortOpen.setForeground(Color.green);
                        checkPortOpen.setText("Port is open.");
                        openPortGuide.setVisible(false);
                    } else {
                        checkPortOpen.setForeground(Color.red);
                        checkPortOpen.setText("Port is not open.");
                        openPortGuide.setVisible(true);
                    }
                });
                thread.start();

                checkPortOpen.revalidate();
                checkPortOpen.repaint();
            }
        });

        ipSettingsPanel.add(checkPortOpen);
        //
        JLabel ipLabel = new JLabel((isExternalIpAvailable ? "<html><b>You're external ip: </b><i>" : "<html><b>You're internal ip: </b><i>") +
                Main.data.getMyExternalIp() + " : " + Main.data.getMyServerPort() +
                "</i></html>");
        ipLabel.setBounds(20,120,300,40);
        ipSettingsPanel.add(ipLabel);

        boolean finalIsExternalIpAvailable = isExternalIpAvailable;
        inputPort.addActionListener(e -> {
            try {
                int port = Integer.parseInt(inputPort.getText());
                Main.data.setMyServerPort(port);
                ipLabel.setText(finalIsExternalIpAvailable ? "<html><b>You're external ip: </b><i>" : "<html><b>You're internal ip: </b><i>" +
                        Main.data.getMyExternalIp() + ":" + Main.data.getMyServerPort() +
                        "</i></html>");
                inputPortErrorLabel.setText("");
                inputPort.setForeground(Color.black);
            } catch (Exception err) {
                inputPortErrorLabel.setText(
                        "<html>"
                                + "<span style='color:red; font-weight:bold; font-size:10px;'>Error:</span> "
                                + "<span style='color:black; font-style:italic; font-size:8px;'>" + err.getMessage() + "</span>"
                                + "</html>"
                );
                inputPort.setForeground(Color.RED);
            }
            checkPortOpen.setCursor(new Cursor(Cursor.HAND_CURSOR));
            checkPortOpen.setText("<html><a href=\"#\">Check is Port Open?</a></html>");
            openPortGuide.setVisible(false);
            //
            ipSettingsPanel.repaint();
            ipSettingsPanel.revalidate();
        });

        JButton nextButton = new JButton("Countinue");
        nextButton.setBounds(10,280,265,30);
        ipSettingsPanel.add(nextButton);
        ipSettingsPanel.add(inputPort);

        //
        ipSettingsPanel.repaint();
        ipSettingsPanel.revalidate();
    }

    private void showMainWindow() {
        frame.getContentPane().removeAll();
        frame.setSize(340,420);
        frame.setLocationRelativeTo(null);
        //
        // --------------
        // Menu Bar
        // --------------
        JMenuBar menuBar = new JMenuBar();
        JMenu settingsMenu = new JMenu("Settings");
        // Settings menu items
        JMenuItem ipSettingsItem = new JMenuItem("IP Settings");
        ipSettingsItem.addActionListener(e -> showIPSettingsPanel());
        settingsMenu.add(ipSettingsItem);
        //
        menuBar.add(settingsMenu);
        frame.setJMenuBar(menuBar);
        //
        frame.repaint();
        frame.revalidate();
    }
}

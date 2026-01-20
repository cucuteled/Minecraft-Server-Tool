import globl.global;
import tools.AppUtils;
import tools.IpFieldValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class serverGUI {

    private JFrame frame = new JFrame(new File(Main.data.getWorkingPath()).getName());
    private final String serverPath = Main.data.getWorkingPath();
    private boolean isFirstSetup = false;

    public JTextArea consoleWindow = new JTextArea("");
    private serverThread server;

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
                isFirstSetup = true;
            }
            // Show window
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                        //System.out.println("[SERVER] " + line);
                     }
                } catch (Exception e) {
                    //System.out.println(e);
                }
                int exitCode = process.waitFor();
                //
            } catch (Exception e) {
                //System.out.println(e);
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
        JLabel portlabel = new JLabel("<html>Port:  &emsp;&emsp;&emsp;&emsp;&emsp;  " +
                "<span style='font-size:8px; color:gray;'><i>(Default: 25565)</i></span></html>");
        portlabel.setBounds(20,55, 300, 30);
        ipSettingsPanel.add(portlabel);

        JTextField inputPort = new JTextField();
        inputPort.setText(Main.data.getMyServerPort() + "");
        inputPort.setBounds(60,55,50,30);

        JLabel inputPortErrorLabel = new JLabel("");
        inputPortErrorLabel.setText("");
        inputPortErrorLabel.setBounds(20,80,300,30);
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

        // Settings of internal server ip
        JLabel internalIpLabel = new JLabel("Internal IP:");
        internalIpLabel.setBounds(20,180,80,30);
        ipSettingsPanel.add(internalIpLabel);

        JTextField inputInternalIp = new JTextField();
        inputInternalIp.setText(Main.data.getMyInternalIp());
        IpFieldValidator.apply(inputInternalIp);
        inputInternalIp.setBounds(95,180,105,30);
        inputInternalIp.addFocusListener(new FocusAdapter() { // Check if there's something behind the IP (Doesn't matter)
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                Thread thread = new Thread(() -> {
                    ProcessBuilder pb = new ProcessBuilder(
                            "cmd", "/c", "ping " + inputInternalIp.getText() + " -n 1 -w 500");
                try {
                    Process p = pb.start();
                    int exitCode = p.waitFor();
                    inputInternalIp.setForeground(exitCode == 0 ? Color.black : Color.orange);
                } catch (Exception err) {}
                });
                thread.start();
            }
        });
        ipSettingsPanel.add(inputInternalIp);
        // Set internal ip to default
        JLabel setInternalIPToDefault = new JLabel("<html><a href=\"#\"><i>(Default)</i></a></html>");
        setInternalIPToDefault.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setInternalIPToDefault.setBounds(210,180,60,30);
        setInternalIPToDefault.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                inputInternalIp.setText(AppUtils.getInternalIp());
            }
        });
        ipSettingsPanel.add(setInternalIPToDefault);

        // Check Port is Open

        JLabel checkPortOpen =  new JLabel("<html><a href=\"#\">Check is Port Open?</a></html>");
        checkPortOpen.setBounds(20,130,200,30);
        checkPortOpen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkPortOpen.setVisible(isExternalIpAvailable);

        JLabel openPortGuide = new JLabel("<html><a href=\"#\"> #How to open port?</a></html");
        openPortGuide.setBounds(150,130,200,30);
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
        JLabel ipLabel = new JLabel((isExternalIpAvailable ? "<html><b>You're external IP: </b><i>" : "<html><b>You're internal IP: </b><i>") +
                Main.data.getMyExternalIp() + " : " + Main.data.getMyServerPort() +
                "</i></html>");
        ipLabel.setBounds(20,100,300,40);
        ipSettingsPanel.add(ipLabel);

        boolean finalIsExternalIpAvailable = isExternalIpAvailable;
        inputPort.addActionListener(e -> {
            try {
                if (inputPort.getText().isBlank()) inputPort.setText("25565");
                ipLabel.setText((finalIsExternalIpAvailable ? "<html><b>You're external IP: </b><i>" : "<html><b>You're internal IP: </b><i>") +
                        Main.data.getMyExternalIp() + " : " + inputPort.getText() +
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
            ipLabel.repaint();
            ipLabel.revalidate();
            ipSettingsPanel.repaint();
            ipSettingsPanel.revalidate();
        });

        JButton nextButton = new JButton( isFirstSetup? "Continue" : "Save");
        nextButton.setBounds(10,280,265,30);
        nextButton.addActionListener(l -> {
            Main.data.setMyInternalIp(inputInternalIp.getText());
            if (inputPort.getText().isBlank()) inputPort.setText("25565");
            Main.data.setMyServerPort(Integer.parseInt( inputPort.getText() ));
            if (isFirstSetup) showMainWindow();
            if (Main.data.isServerRunning()) {
                JOptionPane.showMessageDialog(
                        null,
                        "You need to restart the server, for the modifications to take place.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            AppUtils.saveServerData(Main.data); // save
            ipSettingsPanel.dispose();
        });
        ipSettingsPanel.add(nextButton);

        ipSettingsPanel.add(inputPort);
        //
        ipSettingsPanel.repaint();
        ipSettingsPanel.revalidate();
    }

    private void showMainWindow() {
        frame.getContentPane().removeAll();
        frame.setSize(720,520);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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

        JMenuItem propertiesSettingsMenu = new JMenuItem("Settings");
        propertiesSettingsMenu.addActionListener(a -> {
            // todo: show settings menu
        });
        settingsMenu.add(propertiesSettingsMenu);
        // About
        settingsMenu.addSeparator();
        JMenuItem aboutInfo = new JMenuItem("About");
        aboutInfo.addActionListener(a -> {
            JOptionPane.showMessageDialog(
                    null,
                    "Developed by cucuteled @All rights reserved\n" +
                    "\nMinecraft Server files are provided by Mojang API.",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        settingsMenu.add(aboutInfo);
        //
        menuBar.add(settingsMenu);
        frame.setJMenuBar(menuBar);
        // ------------
        // Tools
        // -------------
        JMenu toolsMenu = new JMenu("Tools");

        JMenuItem logSearcher = new JMenuItem("Log Searcher");
        logSearcher.addActionListener(e -> {
            if (Main.data.isServerRunning() || !new File(Main.data.getWorkingPath() + "\\\\logs").exists()) {
                JOptionPane.showMessageDialog(
                        null,
                        "In order to use this function, you need to stop the server first.\nor there's no logs yet.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            } else {
                //todo: open logsearcher
            }
        });
        toolsMenu.add(logSearcher);

        menuBar.add(toolsMenu);
        // -------------
        // Main Window
        // -------------
        // Console
        JScrollPane consoleScrollPane = new JScrollPane();
        consoleScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        consoleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        consoleScrollPane.setBounds(5,5,400,420);

        consoleWindow.setEditable(false);
        consoleWindow.setBackground(Color.white);
        consoleWindow.setMinimumSize(new Dimension(400,440));
        consoleScrollPane.setViewportView(consoleWindow);

        frame.add(consoleScrollPane);
        // Start - Stop Button
        JButton launchButton = new JButton("start");
        launchButton.setBounds(415,5,280,30);
        // Options
        JCheckBox onlineModeToggle = new JCheckBox("Online mode \uD83D\uDEC8");
        onlineModeToggle.setBounds(415,40,200,30);
        onlineModeToggle.setToolTipText("<html>Enable official Authentication.<br>" +
                "This will require to have a purchased copy of the game.<br>" +
                "If you or somebody use cracked client, uncheck this.</html>");
        onlineModeToggle.setSelected(Main.data.getProperty("online-mode").equalsIgnoreCase("true"));
        onlineModeToggle.addActionListener(e -> {
            if (!Main.data.isServerRunning()) {
                Main.data.setProperty("online-mode", onlineModeToggle.isSelected() ? "true" : "false");
                AppUtils.saveServerData(Main.data);
            }
        });
        frame.add(onlineModeToggle);

        // Console INPUT
        JTextField consoleInputField = new JTextField();
        consoleInputField.setBounds(5,425,350,30);
        consoleInputField.setEnabled(false);
        frame.add(consoleInputField);
        JButton consoleInputSend = new JButton(">>");
        consoleInputSend.setBounds(355,425,50,30);
        consoleInputSend.setEnabled(false);
        frame.add(consoleInputSend);
        //
        launchButton.addActionListener(a -> {
            if (!Main.data.isServerRunning()) {
                launchButton.setText("Stop"); //todo:: launch
                this.server = new serverThread(consoleWindow,serverPath);
                server.start();
            } else {
                launchButton.setText("Start"); //todo:: stop
                this.server.sendCommand("stop");
            }
        });
        frame.add(launchButton);
        //
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Main.data.isServerRunning()) server.sendCommand("stop"); //todo:: are you sure you want to exit?
            }
        });

        //
        frame.repaint();
        frame.revalidate();
    }

}



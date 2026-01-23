import globl.global;
import tools.AppUtils;
import tools.FileService;
import tools.IpFieldValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;
import javax.swing.JPanel;


public class serverGUI {

    private JFrame frame = new JFrame(new File(Main.data.getWorkingPath()).getName());
    private final String serverPath = Main.data.getWorkingPath();
    private boolean isFirstSetup = false;

    // Server controls
    public JButton launchButton;
    public JTextField consoleInputField;
    public JButton consoleInputSend;
    public JCheckBox onlineModeToggle;
    public JTextArea consoleWindow = new JTextArea("");
    private serverThread server;

    private JLabel totalSizeLabel;
    public  JLabel usageStatusLabel;

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
                         //
                     }
                } catch (Exception ignored) {
                }
                int exitCode = process.waitFor();
                //
            } catch (Exception ignored) {
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

    private void setTotalSizeLabel() {
        float sizeGb = (float) AppUtils.getFolderSize(new File(serverPath)) / 1024 / 1024;
        boolean isGB = false;
        if (sizeGb > 1000.0) {
            sizeGb = sizeGb / 1024;
            isGB = true;
        }
        totalSizeLabel.setText("<html><i>Server size: " + String.format("%.2f", sizeGb) + (isGB ? " GB" : " MB") + "</i></html>");
        FontMetrics fm = totalSizeLabel.getFontMetrics(totalSizeLabel.getFont());
        totalSizeLabel.setBounds(820 - fm.stringWidth(totalSizeLabel.getText()) - 10, 430,200,30);
    }

    public void updateOnlinePlayers(List<String> players) {
        onlinePlayersLabel.setText("""
        <html><span style="font-size:8px;color:gray;font-style:italic;">Online players: %s</span></html>
        """.formatted(players.size()));
        StringBuilder playerList = new StringBuilder();
        for (String player : players) {
            playerList.append(player).append("<br>");
        }
        onlinePlayersLabel.setToolTipText("""
                <html>
                %s
                </html>
                """.formatted(playerList.toString()));
        managePlayers.setVisible(onlinePlayersLabel.isVisible() && !players.isEmpty());
    }

    // Server info
    private JLabel onlinePlayersLabel;
    private JLabel managePlayers;

    private void showMainWindow() {
        frame.getContentPane().removeAll();
        frame.setSize(720,520);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // Info Label
        totalSizeLabel = new JLabel("");
        setTotalSizeLabel();
        frame.add(totalSizeLabel);

        usageStatusLabel = new JLabel("");
        usageStatusLabel.setBounds(405,430,200,30);
        frame.add(usageStatusLabel);

        onlinePlayersLabel = new JLabel("""
        <html><span style="font-size:8px;color:gray;font-style:italic;">Online players: 0</span></html>
        """);
        onlinePlayersLabel.setVisible(false);
        onlinePlayersLabel.setBounds(415, 80, 100, 30);
        frame.add(onlinePlayersLabel);

        managePlayers = new JLabel("<html><span style=\"font-size:8px;\"><a href=\"#\" style=\"font-style:italic;\">(Manage players)</a></span></html>");
        managePlayers.setBounds(530,80,100,30);
        managePlayers.setCursor(new Cursor(Cursor.HAND_CURSOR));
        managePlayers.setVisible(false);
        managePlayers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                showManagePlayersPanel();
            }
        });
        frame.add(managePlayers);
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
            showPropertiesMenuSettings();
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
                showToolLogSearcher();
            }
        });
        toolsMenu.add(logSearcher);

        JMenuItem webServer = new JMenuItem("webServer");
        webServer.addActionListener(a -> {
            //todo: webserver
        });
        toolsMenu.add(webServer);

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
        launchButton = new JButton("start");
        launchButton.setBounds(415,5,280,30);
        // Options
        onlineModeToggle = new JCheckBox("Online mode \uD83D\uDEC8");
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
        consoleInputField = new JTextField();
        consoleInputField.setBounds(5,425,350,30);
        consoleInputField.setEnabled(false);
        consoleInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Main.data.isServerRunning())  {
                        server.sendCommand(consoleInputField.getText());
                        consoleInputField.setText("");
                    }
                }
            }
        });
        frame.add(consoleInputField);

        consoleInputSend = new JButton(">>");
        consoleInputSend.setBounds(355,425,50,30);
        consoleInputSend.setEnabled(false);
        consoleInputSend.addActionListener(a -> {
            if (Main.data.isServerRunning())  {
                server.sendCommand(consoleInputField.getText());
                consoleInputField.setText("");
            }
        });
        frame.add(consoleInputSend);
        //
        launchButton.addActionListener(a -> {
            if (!Main.data.isServerRunning()) {
                launchButton.setEnabled(false);
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                launchButton.setBackground(new Color(196, 134, 44));
                consoleWindow.setText(""); // clear previous logs from console
                this.server = new serverThread(consoleWindow,serverPath);
                server.start();
            } else {
                launchButton.setEnabled(false);
                launchButton.setBackground(new Color(196, 134, 44));
                this.server.sendCommand("stop");
            }
        });
        frame.add(launchButton);
        //
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Main.data.isServerRunning()) {
                    int result = JOptionPane.showConfirmDialog(
                            null,
                            "Server is running. Are you sure you want to exit?",
                            "Confirm Exit",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (result == JOptionPane.YES_OPTION) {
                        server.sendCommand("stop");
                        frame.dispose();
                        System.exit(0);
                    }
                }
            }
        });

        //
        frame.repaint();
        frame.revalidate();
    }

    private void showManagePlayersPanel() {
        JFrame managePlayersPanel = new JFrame("Manage Players");
        managePlayersPanel.setSize(220,300);
        managePlayersPanel.setLocationRelativeTo(null);
        managePlayersPanel.setResizable(false);
        managePlayersPanel.setIconImage(global.appIMG);
        managePlayersPanel.setLayout(new BorderLayout());
        // Fill with online players
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0,0,180,250);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.X_AXIS));

        scrollPane.setViewportView(playersPanel);
        managePlayersPanel.add(scrollPane);
        if (Main.data.isServerRunning()) {
            for (String player : server.getOnlinePlayers()) {
                JPanel playerPanel = new JPanel();
                playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
                //playerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));


                JLabel nameLabel = new JLabel(player);

                JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

                JLabel kickPlayer = new JLabel("<html><a href=\"#\">kick</a>  </html>");
                kickPlayer.setCursor(new Cursor(Cursor.HAND_CURSOR));
                kickPlayer.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        server.sendCommand("kick " + player);
                        //todo: eltüteni a listából
                    }
                });
                JLabel banPlayer = new JLabel("<html><a href=\"#\">ban</a></html>");
                banPlayer.setCursor(new Cursor(Cursor.HAND_CURSOR));
                banPlayer.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        server.sendCommand("ban " + player);
                        //todo: eltüteni a listából
                    }
                });

                actionsPanel.add(kickPlayer);
                actionsPanel.add(banPlayer);

                playerPanel.add(nameLabel, BorderLayout.WEST);
                playerPanel.add(actionsPanel, BorderLayout.EAST);
                //
                playersPanel.add(playerPanel);
            }
            playersPanel.revalidate();
            playersPanel.repaint();
        }
        //
        managePlayersPanel.revalidate();
        managePlayersPanel.repaint();
        managePlayersPanel.setVisible(true);
    }

    // ===========================================
    // Callable from outside CLASS (SERVERTHREAD)
    // ===========================================
    public void GUIstateServerStarted() {
        launchButton.setEnabled(true);
        launchButton.setText("Stop");
        launchButton.setBackground(new Color(187, 34, 34));
        onlinePlayersLabel.setVisible(true);
        //
        consoleInputField.setEnabled(true);
        consoleInputSend.setEnabled(true);
        onlineModeToggle.setEnabled(false);
    }
    public void GUIstateServerStopped() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launchButton.setEnabled(true);
        launchButton.setText("Start");
        launchButton.setBackground(UIManager.getColor("Button.background"));
        //
        onlinePlayersLabel.setVisible(false);
        refreshMainWindowAffectedByProperties();
        //
        consoleInputField.setEnabled(false);
        consoleInputSend.setEnabled(false);
        onlineModeToggle.setEnabled(true);
    }

    // =================
    // Properties Menu
    // =================

    private List<PropertiesObject> properties = new ArrayList<>();

    private void refreshMainWindowAffectedByProperties() {
        onlineModeToggle.setSelected(Main.data.getProperty("online-mode").equalsIgnoreCase("true"));
    }

    private void showPropertiesMenuSettings() {
        JFrame propertiesSettingsPanel = new JFrame("Properties Settings");
        propertiesSettingsPanel.setSize(480,420);
        propertiesSettingsPanel.setLocationRelativeTo(null);
        propertiesSettingsPanel.setResizable(false);
        propertiesSettingsPanel.setIconImage(global.appIMG);
        propertiesSettingsPanel.setLayout(null);
        propertiesSettingsPanel.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        // Search Bar
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setBounds(5,3,80,30);
        propertiesSettingsPanel.add(searchLabel);

        JTextField inputSearch = new JTextField();
        inputSearch.setBounds(52,3,410,30);

        // Properties
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(0,35,465,350);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        scrollPane.setViewportView(content);
        propertiesSettingsPanel.add(scrollPane);

        if (properties.isEmpty()) {
            Map<String, String> propertiesMap = Main.data.getPropertiesMap();

            for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                properties.add(new PropertiesObject(key, value));
            }

            for (PropertiesObject p : properties) {
                content.add(p.getObject());
            }
        }
        content.revalidate();
        content.repaint();
        //
        inputSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                String search = inputSearch.getText().toLowerCase();

                Component[] comps = content.getComponents();
                List<Component> list = new ArrayList<>(Arrays.asList(comps));

                list.sort((c1, c2) -> {
                    PropertiesObject p1 = (PropertiesObject) ((JComponent)c1).getClientProperty("prop");
                    PropertiesObject p2 = (PropertiesObject) ((JComponent)c2).getClientProperty("prop");

                    boolean m1 = p1.getPropertiesName().toLowerCase().contains(search);
                    boolean m2 = p2.getPropertiesName().toLowerCase().contains(search);

                    if (m1 && !m2) return -1;
                    if (m2 && !m1) return 1;

                    return p1.getPropertiesName().compareToIgnoreCase(p2.getPropertiesName());
                });

                content.removeAll();

                for (Component c : list) {
                    if (!search.isBlank()) {
                        PropertiesObject p = (PropertiesObject) ((JComponent) c).getClientProperty("prop");
                        JLabel nameLabel = (JLabel) ((JComponent) c).getClientProperty("nameLabel");


                        boolean match = p.getPropertiesName().toLowerCase().contains(search);

                        if (match) {
                            nameLabel.setOpaque(true);
                            nameLabel.setBackground(Color.ORANGE);
                        } else {
                            nameLabel.setOpaque(false);
                            nameLabel.setBackground(null);
                        }
                    }
                    content.add(c);
                }

                scrollPane.getVerticalScrollBar().setValue(0);
                content.revalidate();
                content.repaint();
            }
        });
        propertiesSettingsPanel.add(inputSearch);
        //
        propertiesSettingsPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                boolean isThereChange = false;
                for (PropertiesObject p : properties) {
                    if (p.isThereChange()) {
                        isThereChange = true;
                        break;
                    }
                }
                if (isThereChange) {
                    int result = JOptionPane.showConfirmDialog(
                            null,
                            "Do you want to save changes?",
                            "Confirm Save",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (result == JOptionPane.YES_OPTION) {
                        // Save changes
                        for (PropertiesObject p : properties) {
                            if (p.isThereChange()) {
                               Main.data.setProperty(p.getPropertiesName(),p.getValue());
                            }
                        }
                        AppUtils.saveServerData(Main.data);
                        refreshMainWindowAffectedByProperties();
                        if (Main.data.isServerRunning()) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Server is running. Restart the server for the changes to take place.",
                                    "Server is running",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }
                        //
                        propertiesSettingsPanel.dispose();
                    } else {
                        propertiesSettingsPanel.dispose();
                    }
                } else {
                    propertiesSettingsPanel.dispose();
                }
            }
        });
        //
        propertiesSettingsPanel.revalidate();
        propertiesSettingsPanel.repaint();
        propertiesSettingsPanel.setVisible(true);
    }

    // =================
    // Tool: Log Searcher
    // =================
    private void showToolLogSearcher() {
        JFrame toolLogSearcherPanel = new JFrame("Log Seacher");
        toolLogSearcherPanel.setSize(520,320);
        toolLogSearcherPanel.setLocationRelativeTo(null);
        toolLogSearcherPanel.setResizable(false);
        toolLogSearcherPanel.setIconImage(global.appIMG);
        toolLogSearcherPanel.setLayout(null);
        //
        JLabel inputFieldLabel = new JLabel("Filter:");
        inputFieldLabel.setBounds(5,5,200,30);
        toolLogSearcherPanel.add(inputFieldLabel);

        JTextField inputField = new JTextField();
        inputField.setBounds(40,5,380,30);
        inputField.setToolTipText("Use ',' to distinct keywords. For example: hi, can you, please");
        toolLogSearcherPanel.add(inputField);

        // Logs result
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(2,38,500,240);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JTextArea logs = new JTextArea();
        logs.setEditable(false);
        scrollPane.setViewportView(logs);
        toolLogSearcherPanel.add(scrollPane);
        //
        JButton startSearch = new JButton("Search");
        startSearch.setBounds(420,5,80,30);
        startSearch.setEnabled(false);
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                startSearch.setEnabled((inputField.getText().length() >= 2));
            }
        });
        startSearch.addActionListener(a -> {
            if (inputField.getText().isBlank()) return;
            logs.setText(""); // clear previous
            inputField.setEnabled(false);
            startSearch.setEnabled(false);
            new Thread(() ->{
               File logsPath = new File(serverPath + "\\\\logs");
               if (!logsPath.exists()) return;
               File[] files = logsPath.listFiles();
               if (files == null || files.length == 0) return;

               // Filter
               List<String> filteredKeyWords = new ArrayList<>(List.of(inputField.getText().split(",")));
               int totalFiles = files.length;
               int count = 0;
               for (File file : files ) {
                   String fileName = file.getName().toLowerCase();
                   List<String> lines;
                   count = count++;
                   toolLogSearcherPanel.setTitle("Log Searcher (" + count + "/" + totalFiles + ")");
                   boolean isThereResult = false;

                   // ZIP
                   if (fileName.endsWith(".zip")) {
                       lines = new ArrayList<>();
                       try (ZipFile zip = new ZipFile(file)) {
                           zip.stream().forEach(entry -> {

                               try (InputStream is = zip.getInputStream(entry);
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                                   String line;
                                   while ((line = reader.readLine()) != null) {
                                       lines.add(line);
                                   }

                               } catch (IOException e) {
                                   logs.append("Error reading file: " + e.getMessage());
                               }
                           });
                       } catch (IOException e) {
                           logs.append("Error reading .ZIP file: " + e.getMessage());
                       }
                   }

                   // GZ
                   else if (fileName.endsWith(".gz")) {
                       lines = new ArrayList<>();

                       try (FileInputStream fis = new FileInputStream(file);
                            GZIPInputStream gis = new GZIPInputStream(fis);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8))) {

                           String line;
                           while ((line = reader.readLine()) != null) {
                               lines.add(line);
                           }

                       } catch (IOException e) {
                           logs.append("Error reading .GZ file: " + e.getMessage());
                       }
                   }

                   // Normal txt
                   else if (fileName.endsWith(".log") || fileName.endsWith(".txt")) {
                       lines = FileService.readFile(file.getAbsolutePath())
                               .stream()
                               .map(o -> (String) o)
                               .toList();
                   } else {
                       lines = new ArrayList<>();
                   }

                   if (lines.isEmpty()) continue;

                   for (String line : lines) {
                       for (String filter : filteredKeyWords) {
                           if (line.toLowerCase().contains(filter.toLowerCase().trim().replaceAll(" ", ""))) {
                               if (!isThereResult) {
                                   logs.append("\n>>>>>>> " + file.getName() + "::\n");
                                   isThereResult = true;
                               }
                               logs.append("  " + line + "\n");
                               break;
                           }
                       }
                   }
               }
                inputField.setEnabled(true);
                startSearch.setEnabled(true);
                toolLogSearcherPanel.setTitle("Log Searcher");
            }).start();
        });
        toolLogSearcherPanel.add(startSearch);
        //
        toolLogSearcherPanel.revalidate();
        toolLogSearcherPanel.repaint();
        toolLogSearcherPanel.setVisible(true);
    }


}



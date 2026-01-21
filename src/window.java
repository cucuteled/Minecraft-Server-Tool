import data.NewServerForm;
import globl.Data;
import tools.AppUtils;
import tools.FileService;
import tools.TransferableImage;
import globl.global;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.List;

public class window {

    private JFrame frame = new JFrame("UMT |" + " build-(" + global.buildID + ")");
    private NewServerForm newserverform = new NewServerForm();

    public window() {
        // Set window propeties;
        frame.setResizable(false);
        frame.setIconImage(global.appIMG);
        // Set Sceen
        welcomeScreen();
        // Show window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // *****************************
    // Main Menu Window
    // *****************************
    public void welcomeScreen() {
        frame.getContentPane().removeAll();
        frame.setSize(400,360);
        frame.setLocationRelativeTo(null);
        //
        frame.getContentPane().removeAll();
        frame.setLayout(null);
        //
        JLabel welcomeLabel = new JLabel("<html><h1>Ultimate Minecraft Tool</h1></html>");
        welcomeLabel.setBounds(frame.getSize().width / 2 - 150,30,400,30);
        frame.add(welcomeLabel);
        // Buttons
        JButton hostButton = new JButton("Host Server");
        hostButton.addActionListener(e -> {
            hostWindow();
        });
        hostButton.setBounds(10,100,360,30);
        frame.add(hostButton);

        JButton screenshotButton = new JButton("Manage Screenshots");
        screenshotButton.addActionListener(e -> {
            // opens new scene
            screenshotWindow();
        });
        screenshotButton.setBounds(10,150,360,30);
        frame.add(screenshotButton);

        JButton openFolderButton = new JButton("Open Game Folder");
        openFolderButton.addActionListener(e -> {
            try { Process p = Runtime.getRuntime().exec("explorer.exe " + global.minecraftPath); } catch (Exception err) { }
        });
        openFolderButton.setBounds(10,200,360,30);
        frame.add(openFolderButton);
        // credit
        JLabel creditText = new JLabel("<html><i><b>developed by cucuteled</b></i></html>");
        creditText.setBounds(123,300,300,20);
        frame.add(creditText);
        //
        frame.repaint();
        frame.revalidate();
    }

    // *****************************
    // Manage Screenshots Window
    // *****************************
    public void screenshotWindow() {
        frame.getContentPane().removeAll();
        frame.setSize(765,450);
        frame.setLocationRelativeTo(null);
        //
        JLabel label = new JLabel("My Screenshots");
        label.setBounds(10,5,300,30);
        frame.add(label);

        JLabel backButton = new JLabel("""
                <html><a href="#">back to menu</a></html>""");
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                welcomeScreen();
            }
        });
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBounds(8,390,80,20);
        frame.add(backButton);
        // imgs Panel
        JPanel screenshotPanel = new JPanel();
        //screenshotPanel.setBounds(0,35,650,350);
        screenshotPanel.setLayout(
                new FlowLayout(FlowLayout.LEFT, 8, 8)
        );

        JScrollPane scrollPane = new JScrollPane(screenshotPanel);
        scrollPane.setBounds(0, 35, 750, 350);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        );
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        frame.add(scrollPane);
        Thread loadimgs = new Thread(() -> {
            File screenshotsPath = new File(global.minecraftPath + "\\screenshots\\");
            List<String> imgs;
            try {
                imgs = new ArrayList<>(List.of(Objects.requireNonNull(screenshotsPath.list())));
            } catch (Exception e) {
                return;
            }
            imgs = imgs.reversed();
            screenshotPanel.setPreferredSize(new Dimension(650,imgs.size() / 3 * 135));
            for (String img : imgs) {
                if (!img.endsWith(".png")) continue;
                String uri = new File(
                        screenshotsPath, img
                ).toURI().toString();
                JLabel pic = new JLabel(
                        "<html><img src=\"" + uri +
                                "\" width=\"230\" height=\"130\"/></html>"
                );
                pic.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                pic.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            showPopup(e);
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            showPopup(e);
                        }
                    }

                    private void showPopup(MouseEvent e) {
                        JPopupMenu popup = new JPopupMenu();

                        // --- OPEN ---
                        JMenuItem openItem = new JMenuItem("Open");
                        openItem.addActionListener(ae -> {
                            try {
                                ProcessBuilder pb = new ProcessBuilder(
                                        "cmd", "/c", "start " + screenshotsPath.getAbsolutePath() + "\\" + img
                                );
                                Process process = pb.start();
                            } catch (Exception err) { }
                        });

                        // --- COPY ---
                        JMenuItem copyItem = new JMenuItem("Copy");
                        copyItem.addActionListener(ae -> {
                            try {
                                File imgFile = new File(
                                        screenshotsPath, img
                                );

                                Image image = ImageIO.read(imgFile);

                                Toolkit.getDefaultToolkit()
                                        .getSystemClipboard()
                                        .setContents(
                                                new TransferableImage(image),
                                                null
                                        );
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });

                        // --- DELETE ---
                        JMenuItem deleteItem = new JMenuItem("Delete");
                        deleteItem.addActionListener(ae -> {
                            int result = JOptionPane.showConfirmDialog(
                                    frame,
                                    "Are you sure you want to delete this image?",
                                    "Confirm delete",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE
                            );

                            if (result == JOptionPane.YES_OPTION) {
                                File imgFile = new File(
                                        screenshotsPath, img
                                );
                                imgFile.delete();

                                screenshotPanel.remove(pic);
                                screenshotPanel.revalidate();
                                screenshotPanel.repaint();
                            }
                        });

                        popup.add(openItem);
                        popup.add(copyItem);
                        popup.addSeparator();
                        popup.add(deleteItem);

                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                });
                //
                screenshotPanel.add(pic);
            }
            screenshotPanel.revalidate();
            screenshotPanel.repaint();
        });
        loadimgs.start();
        //
        frame.repaint();
        frame.revalidate();
    }

    // *****************************
    // Host Server Window
    // *****************************
    public void hostWindow() {
        frame.getContentPane().removeAll();
        frame.setSize(384,400);
        frame.setLocationRelativeTo(null);
        //
        JLabel label = new JLabel("<html><b>Select a server to begin with:</b></html>");
        label.setBounds(20,20,200,30);
        frame.add(label);

        // Server List
        DefaultListModel<String> model = new DefaultListModel<>();

        for (String server : global.getMyServers()) {
            String[] data = server.split(";");
            model.addElement(data[0]);
        }

        JList<String> serverList = new JList<>(model);
        serverList.setCursor(new Cursor(Cursor.HAND_CURSOR));
        serverList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            public void showPopup(MouseEvent e) {
                if (!serverList.isSelectionEmpty()) {
                    JPopupMenu popup = new JPopupMenu();

                    JMenuItem deleteItem = new JMenuItem("Delete");
                    deleteItem.addActionListener(ae -> {
                        int result = JOptionPane.showConfirmDialog(
                                frame,
                                "Are you sure you want to delete this server? It will be permanently removed.",
                                "Confirm delete",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );

                        if (result == JOptionPane.YES_OPTION) {
                            Boolean isDeleteSuccess = false;
                            if (FileService.deleteServer(serverList.getSelectedValue())) {
                                isDeleteSuccess = true;
                                //
                                model.remove(model.indexOf(serverList.getSelectedValue()));
                                serverList.repaint();
                                serverList.revalidate();
                            }
                            String msg = isDeleteSuccess ? "Delete was successful." : "Unable to delete server.";
                            JOptionPane.showConfirmDialog(
                                    frame,
                                    msg,
                                    "Confirm delete",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }
                    });

                    popup.add(deleteItem);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });


        JScrollPane scrollPane = new JScrollPane(serverList);
        scrollPane.setBounds(20, 50, 330, 250);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(scrollPane);

        // select button & add new server button
        JButton selectButton = new JButton("Select");
        selectButton.setBounds(20,305,160,30);
        selectButton.addActionListener(e -> {
            String selected = serverList.getSelectedValue();
            for (String s : global.getMyServers()) {
                String[] parts = s.split(";");
                if (parts[0].equals(selected)) startServer(s);
            }
        });
        frame.add(selectButton);
        JButton addNewServerButton = new JButton("New Server");
        addNewServerButton.setBounds(190,305,160,30);
        addNewServerButton.addActionListener(e -> {
            newServer();
        });
        frame.add(addNewServerButton);

        // Back to menu page
        JLabel backButton = new JLabel("""
                <html><a href="#">back to menu</a></html>""");
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                welcomeScreen();
            }
        });
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBounds(143,340,80,20);
        frame.add(backButton);
        //
        frame.repaint();
        frame.revalidate();
    }

    private void startServer(String server) {
        Main.data = new Data(AppUtils.extractPath(server));
        openServerGUI();
    }

    // *****************************
    // New Server Window
    // *****************************
    private void newServer() {
        frame.getContentPane().removeAll();
        frame.setSize(420,500);
        frame.setLocationRelativeTo(null);
        // Create New Server
        JLabel label = new JLabel("""
                <html><h1>Create new server</h1>
                <br>
                <i>Server name:</i>
                <br><br><br><br><br>
                <i>Server path:</i>
                <br><br><br><br><br>
                Do you already have a server file?
                <br><br><br><br>
                </html>
                """);
        label.setBounds(10,-50,400,400);
        frame.add(label);
        //
        JTextField inputServerName = new PlaceholderTextField("e.g. My Server");
        inputServerName.setBounds(10,100,320,45);
        frame.add(inputServerName);
        JTextField inputServerPath = new PlaceholderTextField("e.g. C:\\myservers\\My Server 1");
        inputServerPath.setBounds(10,180,320,45);
        frame.add(inputServerPath);

        if (!newserverform.getServerName().isEmpty()) {
            inputServerName.setText(newserverform.getServerName());
            inputServerName.setForeground(Color.BLACK);
        }
        if (!newserverform.getServerPath().isEmpty()) {
            inputServerPath.setText(newserverform.getServerPath());
            inputServerPath.setForeground(Color.BLACK);
        } else {
                String defaultPath = System.getProperty("user.home") + "\\Documents";
                    inputServerPath.setText(defaultPath);
                    inputServerPath.setForeground(Color.BLACK);
        }

        // Path Selector
        JButton pathSelectorButton = new JButton("...");
        pathSelectorButton.setBounds(333,180,45,45);
        pathSelectorButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                inputServerPath.setText(selectedDir.getAbsolutePath());
                inputServerPath.setForeground(Color.BLACK);
            }
        });
        frame.add(pathSelectorButton);

        JCheckBox isServerFileExists = new JCheckBox("Yes, i do.");
        isServerFileExists.setBounds(10,250,200,45);
        frame.add(isServerFileExists);

        // Next Button
        JButton nextButton = new JButton("Next");
        nextButton.setVisible(false);
        nextButton.setBounds(315, 425, 80, 30);
        nextButton.addActionListener(e -> {
            if (validateNewServerData(inputServerName.getText(), inputServerPath.getText())) {
                newserverform.setServerName(inputServerName.getText());
                newserverform.setServerPath(inputServerPath.getText());
                if (isServerFileExists.isSelected()) {
                    // Skip version downloader
                    openServerGUI();
                } else {
                    // Open version downloader
                    selectVersionWindow();
                }
            }
        });
        frame.add(nextButton);

        // Add listeners
        DocumentListener validator = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validate();
            }

            private void validate() {
                nextButton.setVisible(
                        validateNewServerData(inputServerName.getText(), inputServerPath.getText())
                );
            }
        };

        inputServerName.getDocument().addDocumentListener(validator);
        inputServerPath.getDocument().addDocumentListener(validator);
        //
        // Back to menu page
        JLabel backButton = new JLabel("""
                <html><a href="#">back to menu</a></html>""");
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                welcomeScreen();
            }
        });
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBounds(5,440,80,20);
        frame.add(backButton);
        //
        nextButton.setVisible(
                validateNewServerData(inputServerName.getText(), inputServerPath.getText())
        );
        //
        frame.repaint();
        frame.revalidate();
    }

    // *****************************
    // Select Version Window
    // *****************************
    private void selectVersionWindow() {
        frame.getContentPane().removeAll();
        frame.setSize(340,420);
        frame.setLocationRelativeTo(null);
        // Select Version

        JLabel label = new JLabel("""
                <html><b>Select version from list:</b></html>
                """);
        label.setBounds(40,30,150,30);
        frame.add(label);

        // API: https://piston-meta.mojang.com/mc/game/version_manifest_v2.json
        Map<String,String> versions = AppUtils.getMCVersions();

        JComboBox<String> inputVersion = new JComboBox<>();
        for (String key : versions.keySet()) { inputVersion.addItem(key); }
        inputVersion.setBounds(40,65,240,40);
        frame.add(inputVersion);

        JLabel updateVersionList = new JLabel("""
                <html><a href="#">Help, i don't see newer versions here.</a></html>
                """);
        updateVersionList.setBounds(40,110,240,30);
        frame.add(updateVersionList);

        // Listeners
        updateVersionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Boolean result = AppUtils.updateLocalVersionList();
                JOptionPane.showMessageDialog(
                        null,
                        result ? "Update was successful" : "Error on updating!",
                        result ? "Success" : "Error",
                        JOptionPane.INFORMATION_MESSAGE
                );
                if (result) {
                    updateVersionList.setVisible(false);
                    // update list elements
                    Map<String,String> versions = AppUtils.getMCVersions();
                    inputVersion.removeAllItems();
                    for (String key : versions.keySet()) { inputVersion.addItem(key); }
                    inputVersion.repaint();
                    inputVersion.revalidate();
                }
            }
        });
        // Next
        JButton nextButton = new JButton("Select Version");
        nextButton.setBounds(40,160,240,30);
        nextButton.addActionListener(e -> {
            newserverform.setVersion(inputVersion.getSelectedItem().toString());
            // Download server and go to setup
            downloadServerFile(versions.get(newserverform.getVersion()));
        });
        inputVersion.addActionListener(e -> {
            nextButton.setVisible(inputVersion.getSelectedItem() != null);
        });
        frame.add(nextButton);
        // Back
        JLabel backButton = new JLabel("""
                <html><a href="#">back to previous</a></html>""");
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                newServer();
            }
        });
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBounds(5,360,160,20);
        frame.add(backButton);
        //
        frame.repaint();
        frame.revalidate();
    }

    private void downloadServerFile(String sha1) {
        if (AppUtils.downloadServerFile(sha1, newserverform)) {
            openGUIwithSave();
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while trying to download the file from the server.",
                    "Error while downloading",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void openGUIwithSave() {
        if (FileService.addNewServer(newserverform)) {
            Main.data = new Data(newserverform.getServerPath());
            openServerGUI();
        } else {
            JOptionPane.showMessageDialog(
                    null,
                "An error occurred while trying to save. Please try again!",
                    "Error while saving",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void openServerGUI() {
        Thread thread = new Thread(() -> {
            frame.dispose();
            Main.mainWindow = new serverGUI();
        });
        thread.start();
    }

    // validate New Server Data
    private static Boolean validateNewServerData(String name, String path) {
        if (name.startsWith("e.g. ")) return false;
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) { return false; }

        if (name == null || name.trim().length() < 1) {
            return false;
        }
        String invalidChars = "/\\\\:*?\"<>|";
        for (char c : invalidChars.toCharArray()) {
            if (name.indexOf(c) >= 0) {
                return false;
            }
        }

        return true;
    }

    // PlaceHolder Text Field
    public static class PlaceholderTextField extends JTextField {

        private String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;

            setForeground(Color.GRAY);

            this.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) {
                        setText("");
                        setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(Color.GRAY);
                    }
                }
            });

            setText(placeholder);
        }
    }


}


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.List;

public class window {

    private JFrame frame = new JFrame("UMT |" + " build-(" + global.buildID + ")");

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
            List<String> imgs = new ArrayList<>(List.of(Objects.requireNonNull(screenshotsPath.list())));
            imgs = imgs.reversed();
            screenshotPanel.setPreferredSize(new Dimension(650,imgs.size() / 3 * 135));
            for (String img : imgs) {
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
                screenshotPanel.revalidate();
                screenshotPanel.repaint();
            }
        });
        loadimgs.start();
        //
        frame.repaint();
        frame.revalidate();
    }

    public void hostWindow() {

    }

}


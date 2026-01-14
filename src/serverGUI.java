import globl.global;
import tools.AppUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

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
        frame.getContentPane().removeAll();
        frame.setSize(300,420);
        frame.setLocationRelativeTo(null);
        //
         //todo::
        //
        frame.repaint();
        frame.revalidate();
    }

    private void showMainWindow() {
        frame.getContentPane().removeAll();
        frame.setSize(340,420);
        frame.setLocationRelativeTo(null);
        //

        //
        frame.repaint();
        frame.revalidate();
    }
}

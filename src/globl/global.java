package globl;
import tools.FileService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class global {
    public static String buildID = "v1.0.0";
    public static Image appIMG = new ImageIcon("src/data/icon.png").getImage();
    public static String minecraftPath = System.getenv("APPDATA") + "\\.minecraft";

    public static List<String> getMyServers() {
        return (List<String>) FileService.readFile("src/data/myservers.txt");
    }
}

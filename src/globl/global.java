package globl;
import tools.FileService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class global {
    public static String buildID = "v1.0.0";
    public static Image appIMG = new ImageIcon("src/data/icon.png").getImage();
    public static String minecraftPath = System.getenv("APPDATA") + "\\.minecraft";

    public static List<String> getMyServers() {
        return FileService.readFile("src/data/myservers.txt")
                .stream()
                .map(o -> (String) o)
                .collect(Collectors.toList());
    }
}

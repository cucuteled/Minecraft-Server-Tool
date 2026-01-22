package globl;
import tools.FileService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class global {
    public static final String buildID = "v1.0.0";
    public static final Image appIMG = new ImageIcon("src/data/icon.png").getImage();
    public static final String minecraftPath = System.getenv("APPDATA") + "\\.minecraft";
    public static final String loadingIMGPath =
            new java.io.File("src/data/loading.gif").toURI().toString();
    public static final String emarkIMGPath = new java.io.File("src/data/emark.png").toURI().toString();
    public static final String openPortGuidePath = new File("src/data/openingPortGuide.pdf").getAbsolutePath();


    public static List<String> getMyServers() {
        return FileService.readFile("src/data/myservers.txt")
                .stream()
                .map(o -> (String) o)
                .collect(Collectors.toList());
    }
}

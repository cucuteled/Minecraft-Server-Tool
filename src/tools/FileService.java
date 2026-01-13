package tools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileService {

    // Read file and return lines in list
    public static List<?> readFile(String path) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {}
        return lines;
    }

    // Delete server
    public static Boolean deleteServer(String serverName) {
        for (String server : globl.global.getMyServers()) {
            if (serverName.equals(server.split(";")[0])) {
                String fileName = AppUtils.extractPath(server);
                if ( deleteDirectory(new File(fileName)) ) {
                    // delete from list
                    File serverstxt = new File("src/data/myservers.txt");
                    List<String> servers = new ArrayList<>(readFile(serverstxt.getPath())
                            .stream()
                            .map(o -> (String) o)
                            .toList());
                    servers.remove(server);
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(serverstxt))) {
                        String out = String.valueOf(servers);
                        out = out.replaceAll("\\[", "")
                                .replaceAll("]", "")
                                .replaceAll(", ", "\n");
                        bw.write(out);
                    } catch (Exception e) {}
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean deleteDirectory(File dir) {
        if (!dir.exists()) return false;

        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        return dir.delete();
    }


}

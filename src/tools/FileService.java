package tools;

import data.NewServerForm;
import globl.global;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

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
    // Write file and return boolean as success
    public static Boolean writeFile(File file, String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(data);
        } catch (Exception e) { return false; }
        return true;
    }
    // Decompress and read binary file
    public static byte[] readGzipBytes(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             GZIPInputStream gis = new GZIPInputStream(fis);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int len;

            while ((len = gis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }


    // Delete server
    public static Boolean deleteServer(String serverName) {
        for (String server : global.getMyServers()) {
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
                    String out = String.valueOf(servers);
                    out = out.replaceAll("\\[", "")
                            .replaceAll("]", "")
                            .replaceAll(", ", "\n");
                    return writeFile(serverstxt, out);
                }
            }
        }
        return false;
    }

    // ADD NEW SERVER to the list
    public static Boolean addNewServer(NewServerForm nsf) {
        File serverlist = new File("src/data/myservers.txt");
        String newServer = nsf.getServerName() + "; " +  nsf.getServerPath() + "\\" + nsf.getServerName() + ";" + nsf.getVersion();
        // CREATE FOLDER
        File destinationFolder = new File(nsf.getServerPath() + "\\" + nsf.getServerName());
        if (!destinationFolder.exists() && !destinationFolder.isDirectory()) {
            if (!destinationFolder.mkdirs()) return false;
        }
        //
        List<String> servers = new ArrayList<>((List<String>) readFile(serverlist.getPath())
                .stream()
                .map(o -> (String) o)
                .toList());
        servers.add(newServer);
        String out = "";
        for (String line : servers) {
            if (!line.trim().isBlank()) out += line + "\n";
        }
        return writeFile(serverlist, out);
    }

    // Recursive Directory element deleter for delet files
    private static boolean deleteDirectory(File dir) {
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

    // Download file from web
    public static void downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

}

package tools;

import data.NewServerForm;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class AppUtils {

    public static String normalizePath(String path) {
        return path
                .replaceAll("\\\\","/")
                .replaceFirst(" ", "");
    }

    public static String extractPath(String path) {
        return normalizePath(path.split(";")[1]);
    }

    public static String readURL(String webURL) {
        StringBuilder returnData = new StringBuilder();
        try {
            URL page = new URL(webURL);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(page.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                returnData.append(inputLine);
            in.close();
        } catch (Exception e) { return "err" + e.toString(); }
        return returnData.toString();
    }

    public static Map<String, String> getMCVersions() {
        Map<String, String> allVersion = new LinkedHashMap<>();
        String localData = FileService.readFile("src/data/localversiondata.txt").toString();
        // PARSING JSON
        String keyword = "";
        String bufferID = "";
        String bufferSHA1 = "";
        for (char pos : localData.toCharArray()) {
            if (keyword.contains("{\"id\":")) {
                bufferID += pos;
                if (bufferID.contains(",")) {
                    // ID EXTRACTED FROM LINE
                    bufferID = bufferID.replaceAll(" \"", "")
                            .replaceAll("\",", "");
                    keyword = "";
                }
            } else {
                if (keyword.contains("\"sha1\":")) {
                    // SHA1 EXTRACT FROM LINE
                    bufferSHA1 += pos;
                    if (bufferSHA1.contains("\",")) {
                        bufferSHA1 = bufferSHA1.replaceAll(" \"", "")
                                .replaceAll("\",", "");
                        keyword = "";
                        // AFTER EXTRACTION ADDING IT TO DICTIONARY
                        allVersion.put(bufferID, bufferSHA1);
                        bufferID = "";
                        bufferSHA1 = "";
                    }
                } else {
                    // NO KEYWORD PARSING FORWARD
                    keyword += pos;
                }
            }
        }
        return allVersion;
    }

    public static Boolean updateLocalVersionList() {
        String data = readURL("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("src/data/localversiondata.txt")))) {
            bw.write(data);
        } catch (Exception e) { return false; }
        return true;
    }

    public static Boolean downloadServerFile(String sh1, NewServerForm nsf) {
        // Retreive URL FROM PACKAGE JSON
        // example: https://piston-meta.mojang.com/v1/packages/30bb79802dcf36de95322ef6a055960c88131d2b/1.21.11.json
        String sh1server = "";
        String packagetext = readURL("https://piston-meta.mojang.com/v1/packages/" + sh1 + "/" + nsf.getVersion() + ".json");
        // Extract server URL from packagetext
        String keyword = "";
        boolean found = false;
        for (char pos : packagetext.toCharArray()) {
            if (found) break;
            if (keyword.contains("\"server\": {\"sha1\": \"")) {
                if (pos == '"') {
                    found = true;
                    break;
                } else {
                    sh1server += pos;
                }
            }
            keyword += pos;
        }
        //
        String URL = "https://piston-data.mojang.com/v1/objects/" + sh1server + "/server.jar";
        String filePath = nsf.getServerPath() + "\\" + nsf.getServerName();
        if (!new File(filePath).exists()) new File(filePath).mkdirs();
        filePath += "\\server.jar";
        try {
            FileService.downloadUsingStream(URL,  filePath);
        } catch (Exception e) { System.out.println(e);return false; }
        return true;
    }

    public static Boolean isEulaAccepted(String file) {
        File eulaFile = new File(file);
        if (!eulaFile.exists()) return false;
        return FileService.readFile(eulaFile.getAbsolutePath()).toString().contains("eula=true");
    }

    public static Boolean acceptEula(String filePath) {
        File eulaFile = new File(filePath);
        String out = "";
        if (!eulaFile.exists()) return false;
        List<String> data = FileService.readFile(filePath)
                .stream()
                .map(o -> (String) o)
                .toList();
        for (String line : data) {
            if (line.contains("eula=")) {
                out += "\neula=true";
            } else {
                out += line + "\n";
            }
        }
        return FileService.writeFile(eulaFile,out);
    }

}

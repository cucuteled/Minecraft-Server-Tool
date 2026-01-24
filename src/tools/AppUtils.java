package tools;

import data.NewServerForm;
import globl.Data;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class AppUtils {

    public static String normalizePath(String path) {
        return path
                .replaceAll("\\\\","/")
                .replaceFirst(" ", "");
    }

    public static String extractPath(String path) {
        return normalizePath(path.split(";")[1]);
    }

    public static String extractVersion(String path) throws Exception { return normalizePath(path.split(";")[2]); }

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

    public static Boolean isEulaAccepted(String serverPath) {
        File eulaFile = new File(serverPath + "\\\\eula.txt");
        if (!eulaFile.exists()) return false;
        return FileService.readFile(eulaFile.getAbsolutePath()).toString().toLowerCase().contains("eula=true");
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
                if (!line.isBlank()) {
                    out += line + "\n";
                }
            }
        }
        return FileService.writeFile(eulaFile,out);
    }

    public static String getExternalIp() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            String ip = in.readLine();
            return ip;
        } catch (Exception e) { throw new RuntimeException("Can't fetch ip"); }
    }

    public static String getInternalIp() {
        try {
            return InetAddress.getLocalHost().toString().split("/")[1];
        } catch (Exception e) {
            return "localhost";
        }
    }

    public static void validatePort(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }

        try (java.net.ServerSocket socket = new java.net.ServerSocket(port)) {
            socket.setReuseAddress(true);
        } catch (IOException e) {
            throw new IllegalStateException("Port " + port + " is already in use", e);
        }
    }

    public static boolean isPortOpen(String ip, int port, boolean isServerRunning) {
        AtomicReference<ServerSocket> serverRef = new AtomicReference<>();
        Thread serverThread = null;

        if (!isServerRunning) {
            try {
                ServerSocket ss = new ServerSocket(port);
                ss.setReuseAddress(true);
                serverRef.set(ss);

                serverThread = new Thread(() -> {
                    try {
                        while (!serverRef.get().isClosed()) {
                            serverRef.get().accept();
                        }
                    } catch (IOException ignored) {}
                });

                serverThread.setDaemon(true);
                serverThread.start();

                Thread.sleep(50);

            } catch (Exception e) {
                return false;
            }
        }

        boolean isOpen;
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), 1500);
            isOpen = true;
        } catch (IOException e) {
            isOpen = false;
        }

        ServerSocket ss = serverRef.get();
        if (ss != null) {
            try {
                ss.close();
            } catch (IOException ignored) {}
        }

        return isOpen;
    }

    public static void saveServerData(Data data) { // Properties txt file
        File propertiesFile = new File(data.getWorkingPath() + "\\\\server.properties");
        StringBuilder newProperties = new StringBuilder();
        List<String> currentProperties = FileService.readFile(propertiesFile.getAbsolutePath())
                .stream()
                .map(o -> (String) o)
                .toList();
        newProperties.append(currentProperties.get(0)).append("\n");
        newProperties.append(currentProperties.get(1)).append("\n");
        for (Map.Entry<String, String> entry : data.getPropertiesMap().entrySet()) {
            newProperties.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("\n");
        }
        FileService.writeFile(propertiesFile, newProperties.toString());
    }

    public static long getFolderSize(File folder) {
        long length = 0;

        File[] files = folder.listFiles();
        if (files == null) return 0;

        for (File file : files) {
            if (file.isFile()) {
                length += file.length();
            } else {
                length += getFolderSize(file);
            }
        }
        return length;
    }

    public static String getDataFromNBT(byte[] data, String dataName) {
        try {
            byte[] nameBytes = dataName.getBytes(StandardCharsets.UTF_8);

            for (int i = 0; i < data.length - nameBytes.length; i++) {

                boolean match = true;
                for (int j = 0; j < nameBytes.length; j++) {
                    if (data[i + j] != nameBytes[j]) {
                        match = false;
                        break;
                    }
                }

                if (!match) continue;

                int nameStart = i;

                int typeIdPos = nameStart - 3;
                int typeId = data[typeIdPos] & 0xFF;
                System.out.println(dataName + ": " + typeId);

                int valuePos = nameStart + nameBytes.length;

                switch (typeId) {
                    case 1: // TAG_Byte (Boolean)
                        return Byte.toString(data[valuePos]);

                    case 2: // TAG_Short
                        return Short.toString((short)(
                                ((data[valuePos] & 0xFF) << 8) |
                                        (data[valuePos+1] & 0xFF)
                        ));

                    case 3: // TAG_Int
                        return Integer.toString(
                                ((data[valuePos] & 0xFF) << 24) |
                                        ((data[valuePos+1] & 0xFF) << 16) |
                                        ((data[valuePos+2] & 0xFF) << 8) |
                                        (data[valuePos+3] & 0xFF)
                        );

                    case 4: // TAG_Long
                        return Long.toString(
                                ((long)(data[valuePos] & 0xFF) << 56) |
                                        ((long)(data[valuePos+1] & 0xFF) << 48) |
                                        ((long)(data[valuePos+2] & 0xFF) << 40) |
                                        ((long)(data[valuePos+3] & 0xFF) << 32) |
                                        ((long)(data[valuePos+4] & 0xFF) << 24) |
                                        ((long)(data[valuePos+5] & 0xFF) << 16) |
                                        ((long)(data[valuePos+6] & 0xFF) << 8) |
                                        ((long)(data[valuePos+7] & 0xFF))
                        );

                    case 5: // TAG_Float
                        int floatBits =
                                ((data[valuePos] & 0xFF) << 24) |
                                        ((data[valuePos+1] & 0xFF) << 16) |
                                        ((data[valuePos+2] & 0xFF) << 8) |
                                        (data[valuePos+3] & 0xFF);
                        return Float.toString(Float.intBitsToFloat(floatBits));

                    case 6: // TAG_Double
                        long doubleBits =
                                ((long)(data[valuePos] & 0xFF) << 56) |
                                        ((long)(data[valuePos+1] & 0xFF) << 48) |
                                        ((long)(data[valuePos+2] & 0xFF) << 40) |
                                        ((long)(data[valuePos+3] & 0xFF) << 32) |
                                        ((long)(data[valuePos+4] & 0xFF) << 24) |
                                        ((long)(data[valuePos+5] & 0xFF) << 16) |
                                        ((long)(data[valuePos+6] & 0xFF) << 8) |
                                        ((long)(data[valuePos+7] & 0xFF));
                        return Double.toString(Double.longBitsToDouble(doubleBits));
                    case 8:
                        int stringLength =
                                ((data[valuePos] & 0xFF) << 8) |
                                        (data[valuePos+1] & 0xFF);
                        valuePos += 2;
                        return new String(data, valuePos, stringLength, StandardCharsets.UTF_8);
                    case 11: // TAG_Int_Array
                        int arrayLength =
                                ((data[valuePos] & 0xFF) << 24) |
                                        ((data[valuePos+1] & 0xFF) << 16) |
                                        ((data[valuePos+2] & 0xFF) << 8) |
                                        (data[valuePos+3] & 0xFF);
                        int pos = valuePos + 4;

                        StringBuilder sb = new StringBuilder();
                        for (int k = 0; k < arrayLength; k++) {
                            int v =
                                    ((data[pos] & 0xFF) << 24) |
                                            ((data[pos+1] & 0xFF) << 16) |
                                            ((data[pos+2] & 0xFF) << 8) |
                                            (data[pos+3] & 0xFF);

                            sb.append(v).append(",");
                            pos += 4;
                        }
                        return sb.toString();
                }
            }

        } catch (Exception ignored) {}

        return "unknown";
    }

    public static byte[] getCompoundFromNBT(byte[] rawData, String compoundName) {
        try {
            byte[] nameBytes = compoundName.getBytes(StandardCharsets.UTF_8);

            for (int i = 0; i < rawData.length - nameBytes.length; i++) {
                boolean match = true;
                for (int j = 0; j < nameBytes.length; j++) {
                    if (rawData[i + j] != nameBytes[j]) {
                        match = false;
                        break;
                    }
                }
                if (!match) continue;

                int compoundNamePos = i;
                int typeIdPos = compoundNamePos - 3;
                int typeId = rawData[typeIdPos] & 0xFF;
                if (typeId != 0x0A) {
                    // not compound
                    continue;
                }

                int pos = compoundNamePos + nameBytes.length;

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    // Read until TAG_End (0x00)
                    while (pos < rawData.length) {
                        int tagType = rawData[pos] & 0xFF;
                        baos.write(tagType);
                        pos++;

                        if (tagType == 0x00) {
                            break;
                        }

                        int nameLength =
                                ((rawData[pos] & 0xFF) << 8) |
                                        (rawData[pos+1] & 0xFF);

                        // 2 is the bytes of nameLength; nameLength is the length of the tag Name
                        int dataLength = 2 + nameLength;
                        int valuePos = dataLength + pos;
                        switch (tagType) {
                            case 1: dataLength += 1; break;
                            case 2: dataLength += 2; break;
                            case 3: dataLength += 4; break;
                            case 4: dataLength += 8; break;
                            case 5: dataLength += 4; break;
                            case 6: dataLength += 8; break;
                            // Above is the following bytes of the tag type

                            case 7: { // TAG_Byte_Array
                                int len =
                                        ((rawData[valuePos] & 0xFF) << 24) |
                                                ((rawData[valuePos+1] & 0xFF) << 16) |
                                                ((rawData[valuePos+2] & 0xFF) << 8) |
                                                (rawData[valuePos+3] & 0xFF);

                                dataLength += 4 + len;
                                break;
                            }
                            case 8: { // TAG_String
                                int len =
                                        ((rawData[valuePos] & 0xFF) << 8) |
                                                (rawData[valuePos+1] & 0xFF);

                                dataLength += 2 + len;
                                break;
                            }
                            case 9: { // TAG_List
                                // tag list currently not supported
                                return new byte[0];
                            }
                            case 10:
                                // compound in compound currently not supported
                                return new byte[0];
                            case 11: { // TAG_Int_Array
                                int len =
                                        ((rawData[valuePos] & 0xFF) << 24) |
                                                ((rawData[valuePos+1] & 0xFF) << 16) |
                                                ((rawData[valuePos+2] & 0xFF) << 8) |
                                                (rawData[valuePos+3] & 0xFF);
                                dataLength += 4 + len * 4;
                                break;
                            }
                            case 12: { // TAG_Long_Array
                                int len =
                                        ((rawData[valuePos] & 0xFF) << 24) |
                                                ((rawData[valuePos+1] & 0xFF) << 16) |
                                                ((rawData[valuePos+2] & 0xFF) << 8) |
                                                (rawData[valuePos+3] & 0xFF);
                                dataLength += 4 + len * 8;
                                break;
                            }
                        }
                        baos.write(rawData, pos, dataLength);
                        pos += dataLength;
                    }
                    return baos.toByteArray();
                }
            }
        } catch (Exception ignored) {}
        return new byte[0];
    }




}

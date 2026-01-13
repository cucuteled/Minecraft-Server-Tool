package tools;

public class AppUtils {

    public static String normalizePath(String path) {
        return path
                .replaceAll("\\\\","/")
                .replaceFirst(" ", "");
    }

    public static String extractPath(String path) {
        return normalizePath(path.split(";")[1]);
    }

}

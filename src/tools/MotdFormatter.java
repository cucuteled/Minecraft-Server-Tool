package tools;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class MotdFormatter {

    private static final Random rnd = new Random();

    public static String formatMinecraftTextToHTML(String text) {
        StringBuilder out = new StringBuilder();
        Deque<String> closers = new ArrayDeque<>();
        return process(text, 0, out, closers, false);
    }

    private static String process(String text, int i, StringBuilder out, Deque<String> closers, boolean obf) {
        if (i >= text.length()) {
            while (!closers.isEmpty()) {
                out.append(closers.pop());
            }
            return out.toString();
        }

        char c = text.charAt(i);

        if (c == '&' && i + 1 < text.length()) {
            char code = Character.toLowerCase(text.charAt(i + 1));

            switch (code) {
                case '0': openColorSpan(out, closers, "#000000"); return process(text, i + 2, out, closers, false);
                case '1': openColorSpan(out, closers, "#0000AA"); return process(text, i + 2, out, closers, false);
                case '2': openColorSpan(out, closers, "#00AA00"); return process(text, i + 2, out, closers, false);
                case '3': openColorSpan(out, closers, "#00AAAA"); return process(text, i + 2, out, closers, false);
                case '4': openColorSpan(out, closers, "#AA0000"); return process(text, i + 2, out, closers, false);
                case '5': openColorSpan(out, closers, "#AA00AA"); return process(text, i + 2, out, closers, false);
                case '6': openColorSpan(out, closers, "#FFAA00"); return process(text, i + 2, out, closers, false);
                case '7': openColorSpan(out, closers, "#AAAAAA"); return process(text, i + 2, out, closers, false);
                case '8': openColorSpan(out, closers, "#555555"); return process(text, i + 2, out, closers, false);
                case '9': openColorSpan(out, closers, "#5555FF"); return process(text, i + 2, out, closers, false);
                case 'a': openColorSpan(out, closers, "#55FF55"); return process(text, i + 2, out, closers, false);
                case 'b': openColorSpan(out, closers, "#55FFFF"); return process(text, i + 2, out, closers, false);
                case 'c': openColorSpan(out, closers, "#FF5555"); return process(text, i + 2, out, closers, false);
                case 'd': openColorSpan(out, closers, "#FF55FF"); return process(text, i + 2, out, closers, false);
                case 'e': openColorSpan(out, closers, "#FFFF55"); return process(text, i + 2, out, closers, false);
                case 'f': openColorSpan(out, closers, "#FFFFFF"); return process(text, i + 2, out, closers, false);

                case 'l': openTag(out, closers, "<b>", "</b>"); return process(text, i + 2, out, closers, obf);
                case 'n': openTag(out, closers, "<u>", "</u>"); return process(text, i + 2, out, closers, obf);
                case 'o': openTag(out, closers, "<i>", "</i>"); return process(text, i + 2, out, closers, obf);
                case 'm': openTag(out, closers, "<s>", "</s>"); return process(text, i + 2, out, closers, obf);

                case 'k': return process(text, i + 2, out, closers, true);

                case 'r':
                    while (!closers.isEmpty()) out.append(closers.pop());
                    return process(text, i + 2, out, closers, false);

                default:
                    out.append(escapeHtml(c));
                    return process(text, i + 1, out, closers, obf);
            }
        }

        if (obf) {
            out.append(randomObfChar());
        } else {
            out.append(escapeHtml(c));
        }

        return process(text, i + 1, out, closers, obf);
    }

    private static void openColorSpan(StringBuilder out, Deque<String> closers, String color) {
        if (!closers.isEmpty() && "</span>".equals(closers.peek())) {
            out.append(closers.pop());
        }
        out.append("<span style=\"color:").append(color).append("\">");
        closers.push("</span>");
    }

    private static void openTag(StringBuilder out, Deque<String> closers, String open, String close) {
        out.append(open);
        closers.push(close);
    }

    private static char randomObfChar() {
        int base = 0x4E00;
        return (char) (base + rnd.nextInt(2000));
    }

    private static String escapeHtml(char c) {
        switch (c) {
            case '<': return "&lt;";
            case '>': return "&gt;";
            case '&': return "&amp;";
            case '"': return "&quot;";
            case '\'': return "&#39;";
            default: return String.valueOf(c);
        }
    }
}

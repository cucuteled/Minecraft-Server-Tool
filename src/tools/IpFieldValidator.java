package tools;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.regex.Pattern;

public class IpFieldValidator {

    private static final Pattern IP_PATTERN =
            Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){0,3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)?$");

    public static void apply(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {

                String newText = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()))
                        .replace(offset, offset + length, text)
                        .toString();

                if (IP_PATTERN.matcher(newText).matches()) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                    throws BadLocationException {

                String newText = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()))
                        .insert(offset, text)
                        .toString();

                if (IP_PATTERN.matcher(newText).matches()) {
                    super.insertString(fb, offset, text, attrs);
                }
            }
        });
    }
}
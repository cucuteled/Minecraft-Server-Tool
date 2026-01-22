import globl.global;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PropertiesObject {

    private String value;
    private String valueDefault;
    private final String propertiesName;

    public PropertiesObject(String propertiesName, String initValue) {
        this.value = initValue;
        this.propertiesName = propertiesName;
    }

    public String getValue() { return value; }

    public void setValue(String newValue) {
        Main.data.setProperty(propertiesName, newValue);
        this.value = newValue;
    }

    public boolean isThereChange() {
        return !value.equals(valueDefault);
    }

    public String getPropertiesName() { return propertiesName; }

    public JPanel getObject() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(465,50));
        //panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.putClientProperty("prop", this);
        //
        JLabel label = new JLabel("  " + this.propertiesName + ":   ");
        panel.putClientProperty("nameLabel", label);
        panel.add(label);

        JLabel image = new JLabel("""
                <html>
                    <img src="%s" width="20" height="30">
                </html>
                """.formatted(global.emarkIMGPath));
        image.setVisible(false);

        JTextField inputValue = new JTextField(this.value);
        inputValue.setPreferredSize(new Dimension(465 - label.getWidth() - 40,30));

        String defaultValue = this.value;
        valueDefault = defaultValue;
        inputValue.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                String current = inputValue.getText().trim().replaceAll(" ", "");
                String def = defaultValue.trim().replaceAll(" ", "");
                image.setVisible(!current.equalsIgnoreCase(def));
                value = inputValue.getText();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });
        //
        panel.add(inputValue);
        panel.add(image);
        //
        return panel;
    }
}

package com.meenigam.Components;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MultiInputDialog {

    public static Map<String, String> getUserInputs(ArrayList<String> inputfields) {
//        JTextField nameField = new JTextField(10);
//        JTextField ageField = new JTextField(10);
//        JTextField cityField = new JTextField(10);
        ArrayList<JTextField> textFields = new ArrayList<>();
        JPanel panel = new JPanel();

        for (String inputs : inputfields) {
            JTextField cur = new JTextField(10);
            textFields.add(cur);
            panel.add(new JLabel(inputs));
            panel.add(cur);
            panel.add(Box.createHorizontalStrut(15)); // Spacer
        }

//        panel.add(new JLabel("Name:"));
//        panel.add(nameField);
//        panel.add(Box.createHorizontalStrut(15)); // Spacer
//        panel.add(new JLabel("Age:"));
//        panel.add(ageField);
//        panel.add(Box.createHorizontalStrut(15)); // Spacer
//        panel.add(new JLabel("City:"));
//        panel.add(cityField);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Enter Details",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            // Create a map to store user inputs
            Map<String, String> userInputs = new HashMap<>();
//            userInputs.put("name", nameField.getText());
//            userInputs.put("age", ageField.getText());
//            userInputs.put("city", cityField.getText());
            for (int i = 0; i < inputfields.size(); i++) {
                userInputs.put(inputfields.get(i), textFields.get(i).getText());
            }
            return userInputs;
        }

        return null; // Return null if the user cancels or closes the dialog
    }

    public static void main(String[] args) {
        ArrayList<String> options = new ArrayList<>(Arrays.asList("Name", "age", "hello"));
        Map<String, String> inputs = MultiInputDialog.getUserInputs(options);

        if (inputs != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Name: " + inputs.get("name") +
                            "\nAge: " + inputs.get("age") +
                            "\nCity: " + inputs.get("city")
            );
        } else {
            JOptionPane.showMessageDialog(null, "No input provided.");
        }
    }
}
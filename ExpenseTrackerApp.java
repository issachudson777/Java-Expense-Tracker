import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ExpenseTrackerApp {
    // GUI Components
    private static JTextField amountField, categoryField;
    private static DefaultListModel<String> expenseListModel;
    private static JLabel totalLabel;
    private static JTextArea categoryTotalsArea;
    private static JList<String> expenseList;

    // Data Storage
    private static double total = 0;
    private static Map<String, Double> categoryTotals = new HashMap<>();

    public static void main(String[] args) {
        // 1. Create the main frame
        JFrame frame = new JFrame("Expense Tracker");
        frame.setSize(400, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 2. Top panel with FlowLayout for input fields and buttons
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField(8);
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField(8);
        inputPanel.add(categoryField);

        JButton addButton = new JButton("Add Expense");
        inputPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Expense");
        inputPanel.add(deleteButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        // 3. Center panel with a scrollable list of expenses
        expenseListModel = new DefaultListModel<>();
        expenseList = new JList<>(expenseListModel);
        JScrollPane scrollPane = new JScrollPane(expenseList);
        frame.add(scrollPane, BorderLayout.CENTER);

        // 4. Bottom panel with category totals and total label
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        categoryTotalsArea = new JTextArea(5, 30);
        categoryTotalsArea.setEditable(false);
        categoryTotalsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        bottomPanel.add(new JScrollPane(categoryTotalsArea), BorderLayout.NORTH);

        totalLabel = new JLabel("Total: $0.00");
        bottomPanel.add(totalLabel, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // 5. Add Expense Button Functionality
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String amountText = amountField.getText().trim();
                String category = categoryField.getText().trim();

                if (amountText.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both amount and category.");
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountText);

                    // Add to list
                    String entry = category + ": $" + String.format("%.2f", amount);
                    expenseListModel.addElement(entry);

                    // Update total
                    total += amount;
                    totalLabel.setText("Total: $" + String.format("%.2f", total));

                    // Update category total
                    categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
                    updateCategoryTotalsText();

                    // Clear input fields
                    amountField.setText("");
                    categoryField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid amount entered.");
                }
            }
        });

        // 6. Delete Expense Button Functionality
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = expenseList.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedItem = expenseListModel.getElementAt(selectedIndex);

                    // Example: "Food: $12.50"
                    String[] parts = selectedItem.split(": \\$");
                    if (parts.length == 2) {
                        String category = parts[0];
                        double amount = Double.parseDouble(parts[1]);

                        // Update total
                        total -= amount;
                        totalLabel.setText("Total: $" + String.format("%.2f", total));

                        // Update category total
                        categoryTotals.put(category, categoryTotals.get(category) - amount);
                        if (categoryTotals.get(category) <= 0.001) {
                            categoryTotals.remove(category);
                        }

                        updateCategoryTotalsText();
                    }

                    // Remove from list
                    expenseListModel.removeElementAt(selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an expense to delete.");
                }
            }
        });

        // 7. Show the frame
        frame.setVisible(true);
    }

    // Method to update category totals display
    private static void updateCategoryTotalsText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Category Totals:\n");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            sb.append(entry.getKey())
              .append(": $")
              .append(String.format("%.2f", entry.getValue()))
              .append("\n");
        }
        categoryTotalsArea.setText(sb.toString());
    }
}

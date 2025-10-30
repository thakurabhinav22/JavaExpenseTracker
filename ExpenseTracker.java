import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.border.*;
import java.util.List;

/**
 * ExpenseTracker - Budget-focused expense management application
 * Features: Date-based file storage, monthly budget tracking with auto-update
 */
public class ExpenseTracker extends JFrame {
    private static final String DATA_FOLDER = "data";
    private static final String BUDGET_FILE = "monthly_budget.txt";
   
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JTextField amountField, descField;
    private JComboBox<String> categoryCombo;
    private JLabel totalLabel, monthlyLabel, categoryWiseLabel, budgetLabel, remainingLabel;
    private JSpinner dateSpinner, filterFromSpinner, filterToSpinner;
    private double monthlyBudget = 0;
   
    private final Color PRIMARY = new Color(255, 153, 51);
    private final Color SECONDARY = new Color(249, 250, 251);
    private final Color ACCENT = new Color(19, 136, 8);
    private final Color DANGER = new Color(220, 38, 38);
    private final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private final Color BUDGET_COLOR = new Color(147, 51, 234);
   
    public ExpenseTracker() {
        setTitle("Expense Tracker");
        setSize(1350, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
       
        // Create data folder
        new File(DATA_FOLDER).mkdirs();
       
        loadMonthlyBudget();
        initComponents();
        loadExpenses();
        updateUI();
        setVisible(true);
    }
   
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(SECONDARY);
       
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
       
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(SECONDARY);
        mainPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
       
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);
       
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(SECONDARY);
       
        JPanel filterPanel = createFilterPanel();
        centerPanel.add(filterPanel, BorderLayout.NORTH);
       
        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);
       
        mainPanel.add(centerPanel, BorderLayout.CENTER);
       
        JPanel statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.SOUTH);
       
        add(mainPanel, BorderLayout.CENTER);
    }
   
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
       
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setBackground(PRIMARY);
       
        JLabel titleLabel = new JLabel("Expense Tracker Pro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
       
        JLabel subtitleLabel = new JLabel("Track expenses and manage your monthly budget");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 220, 180));
       
        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);
       
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(PRIMARY);
       
        JPanel budgetPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        budgetPanel.setBackground(new Color(255, 255, 255, 30));
        budgetPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
       
        JLabel budgetTitle = new JLabel("Monthly Budget");
        budgetTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        budgetTitle.setForeground(new Color(255, 220, 180));
       
        budgetLabel = new JLabel(monthlyBudget > 0 ? formatCurrency(monthlyBudget) : "Not Set");
        budgetLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        budgetLabel.setForeground(Color.WHITE);
       
        budgetPanel.add(budgetTitle);
        budgetPanel.add(budgetLabel);
       
        JButton budgetBtn = new JButton("Set Budget");
        budgetBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        budgetBtn.setBackground(Color.WHITE);
        budgetBtn.setForeground(PRIMARY);
        budgetBtn.setFocusPainted(false);
        budgetBtn.setBorderPainted(false);
        budgetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        budgetBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        budgetBtn.addActionListener(e -> setMonthlyBudget());
       
        rightPanel.add(budgetPanel);
        rightPanel.add(budgetBtn);
       
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
       
        return panel;
    }
   
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
       
        JLabel sectionLabel = new JLabel("Add New Expense");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionLabel.setForeground(TEXT_PRIMARY);
        panel.add(sectionLabel, BorderLayout.NORTH);
       
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
       
        // Date Spinner
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(TEXT_SECONDARY);
        fieldsPanel.add(dateLabel, gbc);
       
        gbc.gridx = 1; gbc.weightx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(dateSpinner, gbc);
       
        // Amount in INR
        gbc.gridx = 2; gbc.weightx = 0;
        JLabel amtLabel = new JLabel("Amount (INR):");
        amtLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        amtLabel.setForeground(TEXT_SECONDARY);
        fieldsPanel.add(amtLabel, gbc);
       
        gbc.gridx = 3; gbc.weightx = 1;
        amountField = new JTextField(12);
        styleTextField(amountField);
        fieldsPanel.add(amountField, gbc);
       
        // Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel catLabel = new JLabel("Category:");
        catLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        catLabel.setForeground(TEXT_SECONDARY);
        fieldsPanel.add(catLabel, gbc);
       
        gbc.gridx = 1; gbc.weightx = 1;
        String[] categories = {
            "Food & Dining", "Transport", "Groceries", "Bills & Utilities",
            "Entertainment", "Healthcare", "Clothing", "Education",
            "Rent/EMI", "Mobile/Internet", "Fuel", "Gifts",
            "Investment", "Other"
        };
        categoryCombo = new JComboBox<>(categories);
        styleComboBox(categoryCombo);
        fieldsPanel.add(categoryCombo, gbc);
       
        // Description
        gbc.gridx = 2; gbc.weightx = 0;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(TEXT_SECONDARY);
        fieldsPanel.add(descLabel, gbc);
       
        gbc.gridx = 3; gbc.weightx = 1.5;
        descField = new JTextField(20);
        styleTextField(descField);
        fieldsPanel.add(descField, gbc);
       
        // Add Button
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 0;
        JButton addBtn = createStyledButton("Add Expense", ACCENT, true);
        addBtn.addActionListener(e -> addExpense());
        fieldsPanel.add(addBtn, gbc);
       
        panel.add(fieldsPanel, BorderLayout.CENTER);
       
        return panel;
    }
   
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
       
        JLabel filterLabel = new JLabel("Filter by Date Range:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(TEXT_PRIMARY);
       
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fromLabel.setForeground(TEXT_SECONDARY);
       
        filterFromSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(filterFromSpinner, "dd-MM-yyyy");
        filterFromSpinner.setEditor(fromEditor);
        filterFromSpinner.setPreferredSize(new Dimension(150, 30));
       
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        toLabel.setForeground(TEXT_SECONDARY);
       
        filterToSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor toEditor = new JSpinner.DateEditor(filterToSpinner, "dd-MM-yyyy");
        filterToSpinner.setEditor(toEditor);
        filterToSpinner.setValue(new Date());
        filterToSpinner.setPreferredSize(new Dimension(150, 30));
       
        JButton filterBtn = createStyledButton("Apply Filter", PRIMARY, false);
        filterBtn.addActionListener(e -> filterByDateRange());
       
        JButton resetBtn = createStyledButton("Reset", TEXT_SECONDARY, false);
        resetBtn.addActionListener(e -> resetFilter());
       
        panel.add(filterLabel);
        panel.add(fromLabel);
        panel.add(filterFromSpinner);
        panel.add(toLabel);
        panel.add(filterToSpinner);
        panel.add(filterBtn);
        panel.add(resetBtn);
       
        return panel;
    }
   
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
       
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
       
        JLabel sectionLabel = new JLabel("Expense History");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(sectionLabel, BorderLayout.WEST);
       
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setBackground(Color.WHITE);
       
        JButton deleteBtn = createStyledButton("Delete Selected", DANGER, false);
        deleteBtn.addActionListener(e -> deleteExpense());
       
        JButton clearBtn = createStyledButton("Clear All", DANGER, false);
        clearBtn.addActionListener(e -> clearAllExpenses());
       
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);
        headerPanel.add(btnPanel, BorderLayout.EAST);
       
        panel.add(headerPanel, BorderLayout.NORTH);
       
        String[] columns = {"Date", "Amount (INR)", "Category", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
       
        expenseTable = new JTable(tableModel);
        styleTable(expenseTable);
       
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(new LineBorder(new Color(229, 231, 235), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
       
        return panel;
    }
   
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(SECONDARY);
       
        JPanel totalCard = createStatCard("Total Expenses", "INR 0.00", PRIMARY);
        totalLabel = (JLabel) ((JPanel) totalCard.getComponent(1)).getComponent(0);
        panel.add(totalCard);
       
        JPanel monthlyCard = createStatCard("This Month", "INR 0.00", ACCENT);
        monthlyLabel = (JLabel) ((JPanel) monthlyCard.getComponent(1)).getComponent(0);
        panel.add(monthlyCard);
       
        JPanel remainingCard = createStatCard("Budget Remaining", "INR 0.00", BUDGET_COLOR);
        remainingLabel = (JLabel) ((JPanel) remainingCard.getComponent(1)).getComponent(0);
        panel.add(remainingCard);
       
        JPanel categoryCard = createStatCard("Top Category", "-", DANGER);
        categoryWiseLabel = (JLabel) ((JPanel) categoryCard.getComponent(1)).getComponent(0);
        panel.add(categoryCard);

         JPanel deatilCard = createStatCard("By", "SE-IT-A 65-70", DANGER);
        categoryWiseLabel = (JLabel) ((JPanel) categoryCard.getComponent(1)).getComponent(0);
        panel.add(deatilCard);
       
        return panel;
    }
   
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
       
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_SECONDARY);
        card.add(titleLabel, BorderLayout.NORTH);
       
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setBackground(Color.WHITE);
       
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valuePanel.add(valueLabel);
       
        card.add(valuePanel, BorderLayout.CENTER);
       
        return card;
    }
   
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(209, 213, 219), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
    }
   
    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
    }
   
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(255, 237, 213));
        table.setSelectionForeground(TEXT_PRIMARY);
       
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(new LineBorder(new Color(229, 231, 235), 1));
       
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
    }
   
    private JButton createStyledButton(String text, Color bg, boolean large) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, large ? 13 : 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(large ? 12 : 8, large ? 24 : 16, large ? 12 : 8, large ? 24 : 16));
       
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
       
        return btn;
    }
   
    private void setMonthlyBudget() {
        String input = JOptionPane.showInputDialog(this,
            "Enter your monthly budget (INR):",
            "Set Monthly Budget",
            JOptionPane.PLAIN_MESSAGE);
       
        if (input != null && !input.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(input.replace(",", ""));
                if (amount >= 0) {
                    monthlyBudget = amount;
                    saveMonthlyBudget();
                    updateUI();
                    JOptionPane.showMessageDialog(this,
                        String.format("Monthly budget set to %s", formatCurrency(monthlyBudget)),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showError("Budget cannot be negative!");
                }
            } catch (NumberFormatException ex) {
                showError("Invalid amount format!");
            }
        }
    }
   
    private String formatCurrency(double amount) {
        // Format with Indian numbering system
        String formatted = String.format("%.2f", Math.abs(amount));
        String[] parts = formatted.split("\\.");
        String integerPart = parts[0];
        String decimalPart = parts[1];
       
        // Add commas in Indian format
        StringBuilder result = new StringBuilder();
        int len = integerPart.length();
       
        if (len <= 3) {
            result.append(integerPart);
        } else {
            // Last 3 digits
            result.insert(0, integerPart.substring(len - 3));
            int remaining = len - 3;
           
            // Add groups of 2 digits
            while (remaining > 0) {
                int start = Math.max(0, remaining - 2);
                result.insert(0, ",");
                result.insert(0, integerPart.substring(start, remaining));
                remaining = start;
            }
        }
       
        return (amount < 0 ? "-INR " : "INR ") + result + "." + decimalPart;
    }
   
    private String getDateFileName(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return DATA_FOLDER + "/expense_" + sdf.format(date) + ".txt";
    }
   
    private void addExpense() {
        try {
            String amtText = amountField.getText().trim();
            String desc = descField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            Date selectedDate = (Date) dateSpinner.getValue();
           
            if (amtText.isEmpty() || desc.isEmpty() || selectedDate == null) {
                showError("Please fill all fields!");
                return;
            }
           
            double amount = Double.parseDouble(amtText.replace(",", ""));
            if (amount <= 0) {
                showError("Amount must be positive!");
                return;
            }
           
            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(selectedDate);
           
            tableModel.addRow(new Object[]{
                date,
                String.format("%,.2f", amount),
                category,
                desc
            });
           
            // Save to date-specific file
            saveExpenseToDateFile(selectedDate, date, amount, category, desc);
           
            // Auto-update UI
            updateUI();
           
            amountField.setText("");
            descField.setText("");
            categoryCombo.setSelectedIndex(0);
            dateSpinner.setValue(new Date());
           
            // Check budget warning
            double monthlyTotal = getMonthlyTotal();
            if (monthlyBudget > 0 && monthlyTotal > monthlyBudget) {
                double exceeded = monthlyTotal - monthlyBudget;
                JOptionPane.showMessageDialog(this,
                    String.format("Budget Alert!\nMonthly expenses (%s) exceeded budget (%s)\nOver budget by: %s",
                    formatCurrency(monthlyTotal), formatCurrency(monthlyBudget), formatCurrency(exceeded)),
                    "Budget Exceeded", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    String.format("Expense added successfully!\nAmount: %s", formatCurrency(amount)),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
           
        } catch (NumberFormatException ex) {
            showError("Invalid amount format!");
        }
    }
   
    private void saveExpenseToDateFile(Date date, String dateTime, double amount, String category, String desc) {
        try {
            String fileName = getDateFileName(date);
            String line = dateTime + "|" + amount + "|" + category + "|" + desc;
           
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                writer.write(line);
                writer.newLine();
            }
        } catch (Exception ex) {
            showError("Error saving expense: " + ex.getMessage());
        }
    }
   
    private void filterByDateRange() {
        Date from = (Date) filterFromSpinner.getValue();
        Date to = (Date) filterToSpinner.getValue();
       
        if (from == null || to == null) {
            showError("Please select both dates!");
            return;
        }
       
        if (from.after(to)) {
            showError("'From' date must be before 'To' date!");
            return;
        }
       
        tableModel.setRowCount(0);
        loadExpensesInRange(from, to);
        updateUI();
       
        JOptionPane.showMessageDialog(this,
            tableModel.getRowCount() + " expenses found in date range",
            "Filter Result", JOptionPane.INFORMATION_MESSAGE);
    }
   
    private void resetFilter() {
        filterFromSpinner.setValue(new Date());
        filterToSpinner.setValue(new Date());
        tableModel.setRowCount(0);
        loadExpenses();
        updateUI();
    }
   
    private void deleteExpense() {
        int row = expenseTable.getSelectedRow();
        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this expense?",
                "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(row);
                saveAllExpenses();
                updateUI();
               
                JOptionPane.showMessageDialog(this,
                    "Expense deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            showError("Please select an expense to delete!");
        }
    }
   
    private void clearAllExpenses() {
        if (tableModel.getRowCount() > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete all expenses?",
                "Confirm Clear All", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
           
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.setRowCount(0);
                saveAllExpenses();
                updateUI();
               
                JOptionPane.showMessageDialog(this,
                    "All expenses cleared successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            showError("No expenses to clear!");
        }
    }
   
    private double getMonthlyTotal() {
        double monthlyTotal = 0;
        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);
       
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String dateStr = tableModel.getValueAt(i, 0).toString().split(" ")[0];
            String amtStr = tableModel.getValueAt(i, 1).toString().replace(",", "");
           
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date expenseDate = sdf.parse(dateStr);
                Calendar expCal = Calendar.getInstance();
                expCal.setTime(expenseDate);
               
                if (expCal.get(Calendar.MONTH) == currentMonth &&
                    expCal.get(Calendar.YEAR) == currentYear) {
                    monthlyTotal += Double.parseDouble(amtStr);
                }
            } catch (Exception ex) {
                // Ignore date parsing errors
            }
        }
        return monthlyTotal;
    }
   
    private void updateUI() {
        double total = 0;
        double monthlyTotal = 0;
        Map<String, Double> categoryMap = new HashMap<>();
       
        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);
       
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String amtStr = tableModel.getValueAt(i, 1).toString().replace(",", "");
            String category = tableModel.getValueAt(i, 2).toString();
            String dateStr = tableModel.getValueAt(i, 0).toString().split(" ")[0];
           
            double amount = Double.parseDouble(amtStr);
            total += amount;
           
            categoryMap.put(category, categoryMap.getOrDefault(category, 0.0) + amount);
           
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date expenseDate = sdf.parse(dateStr);
                Calendar expCal = Calendar.getInstance();
                expCal.setTime(expenseDate);
               
                if (expCal.get(Calendar.MONTH) == currentMonth &&
                    expCal.get(Calendar.YEAR) == currentYear) {
                    monthlyTotal += amount;
                }
            } catch (Exception ex) {
                // Ignore date parsing errors
            }
        }
       
        // Update total expenses
        totalLabel.setText(formatCurrency(total));
       
        // Update monthly expenses
        monthlyLabel.setText(formatCurrency(monthlyTotal));
       
        // Update monthly label color based on budget
        if (monthlyBudget > 0 && monthlyTotal > monthlyBudget) {
            monthlyLabel.setForeground(DANGER);
        } else {
            monthlyLabel.setForeground(ACCENT);
        }
       
        // Update budget display in header
        if (monthlyBudget > 0) {
            budgetLabel.setText(formatCurrency(monthlyBudget));
        } else {
            budgetLabel.setText("Not Set");
        }
       
        // Update top category
        if (!categoryMap.isEmpty()) {
            String topCategory = categoryMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
            categoryWiseLabel.setText(topCategory);
        } else {
            categoryWiseLabel.setText("-");
        }

        /* ---------- Budget Remaining Calculation & Color ---------- */
        if (monthlyBudget > 0) {
            double remaining = monthlyBudget - monthlyTotal;
            remainingLabel.setText(formatCurrency(remaining));

            if (remaining < 0) {
                remainingLabel.setForeground(DANGER);
            } else if (remaining < monthlyBudget * 0.2) {
                remainingLabel.setForeground(PRIMARY);
            } else {
                remainingLabel.setForeground(BUDGET_COLOR);
            }
        } else {
            remainingLabel.setText("Not Set");
            remainingLabel.setForeground(BUDGET_COLOR);
        }
        /* ---------------------------------------------------------- */
    }
   
    private void saveAllExpenses() {
        // Group expenses by date and save to respective files
        Map<String, List<String[]>> expensesByDate = new HashMap<>();
       
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String dateTime = tableModel.getValueAt(i, 0).toString();
            String amount = tableModel.getValueAt(i, 1).toString().replace(",", "");
            String category = tableModel.getValueAt(i, 2).toString();
            String desc = tableModel.getValueAt(i, 3).toString();
           
            String dateOnly = dateTime.split(" ")[0];
           
            expensesByDate.computeIfAbsent(dateOnly, k -> new ArrayList<>())
                          .add(new String[]{dateTime, amount, category, desc});
        }
       
        // Clear all existing files in data folder
        File dataFolder = new File(DATA_FOLDER);
        if (dataFolder.exists()) {
            File[] files = dataFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("expense_")) {
                        file.delete();
                    }
                }
            }
        }
       
        // Save expenses to date-specific files
        for (Map.Entry<String, List<String[]>> entry : expensesByDate.entrySet()) {
            String dateStr = entry.getKey();
            List<String[]> expenses = entry.getValue();
           
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date date = sdf.parse(dateStr);
                String fileName = getDateFileName(date);
               
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    for (String[] expense : expenses) {
                        String line = expense[0] + "|" + expense[1] + "|" + expense[2] + "|" + expense[3];
                        writer.write(line);
                        writer.newLine();
                    }
                }
            } catch (Exception ex) {
                showError("Error saving expenses: " + ex.getMessage());
            }
        }
    }
   
    private void loadExpenses() {
        File dataFolder = new File(DATA_FOLDER);
        if (!dataFolder.exists()) return;
       
        File[] files = dataFolder.listFiles((dir, name) -> name.startsWith("expense_") && name.endsWith(".txt"));
        if (files == null) return;
       
        for (File file : files) {
            loadExpensesFromFile(file);
        }
    }
   
    private void loadExpensesInRange(Date from, Date to) {
        File dataFolder = new File(DATA_FOLDER);
        if (!dataFolder.exists()) return;
       
        File[] files = dataFolder.listFiles((dir, name) -> name.startsWith("expense_") && name.endsWith(".txt"));
        if (files == null) return;
       
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
       
        for (File file : files) {
            try {
                // Extract date from filename: expense_DD-MM-YYYY.txt
                String fileName = file.getName();
                String dateStr = fileName.substring(8, fileName.length() - 4);
                Date fileDate = sdf.parse(dateStr);
               
                // Check if file date is within range
                if (!fileDate.before(from) && !fileDate.after(to)) {
                    loadExpensesFromFile(file);
                }
            } catch (Exception ex) {
                // Skip files with invalid date format
            }
        }
    }
   
    private void loadExpensesFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        tableModel.addRow(new Object[]{
                            parts[0],
                            String.format("%,.2f", Double.parseDouble(parts[1])),
                            parts[2],
                            parts[3]
                        });
                    }
                } catch (Exception ex) {
                    // Skip corrupted lines
                }
            }
        } catch (Exception ex) {
            // Skip files with errors
        }
    }
   
    private void saveMonthlyBudget() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BUDGET_FILE))) {
            writer.write(String.valueOf(monthlyBudget));
        } catch (Exception ex) {
            showError("Error saving monthly budget: " + ex.getMessage());
        }
    }
   
    private void loadMonthlyBudget() {
        File file = new File(BUDGET_FILE);
        if (!file.exists()) {
            monthlyBudget = 0;
            return;
        }
       
        try (BufferedReader reader = new BufferedReader(new FileReader(BUDGET_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                monthlyBudget = Double.parseDouble(line.trim());
            }
        } catch (Exception ex) {
            monthlyBudget = 0;
        }
    }
   
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
   
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Use default look and feel
        }
       
        SwingUtilities.invokeLater(() -> new ExpenseTracker());
    }
}
package CLI;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenseTrackerCLI {
    private static final Scanner sc = new Scanner(System.in);
    private static final String BASE_DIR = "CLI"; // store data inside CLI folder
    private static final String BUDGET_FILE = BASE_DIR + "/budget.txt";

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n========= EXPENSE TRACKER CLI =========");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. Set Monthly Budget");
            System.out.println("4. View Summary");
            System.out.println("5. Delete Expense by Date");
            System.out.println("6. Clear All Expenses");
            System.out.println("7. Clear Screen");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> addExpense();
                case "2" -> viewExpenses();
                case "3" -> setBudget();
                case "4" -> viewSummary();
                case "5" -> deleteExpense();
                case "6" -> clearAllExpenses();
                case "7" -> clearScreen();
                case "8" -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                // case "9" -> addDummyData();
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    // 🧾 ADD EXPENSE
    private static void addExpense() {
        try {
            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(sc.nextLine());
            System.out.print("Enter category: ");
            String category = sc.nextLine();
            System.out.print("Enter description: ");
            String description = sc.nextLine();

            String filename = getDateFileName(new Date());
            File dir = new File(BASE_DIR);
            if (!dir.exists()) dir.mkdirs();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(BASE_DIR + "/" + filename, true))) {
                String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
                bw.write(date + "|" + amount + "|" + category + "|" + description);
                bw.newLine();
            }

            System.out.println(" Expense added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding expense: " + e.getMessage());
        }
    }

    // 📄 VIEW EXPENSES
    private static void viewExpenses() {
        try {
            File dir = new File(BASE_DIR);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
            if (files == null || files.length == 0) {
                System.out.println("No expenses found.");
                return;
            }

            for (File file : files) {
                System.out.println("\n📅 File: " + file.getName());
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split("\\|");
                        if (parts.length == 4)
                            System.out.printf("%s | ₹%s | %s | %s%n", parts[0], parts[1], parts[2], parts[3]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading expenses: " + e.getMessage());
        }
    }

    // 💰 SET BUDGET
    private static void setBudget() {
        try {
            System.out.print("Enter monthly budget: ");
            double budget = Double.parseDouble(sc.nextLine());

            File dir = new File(BASE_DIR);
            if (!dir.exists()) dir.mkdirs();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(BUDGET_FILE))) {
                bw.write(String.valueOf(budget));
            }

            System.out.println(" Monthly budget set successfully!");
        } catch (Exception e) {
            System.out.println("Error setting budget: " + e.getMessage());
        }
    }

    // 📊 VIEW SUMMARY
    private static void viewSummary() {
        try {
            double total = 0;
            File dir = new File(BASE_DIR);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] parts = line.split("\\|");
                            if (parts.length >= 2) total += Double.parseDouble(parts[1]);
                        }
                    }
                }
            }

            double budget = 0;
            File bFile = new File(BUDGET_FILE);
            if (bFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(bFile))) {
                    budget = Double.parseDouble(br.readLine());
                }
            }

            System.out.println("\n========= SUMMARY =========");
            System.out.println("Total Expenses: ₹" + total);
            System.out.println("Monthly Budget: ₹" + budget);
            System.out.println("Remaining: ₹" + (budget - total));
        } catch (Exception e) {
            System.out.println("Error viewing summary: " + e.getMessage());
        }
    }

    // 🗑 DELETE EXPENSE BY DATE
    private static void deleteExpense() {
        System.out.print("Enter filename (e.g., 30-10-2025.txt): ");
        String filename = sc.nextLine();
        File file = new File(BASE_DIR + "/" + filename);
        if (file.exists() && file.delete()) {
            System.out.println(" File deleted successfully!");
        } else {
            System.out.println(" File not found!");
        }
    }

    //  CLEAR ALL EXPENSES
    private static void clearAllExpenses() {
        System.out.print("Are you sure you want to delete all expense files? (yes/no): ");
        if (!sc.nextLine().equalsIgnoreCase("yes")) return;

        File dir = new File(BASE_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File f : files) f.delete();
        }

        System.out.println(" All expenses cleared!");
    }

    // 🧹 CLEAR SCREEN
    private static void clearScreen() {
        try {
                System.out.print("\033[H\033[2J");            
        } catch (Exception e) {
            System.out.println("Unable to clear screen!");
        }
    }


    // 📅 Generate daily expense filename
    private static String getDateFileName(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy").format(date) + ".txt";
    }
}

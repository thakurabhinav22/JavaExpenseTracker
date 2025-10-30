# Expense Tracker 
*SE-IT-A | Roll No: 66-70*  

---

## Overview  

A **desktop-based expense management application** built with **Java Swing**.  
It helps you track daily expenses, categorize them, set a **monthly budget**, and get instant visual feedback on spending.

**Key Features**

| Feature | Description |
|---------|-------------|
| **Date-based storage** | Each day's expenses are saved in a separate file (`data/expense_DD-MM-YYYY.txt`). |
| **Monthly Budget** | Set a budget once – the app warns you when you exceed it. |
| **Live Stats** | Total, monthly, remaining budget, and top-spending category. |
| **Smart Filtering** | View expenses for any custom date range. |
| **Delete / Clear** | Remove single entries or wipe all data with confirmation. |
| **Indian Currency Formatting** | `INR 1,23,456.78` style (lakhs/crores). |
| **Responsive UI** | Clean, modern look with hover effects and color-coded alerts. |


---

## How to Run  

### Prerequisites  
- **Java 8 or higher** (JDK)
- Any IDE (IntelliJ, Eclipse, VS Code) or command line

### Steps  

1. **Clone the repository**  
   ```bash
   git clone https://github.com/thakurabhinav22/JavaExpenseTracker
   cd ExpenseTracker
   javac ExpenseTracker.java
   java ExpenseTracker

import java.until.Scanner;
public class minibank
{
    record BankInfo(String name, String accountNumber, double balance) 
    {
    }
    enum MenuOPtions 
    {  
       OPEN_ACCOUNT,
            DEPOSIT, 
           WITHDRAW, 
      CHECK_BALANCE, 
            EXIT
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankInfo bankInfo = null;
        boolean exit = false;

        while (!exit) {
            System.out.println("Welcome to MiniBank!");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check Balance");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter your name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter account number: ");
                    String accountNumber = scanner.nextLine();
                    bankInfo = new BankInfo(name, accountNumber, 0.0);
                    System.out.println("Account opened successfully!");
                    break;
                case 2:
                    if (bankInfo != null) {
                        System.out.print("Enter deposit amount: ");
                        double depositAmount = scanner.nextDouble();
                        bankInfo = new BankInfo(bankInfo.name(), bankInfo.accountNumber(), bankInfo.balance() + depositAmount);
                        System.out.println("Deposited: " + depositAmount);
                    } else {
                        System.out.println("No account found. Please open an account first.");
                    }
                    break;
                case 3:
                    if (bankInfo != null) {
                        System.out.print("Enter withdrawal amount: ");
                        double withdrawAmount = scanner.nextDouble();
                        if (withdrawAmount <= bankInfo.balance()) {
                            bankInfo = new BankInfo(bankInfo.name(), bankInfo.accountNumber(), bankInfo.balance() - withdrawAmount);
                            System.out.println("Withdrew: " + withdrawAmount);
                        } else {
                            System.out.println("Insufficient balance.");
                        }
                    } else {
                        System.out.println("No account found. Please open an account first.");
                    }
                    break;
                case 4:
                    if (bankInfo != null) {
                        System.out.println("Current balance: " + bankInfo.balance());
                    } else {
                        System.out.println("No account found. Please open an account first.");
                    }
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();

}
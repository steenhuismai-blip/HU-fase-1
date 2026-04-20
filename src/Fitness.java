import java.util.Scanner;
import java.util.function.Predicate;

public class Fitness {

    private final ConsoleUtils consoleUtils = new ConsoleUtils();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Fitness app = new Fitness();
            app.run(scanner);
        }
    }

    public void run(Scanner scanner) {
        boolean running = true;

        while (running) {
            ConsoleUtils.clearConsole();
            System.out.println(ConsoleUI.PURPLE + "Welcome to your fitness tool!" + ConsoleUI.RESET);
            System.out.println(ConsoleUI.PURPLE + "\n--- Choose your role: ---" + ConsoleUI.RESET);
            System.out.println(colorMenuNumber(1) + " Existing Customer");
            System.out.println(colorMenuNumber(2) + " Employee");
            System.out.println(colorMenuNumber(3) + " New Customer");
            System.out.println(ConsoleUI.RED + "0. Exit" + ConsoleUI.RESET);
            System.out.print("Your choice: ");

            String input = scanner.nextLine();
            int role;

            try {
                role = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Please enter a number (0-3).");
                consoleUtils.pauseForEnter(scanner);
                continue;
            }

            switch (role) {
                case 1:
                    handleExistingCustomer(scanner);
                    break;
                case 2:
                    handleEmployee(scanner);
                    break;
                case 3:
                    handleNewCustomer(scanner);
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    printError("Please choose 1, 2, or 3.");
                    consoleUtils.pauseForEnter(scanner);
                    break;
            }
        }
    }

    // ---------------- Helper methods ----------------

    private String colorMenuNumber(int number) {
        return ConsoleUI.CYAN + number + "." + ConsoleUI.RESET;
    }

    private void printError(String message) {
        System.out.println(ConsoleUI.RED + message + ConsoleUI.RESET);
    }

    private void printPasswordHint() {
        System.out.println(ConsoleUI.YELLOW + "Hint: The default password is 'password123'" + ConsoleUI.RESET);
    }

    private void showBmi(Customer customer) {
        System.out.println("Your current BMI: " + customer.getBmiFormatted());
    }

    private void handleLogin(
            Scanner scanner,
            Predicate<String> authenticator,
            Runnable onSuccess,
            String failureMessage
    ) {
        ConsoleUtils.clearConsole();
        int failedAttempts = 0;
        boolean hintShown = false;

        while (true) {
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            if (authenticator.test(password)) {
                onSuccess.run();
                return;
            }

            failedAttempts++;
            if (failedAttempts == 3 && !hintShown) {
                printPasswordHint();
                hintShown = true;
                failedAttempts = 0;
            } else {
                System.out.println(ConsoleUI.RED + failureMessage + ConsoleUI.RESET);
            }
        }
    }

    // ---------------- Customer handling ----------------

    private void handleExistingCustomer(Scanner scanner) {
        Customer existingCustomer = Customer.loadFromFile();

        if (existingCustomer == null) {
            System.out.println(ConsoleUI.YELLOW + "⚠ No saved customer data found. Please create a new customer first." + ConsoleUI.RESET);
            consoleUtils.pauseForEnter(scanner);
            return;
        }

        handleLogin(
            scanner,
            password -> "password123".equals(password),
            () -> {
                System.out.println(ConsoleUI.GREEN + "Welcome back, " + existingCustomer.getName() + "!" + ConsoleUI.RESET);
                consoleUtils.pauseForEnter(scanner);
                ConsoleUtils.clearConsole();
                new Menu().showMenu(scanner, existingCustomer);
            },
            "Incorrect password. Try again."
        );
    }

    private void handleEmployee(Scanner scanner) {
        Employee employee = new Employee("", "password123");

        handleLogin(
            scanner,
            employee::login,
            () -> {
                System.out.println(ConsoleUI.GREEN + "Welcome back!" + ConsoleUI.RESET);
                consoleUtils.pauseForEnter(scanner);
                ConsoleUtils.clearConsole();
                System.out.println("No employee menu defined. Returning to role selection...");
                consoleUtils.pauseForEnter(scanner);
            },
            "Incorrect password. Access denied."
        );
    }

    private void handleNewCustomer(Scanner scanner) {
        ConsoleUtils.clearConsole();
        Customer newCustomer = new Customer();
        newCustomer.fillPersonalInfo(scanner);

        // Show BMI immediately after filling info
        showBmi(newCustomer);

        // Save customer data to JSON
        newCustomer.saveToFile();

        consoleUtils.pauseForEnter(scanner);
        ConsoleUtils.clearConsole();

        new Menu().showMenu(scanner, newCustomer);

        System.out.println("\nReturning to role selection...");
        consoleUtils.pauseForEnter(scanner);
    }
}



     


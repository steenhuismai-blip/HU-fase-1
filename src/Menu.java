import java.util.Scanner;

public class Menu {

    public void showMenu(Scanner scanner, Customer customer) {
        int choice = 0;

        while (choice != 6) { // loop until user selects exit (6)
            System.out.println(ConsoleUI.PURPLE + "\n--- Menu: ---" + ConsoleUI.RESET);
            System.out.println(ConsoleUI.CYAN + "1." + ConsoleUI.RESET + "Show Personal Data");
            System.out.println(ConsoleUI.CYAN + "2." + ConsoleUI.RESET + "Library Exercises");
            System.out.println(ConsoleUI.CYAN + "3." + ConsoleUI.RESET + "Pre-made Week Planning");
            System.out.println(ConsoleUI.CYAN + "4." + ConsoleUI.RESET + "Customizable Week Planning");
            System.out.println(ConsoleUI.CYAN + "5." + ConsoleUI.RESET + "Log individual Exercises");
            System.out.println(ConsoleUI.CYAN + "6." + ConsoleUI.RESET + "Exit");
            System.out.print("Your choice: ");

            String input = scanner.nextLine();
            try {
                choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        customer.showInfo();

                        boolean validResponse = false;
                        while (!validResponse) {
                            System.out.print("\nDo you want to edit any info? (yes/no): ");
                            String response = scanner.nextLine().trim().toLowerCase();

                            if (response.equals("yes")) {
                                validResponse = true;
                                customer.reviewAndEditInfo(scanner);
                                ConsoleUtils.clearConsole();
                            } else if (response.equals("no")) {
                                validResponse = true;
                                ConsoleUtils.clearConsole();
                            } else {
                                System.out.println("Please type 'yes' or 'no'.");
                            }
                        }
                        break;

                    case 2:
                        new LibraryExercisesMenu().show(scanner);
                        break;

                    case 3:
                        new WeekPlanningMenu().show(scanner, customer);
                        break;

                    case 4:
                        ConsoleUtils.clearConsole();
                        new CustomizableWeekPlanningMenu().show(scanner);
                        break;

                    case 5:
                        ConsoleUtils.clearConsole();
                        new LogExercisesMenu().show(scanner);
                        break;

                    case 6:
                        System.out.println("Goodbye!");
                        break;

                    default:
                        System.out.println("Please enter a number from 1 to 6.");
                        choice = 0; // reset invalid choice
                        break;
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a number from 1 to 6.");
            }
        }
    }
}


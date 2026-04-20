import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LogExercisesMenu {

    private final List<Exercise> savedExercises = new ArrayList<>();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            LogExercisesMenu menu = new LogExercisesMenu();
            menu.show(scanner);
        }
    }

    public void show(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println(ConsoleUI.PURPLE + "\n--- Log Individual Exercises ---" + ConsoleUI.RESET);
            System.out.println(ConsoleUI.CYAN + "1)" + ConsoleUI.RESET + " Lower body");
            System.out.println(ConsoleUI.CYAN + "2)" + ConsoleUI.RESET + " Upper body");
            System.out.println(ConsoleUI.CYAN + "3)" + ConsoleUI.RESET + " Core");
            System.out.println(ConsoleUI.CYAN + "4)" + ConsoleUI.RESET + " View saved exercises");
            System.out.println(ConsoleUI.CYAN + "5)" + ConsoleUI.RESET + " Exit to main menu");
            System.out.print("Choose an option (1-5): ");

            String choice = scanner.nextLine().trim();
            ConsoleUtils.clearConsole();

            switch (choice) {
                case "1":
                    logExercise(scanner, ConsoleUI.PURPLE + "Lower body" + ConsoleUI.RESET);
                    break;
                case "2":
                    logExercise(scanner, ConsoleUI.PURPLE + "Upper body" + ConsoleUI.RESET);
                    break;
                case "3":
                    logExercise(scanner, ConsoleUI.PURPLE + "Core" + ConsoleUI.RESET);
                    break;
                case "4":
                    viewSavedExercises(scanner);
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println(ConsoleUI.RED + "Invalid choice — please enter a number from 1 to 5." + ConsoleUI.RESET);
            }
        }

        System.out.println("Exiting Log Exercises Menu. Goodbye!");
        System.out.println("Press ENTER to finish...");
        scanner.nextLine();
        ConsoleUtils.clearConsole(); // <- direct call
    }

    private void logExercise(Scanner scanner, String category) {
        System.out.println("\n--- Log: " + category + " ---");

        System.out.print("What did you do? (exercise name): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Empty name — returning to category menu.");
            return;
        }

        Integer times = null;
        while (times == null) {
            System.out.print("How many times (enter a full number, e.g. repetitions or sets): ");
            String line = scanner.nextLine().trim();
            try {
                times = Integer.parseInt(line);
                if (times < 0) {
                    System.out.println("Please enter zero or a positive whole number.");
                    times = null;
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleUI.RED + "Invalid number. Try again." + ConsoleUI.RESET);
            }
        }

        Double weight = null;
        while (weight == null) {
            System.out.print("Weight used (kg) — enter 0 if only bodyweight was used / not applicable: ");
            String line = scanner.nextLine().trim();
            try {
                weight = Double.parseDouble(line);
                if (weight < 0) {
                    System.out.println("Please enter zero or a positive whole number.");
                    weight = null;
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleUI.RED + "Invalid number. Try again." + ConsoleUI.RESET);
            }
        }

        Exercise draft = new Exercise(category, name, times, weight, LocalDateTime.now());
        System.out.println("\nYou entered: \n" + draft.toMultilineString(dtf));

        boolean decisionLoop = true;
        while (decisionLoop) {
            System.out.print("Save? (yes / no / exit to category menu): ");
            String decision = scanner.nextLine().trim().toLowerCase();
            switch (decision) {
                case "yes":
                case "y":
                    savedExercises.add(draft);
                    System.out.println(ConsoleUI.GREEN + "Exercise saved." + ConsoleUI.RESET);
                    decisionLoop = false;
                    break;
                case "no":
                case "n":
                    System.out.println(ConsoleUI.RED + "Not saved." + ConsoleUI.RESET);
                    decisionLoop = false;
                    break;
                case "exit":
                    System.out.println("Exiting to category menu.");
                    decisionLoop = false;
                    break;
                default:
                    System.out.println("Please type 'yes', 'no' or 'exit'.");
            }
        }
        ConsoleUtils.clearConsole();
    }

    private void viewSavedExercises(Scanner scanner) {
        System.out.println(ConsoleUI.PURPLE + "\n--- Saved Exercises ---" + ConsoleUI.RESET);
        if (savedExercises.isEmpty()) {
            System.out.println("No saved exercises yet.");
        } else {
            for (int i = 0; i < savedExercises.size(); i++) {
                Exercise e = savedExercises.get(i);
                System.out.println((i + 1) + ") " + e.summaryString(dtf));
            }

            boolean inSavedMenu = true;
            while (inSavedMenu) {
                System.out.println(ConsoleUI.PURPLE + "\nOptions:" + ConsoleUI.RESET);
                System.out.println(ConsoleUI.PURPLE + "Enter exercise " + ConsoleUI.RESET 
                     + ConsoleUI.CYAN + "number " + ConsoleUI.RESET 
                    + ConsoleUI.PURPLE + "to: " + ConsoleUI.RESET
                   + "edit/delete");

                System.out.println(ConsoleUI.PURPLE + "Press " + ConsoleUI.RESET 
                     + ConsoleUI.CYAN + "ENTER " + ConsoleUI.RESET 
                     + ConsoleUI.PURPLE + "to return to: " + ConsoleUI.RESET 
                     + "category menu");

                System.out.print("Choice: ");
                String opt = scanner.nextLine().trim();

                if (opt.isEmpty()) {
                    inSavedMenu = false;
                } else {
                    try {
                        int selected = Integer.parseInt(opt) - 1;
                        if (selected >= 0 && selected < savedExercises.size()) {
                            Exercise e = savedExercises.get(selected);

                            System.out.println("\nSelected Exercise:");
                            System.out.println(e.toMultilineString(dtf));
                            System.out.print("Do you want to edit or delete this exercise? (edit/delete/exit): ");
                            String action = scanner.nextLine().trim().toLowerCase();

                            switch (action) {
                                case "edit":
                                    System.out.print(ConsoleUI.PURPLE + "What did you do? (exercise name): " + ConsoleUI.RESET);
                                    String newName = scanner.nextLine().trim();
                                    if (!newName.isEmpty()) e.name = newName;

                                    System.out.print(ConsoleUI.PURPLE + "How many times (enter a full number, e.g. repetitions or sets): " + ConsoleUI.RESET);
                                    String timesStr = scanner.nextLine().trim();
                                    if (!timesStr.isEmpty()) {
                                        try { e.times = Integer.parseInt(timesStr); } 
                                        catch (NumberFormatException ex) { System.out.println("Invalid number, keeping old value."); }
                                    }

                                    System.out.print(ConsoleUI.PURPLE + "Weight used (kg) — enter 0 if bodyweight / not applicable: " + ConsoleUI.RESET);
                                    String weightStr = scanner.nextLine().trim();

                                    if (!weightStr.isEmpty()) {
                                        try { e.weight = Double.parseDouble(weightStr); } 
                                        catch (NumberFormatException ex) { System.out.println("Invalid number, keeping old value."); }
                                    }

                                    System.out.println(ConsoleUI.GREEN + "Exercise updated." + ConsoleUI.RESET);
                                    break;

                                case "delete":
                                    savedExercises.remove(selected);
                                    System.out.println(ConsoleUI.RED + "Exercise deleted." + ConsoleUI.RESET);
                                    break;

                                case "exit":
                                    break;

                                default:
                                    System.out.println("Invalid option. Returning to saved exercises menu.");
                            }

                        } else {
                            System.out.println("Number out of range.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");
                    }
                }
            }
        }

    }

    private static class Exercise {
        String category;
        String name;
        int times;
        double weight;
        final LocalDateTime timestamp;

        Exercise(String category, String name, int times, double weight, LocalDateTime timestamp) {
            this.category = category;
            this.name = name;
            this.times = times;
            this.weight = weight;
            this.timestamp = timestamp;
        }

        String summaryString(DateTimeFormatter dtf) {
            return String.format("[%s] %s — %d times, %.2f kg", dtf.format(timestamp), name, times, weight);
        }

String toMultilineString(DateTimeFormatter dtf) {
    return ConsoleUI.CYAN + "Category: " + ConsoleUI.RESET + category + "\n" +
           ConsoleUI.CYAN + "Exercise: " + ConsoleUI.RESET + name + "\n" +
           ConsoleUI.CYAN + "Times: " + ConsoleUI.RESET + times + "\n" +
           ConsoleUI.CYAN + "Weight: " + ConsoleUI.RESET + weight + " kg\n" +
           ConsoleUI.CYAN + "When: " + ConsoleUI.RESET + dtf.format(timestamp);
}

    }
}

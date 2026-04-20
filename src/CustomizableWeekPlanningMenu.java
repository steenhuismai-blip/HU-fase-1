import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CustomizableWeekPlanningMenu {

    private static final List<String> VALID_DAYS = Arrays.asList(
        "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    );

    private final Map<String, List<String>> regularLibrary;
    private final Map<String, List<String>> advancedLibrary;

    public CustomizableWeekPlanningMenu() {
        // Mock library of exercises
        regularLibrary = new HashMap<>();
        regularLibrary.put("Lower body", Arrays.asList("Squats", "Lunges", "Leg Press"));
        regularLibrary.put("Upper body", Arrays.asList("Push-ups", "Pull-ups", "Dumbbell Rows"));
        regularLibrary.put("Core", Arrays.asList("Plank", "Crunches", "Bicycle Crunches"));

        advancedLibrary = new HashMap<>();
        advancedLibrary.put("Lower body", Arrays.asList("Pistol Squats", "Jump Squats"));
        advancedLibrary.put("Upper body", Arrays.asList("Handstand Push-ups", "Weighted Pull-ups"));
        advancedLibrary.put("Core", Arrays.asList("Dragon Flag", "Hanging Leg Raises"));
    }

    public void show(Scanner scanner) {
        int choice = 0;
        while (choice != 3) {
            ConsoleUtils.clearConsole();
            System.out.println(ConsoleUI.PURPLE + "\n--- Customizable Week Planning ---" + ConsoleUI.RESET);
            System.out.println(ConsoleUI.CYAN + "1." + ConsoleUI.RESET + " Single Day");
            System.out.println(ConsoleUI.CYAN + "2." + ConsoleUI.RESET + " Multi Days");
            System.out.println(ConsoleUI.CYAN + "3." + ConsoleUI.RESET + " Exit");
            System.out.print(ConsoleUI.PURPLE + "Your choice: " + ConsoleUI.RESET);

            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> handleSingleDay(scanner);
                    case 2 -> handleMultiDays(scanner);
                    case 3 -> {
                        ConsoleUtils.clearConsole();
                        System.out.println("Returning to main menu...");
                    }
                    default -> System.out.println(ConsoleUI.RED + "Please enter a number from 1 to 3." + ConsoleUI.RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleUI.RED + "Please enter a valid number (1-3)." + ConsoleUI.RESET);
            }
        }
    }

    private void handleSingleDay(Scanner scanner) {
        while (true) {
            System.out.print("Select a day (Mon, Tue, Wed, Thu, Fri, Sat, Sun) or type 'exit': ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) return;

            String day = normalizeDay(input);
            if (day != null) {
                ConsoleUtils.clearConsole();
                System.out.println("You selected: " + day);
                planWorkout(scanner, day);
                break;
            } else {
                System.out.println(ConsoleUI.RED + "Invalid day" + ConsoleUI.RESET + ". Try again.");
            }
        }
    }

    private void handleMultiDays(Scanner scanner) {
        while (true) {
            System.out.print("Enter days separated by commas (e.g., Mon,Wed,Fri) or type 'exit': ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) return;

            String[] days = input.split(",");
            List<String> selectedDays = new ArrayList<>();
            List<String> invalidDays = new ArrayList<>();

            for (String d : days) {
                String day = normalizeDay(d);
                if (day != null && !selectedDays.contains(day)) {
                    selectedDays.add(day);
                } else if (day == null) {
                    invalidDays.add(d.trim());
                }
            }

            if (!invalidDays.isEmpty()) {
                System.out.println(ConsoleUI.RED + "Invalid days: " + String.join(", ", invalidDays) + ConsoleUI.RESET);
                continue;
            }

            if (!selectedDays.isEmpty()) {
                ConsoleUtils.clearConsole();
                System.out.println("You selected: " + String.join(", ", selectedDays));
                for (String day : selectedDays) planWorkout(scanner, day);
                break;
            } else {
                System.out.println(ConsoleUI.RED + "Invalid input. Try again." + ConsoleUI.RESET);
            }
        }
    }

    private void planWorkout(Scanner scanner, String day) {
        Map<String, List<String>> chosenExercises = new LinkedHashMap<>();
        Map<String, Integer> progress = new LinkedHashMap<>();
        List<String> categories = Arrays.asList("Lower body", "Upper body", "Core");

        // Select categories
        boolean doneSelecting = false;
        while (!doneSelecting) {
            ConsoleUtils.clearConsole();
            System.out.println("\nChoose categories to train (comma-separated numbers), type 'done' to finish selection or 'exit':");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i));
            }
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) return;
            if (input.equalsIgnoreCase("done")) break;

            String[] choices = input.split(",");
            for (String c : choices) {
                try {
                    int idx = Integer.parseInt(c.trim()) - 1;
                    if (idx >= 0 && idx < categories.size()) {
                        String category = categories.get(idx);
                        List<String> exercises = chooseExercises(scanner, category, chosenExercises.getOrDefault(category, new ArrayList<>()));
                        if (!exercises.isEmpty()) {
                            chosenExercises.put(category, exercises);
                            progress.put(category, 0);
                        } else {
                            chosenExercises.remove(category);
                            progress.remove(category);
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // Overview loop
        boolean inOverview = true;
        while (inOverview) {
            showOverview(chosenExercises, progress, day);
            System.out.println("\nOptions: 'start', 'edit', 'reset', or 'exit'");
            System.out.print("> ");
            String action = scanner.nextLine().trim().toLowerCase();

            switch (action) {
                case "start" -> {
                    boolean anyRemaining = chosenExercises.entrySet().stream()
                        .anyMatch(e -> progress.getOrDefault(e.getKey(), 0) < e.getValue().size());
                    if (!anyRemaining) {
                        System.out.println("All selected exercises are already completed. Use 'reset' or 'edit'.");
                        break;
                    }
                    boolean completedAll = startWorkout(scanner, chosenExercises, progress);
                    if (completedAll) {
                        System.out.println("\n✔ All categories completed for " + day + "!");
                        inOverview = false;
                    } else {
                        System.out.println("\nWorkout paused. Returning to overview...");
                    }
                }
                case "edit" -> handleEditMenu(scanner, chosenExercises, progress, categories);
                case "reset" -> handleResetMenu(scanner, chosenExercises, progress);
                case "exit" -> {
                    return;
                }
                default -> System.out.println("Invalid option. Try 'start', 'edit', 'reset', or 'exit'.");
            }
        }
    }

    private void handleEditMenu(Scanner scanner, Map<String, List<String>> chosenExercises, Map<String, Integer> progress, List<String> categories) {
        System.out.println("Type category name to edit, 'add' to choose more, or 'back':");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("back")) return;

        if (input.equalsIgnoreCase("add")) {
            System.out.println("Choose categories to add/edit (comma-separated numbers) or 'back':");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i));
            }
            System.out.print("Your choice: ");
            String catChoice = scanner.nextLine().trim();
            if (catChoice.equalsIgnoreCase("back")) return;
            String[] picks = catChoice.split(",");
            for (String p : picks) {
                try {
                    int idx = Integer.parseInt(p.trim()) - 1;
                    if (idx >= 0 && idx < categories.size()) {
                        String category = categories.get(idx);
                        List<String> exercises = chooseExercises(scanner, category, chosenExercises.getOrDefault(category, new ArrayList<>()));
                        if (!exercises.isEmpty()) {
                            chosenExercises.put(category, exercises);
                            progress.put(category, 0);
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        } else {
            for (String cat : categories) {
                if (cat.equalsIgnoreCase(input)) {
                    List<String> exercises = chooseExercises(scanner, cat, chosenExercises.getOrDefault(cat, new ArrayList<>()));
                    if (!exercises.isEmpty()) {
                        chosenExercises.put(cat, exercises);
                        progress.put(cat, 0);
                    }
                    return;
                }
            }
            System.out.println("Unknown category name.");
        }
    }

    private void handleResetMenu(Scanner scanner, Map<String, List<String>> chosenExercises, Map<String, Integer> progress) {
        System.out.println("Type category name to reset progress, 'all' to reset everything, or 'back':");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("back")) return;
        if (input.equalsIgnoreCase("all")) {
            for (String cat : chosenExercises.keySet()) progress.put(cat, 0);
            System.out.println("All progress reset.");
        } else {
            for (String cat : chosenExercises.keySet()) {
                if (cat.equalsIgnoreCase(input)) {
                    progress.put(cat, 0);
                    System.out.println("Progress reset for " + cat);
                    return;
                }
            }
            System.out.println("No matching category to reset.");
        }
    }

    private void showOverview(Map<String, List<String>> chosenExercises, Map<String, Integer> progress, String day) {
        ConsoleUtils.clearConsole();
        System.out.println(ConsoleUI.PURPLE + "\n--- Overview for " + day + " ---" + ConsoleUI.RESET);
        if (chosenExercises.isEmpty()) {
            System.out.println("(No exercises selected yet)");
            return;
        }

        int totalExercises = 0;
        int totalCompleted = 0;
        for (Map.Entry<String, List<String>> entry : chosenExercises.entrySet()) {
            String cat = entry.getKey();
            List<String> list = entry.getValue();
            int done = Math.min(progress.getOrDefault(cat, 0), list.size());
            totalExercises += list.size();
            totalCompleted += done;
            System.out.println("\n" + cat + " — " + done + "/" + list.size() + " completed");
            for (int i = 0; i < list.size(); i++) {
                String marker = (i < done) ? "[x] " : "[ ] ";
                System.out.println("  " + marker + (i + 1) + ". " + list.get(i));
            }
        }
        System.out.println(ConsoleUI.GREEN + "\nTotal: " + totalCompleted + " / " + totalExercises + " exercises completed" + ConsoleUI.RESET);
    }

    private boolean startWorkout(Scanner scanner, Map<String, List<String>> exercises, Map<String, Integer> progress) {
        for (String category : exercises.keySet()) {
            List<String> list = exercises.get(category);
            if (list == null || list.isEmpty()) continue;
            int startIndex = progress.getOrDefault(category, 0);
            if (startIndex >= list.size()) continue;

            for (int i = startIndex; i < list.size(); i++) {
                ConsoleUtils.clearConsole();
                int doneSoFar = progress.getOrDefault(category, 0);
                System.out.println(ConsoleUI.PURPLE + "=== Category: " + category + " ===" + ConsoleUI.RESET);
                System.out.println("Progress: " + doneSoFar + " / " + list.size() + "\n");
                System.out.println("Exercise " + (i + 1) + " / " + list.size() + ":");
                System.out.println(">>>  " + list.get(i) + "  <<<");
                System.out.println("\nPress ENTER when done, 'skip' to skip, or 'exit' to pause.");
                System.out.print("> ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    progress.put(category, i);
                    return false;
                } else {
                    progress.put(category, i + 1);
                }
            }

            progress.put(category, list.size());
            ConsoleUtils.clearConsole();
            System.out.println(ConsoleUI.GREEN + "Finished category: " + category + ConsoleUI.RESET);
            System.out.println("Press ENTER to continue or type 'exit' to stop.");
            System.out.print("> ");
            if (scanner.nextLine().trim().equalsIgnoreCase("exit")) return false;
        }
        return true;
    }

    private List<String> chooseExercises(Scanner scanner, String category, List<String> existing) {
        List<String> regular = regularLibrary.getOrDefault(category, new ArrayList<>());
        List<String> advanced = advancedLibrary.getOrDefault(category, new ArrayList<>());
        List<String> chosen = new ArrayList<>(existing);

        boolean done = false;
        while (!done) {
            ConsoleUtils.clearConsole();
            System.out.println(ConsoleUI.CYAN + category + ConsoleUI.RESET + " - Choose exercises (comma-separated numbers), 'done' to finish, or 'exit':");

            int index = 1;
            Map<Integer, String> map = new LinkedHashMap<>();

            System.out.println(ConsoleUI.BOLD + "Regular Exercises:" + ConsoleUI.RESET);
            for (String e : regular) {
                map.put(index, e);
                System.out.println(index + ". " + e);
                index++;
            }

            System.out.println(ConsoleUI.BOLD + "Advanced Exercises:" + ConsoleUI.RESET);
            for (String e : advanced) {
                map.put(index, e);
                System.out.println(index + ". " + e);
                index++;
            }

            System.out.print("Your choice: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) return chosen;
            if (input.equalsIgnoreCase("done")) break;

            String[] selections = input.split(",");
            for (String s : selections) {
                try {
                    int num = Integer.parseInt(s.trim());
                    String ex = map.get(num);
                    if (ex != null && !chosen.contains(ex)) chosen.add(ex);
                } catch (NumberFormatException ignored) {}
            }
        }
        return chosen;
    }

    private String normalizeDay(String input) {
        if (input == null || input.isEmpty()) return null;
        String lower = input.trim().toLowerCase();
        for (String valid : VALID_DAYS) {
            if (lower.equals(valid.toLowerCase()) || lower.equals(valid.toLowerCase() + "day")) {
                return valid;
            }
        }
        return null;
    }

    // Main for testing
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new CustomizableWeekPlanningMenu().show(scanner);
    }
}



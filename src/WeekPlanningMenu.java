import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Random;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class WeekPlanningMenu {

    private final LibraryExercisesMenu library;

    public WeekPlanningMenu(LibraryExercisesMenu library) {
        this.library = library != null ? library : new LibraryExercisesMenu();
    }

    public WeekPlanningMenu() {
        this(new LibraryExercisesMenu());

    }

    public void show(Scanner scanner, Customer customer) {
        ConsoleUtils.clearConsole();
        System.out.println(ConsoleUI.PURPLE + "\n--- Week Planning ---" + ConsoleUI.RESET);

        String levelChoice = chooseLevel(scanner);
        Map<String, List<Exercise>> weeklyPlan = generateWeeklyPlan(levelChoice);
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Map<String, String> dayHeaders = new LinkedHashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM", Locale.ENGLISH);

        for (int i = 0; i < dayNames.length; i++) {
            LocalDate date = monday.plusDays(i);
            dayHeaders.put(dayNames[i], dayNames[i] + " " + df.format(date));
        }

        boolean exit = false;
        String selectedDay = null;
        boolean showExercises = false;

        while (!exit) {
            ConsoleUtils.clearConsole();

            // Show the week table
            printWeekTable(dayNames, weeklyPlan, dayHeaders, selectedDay, showExercises);

            System.out.println("\nSelect a day to view exercises (1-7) or 8 to Exit to main menu:");
            String choice = scanner.nextLine().trim();

            if (choice.equals("8")) {
                exit = true;
                continue;
            }

            int dayIndex;
            try {
                dayIndex = Integer.parseInt(choice) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Press ENTER to continue...");
                scanner.nextLine();
                continue;
            }

            if (dayIndex < 0 || dayIndex > 6) {
                System.out.println("Invalid choice. Press ENTER to continue...");
                scanner.nextLine();
                continue;
            }

            selectedDay = dayNames[dayIndex];

            // Show exercises overview for the selected day (names white, checkmarks green)
            showExercises = true;
            ConsoleUtils.clearConsole();
            printWeekTable(dayNames, weeklyPlan, dayHeaders, selectedDay, true);

            System.out.println("\nStart exercises for " + selectedDay + "? (y/n): ");
            String start = scanner.nextLine().trim();
            if (start.equalsIgnoreCase("y")) {
                runExercisesForDay(selectedDay, weeklyPlan.get(selectedDay), scanner);
            }
        }

        ConsoleUtils.clearConsole();
        System.out.println("Returning to main menu...");
    }

    private String chooseLevel(Scanner scanner) {
        String levelChoice = "";
        boolean validChoice = false;
        while (!validChoice) {
            System.out.println("Choose your experience level:");
            System.out.println(ConsoleUI.CYAN + "1." + ConsoleUI.RESET + "Beginner");
            System.out.println(ConsoleUI.CYAN + "2." + ConsoleUI.RESET + "Advanced");
            System.out.print("Your choice (1 or 2): ");
            levelChoice = scanner.nextLine().trim();
            if (levelChoice.equals("1") || levelChoice.equals("2")) validChoice = true;
            else System.out.println("Invalid choice. Please enter 1 or 2.\n");
        }
        return levelChoice;
    }

private Map<String, List<Exercise>> generateWeeklyPlan(String levelChoice) {
    Map<String, List<Exercise>> plan = new LinkedHashMap<>();
    String[] shortDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    // Get library exercises
    List<?> indoorLib = library.getAllExercises("Indoor");   // unknown type, could be inner Exercise
    List<?> outdoorLib = library.getAllExercises("Outdoor");

    // Convert to top-level Exercise
    List<Exercise> allExercises = new ArrayList<>();
    for (Object obj : indoorLib) {
        Object libEx = obj; // assume it's LibraryExercisesMenu.Exercise
        try {
            // Use reflection to call getName() and getDescription() on inner Exercise
            String name = (String) libEx.getClass().getMethod("getName").invoke(libEx);
            String desc = (String) libEx.getClass().getMethod("getDescription").invoke(libEx);
            allExercises.add(new Exercise(name, desc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    for (Object obj : outdoorLib) {
        Object libEx = obj;
        try {
            String name = (String) libEx.getClass().getMethod("getName").invoke(libEx);
            String desc = (String) libEx.getClass().getMethod("getDescription").invoke(libEx);
            allExercises.add(new Exercise(name, desc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Random random = new Random();

    for (String day : shortDays) {
        List<Exercise> dayExercises = new ArrayList<>();
        List<Exercise> tempPool = new ArrayList<>(allExercises);
        Collections.shuffle(tempPool, random);

        // Take first 8 exercises without duplicates
        for (int i = 0; i < Math.min(8, tempPool.size()); i++) {
            Exercise original = tempPool.get(i);
            Exercise copy = new Exercise(original.getName(), original.getDescription());
            dayExercises.add(copy);
        }

        plan.put(day, dayExercises);
    }

    return plan;
}


    private void printWeekTable(String[] dayNames,
                                Map<String, List<Exercise>> weeklyPlan,
                                Map<String, String> dayHeaders,
                                String selectedDay,
                                boolean showExercises) {

        final int colWidth = 36;
        final int contentWidth = colWidth - 2;
        final int numRows = 8;

        String[] firstHalf = {"Mon", "Tue", "Wed"};
        String[] secondHalf = {"Thu", "Fri", "Sat", "Sun"};

        printTableSection(firstHalf, weeklyPlan, dayHeaders, selectedDay, colWidth, contentWidth, numRows, showExercises);
        System.out.println();
        printTableSection(secondHalf, weeklyPlan, dayHeaders, selectedDay, colWidth, contentWidth, numRows, showExercises);
    }

    private void printTableSection(String[] days,
                                   Map<String, List<Exercise>> weeklyPlan,
                                   Map<String, String> dayHeaders,
                                   String selectedDay,
                                   int colWidth,
                                   int contentWidth,
                                   int numRows,
                                   boolean showExercises) {

        StringBuilder horiz = new StringBuilder("+");
        for (int i = 0; i < days.length; i++) {
            horiz.append("-".repeat(colWidth)).append("+");
        }
        String horizontalBorder = horiz.toString();
        System.out.println(horizontalBorder);

        // Print headers
        for (String day : days) {
            List<Exercise> exercises = weeklyPlan.get(day);
            long completed = exercises.stream().filter(Exercise::isCompleted).count();

            String header = dayHeaders.get(day);
            String headerPlain;
            if (completed == exercises.size() && completed > 0) {
                headerPlain = header + " ✔";
            } else if (completed > 0) {
                headerPlain = header + " (" + completed + "/" + exercises.size() + ")";
            } else {
                headerPlain = header;
            }

            String paddedHeader = padRightPlain(stripAnsi(headerPlain), contentWidth);
            System.out.print("| " + ConsoleUI.PURPLE + paddedHeader + ConsoleUI.RESET + " ");
        }
        System.out.println("|");
        System.out.println(horizontalBorder);

        // Print exercise rows
        for (int row = 0; row < numRows; row++) {
            for (String day : days) {
                String cell = " ".repeat(contentWidth);

                if (showExercises && day.equals(selectedDay)) {
                    List<Exercise> exercises = weeklyPlan.get(day);
                    if (row < exercises.size()) {
                        Exercise ex = exercises.get(row);
                        String name = ex.getName();

                        // Overview mode: white names, green checkmark if completed
                        String displayName = name;
                        if (ex.isCompleted()) {
                            displayName = name + ConsoleUI.GREEN + " ✔" + ConsoleUI.RESET;
                        }

                        // Compute visible length ignoring ANSI codes
                        int visibleLength = stripAnsi(displayName).length();

                        cell = padRightPlain(stripAnsi(displayName), contentWidth - 1);
                        System.out.print("| " + displayName + padRightPlain("", contentWidth - visibleLength) + " ");
                    } else {
                        System.out.print("| " + cell + " ");
                    }
                } else {
                    System.out.print("| " + cell + " ");
                }
            }
            System.out.println("|");
        }

        System.out.println(horizontalBorder);
    }

    private void runExercisesForDay(String selectedDay, List<Exercise> exercises, Scanner scanner) {
        int total = exercises.size();
        int completedCount = (int) exercises.stream().filter(Exercise::isCompleted).count();

        for (Exercise ex : exercises) {
            if (!ex.isCompleted()) {
                ConsoleUtils.clearConsole();
                System.out.println(ConsoleUI.PURPLE + "\n" + selectedDay + " Progress: "
                        + completedCount + "/" + total + " completed" + ConsoleUI.RESET);

                // Exercise mode: blue title
                String namePlain = stripAnsi(ex.getName());
                System.out.println("\n" + ConsoleUI.CYAN + ConsoleUI.BOLD + namePlain + ConsoleUI.RESET);

                String descPlain = stripAnsi(ex.getDescription() == null ? "" : ex.getDescription());
                String descWithColoredNumbers = descPlain.replaceAll("(?m)(^|\\s)(\\d+\\.)",
                        "$1" + ConsoleUI.CYAN + "$2" + ConsoleUI.RESET);

                String[] descLines = descWithColoredNumbers.split("\\r?\\n");
                for (String line : descLines) {
                    System.out.println(line);
                }

                boolean completed = false;
                while (!completed) {
                    System.out.print("Mark as completed?" + ConsoleUI.GREEN + "(y)" + ConsoleUI.RESET
                            + " or exit to return to week planning" + ConsoleUI.RED + "(e)" + ConsoleUI.RESET + ": ");
                    String input = scanner.nextLine().trim();
                    if (input.equalsIgnoreCase("y")) {
                        ex.markCompleted();
                        completedCount++;
                        System.out.println(ConsoleUI.GREEN + "Completed!" + ConsoleUI.RESET);
                        completed = true;
                    } else if (input.equalsIgnoreCase("e")) {
                        return;
                    }
                }
            }
        }

        ConsoleUtils.clearConsole();
        System.out.println(ConsoleUI.GREEN + "All exercises completed for " + selectedDay + "!" + ConsoleUI.RESET);
        System.out.println("Press ENTER to return to week planning...");
        scanner.nextLine();
    }

    private String stripAnsi(String s) {
        if (s == null) return "";
        return s.replaceAll("\\u001B\\[[;\\d]*m", "");
    }

    private String padRightPlain(String plain, int width) {
        if (plain == null) plain = "";
        if (plain.length() >= width) return plain.substring(0, width);
        StringBuilder sb = new StringBuilder(plain);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }
}

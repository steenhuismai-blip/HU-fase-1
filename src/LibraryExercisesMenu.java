import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;         
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Scanner;


public class LibraryExercisesMenu {

    // Data structure: Location -> Category -> Point -> List of Exercises
    private final Map<String, LinkedHashMap<String, LinkedHashMap<String, List<Exercise>>>> data = new LinkedHashMap<>();


    // Default constructor for backward compatibility (creates its own ConsoleUtils)
    public LibraryExercisesMenu() {
        this(new ConsoleUtils());
    }
    public LibraryExercisesMenu(ConsoleUtils consoleUtils) {
        buildData();
    }

    public void show(Scanner scanner) {
        outerLoop: while (true) {
            ConsoleUtils.clearConsole();

            List<String> locations = new ArrayList<>(data.keySet());

            // ------------------ LOCATION SELECTION ------------------
            int[] locWidths = computeColWidths(
                    combineWithHeader("Location", locations),
                    combineWithHeader("Category", List.of()),
                    combineWithHeader("Point", List.of())
            );
            printHeaderAndSeparator(locWidths);

            for (int i = 0; i < locations.size(); i++) {
                System.out.printf("%-" + locWidths[0] + "s | %-" + locWidths[1] + "s | %-" + locWidths[2] + "s%n",
                        (i + 1) + ") " + locations.get(i), "", "");
            }

            System.out.print("\nEnter location number: ");
            String locLine = scanner.nextLine().trim();
            int locChoice;
            try {
                locChoice = Integer.parseInt(locLine);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Press ENTER...");
                scanner.nextLine();
                continue;
            }
            if (locChoice == 0) break; // exit program
            if (locChoice < 1 || locChoice > locations.size()) {
                System.out.println("Choice out of range. Press ENTER...");
                scanner.nextLine();
                continue;
            }
            String chosenLoc = locations.get(locChoice - 1);
            LinkedHashMap<String, LinkedHashMap<String, List<Exercise>>> categoriesMap = data.get(chosenLoc);
            List<String> categories = new ArrayList<>(categoriesMap.keySet());

            // ------------------ CATEGORY SELECTION ------------------
            categoriesLoop: while (true) {
                ConsoleUtils.clearConsole();

                int[] catWidths = computeColWidths(
                        combineWithHeader("Location", List.of(chosenLoc)),
                        combineWithHeader("Category", categories),
                        combineWithHeader("Point", List.of())
                );
                printHeaderAndSeparator(catWidths);

                System.out.printf("%-" + catWidths[0] + "s | %-" + catWidths[1] + "s | %-" + catWidths[2] + "s%n",
                        chosenLoc, "", "");

                for (int i = 0; i < categories.size(); i++) {
                    System.out.printf("%-" + catWidths[0] + "s | %-" + catWidths[1] + "s | %-" + catWidths[2] + "s%n",
                            "", (i + 1) + ") " + categories.get(i), "");
                }

                System.out.print("\nEnter category number: ");
                String catLine = scanner.nextLine().trim();
                int catChoice;
                try {
                    catChoice = Integer.parseInt(catLine);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Press ENTER...");
                    scanner.nextLine();
                    continue;
                }
                if (catChoice == 0) break;
                if (catChoice < 1 || catChoice > categories.size()) {
                    System.out.println("Choice out of range. Press ENTER...");
                    scanner.nextLine();
                    continue;
                }

                String chosenCat = categories.get(catChoice - 1);
                LinkedHashMap<String, List<Exercise>> pointsMap = categoriesMap.get(chosenCat);
                List<String> points = new ArrayList<>(pointsMap.keySet());

                // ------------------ POINT SELECTION ------------------
                pointsLoop: while (true) {
                    ConsoleUtils.clearConsole();

                    int[] pointWidths = computeColWidths(
                            combineWithHeader("Location", List.of(chosenLoc)),
                            combineWithHeader("Category", List.of(chosenCat)),
                            combineWithHeader("Point", points)
                    );
                    printHeaderAndSeparator(pointWidths);

                    System.out.printf("%-" + pointWidths[0] + "s | %-" + pointWidths[1] + "s | %-" + pointWidths[2] + "s%n",
                            chosenLoc, chosenCat, "");

                    for (int i = 0; i < points.size(); i++) {
                        System.out.printf("%-" + pointWidths[0] + "s | %-" + pointWidths[1] + "s | %-" + pointWidths[2] + "s%n",
                                "", "", (i + 1) + ") " + points.get(i));
                    }

                    System.out.print("\nEnter point number: ");
                    String pointLine = scanner.nextLine().trim();
                    int pointChoice;
                    try {
                        pointChoice = Integer.parseInt(pointLine);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number. Press ENTER...");
                        scanner.nextLine();
                        continue;
                    }
                    if (pointChoice == 0) break; // back to categories
                    if (pointChoice < 1 || pointChoice > points.size()) {
                        System.out.println("Choice out of range. Press ENTER...");
                        scanner.nextLine();
                        continue;
                    }

                    String chosenPoint = points.get(pointChoice - 1);
                    List<Exercise> exercises = pointsMap.get(chosenPoint);

                    // ------------------ EXERCISES DISPLAY ------------------
                    ConsoleUtils.clearConsole();

                    int[] exerWidths = computeColWidths(
                            combineWithHeader("Location", List.of(chosenLoc)),
                            combineWithHeader("Category", List.of(chosenCat)),
                            combineWithHeader("Point", List.of(chosenPoint))
                    );
                    printHeaderAndSeparator(exerWidths);

                    System.out.printf("%-" + exerWidths[0] + "s | %-" + exerWidths[1] + "s | %-" + exerWidths[2] + "s%n",
                            chosenLoc, chosenCat, chosenPoint);
                    System.out.println(repeat('-', exerWidths[0] + exerWidths[1] + exerWidths[2] + 6));

                    for (int i = 0; i < exercises.size(); i++) {
                        Exercise e = exercises.get(i);
                        System.out.println(ConsoleUI.CYAN + (i + 1) + ")" + ConsoleUI.RESET + " " + e.getName());
                        wrapAndPrint(e.getDescription(), exerWidths[0] + exerWidths[1] + exerWidths[2] + 6);
                        System.out.println();
                    }

                    System.out.println("Options: " + ConsoleUI.GREEN + "[1]" + ConsoleUI.RESET + " Back to points | "
                            + ConsoleUI.GREEN + "[2]" + ConsoleUI.RESET + " Back to categories | "
                            + ConsoleUI.GREEN + "[3]" + ConsoleUI.RESET + " Change location | "
                            + ConsoleUI.RED + "[0]" + ConsoleUI.RESET + " Exit");

                    String option = scanner.nextLine().trim();
                    switch (option) {
                        case "0":
                            ConsoleUtils.clearConsole();
                            return;
                        case "1":
                            continue pointsLoop;
                        case "2":
                            continue categoriesLoop;
                        case "3":
                            continue outerLoop;
                        default:
                            System.out.println("Invalid option. Press ENTER...");
                            scanner.nextLine();
                    }
                }
            }
        }
    }

    private int[] computeColWidths(List<String> col1Items, List<String> col2Items, List<String> col3Items) {
        int w1 = col1Items.stream().mapToInt(s -> stripAnsi(s).length()).max().orElse(0);
        int w2 = col2Items.stream().mapToInt(s -> stripAnsi(s).length()).max().orElse(0);
        int w3 = col3Items.stream().mapToInt(s -> stripAnsi(s).length()).max().orElse(0);

        // minimum widths to keep things readable
        w1 = Math.max(w1, 8);
        w2 = Math.max(w2, 8);
        w3 = Math.max(w3, 8);

        // add little padding
        w1 += 2;
        w2 += 2;
        w3 += 2;

        return new int[] { w1, w2, w3 };
    }

    private String stripAnsi(String text) {
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    private List<String> combineWithHeader(String header, List<String> items) {
        List<String> result = new ArrayList<>();
        result.add(header);
        result.addAll(items);
        return result;
    }

    private void printHeaderAndSeparator(int[] widths) {
        // Colored headers
        String locationHeader = ConsoleUI.PURPLE + "Location" + ConsoleUI.RESET;
        String categoryHeader = ConsoleUI.PURPLE + "Category" + ConsoleUI.RESET;
        String pointHeader = ConsoleUI.PURPLE + "Point" + ConsoleUI.RESET;

        // Print header row with proper column widths
        String header = String.format("%-" + widths[0] + "s | %-" + widths[1] + "s | %-" + widths[2] + "s",
                locationHeader, categoryHeader, pointHeader);
        System.out.println(header);

        // Separator line matching total width
        int total = widths[0] + widths[1] + widths[2] + 6; // 3 separators " | "
        System.out.println(repeat('-', total));
    }

    private String repeat(char c, int n) {
        if (n <= 0)
            return "";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++)
            sb.append(c);
        return sb.toString();
    }

    private void wrapAndPrint(String text, int width) {
        if (text == null)
            return;
        int max = Math.max(width - 2, 20); // safety minimum width for wrapping
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            if (line.length() + w.length() + 1 > max) {
                System.out.println(line.toString());
                line = new StringBuilder(w);
            } else {
                if (line.length() > 0)
                    line.append(' ');
                line.append(w);
            }
        }
        if (line.length() > 0)
            System.out.println(line.toString());
    }

private void buildData() {
    // -------------------- Indoor --------------------
    LinkedHashMap<String, LinkedHashMap<String, List<Exercise>>> indoor = new LinkedHashMap<>();

    // 1. Strength & Weight Training
    LinkedHashMap<String, List<Exercise>> strength = new LinkedHashMap<>();
    strength.put("Free Weights", Arrays.asList(
            new Exercise(ConsoleUI.CYAN + "Dumbbell Bench Press" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Lie on a bench with a dumbbell in each hand, elbows bent at 90°.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Press dumbbells upward until arms are straight, keeping wrists neutral.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Slowly lower back to start position.\n"
                            + "Targets chest, shoulders, and triceps.\n"
                            + "Common mistakes: flaring elbows, arching back excessively.\n"
                            + "Recommended: 3 sets of 10-12 reps. Variation: Use one dumbbell or increase weight for progression."),
            new Exercise(ConsoleUI.CYAN + "Barbell Squat" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Place barbell on upper back, feet shoulder-width apart.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Squat down by bending knees and hips, keeping chest upright.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Push through heels to return to standing.\n"
                            + "Targets quads, glutes, and core.\n"
                            + "Common mistakes: knees caving in, leaning too far forward.\n"
                            + "Recommended: 3-4 sets of 8-12 reps. Variation: Goblet squat or resistance band squat for beginners.")
    ));
    strength.put("Machines", Arrays.asList(
            new Exercise(ConsoleUI.CYAN + "Leg Press Machine" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Sit on leg press machine, feet shoulder-width on platform.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Push platform away by extending legs, keeping back against seat.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Slowly return to start.\n"
                            + "Targets quads, hamstrings, glutes.\n"
                            + "Common mistakes: locking knees, lifting hips off seat.\n"
                            + "Recommended: 3 sets of 12-15 reps. Variation: Narrow or wide foot placement for different emphasis."),
            new Exercise(ConsoleUI.CYAN + "Lat Pulldown" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Sit at machine, grip bar wider than shoulders.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Pull bar to upper chest while squeezing shoulder blades.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Slowly release to full arm extension.\n"
                            + "Targets lats, biceps, and upper back.\n"
                            + "Common mistakes: leaning back too far, jerking the bar.\n"
                            + "Recommended: 3 sets of 10-12 reps. Variation: Reverse grip for biceps focus.")
    ));
    strength.put("Functional Strength", Arrays.asList(
            new Exercise(ConsoleUI.CYAN + "Resistance Band Rows" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Anchor band at chest height, hold handles.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Step back to create tension, pull hands toward chest.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Slowly release.\n"
                            + "Targets back and biceps.\n"
                            + "Common mistakes: rounding shoulders, using momentum.\n"
                            + "Recommended: 3 sets of 12-15 reps. Variation: Seated or standing rows."),
            new Exercise(ConsoleUI.CYAN + "Sandbag Deadlift" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Place sandbag on ground, feet hip-width.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Hinge at hips, bend knees, and lift bag to standing.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Lower with control.\n"
                            + "Targets total body and grip strength.\n"
                            + "Common mistakes: rounded back, jerking the weight.\n"
                            + "Recommended: 3 sets of 8-10 reps. Variation: Single-arm lift or carry for advanced.")
    ));
    indoor.put("Strength & Weight Training", strength);

    // 2. Cardio & Endurance
    LinkedHashMap<String, List<Exercise>> cardio = new LinkedHashMap<>();
    cardio.put("Treadmill", Arrays.asList(
            new Exercise(ConsoleUI.CYAN + "Steady Run" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Run at a moderate, consistent pace.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Maintain upright posture, light foot strike.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Keep breathing steady and controlled.\n"
                            + "Targets cardiovascular endurance.\n"
                            + "Recommended: 20-40 minutes. Variation: Increase incline for added intensity."),
            new Exercise(ConsoleUI.CYAN + "Incline Walk" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET + " Set treadmill to incline.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Walk at steady pace, keeping chest upright.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Swing arms naturally.\n"
                            + "Targets legs and aerobic system.\n"
                            + "Recommended: 20-30 minutes. Variation: Increase speed or incline for challenge.")
    ));
    cardio.put("Stationary Bike", Arrays.asList(
            new Exercise(ConsoleUI.CYAN + "Endurance Ride" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET + " Pedal at steady pace.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Keep core engaged and shoulders relaxed.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Maintain consistent resistance.\n"
                            + "Targets leg stamina and aerobic capacity.\n"
                            + "Recommended: 30-60 minutes. Variation: Add resistance intervals for intensity."),
            new Exercise(ConsoleUI.CYAN + "Sprint Intervals" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Sprint all-out for 20-40 seconds.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Recover with light pedaling for 60-90 seconds.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Repeat desired intervals.\n"
                            + "Targets anaerobic power and cardiovascular fitness.\n"
                            + "Recommended: 8-12 intervals. Common mistakes: poor posture, excessive resistance.")
    ));
    indoor.put("Cardio & Endurance", cardio);

    // 3. Functional / Bodyweight Training
    LinkedHashMap<String, List<Exercise>> functional = new LinkedHashMap<>();
    functional.put("TRX / Suspension", Arrays.asList(
            new Exercise(ConsoleUI.CYAN + "TRX Rows" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Grip TRX handles, lean back.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Pull chest to handles, keeping body straight.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Lower slowly.\n"
                            + "Targets back and biceps.\n"
                            + "Common mistakes: sagging hips, jerky motion.\n"
                            + "Recommended: 3 sets of 10-12 reps. Variation: Adjust body angle to change difficulty."),
            new Exercise(ConsoleUI.CYAN + "TRX Push-ups" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Place hands in straps, body straight.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Lower chest to hands.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Press back up.\n"
                            + "Targets chest, shoulders, and core.\n"
                            + "Common mistakes: hips sagging, elbows flaring.\n"
                            + "Recommended: 3 sets of 8-12 reps. Variation: Move feet closer for higher difficulty.")
    ));
    functional.put("Medicine Balls / Slam Balls", Arrays.asList(
            new Exercise(ConsoleUI.CYAN + "Medicine Ball Slams" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET + " Lift ball overhead.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Slam it forcefully to the ground.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Pick up and repeat.\n"
                            + "Targets core and power.\n"
                            + "Common mistakes: rounding back, using arms only.\n"
                            + "Recommended: 3 sets of 12-15 reps."),
            new Exercise(ConsoleUI.CYAN + "Rotational Throw" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Stand sideways to wall, hold ball.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Rotate torso and throw ball against wall.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Catch and repeat.\n"
                            + "Targets obliques and rotational core strength.\n"
                            + "Recommended: 3 sets of 10-12 reps per side.")
    ));
    functional.put("Plyometric Boxes", Arrays.asList(
            new Exercise(ConsoleUI.CYAN + "Box Jumps" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                            + " Stand in front of box, feet shoulder-width.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Jump explosively onto box, land softly.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Step down carefully.\n"
                            + "Targets legs and reactive power.\n"
                            + "Common mistakes: landing stiffly, jumping from too far.\n"
                            + "Recommended: 3 sets of 8-10 reps. Variation: Increase box height for challenge."),
            new Exercise(ConsoleUI.CYAN + "Step-Ups" + ConsoleUI.RESET,
                    ConsoleUI.CYAN + "1." + ConsoleUI.RESET + " Place one foot on box.\n"
                            + ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                            + " Push through heel to stand.\n"
                            + ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                            + " Step down and repeat with other leg.\n"
                            + "Targets quads, glutes, and balance.\n"
                            + "Recommended: 3 sets of 12 reps per leg. Variation: Hold dumbbells for added resistance.")
    ));
    indoor.put("Functional / Bodyweight Training", functional);
    data.put("Indoor", indoor);

// -------------------- Outdoor --------------------
LinkedHashMap<String, LinkedHashMap<String, List<Exercise>>> outdoor = new LinkedHashMap<>();

// 1. Bodyweight / Calisthenics
LinkedHashMap<String, List<Exercise>> bodyweightOutdoor = new LinkedHashMap<>();
bodyweightOutdoor.put("Pull-up Bars", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Pull-ups" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Hang from bar, palms away, arms straight.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Pull chest toward bar until chin passes bar.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Lower slowly.\n" +
                        "Targets lats, biceps, grip.\n" +
                        "Recommended: 3 sets of 5-12 reps. Variation: Assisted with bands."),
        new Exercise(ConsoleUI.CYAN + "Hanging Leg Raises" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Hang from bar, legs straight.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Lift legs to 90° or higher.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Lower slowly.\n" +
                        "Targets abs, hip flexors.\n" +
                        "Recommended: 3 sets of 10-15 reps. Variation: Knee raises for beginners.")
));
bodyweightOutdoor.put("Parallel / Dip Bars", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Dips" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Grip bars, elbows straight.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Lower body to 90° elbow bend.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Press back up.\n" +
                        "Targets chest, shoulders, triceps.\n" +
                        "Recommended: 3 sets of 8-12 reps. Variation: Assisted dips."),
        new Exercise(ConsoleUI.CYAN + "L-sit Hold" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Support body on bars, legs straight out.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Hold position.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Keep core tight.\n" +
                        "Targets core, arms.\n" +
                        "Recommended: 3-5 holds of 10-20 sec. Variation: Tuck legs if too hard.")
));
bodyweightOutdoor.put("Push-up Stations", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Standard Push-ups" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Hands shoulder-width, body straight.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Lower chest to ground.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Press back up.\n" +
                        "Targets chest, triceps, core.\n" +
                        "Recommended: 3 sets of 10-20 reps. Variation: Feet elevated for harder."),
        new Exercise(ConsoleUI.CYAN + "Incline Push-ups" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Hands on elevated surface, body straight.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Lower chest to hands.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Press back up.\n" +
                        "Targets upper chest, triceps.\n" +
                        "Recommended: 3 sets of 10-15 reps.")
));
outdoor.put("Bodyweight / Calisthenics", bodyweightOutdoor);

// 2. Functional & Strength
LinkedHashMap<String, List<Exercise>> outdoorFunctional = new LinkedHashMap<>();
outdoorFunctional.put("Resistance Band / Outdoor Pulleys", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Band Chest Press" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Anchor band at chest height, hold handles.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Step forward and press hands forward until arms extend.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Slowly return to start.\n" +
                        "Targets chest and triceps.\n" +
                        "Common mistakes: leaning back, locking elbows.\n" +
                        "Recommended: 3 sets of 12-15 reps."),
        new Exercise(ConsoleUI.CYAN + "Band Squats" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Stand on band, feet shoulder-width apart.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Hold handles at shoulders and squat down.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Stand back up.\n" +
                        "Targets quads, glutes, core.\n" +
                        "Common mistakes: knees caving in, leaning forward.\n" +
                        "Recommended: 3 sets of 12-15 reps.")
));
outdoorFunctional.put("Sandbag / Kettlebell Zones", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Sandbag Carry" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Lift sandbag to shoulders or chest.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Walk a set distance while maintaining upright posture.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Lower sandbag safely.\n" +
                        "Targets grip, core, conditioning.\n" +
                        "Recommended: 3-5 carries of 20-50 meters. Variation: Vary carry position for challenge."),
        new Exercise(ConsoleUI.CYAN + "Kettlebell Swings" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Stand with feet hip-width, hold kettlebell.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Hinge at hips, swing kettlebell to chest/shoulder height.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Control swing back between legs.\n" +
                        "Targets posterior chain and cardio.\n" +
                        "Common mistakes: rounding back, using arms only.\n" +
                        "Recommended: 3 sets of 15-20 reps.")
));
outdoorFunctional.put("Core Benches / Balance Beams", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Sit-ups on Bench" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Lie on bench, knees bent, feet secured.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Lift torso, engage abs.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Lower slowly.\n" +
                        "Targets core.\n" +
                        "Common mistakes: pulling neck, using momentum.\n" +
                        "Recommended: 3 sets of 12-20 reps."),
        new Exercise(ConsoleUI.CYAN + "Balance Beam Walk" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Walk slowly along beam, focus on steady footing.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Keep core engaged and arms out.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Complete desired passes.\n" +
                        "Targets balance, ankle stability, core.\n" +
                        "Recommended: 3-5 passes. Variation: Close eyes for advanced challenge.")
));
outdoor.put("Functional & Strength", outdoorFunctional);

// 3. Cardio / Endurance
LinkedHashMap<String, List<Exercise>> cardioOutdoor = new LinkedHashMap<>();
cardioOutdoor.put("Running Tracks / Circuits", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Interval Sprints" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Sprint all-out for 20-40 seconds.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Walk or jog for 60-90 seconds to recover.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Repeat for desired intervals.\n" +
                        "Targets speed, anaerobic capacity, conditioning.\n" +
                        "Recommended: 6-10 intervals."),
        new Exercise(ConsoleUI.CYAN + "Long Jog" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Jog at a steady, moderate pace.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Maintain upright posture, relaxed breathing.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Keep cadence consistent.\n" +
                        "Targets aerobic endurance.\n" +
                        "Recommended: 20-60 minutes depending on fitness level.")
));
cardioOutdoor.put("Stairs / Hills", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Stair Runs" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Sprint up stairs at full effort.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Walk down carefully.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Repeat for desired sets.\n" +
                        "Targets legs, glutes, cardiovascular fitness.\n" +
                        "Recommended: 6-10 sprints. Common mistakes: tripping, leaning forward too much."),
        new Exercise(ConsoleUI.CYAN + "Hill Climbs" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Run uphill at moderate effort.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Drive knees forward, keep chest upright.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Walk down to recover.\n" +
                        "Targets leg strength, cardiovascular endurance.\n" +
                        "Recommended: 5-8 repeats depending on hill length.")
));
cardioOutdoor.put("Agility Ladders / Hurdles", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Quick Feet Drill" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Step quickly through ladder, one foot per box.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Maintain light, quick steps.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Repeat for desired passes.\n" +
                        "Targets coordination, foot speed, agility.\n" +
                        "Recommended: 3-5 passes. Variation: Add lateral movement."),
        new Exercise(ConsoleUI.CYAN + "Hurdle Jumps" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Jump over low hurdles with both feet.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Land softly and immediately jump next hurdle.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Complete desired repetitions.\n" +
                        "Targets explosive lower-body power.\n" +
                        "Recommended: 3 sets of 10-12 jumps.")
));
outdoor.put("Cardio / Endurance", cardioOutdoor);

// 4. Playground-style Fitness
LinkedHashMap<String, List<Exercise>> playground = new LinkedHashMap<>();
playground.put("Monkey Bars", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Swing Across" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Grip bars, move hand-to-hand across.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Engage core and shoulders to control swing.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Complete full traverse.\n" +
                        "Targets grip, shoulders, core.\n" +
                        "Recommended: 3-5 passes. Variation: Skip bars for challenge."),
        new Exercise(ConsoleUI.CYAN + "Static Holds" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Hang from bar, arms straight.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Hold position with body steady.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Maintain grip throughout.\n" +
                        "Targets grip, shoulders, endurance.\n" +
                        "Recommended: 3-5 holds of 15-30 sec.")
));
playground.put("Tires / Ropes", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Tire Flips" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET + " Squat and grip tire.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Drive through legs and hips to flip.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Reset and repeat.\n" +
                        "Targets total body power and conditioning.\n" +
                        "Recommended: 3-5 sets of 5-10 flips."),
        new Exercise(ConsoleUI.CYAN + "Battle Ropes" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Grip rope ends, stand with feet shoulder-width.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Wave or slam ropes in controlled bursts.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Maintain rhythm and breathing.\n" +
                        "Targets arms, core, cardio.\n" +
                        "Recommended: 3-5 rounds of 20-30 sec.")
));
playground.put("Combo Stations", Arrays.asList(
        new Exercise(ConsoleUI.CYAN + "Pull-up & Dip Superset" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET
                        + " Perform pull-ups with proper form.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Immediately perform dips.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Repeat for desired sets.\n" +
                        "Targets upper body strength and endurance.\n" +
                        "Recommended: 3-4 supersets of 6-10 reps each."),
        new Exercise(ConsoleUI.CYAN + "Push-up + Jump Squat Circuit" + ConsoleUI.RESET,
                ConsoleUI.CYAN + "1." + ConsoleUI.RESET + " Perform push-ups.\n" +
                        ConsoleUI.CYAN + "2." + ConsoleUI.RESET
                        + " Immediately do jump squats.\n" +
                        ConsoleUI.CYAN + "3." + ConsoleUI.RESET
                        + " Repeat for desired rounds.\n" +
                        "Targets full-body conditioning, power.\n" +
                        "Recommended: 3-5 circuits of 10 reps per exercise.")
));
outdoor.put("Playground-style Fitness", playground);

data.put("Outdoor", outdoor);

}
    public List<Exercise> getAllExercises(String location) {
        List<Exercise> all = new ArrayList<>();
        LinkedHashMap<String, LinkedHashMap<String, List<Exercise>>> categoriesMap = data.get(location);
        if (categoriesMap != null) {
            for (LinkedHashMap<String, List<Exercise>> pointsMap : categoriesMap.values()) {
                for (List<Exercise> exercises : pointsMap.values()) {
                    all.addAll(exercises);
                }
            }
        }
        return all;
    }

    // Inner Exercise class (with getters)
    public class Exercise {
        private String name;
        private String description;
        private boolean completed = false;

        public Exercise(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void markCompleted() {
            completed = true;
        }
    }
}



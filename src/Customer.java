import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Customer {

    private String name;
    private int age;
    private double weight; // kg
    private double height; // cm
    private String gender;

    private String lastChangedField = null;

    private static final String FIELD_NAME = "name";
    private static final String FIELD_AGE = "age";
    private static final String FIELD_WEIGHT = "weight";
    private static final String FIELD_HEIGHT = "height";
    private static final String FIELD_GENDER = "gender";

    private final ConsoleUtils consoleUtils = new ConsoleUtils();

    // -------------------- Constructors --------------------
    public Customer() {}

    public Customer(String name, int age, double weight, double height, String gender) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
    }

    // -------------------- Getters & Setters --------------------
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    // -------------------- Public Methods --------------------

    public void fillPersonalInfo(Scanner scanner) {
        ConsoleUtils.clearConsole();
        System.out.println("Welcome! Please fill in your info:\n");

        collectInitialInfo(scanner);
        showOverview();

        if (promptYesNo(scanner, "Do you want to edit any info? (press ENTER for no / type 'yes' to edit): ")) {
            reviewAndEditInfo(scanner);
        }
    }

    public void reviewAndEditInfo(Scanner scanner) {
        boolean confirmed = false;

        while (!confirmed) {
            ConsoleUtils.clearConsole();
            System.out.println(ConsoleUI.PURPLE + "\n--- Your Current Info ---" + ConsoleUI.RESET);
            showInfo();

            System.out.println(ConsoleUI.PURPLE + "\nWhich section do you want to edit?" + ConsoleUI.RESET);
            System.out.println(ConsoleUI.CYAN + "1." + ConsoleUI.RESET + " Name");
            System.out.println(ConsoleUI.CYAN + "2." + ConsoleUI.RESET + " Age");
            System.out.println(ConsoleUI.CYAN + "3." + ConsoleUI.RESET + " Weight");
            System.out.println(ConsoleUI.CYAN + "4." + ConsoleUI.RESET + " Height");
            System.out.println(ConsoleUI.CYAN + "5." + ConsoleUI.RESET + " Gender");
            System.out.println(ConsoleUI.GREEN + "6. Confirm and continue" + ConsoleUI.RESET);
            System.out.print("Your choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.isEmpty() || choice.equals("6")) {
                confirmed = true;
                break;
            }

            lastChangedField = null;

            switch (choice) {
                case "1":
                    name = getName(scanner);
                    lastChangedField = FIELD_NAME;
                    break;
                case "2":
                    age = getIntInRange(scanner, "Age (13-120): ", 13, 120);
                    lastChangedField = FIELD_AGE;
                    break;
                case "3":
                    weight = getDoubleInRange(scanner, "Weight in kg (40-500): ", 40, 500);
                    lastChangedField = FIELD_WEIGHT;
                    break;
                case "4":
                    height = getDoubleInRange(scanner, "Height in cm (140-300): ", 140, 300);
                    lastChangedField = FIELD_HEIGHT;
                    break;
                case "5":
                    gender = getGender(scanner);
                    lastChangedField = FIELD_GENDER;
                    break;
                default:
                    printError("Please enter a valid option.");
                    consoleUtils.pauseForEnter(scanner);
            }

            if (lastChangedField != null) {
                ConsoleUtils.clearConsole();
                System.out.println(ConsoleUI.PURPLE + "\n--- Updated Info (highlighted last change) ---" + ConsoleUI.RESET);
                showInfoWithHighlight();
                System.out.println("BMI: " + getBmiFormatted());
                System.out.println("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    // -------------------- Display Methods --------------------
    public void showInfo() {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Weight: " + weight + " kg");
        System.out.println("Height: " + height + " cm");
        System.out.println("Gender: " + gender);
    }

    public void showInfoWithHighlight() {
        System.out.println("Name: " + highlight(FIELD_NAME, name));
        System.out.println("Age: " + highlight(FIELD_AGE, age));
        System.out.println("Weight: " + highlight(FIELD_WEIGHT, weight));
        System.out.println("Height: " + highlight(FIELD_HEIGHT, height));
        System.out.println("Gender: " + highlight(FIELD_GENDER, gender));
    }

    public void showOverview() {
        System.out.println(ConsoleUI.PURPLE + "\n--- Overview of entered data ---" + ConsoleUI.RESET);
        showInfo();
        System.out.println("BMI: " + getBmiFormatted());
    }

    // -------------------- BMI --------------------
    public double getBmi() {
        if (height <= 0) return 0.0;
        double heightMeters = height / 100.0; // height stored in cm
        return weight / (heightMeters * heightMeters);
    }

    public String getBmiFormatted() {
        double bmi = getBmi();
        if (bmi <= 0) return "N/A";
        return String.format("%.1f (%s)", bmi, getBmiCategory());
    }

    public String getBmiCategory() {
        double bmi = getBmi();
        if (bmi <= 0) return "Unknown";
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal weight";
        if (bmi < 30.0) return "Overweight";
        return "Obesity";
    }

    // ------------------- JSON Persistence (json-simple) --------------------
    public JSONObject toJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("age", age);
        map.put("weight", weight);
        map.put("height", height);
        map.put("gender", gender);

        return new JSONObject(map);
    }

    public void saveToFile() {
        Path dataDir = Paths.get("data");
        Path file = dataDir.resolve("customer.json");

        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            JSONObject jsonObject = toJson();
            String content = jsonObject.toJSONString(); 
            Files.writeString(file, content);
            System.out.println("Customer saved to " + file.toString());
        } catch (IOException e) {
            System.out.println("Failed to save customer data: " + e.getMessage());
        }
    }

    public static Customer loadFromFile() {
        Path file = Paths.get("data", "customer.json");
        if (!Files.exists(file)) {
            // no saved data, return null so caller knows
            return null;
        }

        try {
            String content = Files.readString(file);
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(content);

            Customer c = new Customer();

            // name
            Object nameObj = obj.get("name");
            c.name = nameObj == null ? null : String.valueOf(nameObj);

            // age 
            Object ageObj = obj.get("age");
            if (ageObj instanceof Number) {
                c.age = ((Number) ageObj).intValue();
            } else {
                c.age = 0;
            }

            // weight and height 
            Object weightObj = obj.get("weight");
            c.weight = parseNumberToDouble(weightObj);

            Object heightObj = obj.get("height");
            c.height = parseNumberToDouble(heightObj);

            // gender
            Object genderObj = obj.get("gender");
            c.gender = genderObj == null ? "Not specified" : String.valueOf(genderObj);

            return c;
        } catch (IOException | ParseException e) {
            System.out.println("❌ Failed to read customer data: " + e.getMessage());
            return null;
        }
    }

    private static double parseNumberToDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(o));
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    // -------------------- Private Helpers --------------------

    private void collectInitialInfo(Scanner scanner) {
        name = getName(scanner);
        age = getIntInRange(scanner, "Age (13-120): ", 13, 120);
        weight = getDoubleInRange(scanner, "Weight in kg (40-500): ", 40, 500);
        height = getDoubleInRange(scanner, "Height in cm (140-300): ", 140, 300);
        gender = getGender(scanner);
    }

    private boolean promptYesNo(Scanner scanner, String message) {
        System.out.print(message);
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("yes");
    }

    private String highlight(String fieldName, Object value) {
        if (value == null) value = "";
        return fieldName.equals(lastChangedField) ? ConsoleUI.YELLOW + value + ConsoleUI.RESET : String.valueOf(value);
    }

    private void printError(String message) {
        System.out.println(ConsoleUI.RED + message + ConsoleUI.RESET);
    }

    // -------------------- Input Methods --------------------

    private String getName(Scanner scanner) {
        while (true) {
            System.out.print("Full Name: ");
            String input = scanner.nextLine().trim();
            if (input.matches("[\\p{L} '\\-]+")) return input;
            printError("Please enter a valid name (letters, spaces, hyphens, apostrophes).");
        }
    }

    private int getIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                printError("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    private double getDoubleInRange(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) return value;
                printError("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    private String getGender(Scanner scanner) {
        while (true) {
            System.out.print("Gender (Male/Female) or press Enter to skip: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return "Not specified";
            if (input.equalsIgnoreCase("Male") || input.equalsIgnoreCase("Female")) return input;
            printError("Please type Male, Female, or press Enter to skip.");
        }
    }
}


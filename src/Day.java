import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Day {
    private String activity;

    public Day(String activity) {
        this.activity = activity;
    }

    // Get today's date in Dutch format
    public String getFormattedDate() {
        LocalDate today = LocalDate.now(); // automatically gets current date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.forLanguageTag("nl"));
        return today.format(formatter);
    }

    public void showDay() {
        System.out.println(getFormattedDate() + " → " + activity);
    }

    public static void main(String[] args) {
        Day day = new Day("Programming in Java");
        day.showDay(); // automatically prints today's date
    }
}

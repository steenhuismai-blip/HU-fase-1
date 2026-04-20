public class Exercise {
    private final String name;
    private final String description;
    private boolean completed = false;

    public Exercise(String name, String description){
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
        this.completed = true;
    }

    public String toString() {
        return name + (completed ? " (done)" : "");
    }
}


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class HabitTracker {

    private static final String FILE_NAME = "habits.json";
    private static List<Habit> habits = new ArrayList<>();
    private static int totalPoints = 0;

    public static void main(String[] args) {
        loadHabits();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Habit Tracker ===");
            System.out.println("1. Add Habit");
            System.out.println("2. View Habits");
            System.out.println("3. Mark Habit as Completed");
            System.out.println("4. View Progress");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> addHabit(scanner);
                case 2 -> viewHabits();
                case 3 -> markHabitAsCompleted(scanner);
                case 4 -> viewProgress();
                case 5 -> {
                    saveHabits();
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }

    private static void addHabit(Scanner scanner) {
        System.out.print("Enter habit title: ");
        String title = scanner.nextLine();
        System.out.print("Enter habit description: ");
        String description = scanner.nextLine();

        habits.add(new Habit(title, description));
        System.out.println("Habit added successfully!");
    }

    private static void viewHabits() {
        System.out.println("\n=== Your Habits ===");
        for (int i = 0; i < habits.size(); i++) {
            Habit habit = habits.get(i);
            System.out.printf("%d. %s - %s (Streak: %d days)%n", i + 1, habit.getTitle(), habit.getDescription(), habit.getStreak());
        }
    }

    private static void markHabitAsCompleted(Scanner scanner) {
        viewHabits();
        System.out.print("Select the habit number to mark as completed: ");
        int index = scanner.nextInt() - 1;

        if (index >= 0 && index < habits.size()) {
            Habit habit = habits.get(index);
            if (habit.markCompleted()) {
                totalPoints += 10; // Earn points for each completion
                System.out.println("Habit marked as completed! +10 points earned.");
            } else {
                System.out.println("This habit was already completed today.");
            }
        } else {
            System.out.println("Invalid habit number.");
        }
    }

    private static void viewProgress() {
        System.out.println("\n=== Your Progress ===");
        System.out.printf("Total Points: %d%n", totalPoints);

        System.out.println("Badges Earned:");
        if (totalPoints >= 50) {
            System.out.println("- Streak Master (Complete 5 habits in a row)");
        }
        if (totalPoints >= 100) {
            System.out.println("- Habit Hero (Earn 100 points)");
        }
        // Add more badges as needed
    }

    private static void saveHabits() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            Gson gson = new Gson();
            gson.toJson(habits, writer);
            System.out.println("Habits saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving habits: " + e.getMessage());
        }
    }

    private static void loadHabits() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Habit>>() {}.getType();
            habits = gson.fromJson(reader, listType);
            System.out.println("Habits loaded successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("No previous habits found.");
        } catch (IOException e) {
            System.out.println("Error loading habits: " + e.getMessage());
        }
    }
}

class Habit {
    private final String title;
    private final String description;
    private int streak;
    private String lastCompletedDate;

    public Habit(String title, String description) {
        this.title = title;
        this.description = description;
        this.streak = 0;
        this.lastCompletedDate = "";
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getStreak() {
        return streak;
    }

    public boolean markCompleted() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (!today.equals(lastCompletedDate)) {
            streak++;
            lastCompletedDate = today;
            return true;
        }
        return false;
    }
}

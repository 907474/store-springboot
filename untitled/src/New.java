import java.nio.file.Files;
import java.util.Date; // Legacy Date class
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters; // For start/end of month/year
import java.io.File;
import java.io.FileInputStream; // For reading file content
import java.io.FileOutputStream; // For writing file content
import java.io.IOException; // For handling IO exceptions
import java.nio.charset.StandardCharsets; // For specifying character encoding

public class New {

    public static void main(String[] args) {

        // --- Date and Time Operations ---
        System.out.println("--- Date and Time Operations ---");

        // 1. Initializing and Current Time
        Date legacyDate = new Date(); // Legacy java.util.Date
        System.out.println("Current legacy Date: " + legacyDate);

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalDateTime currentDateTime = LocalDateTime.now();
        System.out.println("Current LocalDate: " + currentDate);
        System.out.println("Current LocalTime: " + currentTime);
        System.out.println("Current LocalDateTime: " + currentDateTime);

        // 2. Setting Specific Date/Time
        LocalDate specificDate = LocalDate.of(2025, 6, 18); // Year, Month, Day
        LocalTime specificTime = LocalTime.of(10, 0, 0); // Hour, Minute, Second
        LocalDateTime specificDateTime = LocalDateTime.of(specificDate, specificTime);
        System.out.println("Specific LocalDate: " + specificDate);
        System.out.println("Specific LocalTime: " + specificTime);
        System.out.println("Specific LocalDateTime: " + specificDateTime);

        // 3. Getting Start/End of Month, Week, Year
        System.out.println("\n--- Temporal Adjusters ---");
        LocalDate firstDayOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDayOfYear = currentDate.with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDayOfYear = currentDate.with(TemporalAdjusters.lastDayOfYear());
        // For start/end of week, you generally need to define what "start of week" means (e.g., Monday or Sunday)
        // For simplicity, let's assume Monday is the start of the week.
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        System.out.println("First day of current month: " + firstDayOfMonth);
        System.out.println("Last day of current month: " + lastDayOfMonth);
        System.out.println("First day of current year: " + firstDayOfYear);
        System.out.println("Last day of current year: " + lastDayOfYear);
        System.out.println("Start of current week (Monday): " + startOfWeek);
        System.out.println("End of current week (Sunday): " + endOfWeek);

        // 4. Advancing/Rewinding Time (Days, Years, Months)
        System.out.println("\n--- Time Manipulation ---");
        LocalDate futureDate = currentDate.plusDays(7);
        LocalDate pastDate = currentDate.minusMonths(3);
        LocalDateTime futureDateTime = currentDateTime.plusYears(1).plusHours(5);
        System.out.println("Current Date + 7 days: " + futureDate);
        System.out.println("Current Date - 3 months: " + pastDate);
        System.out.println("Current DateTime + 1 year + 5 hours: " + futureDateTime);

        // 5. Date Formatting Output (e.g., 2025/06/18 10:00:00)
        System.out.println("\n--- Date Formatting ---");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        System.out.println("Formatted Current LocalDateTime: " + formattedDateTime);
        String formattedSpecificDateTime = specificDateTime.format(formatter);
        System.out.println("Formatted Specific LocalDateTime: " + formattedSpecificDateTime);


        // --- File and IO Operations ---
        System.out.println("\n--- File and IO Operations ---");
        String dirName = "my_java_data";
        String fileName = dirName + File.separator + "example.txt"; // Use File.separator for OS compatibility
        String copyFileName = dirName + File.separator + "example_copy.txt";

        File dataDir = new File(dirName);
        File exampleFile = new File(fileName);
        File copyFile = new File(copyFileName);

        try {
            // 1. File Properties
            System.out.println("\n--- File Properties ---");
            if (!dataDir.exists()) {
                System.out.println("Creating directory: " + dirName + "? " + dataDir.mkdirs());
            }

            if (!exampleFile.exists()) {
                System.out.println("Creating file: " + fileName + "? " + exampleFile.createNewFile());
            }

            System.out.println("File Name: " + exampleFile.getName());
            System.out.println("File Path: " + exampleFile.getPath());
            System.out.println("Absolute Path: " + exampleFile.getAbsolutePath());
            System.out.println("Exists: " + exampleFile.exists());
            System.out.println("Is Directory: " + exampleFile.isDirectory());
            System.out.println("Is File: " + exampleFile.isFile());
            System.out.println("Parent Directory: " + exampleFile.getParent());
            //
            System.out.println("File Length (bytes): " + exampleFile.length());
            System.out.println("Last Modified: " + new Date(exampleFile.lastModified())); // Convert long to Date

            // 2. File Operations (Write, Read, Copy, Delete)
            System.out.println("\n--- File Operations ---");

            // Write content to file
            String contentToWrite = "Hello, this is a test line.\nThis is the second line.";
            try (FileOutputStream fos = new FileOutputStream(exampleFile)) {
                fos.write(contentToWrite.getBytes(StandardCharsets.UTF_8));
                System.out.println("Content successfully written to " + fileName);
            }

            // Read content from file and print to console
            System.out.println("\n--- Reading File Content ---");
            try (FileInputStream fis = new FileInputStream(exampleFile)) {
                int data;
                StringBuilder fileContent = new StringBuilder();
                while ((data = fis.read()) != -1) {
                    fileContent.append((char) data);
                }
                System.out.println("Content of " + fileName + ":\n" + fileContent.toString());
            }

            // Copy file
            System.out.println("\n--- Copying File ---");
            try (FileInputStream in = new FileInputStream(exampleFile);
                 FileOutputStream out = new FileOutputStream(copyFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                System.out.println("File copied from " + fileName + " to " + copyFileName);
            }
//            Files.copy()

            // Delete copied file
            System.out.println("\n--- Deleting File ---");
            if (copyFile.exists()) {
                System.out.println("Deleting " + copyFileName + "? " + copyFile.delete());
            }

        } catch (IOException e) {
            System.err.println("An IO error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up the created directory if it's empty, or the example file
            if (exampleFile.exists() && exampleFile.delete()) {
                System.out.println("Cleaned up " + exampleFile.getName());
            }
            if (dataDir.exists() && dataDir.delete()) { // This will only delete if the directory is empty
                System.out.println("Cleaned up directory " + dataDir.getName());
            } else if (dataDir.exists()) {
                System.out.println("Directory " + dataDir.getName() + " is not empty or could not be deleted.");
            }
        }


        // --- Math Class Operations ---
        System.out.println("\n--- Math Class Operations ---");

        double num1 = 10.5;
        double num2 = 20.3;
        double num3 = -5.7;

        // Min & Max
        System.out.println("Min of " + num1 + " and " + num2 + ": " + Math.min(num1, num2));
        System.out.println("Max of " + num1 + " and " + num2 + ": " + Math.max(num1, num2));

        // Absolute value
        System.out.println("Absolute value of " + num3 + ": " + Math.abs(num3));

        // Rounding
        System.out.println("Floor of " + num1 + ": " + Math.floor(num1)); // Rounds down to nearest integer
        System.out.println("Ceil of " + num1 + ": " + Math.ceil(num1));   // Rounds up to nearest integer
        System.out.println("Round of " + num1 + ": " + Math.round(num1)); // Rounds to nearest long/int
        System.out.println("Round of " + num2 + ": " + Math.round(num2));

        // Power and Square Root
        System.out.println("2 to the power of 3: " + Math.pow(2, 3));
        System.out.println("Square root of 25: " + Math.sqrt(25));

        // Random number (0.0 <= x < 1.0)
        System.out.println("Random number (0.0 to <1.0): " + Math.random());
        // Generate a random integer between 1 and 10
        int randomInt = (int) (Math.random() * 10) + 1;
        System.out.println("Random integer between 1 and 10: " + randomInt);
    }
}

import java.util.Scanner;

public class chatllog {

    public static void main(String[] args) {

        String[] logs = {
            "10:05 alice Hello there",
            "10:10 bob How are you?",
            "10:15 charlie Good morning",
            "10:20 david This is a test",
            "10:25 eve"
        };

        Scanner scanner = new Scanner(System.in);

        // Read keyword from user
        System.out.print("Enter keyword: ");
        String keyword = scanner.nextLine();

        int matches = 0;
        StringBuilder report = new StringBuilder();

        // Process each log line
        for (String line : logs) {

            // Split into maximum 3 parts:
            // time, user, message
            String[] parts = line.split(" ", 3);

            // Skip malformed lines
            if (parts.length < 3) {
                continue;
            }

            String time = parts[0];
            String user = parts[1];
            String message = parts[2];

            // Case-insensitive keyword search
            if (message.toLowerCase().contains(keyword.toLowerCase())) {

                matches++;

                report.append(time)
                      .append(" ")
                      .append(user)
                      .append(": ")
                      .append(message)
                      .append("\n");
            }
        }

        System.out.println("Matches: " + matches);
        System.out.println(report);

        scanner.close();
    }
}
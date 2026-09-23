abstract class Media {
    String title;

    Media(String title) {
        this.title = title;
    }

    // Each media type calculates its own late fee
    abstract double lateFee(int lateDays);
}

// Movie: ₹20 per late day
class Movie extends Media {

    Movie(String title) {
        super(title);
    }

    @Override
    double lateFee(int lateDays) {
        return lateDays * 20;
    }
}

// Book: ₹10 per late day
class Book extends Media {

    Book(String title) {
        super(title);
    }

    @Override
    double lateFee(int lateDays) {
        return lateDays * 10;
    }
}

// Game: ₹30 per late day, with a maximum fee of ₹150
class Game extends Media {

    Game(String title) {
        super(title);
    }

    @Override
    double lateFee(int lateDays) {
        double fee = lateDays * 30;

        if (fee > 150) {
            fee = 150;
        }

        return fee;
    }
}

// Music CD: ₹15 per late day
class MusicCD extends Media {

    MusicCD(String title) {
        super(title);
    }

    @Override
    double lateFee(int lateDays) {
        return lateDays * 15;
    }
}

// Main class
public class MediaLateFees {
    public static void main(String[] args) {

        // Returned batch
        Media[] media = {
            new Movie("Inception"),
            new Book("Java Programming"),
            new Game("Minecraft"),
            new MusicCD("Best Hits"),
            new Movie("Interstellar")
        };

        // Late days for each returned item
        int[] lateDays = {3, 5, 7, 2, 4};

        double totalFees = 0;

        // One loop handles all media types polymorphically
        for (int i = 0; i < media.length; i++) {

            double fee = media[i].lateFee(lateDays[i]);

            System.out.println(
                media[i].title +
                " | Late days: " + lateDays[i] +
                " | Late fee: ₹" + fee
            );

            totalFees += fee;
        }

        System.out.println("----------------------------");
        System.out.println("Total Late Fees: ₹" + totalFees);
    }
}

    
}

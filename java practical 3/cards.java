import java.util.Objects;

class Card {

    private String rank;
    private String suit;

    
    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

   
    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Card other = (Card) obj;

        return rank.equals(other.rank) && suit.equals(other.suit);
    }

    
    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }
}

public class Driver {

    public static void main(String[] args) {

        Card[] cards = new Card[5];
        int count = 0;

        Card[] inputCards = {
            new Card("Ace", "Spades"),
            new Card("King", "Hearts"),
            new Card("Queen", "Diamonds"),
            new Card("Ace", "Spades"),
            new Card("Jack", "Clubs")
        };

        for (Card newCard : inputCards) {

            boolean duplicate = false;

            
            for (int i = 0; i < count; i++) {
                if (newCard.equals(cards[i])) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                System.out.println("Duplicate found: " + newCard);
                break;
            }

            cards[count] = newCard;
            count++;
        }
    }
}
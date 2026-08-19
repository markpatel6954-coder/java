import java.util.Objects;

class Fraction {

    private int num;
    private int den;

   
    public Fraction(int num, int den) {

        if (den == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero");
        }

    
        if (den < 0) {
            num = -num;
            den = -den;
        }

      
        int g = gcd(Math.abs(num), den);

        this.num = num / g;
        this.den = den / g;
    }

   
    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = a % b;
            a = b;
            b = temp;
        }
        return a;
    }

    @Override
    public String toString() {
        return num + "/" + den;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Fraction other = (Fraction) obj;

        return num == other.num && den == other.den;
    }

    
    @Override
    public int hashCode() {
        return Objects.hash(num, den);
    }
}

public class Driver {

    public static void main(String[] args) {

        Fraction f1 = new Fraction(1, 2);
        Fraction f2 = new Fraction(2, 4);
        Fraction f3 = new Fraction(3, 6);

        System.out.println("f1 = " + f1);
        System.out.println("f2 = " + f2);
        System.out.println("f3 = " + f3);

        System.out.println("f1 equals f2: " + f1.equals(f2));
        System.out.println("f1 equals f3: " + f1.equals(f3));
        System.out.println("f2 equals f3: " + f2.equals(f3));
    }
}
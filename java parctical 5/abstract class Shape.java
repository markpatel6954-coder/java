abstract class Shape {
    abstract double area();
}

class Circle extends Shape {
    private double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends Shape {
    private double length;
    private double width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double area() {
        return length * width;
    }
}

class Triangle extends Shape {
    private double base;
    private double height;

    Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    @Override
    double area() {
        return 0.5 * base * height;
    }
}

public class ShapeDemo {
    public static void main(String[] args) {

        Shape[] shapes = {
            new Circle(5),
            new Rectangle(4, 6),
            new Triangle(8, 3),
            new Circle(3)
        };

        double total = 0;
        double largest = 0;

        // One loop handles every shape through polymorphism
        for (Shape shape : shapes) {
            double area = shape.area();

            total += area;

            if (area > largest) {
                largest = area;
            }

            System.out.printf("Area: %.2f, Running total: %.2f%n",
                    area, total);
        }

        System.out.printf("Total area: %.2f%n", total);
        System.out.printf("Largest area: %.2f%n", largest);
    }
}
abstract class Employee {
    protected String name;
    protected int id;

    Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    abstract double monthlySalary();
}

class FullTime extends Employee {
    private double salary;

    FullTime(String name, int id, double salary) {
        super(name, id);
        this.salary = salary;
    }

    @Override
    double monthlySalary() {
        return salary;
    }
}

class PartTime extends Employee {
    private double hours;
    private double rate;

    PartTime(String name, int id, double hours, double rate) {
        super(name, id);
        this.hours = hours;
        this.rate = rate;
    }

    @Override
    double monthlySalary() {
        return hours * rate;
    }
}

class Intern extends Employee {
    private double stipend;

    Intern(String name, int id, double stipend) {
        super(name, id);
        this.stipend = stipend;
    }

    @Override
    double monthlySalary() {
        return stipend;
    }
}

public class PayrollDemo {
    public static void main(String[] args) {

        Employee[] employees = {
            new FullTime("Mark", 101, 50000),
            new PartTime("Neel", 102, 80, 300),
            new Intern("Manav", 103, 15000),
            new FullTime("Manthan", 104, 60000),
            new Intern("Yuvraj", 105, 12000)
        };

        double total = 0;

        for (Employee employee : employees) {
            double salary = employee.monthlySalary();
            total += salary;

            System.out.printf(
                "%s (ID: %d) - Salary: %.2f",
                employee.name,
                employee.id,
                salary
            );

            
            if (employee instanceof Intern) {
                System.out.print(" - Intern");
            }

            System.out.println();
        }

        System.out.printf("Total payroll: %.2f%n", total);
    }
}
abstract class Media {
    protected String title;

    Media(String title) {
        this.title = title;
    }

    abstract double lateFee(int lateDays);
}

class Book extends Media {

    Book(String title) {
        super(title);
    }

    @Override
    double lateFee(int lateDays) {
        return lateDays * 2.0;
    }
}

class DVD extends Media {

    DVD(String title) {
        super(title);
    }

    @Override
    double lateFee(int lateDays) {
        return lateDays * 5.0;
    }
}

class Magazine extends Media {

    Magazine(String title) {
        super(title);
    }

    @Override
    double lateFee(int lateDays) {
        return lateDays * 1.0;
    }
}

class Game extends Media {

    Game(String title) {
        super(title);
    }

    @Override
    double lateFee(int lateDays) {
        return lateDays * 8.0;
    }
}

public class MediaDemo {
    public static void main(String[] args) {

        Media[] returnedMedia = {
            new Book("Java Programming"),
            new DVD("Avengers"),
            new Magazine("Tech Monthly"),
            new Game("Minecraft")
        };

        int[] lateDays = {3, 2, 5, 4};

        double totalFees = 0;

        for (int i = 0; i < returnedMedia.length; i++) {

            double fee = returnedMedia[i].lateFee(lateDays[i]);

            totalFees += fee;

            System.out.printf(
                "%s - Late days: %d - Fee: ₹%.2f%n",
                returnedMedia[i].title,
                lateDays[i],
                fee
            );
        }

        System.out.printf("Total late fees: ₹%.2f%n", totalFees);
    }
}
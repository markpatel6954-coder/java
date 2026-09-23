abstract class Shape {
    abstract double area();
}
class Circle extends Shape {
    double radius;
    Circle(double radius) {
        this.radius = radius;
    }
    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}
class Rectangle extends Shape {
    double length;
    double width;
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
    double base;
    double height;
    Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }
    @Override
    double area() {
        return 0.5 * base * height;
    }
}
class ShapeAreas {
    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle(5),
            new Rectangle(10, 4),
            new Triangle(8, 6),
            new Circle(3),
            new Rectangle(7, 5)
        };
        double total = 0;
        double largest = 0;
        for (Shape s : shapes) {
            double currentArea = s.area();
            System.out.printf("Area: %.2f%n", currentArea);
            total += currentArea;
            if (currentArea > largest) {
                largest = currentArea;
            }
        }
        System.out.println("--------------------");
        System.out.printf("Total Area: %.2f%n", total);
        System.out.printf("Largest Area: %.2f%n", largest);
    }
}
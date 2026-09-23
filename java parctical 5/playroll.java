abstract class Employee {
    String name;
    int id;
    Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }
    abstract double monthlySalary();
}
class FullTime extends Employee {
    double salary;
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
    int hours;
    double rate;
    PartTime(String name, int id, int hours, double rate) {
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
    double stipend;
    Intern(String name, int id, double stipend) {
        super(name, id);
        this.stipend = stipend;
    }
    @Override
    double monthlySalary() {
        return stipend;
    }
}
 class Payroll {
    public static void main(String[] args) {
        Employee[] employees = {
            new FullTime("Rahul", 101, 50000),
            new PartTime("Priya", 102, 80, 300),
            new Intern("Amit", 103, 15000),
            new FullTime("Neha", 104, 60000),
            new PartTime("Riya", 105, 60, 250)
        };
        double totalSalary = 0;
        for (Employee e : employees) {
            double salary = e.monthlySalary();
            System.out.println(
                "Name: " + e.name +
                ", ID: " + e.id +
                ", Salary: ₹" + salary
            );
            if (e instanceof Intern) {
                System.out.println("Note: This employee is an intern.");
            }
            totalSalary += salary;
        }
        System.out.println("----------------------------");
        System.out.println("Total Payroll: ₹" + totalSalary);
    }
}

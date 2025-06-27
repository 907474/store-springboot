import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.util.Objects;

// Annotation
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE)
@interface Review {}

// Abstract Class
abstract class AbstractVehicle {
    protected String brand;
    private int year;
    public AbstractVehicle(String brand, int year) { this.brand = brand; this.year = year; }
    public abstract void startEngine(); // Abstract method
    public void displayInfo() { System.out.println("Brand: " + brand + ", Year: " + year); }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}

// Interface
interface IVehicleActions {
    void accelerate(); // Abstract method
    default void stop() { System.out.println("Vehicle stopped."); } // Default method
    static void checkLights() { System.out.println("Lights checked."); } // Static method
}

// Enum
enum CarType { SEDAN("Sedan"), SUV("SUV");
    private final String desc;
    CarType(String desc) { this.desc = desc; }
    public String getDescription() { return desc; }
}

// Strategy Pattern Interface
interface DrivingStrategy { void drive(); }
class AggressiveDriving implements DrivingStrategy { @Override public void drive() { System.out.println("Driving aggressively."); } }
class DefensiveDriving implements DrivingStrategy { @Override public void drive() { System.out.println("Driving defensively."); } }

// Class demonstrating concepts
@Review
class Car extends AbstractVehicle implements IVehicleActions {
    private String model; // Private instance variable (Encapsulation)
    protected int speed; // Protected instance variable
    String color; // Default (package-private)
    public int doors; // Public
    private boolean isAuto; // Default value false

    public static final int MAX_SPEED = 200; // Static final variable
    private static int totalCars = 0; // Static variable

    private final String licensePlate; // final variable

    CarType type;
    private DrivingStrategy strategy;

    // Inner Class
    // Made public explicitly to resolve potential compiler ambiguity.
    public class Engine {
        private String engineType;
        public Engine(String type) { this.engineType = type; }
        public void start() { System.out.println(engineType + " engine on for " + model); }
    }
    private Engine carEngine;

    // Default Constructor
    public Car() {
        this("Default", "White", 4, false, CarType.SEDAN, "ABC123", new DefensiveDriving());
        System.out.println("Default Car created.");
    }

    // Multi-parameter Constructor (using 'this' and 'super')
    public Car(String model, String color, int doors, boolean isAuto, CarType type, String licensePlate, DrivingStrategy strategy) {
        super("Generic Brand", 2023); // Call superclass constructor
        this.model = model; // 'this' keyword
        this.color = color;
        this.doors = doors;
        this.isAuto = isAuto;
        this.type = type;
        this.licensePlate = licensePlate; // Initialize final
        this.speed = 0;
        this.strategy = strategy;
        this.carEngine = new Engine("V4"); // Inner class instance
        totalCars++;
    }

    // Public method
    public void drive() { System.out.println(model + " driving at " + speed + " km/h. " + strategy.getClass().getSimpleName()); }
    private void internalMethod() { System.out.println("Internal logic."); } // Private method
    protected void checkFluids() { System.out.println(model + " fluid check."); } // Protected method
    void washCar() { System.out.println(model + " washed."); } // Default method

    public static int getTotalCars() { return totalCars; } // Static method

    // Abstract method implementation
    @Override public void startEngine() { System.out.println(model + " engine starting."); carEngine.start(); }
    // Interface method implementation
    @Override public void accelerate() { speed += 10; System.out.println(model + " accelerated to " + speed); }
    // Interface default method override
    @Override public void stop() { System.out.println(model + " stopped hard!"); speed = 0; }

    public void setDrivingStrategy(DrivingStrategy newStrategy) { this.strategy = newStrategy; }

    @Override public String toString() { return "Car [Model: " + model + ", Type: " + type.getDescription() + ", Brand: " + brand + ", LP: " + licensePlate + "]"; }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(model, car.model) && type == car.type;
    }
    @Override public int hashCode() { return Objects.hash(model, type); }
}

// Factory Pattern
class CarFactory {
    public static Car createCar(String type, String model, DrivingStrategy strategy) {
        if ("Sedan".equalsIgnoreCase(type)) return new Car(model, "Blue", 4, true, CarType.SEDAN, "SEDAN-" + Car.getTotalCars(), strategy);
        if ("SUV".equalsIgnoreCase(type)) return new Car(model, "Green", 4, true, CarType.SUV, "SUV-" + Car.getTotalCars(), strategy);
        return new Car(model, "Red", 4, false, CarType.SEDAN, "DEF-" + Car.getTotalCars(), strategy);
    }
}

// Main class to demonstrate
public class Main {
    public static void main(String[] args) {
        System.out.println("--- Class Instantiation ---");
        Car myCar = new Car(); // Default constructor
        myCar.setYear(2024); // Inherited setter
        System.out.println(myCar);

        Car sportsCar = new Car("Sports Model", "Black", 2, true, CarType.SEDAN, "SPORT1", new AggressiveDriving());
        System.out.println(sportsCar);

        System.out.println("\n--- Static Members & Access Modifiers ---");
        System.out.println("Max Speed: " + Car.MAX_SPEED); // Static variable
        System.out.println("Total Cars: " + Car.getTotalCars()); // Static method
        System.out.println("My Car Doors: " + myCar.doors); // Public
        myCar.washCar(); // Default (package-private)
        myCar.checkFluids(); // Protected

        System.out.println("\n--- Inheritance & Interfaces ---");
        myCar.displayInfo(); // Inherited method
        myCar.startEngine(); // Abstract method implementation
        myCar.accelerate(); // Interface method
        myCar.stop(); // Overridden interface default method
        IVehicleActions.checkLights(); // Interface static method

        System.out.println("\n--- Polymorphism ---");
        AbstractVehicle polyVehicle = sportsCar; // Polymorphic reference
        polyVehicle.displayInfo();
        polyVehicle.startEngine();

        IVehicleActions polyAction = myCar;
        polyAction.accelerate();

        System.out.println("\n--- Enum & Strategy Pattern ---");
        System.out.println("Sports Car Type: " + sportsCar.type.getDescription());
        sportsCar.setDrivingStrategy(new DefensiveDriving());
        sportsCar.drive();

        System.out.println("\n--- Factory Pattern ---");
        Car factoryCar = CarFactory.createCar("SUV", "Family SUV", new DefensiveDriving());
        System.out.println(factoryCar);

        System.out.println("\n--- equals & hashCode ---");
        Car carA = new Car("Model X", "Blue", 4, true, CarType.SUV, "LPA", new DefensiveDriving());
        Car carB = new Car("Model X", "Red", 4, false, CarType.SUV, "LPB", new AggressiveDriving());
        System.out.println("carA equals carB: " + carA.equals(carB)); // True if model and type match
    }
}

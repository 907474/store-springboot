import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;

@FunctionalInterface
interface SimpleTask {
    void perform();
}

public class ComprehensiveJavaExample {

    public static void main(String[] args) {

        Runnable runnable = () -> System.out.println("Runnable is running.");
        runnable.run();

        Supplier<String> supplier = () -> "Hello from Supplier!";
        System.out.println(supplier.get());

        Consumer<String> consumer = message -> System.out.println("Consumer consumed: " + message);
        consumer.accept("Test Message");

        Function<Integer, String> function = number -> "The number is: " + number;
        System.out.println(function.apply(10));

        Predicate<Integer> predicate = number -> number > 5;
        System.out.println("Is 10 greater than 5? " + predicate.test(10));
        System.out.println("Is 3 greater than 5? " + predicate.test(3));

        SimpleTask task = () -> System.out.println("Custom functional interface task is performed.");
        task.perform();

        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve");

        List<String> filteredAndMappedNames = names.stream()
                .filter(name -> name.length() > 4)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("Filtered and mapped names: " + filteredAndMappedNames);

        String concatenatedNames = names.stream()
                .reduce("", (current, next) -> current.isEmpty() ? next : current + ", " + next);
        System.out.println("Concatenated names: " + concatenatedNames);

        System.out.println("Using method reference for printing:");
        names.forEach(System.out::println);

        Function<List<String>, List<String>> toUpperCase = list -> list.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        processList(names, toUpperCase).forEach(System.out::println);
    }

    public static <T, R> R processList(T input, Function<T, R> processor) {
        return processor.apply(input);
    }
}

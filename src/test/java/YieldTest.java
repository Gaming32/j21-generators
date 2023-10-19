import io.github.gaming32.generators.Generators;
import io.github.gaming32.generators.Yield;

import java.util.List;

public class YieldTest {
    public static void main(String[] args) {
        for (final String value : test()) {
            System.out.println(value);
        }
    }

    public static Iterable<String> test() {
        return Generators.createGenerator(() -> {
            System.out.println("Before a");
            Yield.yield_("a");
            System.out.println("Before b");
            Yield.yield_("b");
            Yield.yieldAll(List.of("1", "2", "3"));
            System.out.println("Before c");
            Yield.yield_("c");
            System.out.println("After c");
        });
    }
}

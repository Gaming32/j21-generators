import io.github.gaming32.generators.Generator;
import io.github.gaming32.generators.Generators;
import io.github.gaming32.generators.Yield;

import java.util.List;
import java.util.stream.StreamSupport;

public class YieldTest {
    public static void main(String[] args) {
//        for (final String value : test()) {
//            System.out.println(value);
//        }
//        sendTest();
        sendYieldAllTest();
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

    public static void sendTest() {
        final Generator<String> test = Generators.createGeneratorIterator(() -> {
            final Object sent = Yield.yield_("hi");
            System.out.println(sent);
        });
        System.out.println(test.next());
        test.send("bye");
//        test.send("try");
    }

    public static void sendYieldAllTest() {
        final Iterable<String> sendReceiver = Generators.createGenerator(() -> {
            Object sent = Yield.yield_("hi");
            System.out.println(sent);
            sent = Yield.yield_("bye");
            System.out.println(sent);
        });
        final Generator<String> test = Generators.createGeneratorIterator(() -> {
            Yield.yieldAll(sendReceiver);
        });
        System.out.println(test.next());
        test.send("bye");
        test.send("try");
    }
}

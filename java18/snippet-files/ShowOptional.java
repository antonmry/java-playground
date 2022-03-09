import java.util.Optional;

public class ShowOptional {
    void show(Optional<String> x) {
        // @start region="example"
        if (x.isPresent()) {
            System.out.println("x: " + x.get());
        }
        // @end
    }
}

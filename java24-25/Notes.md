# Java 24 and 25

- <https://en.wikipedia.org/wiki/Java_version_history>
- <https://openjdk.java.net/projects/jdk/24/>
- <https://openjdk.java.net/projects/jdk/25/>
- <https://jdk.java.net/24/>
- <https://jdk.java.net/25/>

## Install Java 23, 24 (and 26 ea)

```sh
curl -s "https://get.sdkman.io" | bash
sdk install java 26.ea.22-open
sdk use java 26.ea.22-open
jshell --enable-preview --enable-native-access=ALL-UNNAMED
```

## New features

### [JEP-488](https://openjdk.java.net/jeps/488): Primitive Types in Patterns, instanceof, and switch (Second Preview)

### [JEP-507](https://openjdk.java.net/jeps/507): Primitive Types in Patterns, instanceof, and switch (Third Preview)

```java
public Object processValue(Object obj) {
    return switch (obj) {
        case int i -> "Integer: " + i;
        case long l -> "Long: " + l;
        case String s -> "String: " + s;
        default -> "Unknown: " + obj;
    };
}

processValue(2)
processValue("hello CoruñaJUG!")
```

### [JEP-492](https://openjdk.java.net/jeps/492): Flexible Constructor Bodies (Third Preview)

### [JEP-513](https://openjdk.java.net/jeps/513): Flexible Constructor Bodies

```java
class A {
    final String name;
    A(String name) { this.name = name; }
}

class B extends A {
    B(String name) {
        // pre logic allowed
        var trimmed = name.trim();

        super(trimmed); // this is now legal not at first line

        System.out.println("constructed B with name: '" + this.name + "'");
    }
}

new B("  CoruñaJUG  ")
```

### [JEP-494](https://openjdk.java.net/jeps/494): Module Import Declarations (Second Preview)

### [JEP-511](https://openjdk.java.net/jeps/511): Module Import Declarations

```java
import module java.base;

void main() throws IOException {
    var names = List.of("Alice", "Bob", "Charlie");
    Path filePath = Paths.get("test.txt");
    Files.write(filePath, names);
}
// No need to import java.nio.file.Paths, java.util.List, java.nio.file.Files
```

### [JEP-495](https://openjdk.java.net/jeps/495): Simple Source Files and Instance Main Methods (Fourth Preview)

### [JEP-512](https://openjdk.java.net/jeps/512): Simple Source Files and Instance Main Methods

```java
// Traditional Java program
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}

// Now, with simple source files and instance main methods
void main() {
    System.out.println("Hello, World!");
}
```

```sh
javac HelloWorld.java
java HelloWorld

javac hi.java
java hi
```

### [JEP-485](https://openjdk.java.net/jeps/485): Stream Gatherers

First a quick look to gather:

```java
import java.util.stream.*;

Stream.of(1,2,3,4,5)
    .gather(Gatherers.fold(() -> 0, (acc, x) -> acc + x))
    .toList();

Stream.of(1,2,3)
    .gather(Gatherers.mapConcurrent(3, i -> i * i))
```

Custom

```java
import java.util.stream.*;
import java.util.stream.Gatherer;

Gatherer<Integer, Void, Integer> doubleIt =
    Gatherer.ofSequential(
        Gatherer.Integrator.ofGreedy(
            (Void st, Integer e, Gatherer.Downstream<? super Integer> down) ->
                down.push(e * 2)
        )
    );

Stream.of(1,2,3).gather(doubleIt).toList();  // [2, 4, 6]
```

See [gatherers4j](https://tginsberg.github.io/gatherers4j/) for more gatherers.

## [JEP 499](https://openjdk.java.net/jeps/499): Structured Concurrency (Fourth Preview)

## [JEP 505](https://openjdk.java.net/jeps/505): Structured Concurrency (Fifth Preview)

```java
import java.util.concurrent.*;

String findUser() { return "Anton"; }
int fetchOrder() { return 42; }

try (var scope = StructuredTaskScope.open()) {
    var user  = scope.fork(() -> findUser());
    var order = scope.fork(() -> fetchOrder());

    scope.join(); // default policy: wait for all or stop on failure
    System.out.println(user.get() + " / " + order.get());
}
```

Timeout with cancellation:

```java
import java.time.Duration;
import java.util.concurrent.*;

void zzz(long ms){
    try{ Thread.sleep(ms);}
    catch(InterruptedException e){ Thread.currentThread().interrupt(); throw new RuntimeException(e);}
}

try (var s = StructuredTaskScope.open(
         StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow(),
         c -> c.withTimeout(Duration.ofSeconds(1)))) {

    s.fork(() -> { zzz(800);  return "A"; });
    s.fork(() -> { zzz(1500); return "B"; });

    var result = s.join();
    System.out.println(result);
} catch (StructuredTaskScope.TimeoutException e) {
    System.out.println("Timed out");
}
```

Race: first successful result wins

```java
import java.util.concurrent.*;

String slow(int ms, String v) {
    try { Thread.sleep(ms); return v; }
    catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new RuntimeException(e); }
}

try (var s = StructuredTaskScope.open(
         StructuredTaskScope.Joiner.<String>anySuccessfulResultOrThrow())) {
    s.fork(() -> slow(1200, "slow"));
    s.fork(() -> slow(200,  "fast"));
    System.out.println(s.join());  // "fast"
}
```

### [JEP-487](https://openjdk.java.net/jeps/487): Scoped Values (Fourth Preview)

### [JEP-506](https://openjdk.java.net/jeps/506): Scoped Values

```java
ScopedValue<String> RID = ScopedValue.newInstance();

void deep() { System.out.println("RID=" + RID.get()); }

ScopedValue.where(RID, "req-123").run(() -> deep());  // prints: RID=req-123
```

Rebind inside the scope:

```java
ScopedValue<String> X = ScopedValue.newInstance();

ScopedValue.where(X, "hello").run(() -> {
    System.out.println(X.get());                  // hello
    ScopedValue.where(X, "goodbye").run(() ->     // nested binding
        System.out.println(X.get())               // goodbye
    );
    System.out.println(X.get());                  // hello
});
```

Inherit into child virtual threads (with structured concurrency):

```java
import java.util.concurrent.*;

ScopedValue<String> RID = ScopedValue.newInstance();
void log(String tag) { System.out.println(tag + " -> " + RID.get()); }

ScopedValue.where(RID, "req-42").run(() -> {
    try (var scope = StructuredTaskScope.open()) {
        scope.fork(() -> { log("A"); return 0; });
        scope.fork(() -> { log("B"); return 0; });
        try { scope.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
});
```

### [JEP](https://openjdk.java.net/jeps/):

```java
```

### [JEP](https://openjdk.java.net/jeps/):

```java
```

### [JEP](https://openjdk.java.net/jeps/):

```java
```

### [JEP](https://openjdk.java.net/jeps/):

```java
```

### [JEP](https://openjdk.java.net/jeps/):

```java
```

## TODO

- [ ] Tmux and slime
- [x] Markdown preview?
- [x] Neovim syntax

```
let g:markdown_fenced_languages = ['java']
syntax on
colorscheme lunarpeche
set background=light
```

## Resources

- [Java 24 with examples](https://www.happycoders.eu/java/java-24-features/)
- [Java 25 with examples](https://www.happycoders.eu/java/java-25-features/)
- <https://sdkman.io/>
- <https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html>
- <https://www.jbang.dev/>

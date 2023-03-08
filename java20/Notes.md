# Java 20

- <https://en.wikipedia.org/wiki/Java_version_history>
- <https://openjdk.java.net/projects/jdk/20/>
- <https://jdk.java.net/20/>

## Install Java 20

```sh
sdk install java 20.ea.36-open
sdk use java 20.ea.36-open
jshell --enable-preview --add-modules jdk.incubator.concurrent
```

## New features

## [JEP 420](https://openjdk.java.net/jeps/420): Pattern Matching for switch (Second Preview)
## [JEP 427](https://openjdk.java.net/jeps/427): Pattern Matching for switch (Third Preview)
## [JEP 433](https://openjdk.java.net/jeps/433): Pattern Matching for switch (Fourth Preview)

```java

// Java 17

Object test = "test!";
switch (test) {
  case Integer i -> System.out.println("Integer!");
  case String s  -> System.out.println("Hello " + s);
  default        -> System.out.println("Nop!");
}
```

```java

// Java 18

switch (test) {
  case String s                                 -> System.out.println("Hello " + s);
  case String s && s.equals("unreachable code") -> System.out.println("Hello " + s);
  default                                       -> System.out.println("Nop!");
}
```

```java

sealed interface S permits A, B, C {} // Java 17, JEP 409
final class A implements S {}
final class B implements S {}
record C(int i) implements S {}  // Implicitly final

static int testSealedExhaustive(S s) {
    return switch (s) {
        case A a -> 1;
        case B b -> 2;
        case C c -> 3;
    };
}
```

```java
Object o = "Anton";

switch (o) {
  case Integer i -> {
    if (i >= 0)
      System.out.println("Positive number");
    else
      System.out.println("Negative number");
  }
  case String s -> {
    if (s.contains("foo"))
      System.out.println("String with 'foo'");
    else
      System.out.println("String without 'foo'");
  }
  default -> System.out.println("No String or Integer");
}
```

```java
switch (o) {
  case Integer i when i >= 0 -> System.out.println("Positive number");
  case Integer i -> System.out.println("Negative number");
  case String s when s.contains("foo") -> System.out.println("String with 'foo'");
  case String s -> System.out.println("String without 'foo'");
  default -> System.out.println("No String or Integer");
}
```

```java

sealed interface S permits A, B {}
record A(int x, int y) implements S {}
record B(int x, int y) implements S {}

S o = new A(0, 0);

switch (o) {
  case A(int x, int y) when x >= 0 -> System.out.println("A, positive x");
  case A(int x, int y) when x < 0 -> System.out.println("B, negative x");
  case B(int x, int y) -> System.out.println("B");
  default -> System.out.println("Any of the previous options");
}
```

```java
S o = null;

switch (o) {
  case A(int x, int y) when x >= 0 -> System.out.println("A, positive x");
  case A(int x, int y) when x < 0 -> System.out.println("B, negative x");
  case B(int x, int y) -> System.out.println("B");
  //case null -> System.out.println("Null");
  default -> System.out.println("Any of the previous options");
}
```

## [JEP 405](https://openjdk.java.net/jeps/405): Record Patterns (First Preview)
## [JEP 432](https://openjdk.java.net/jeps/432): Record Patterns (Second Preview)

```java
sealed interface S permits A, B {}
record A(int x, int y) implements S {}
record B(int x, int y) implements S {}

S o = new A(0, 0);

//JEP 394, java 16
if (o instanceof A a) {
    System.out.println(a.x() + a.y());
}

if (o instanceof A(int x, int y)) {
    System.out.println(x + y);
}

```
## [JEP 425](https://openjdk.java.net/jeps/425): Virtual Threads (First Preview)
## [JEP 436](https://openjdk.java.net/jeps/436): Virtual Threads (Second Preview)

> virtual threads can significantly improve application throughput when
> the number of concurrent tasks is high (more than a few thousand), and
> The workload is not CPU-bound, since having many more threads than processor
> cores cannot improve throughput in that case.

```java

var thread = Thread.startVirtualThread(() -> {
  System.out.println("Hello from the virtual thread");
});

thread.join();

Thread.ofPlatform().start(() -> System.out.println(Thread.currentThread()));
Thread.ofVirtual().start(() -> System.out.println(Thread.currentThread()));

```

```java

try (var executor = Executors.newFixedThreadPool(1_000)) {
    IntStream.range(0, 10_000).forEach(i -> {
        executor.submit(() -> {
            Thread.sleep(1000);
            return i;
        });
    });
}

try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    IntStream.range(0, 10_000).forEach(i -> {
        executor.submit(() -> {
            Thread.sleep(1000);
            return i;
        });
    });
}
```

```java
Thread thread = Thread.ofVirtual().start(() -> {int result = 12 / 0;});

Thread thread = Thread.ofPlatform().start(() -> {int result = 12 / 0;});
```

There's no silver bullet. Be careful with:
- Framework / library support
- Thread fairness
- Synchronization
- JNI
- ThreadLocal variables

- [Virtual Thread Deep Dive - Inside Java Newscast #23](https://nipafx.dev/inside-java-newscast-23/)
- [Launching 10 millions virtual threads with Loom - JEP Café #12](https://inside.java/2022/07/07/jepcafe12/)
- [Java 19 Virtual Threads - JEP Café #11](https://inside.java/2022/06/08/jepcafe11/)
- [Loom and Thread Fairness](https://www.morling.dev/blog/loom-and-thread-fairness/)
- [Going inside Java's Project Loom and virtual threads](https://blogs.oracle.com/javamagazine/post/going-inside-javas-project-loom-and-virtual-threads)

## [JEP 428](https://openjdk.java.net/jeps/428): Structured Concurrency (First Incubator)
## [JEP 437](https://openjdk.java.net/jeps/437): Structured Concurrency (Second Incubator)

```java
String getUser() throws InterruptedException {
    Thread.sleep(3_000);
    return "Anton";
};

Integer getOrder() throws InterruptedException {
    Thread.sleep(2000);
//    return 10;
    return 10/0;
};

String theUser  = getUser();
int theOrder = getOrder();
System.out.println(theUser + ": " + theOrder);
```

```java
ExecutorService esvc = Executors.newFixedThreadPool(2);
Future<String>  user  = esvc.submit(() -> getUser());
Future<Integer> order = esvc.submit(() -> getOrder());
String theUser  = user.get();   // Join findUser
int theOrder = order.get();  // Join fetchOrder
System.out.println(theUser + ": " + theOrder);
```

```java
import jdk.incubator.concurrent.StructuredTaskScope;

try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String>  user  = scope.fork(() -> getUser());
    Future<Integer> order = scope.fork(() -> getOrder());

    scope.join();           // Join both forks
    scope.throwIfFailed();  // ... and propagate errors

    // Here, both forks have succeeded, so compose their results
    System.out.println(user.resultNow() + ": " + order.resultNow());
}
```

- [Java Asynchronous Programming Full Tutorial with Loom and Structured Concurrency - JEP Café #13](https://inside.java/2022/08/02/jepcafe13/)
- [Project Loom Brings Structured Concurrency - Inside Java Newscast #17](https://www.youtube.com/watch?v=2J2tJm_iwk0)

## [JEP 429](https://openjdk.java.net/jeps/429): Scoped Values (Incubator)


```java
class Server {
  
    final static ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();      // (1)

    void serve(Request request, Response response) {
        var level     = (request.isAuthorized() ? ADMIN : GUEST);
        var user = new User(level);
        CURRENT_USER.set(user);                                            // (2)
        Application.handle(request, response);
    }
}

class DatabaseManager {
    DBConnection open() {
        var user = Server.CURRENT_USER.get();                              // (3)
        if (!user.canOpen()) throw new InvalidUserException();
        return new DBConnection(...);                                      // (4)
    }
}
```

Design flaws with ThreadLocal:

- Unconstrained mutability: variable's get() and set() methods can be called at
  any time.
- Unbounded lifetime: developers often forget to call ThreadLocal.remove().
- Expensive inheritance: the overhead of thread-local variables when utilizing
  a large number of threads.

```java
class Server {
    final static ScopedValue<User> CURRENT_USER = new ScopedValue<>();      // (1)

    void serve(Request request, Response response) {
        var level = (request. isAuthorized()? ADMIN : GUEST);
        var user  = new User(level);
      
        ScopedValue.where(CURRENT_USER, user)                               // (2)
                   .run(() -> Application.handle(request, response));       // (3)
    }
}

class DatabaseManager {
    DBConnection open() {
        var user = Server.CURRENT_USER.get();                               // (4)
        if (!user.canOpen()) throw new InvalidUserException();
        return new DBConnection(...);
    }
}
```

Advantages of Scoped Values:

- They are only valid during the lifetime of the Runnable passed to the where
  method.
- A scoped value is immutable - it can only be reset for a new scope by
  rebinding.
- The child threads created by StructuredTaskScope have access to the scoped
  value of the parent thread.

- [Java 20 sneak peek](https://blogs.oracle.com/post/java-20-preview)
- [JEP 429: Extent-Local Variables to Promote Immutability in Java](https://www.infoq.com/news/2022/09/extent-local-variables-java/)
- [Scoped values in Java](https://www.happycoders.eu/java/scoped-values/)

## Resources

- <https://sdkman.io/>
- <https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html>
- <https://www.jbang.dev/>

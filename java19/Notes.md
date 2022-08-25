# Java 19

- https://en.wikipedia.org/wiki/Java_version_history
- https://openjdk.java.net/projects/jdk/19/

## Install Java 19

```sh
sdk install java 19-open
sdk use java 19-open
jshell --enable-preview --add-modules jdk.incubator.concurrent
```

## New features

## [JEP 425](https://openjdk.java.net/jeps/425): Virtual Threads (Preview)

> virtual threads can significantly improve application throughput when
>
> The number of concurrent tasks is high (more than a few thousand), and
> The workload is not CPU-bound, since having many more threads than processor
> cores cannot improve throughput in that case.

```java
Thread thread = Thread.ofVirtual().start(() -> System.out.println("Hello from the virtual thread"));

Thread thread = Thread.ofPlatform().start(() -> System.out.println("Hello from the platform thread"));

var vThread = Thread.startVirtualThread(() -> {
  System.out.println("Hello from the virtual thread");
});
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

## [JEP 428](https://openjdk.java.net/jeps/428): Structured Concurrency (Incubator)

```java
String getUser() throws InterruptedException {
    Thread.sleep(5000);
    return "Anton";
};

Integer getOrder() {
    return 10;
//    return 10/0;
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

## [JEP 427](https://openjdk.java.net/jeps/427): Pattern Matching for switch (Third Preview)

## [JEP 405](https://openjdk.java.net/jeps/405): Record Patterns (Preview)

## [JEP 422](https://openjdk.java.net/jeps/422): Linux/RISC-V Port

## [JEP 424](https://openjdk.java.net/jeps/424): Foreign Function & Memory API (Preview)

## [JEP 426](https://openjdk.java.net/jeps/426): Vector API (Fourth Incubator)

## Resources

- https://sdkman.io/
- https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html
- https://www.jbang.dev/
- https://jdk.java.net/19/
- [Java 19 - The Best Java Release? - Inside Java Newscast #27](https://www.youtube.com/watch?v=UG9nViGZCEw)
- Java Magazine: [Coming to Java 19: Virtual threads and platform threads](https://blogs.oracle.com/javamagazine/post/java-loom-virtual-threads-platform-threads)
- [Java Asynchronous Programming Full Tutorial with Loom and Structured Concurrency - JEP Café #13](https://inside.java/2022/08/02/jepcafe13/)
- [Loom and Thread Fairness](https://www.morling.dev/blog/loom-and-thread-fairness/)
- [Project Loom Brings Structured Concurrency - Inside Java Newscast #17](https://www.youtube.com/watch?v=2J2tJm_iwk0)
- Java Magazine: [Pattern matching updates for Java 19's JEP 427: when and null](https://blogs.oracle.com/javamagazine/post/java-pattern-matching-switch-when-null)

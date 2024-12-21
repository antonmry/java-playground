# Java 23

- <https://en.wikipedia.org/wiki/Java_version_history>
- <https://openjdk.java.net/projects/jdk/23/>
- <https://jdk.java.net/23/>

## Install Java 23

```sh
sdk install java 22.ea.36-open
sdk use java 22.ea.36-open
jshell --enable-preview --enable-native-access=ALL-UNNAMED
```

## New features

## [JEP 456](https://openjdk.java.net/jeps/456): Unnamed Variables & Patterns

In preview in Java 21, finalized in Java 22.

```java
try {
  int number = Integer.parseInt("a");
} catch (NumberFormatException e) {
  System.err.println("Not a number");
}

try {
  int number = Integer.parseInt("a");
} catch (NumberFormatException _) {
  System.err.println("Not a number");
}
```

```java
Map<String, Integer> map = new HashMap<>();
map.put("John", 5);
map.forEach((_, x) -> System.out.println(x))
```

## [JEP 447](https://openjdk.java.net/jeps/447): Statements before super(...) (Preview)

```java

public class Rectangle {
  String color;
  double width;
  double length;

  public Rectangle(String color, double width, double lenght) {
      color = color;
      width = width;
      length = length;
    }
};

public class Square extends Rectangle {
  public Square(String color, int area) {
    this(color, Math.sqrt(validateArea(area)));
  }

  private static int validateArea(int area) {
    if (area < 0) throw new IllegalArgumentException();
    return area;
  }

  private Square(String color, double sideLength) {
    super(color, sideLength, sideLength);
  }
}

public class Square extends Rectangle {
  public Square(String color, int area) {
    if (area < 0) throw new IllegalArgumentException();
    double sideLength = Math.sqrt(area);
    super(color, sideLength, sideLength);
  }
}

```

## [JEP 461](https://openjdk.java.net/jeps/461): Stream Gatherers (Preview)

https://download.java.net/java/early_access/jdk23/docs/api/java.base/java/util/stream/Gatherers.html

```java
Stream.of(1,2,3,4,5,6,7,8).gather(Gatherers.windowFixed(3)).toList();

Stream.of(1,2,3,4,5,6,7,8).gather(Gatherers.windowSliding(2)).toList();

Stream.of(1,2,3,4,5,6,7,8).gather(Gatherers.windowSliding(6)).toList();

Stream.of(1,2,3,4,5,6,7,8,9).gather(
  Gatherers.fold(() -> "", (string, number) -> string + number)
).findFirst();

 Stream.of(1,2,3,4,5,6,7,8,9).
  gather(
    Gatherers.scan(() -> "", (string, number) -> string + number)
  ).toList();

 Stream.of("a","b","c","d","e","f","g","h","i").
  gather(
    Gatherers.mapConcurrent(10, s -> s.toUpperCase(Locale.ROOT))
  ).toList();

```

## [JEP 458](https://openjdk.java.net/jeps/458): Launch Multi-File Source-Code Programs
## [JEP 463](https://openjdk.java.net/jeps/463): Implicitly Declared Classes and Instance Main Methods (Second Preview)

```sh
java --enable-preview --source 22 Hello.java Anton
```

## [JEP 454](https://openjdk.java.net/jeps/454): Foreign Function & Memory API

JNI -> Joylessly Navigating the Inferno

> Project Panama finalization after a total of eight incubator and preview versions

```java
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.Arena;
import java.lang.invoke.MethodHandle;

SymbolLookup stdlib = Linker.nativeLinker().defaultLookup();

MethodHandle strlen = Linker.nativeLinker().downcallHandle(
  stdlib.find("strlen").orElseThrow(),FunctionDescriptor.of(
    ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));

try (Arena offHeap = Arena.ofConfined()) {
  MemorySegment str = offHeap.allocateFrom("Happy Coding!");
  long len = (long) strlen.invoke(str);
  System.out.println("len = " + len);
}

```

## [JEP 457](https://openjdk.java.net/jeps/457): Class-File API (Preview)

> The new API is intended to replace the bytecode manipulation framework ASM,
> which is used intensively in the JDK.

## [JEP 423](https://openjdk.java.net/jeps/423): Region Pinning for G1

JNI related. Avoid unnecesary pauses.

## [JEP 459](https://openjdk.java.net/jeps/459): String Templates (Second Preview)

Already covered in Java 21 session.

## [JEP 464](https://openjdk.java.net/jeps/464): Scoped Values (Second Preview)
## [JEP 462](https://openjdk.java.net/jeps/462): Structured Concurrency (Second Preview)

Already covered in Java 20 session.

## [JEP 460](https://openjdk.java.net/jeps/460): Vector API (Seventh Incubator)

Already covered in Java 18 session.

## Resources

- [Java 22 with examples](https://www.happycoders.eu/java/java-22-features/)
- [Hello, Java 22!](https://spring.io/blog/2024/03/19/hello-java-22)
- <https://sdkman.io/>
- <https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html>
- <https://www.jbang.dev/>

# Java 21

- <https://en.wikipedia.org/wiki/Java_version_history>
- <https://openjdk.java.net/projects/jdk/21/>
- <https://jdk.java.net/21/>

## Install Java 21

```sh
sdk install java 21.ea.35-open
sdk use java 21.ea.35-open
jshell --enable-preview --enable-native-access=ALL-UNNAMED
```

## New features

## [JEP 444](https://openjdk.java.net/jeps/444): Virtual Threads
## [JEP 453](https://openjdk.java.net/jeps/453): Structured Concurrency (Preview)
## [JEP 446](https://openjdk.java.net/jeps/446): Scoped Values (Preview)

Already covered in Java 20.

```java
Thread.ofPlatform().start(() -> System.out.println(Thread.currentThread()));
Thread.ofVirtual().start(() -> System.out.println(Thread.currentThread()));
```

> The only significant change is that the StructuredTaskScope::fork(...) method returns a [Subtask] rather than a Future

## [JEP 441](https://openjdk.java.net/jeps/441): Pattern Matching for switch
## [JEP 440](https://openjdk.java.net/jeps/440): Record Patterns

Already covered in Java 20 (and before)

```java

sealed interface S permits A {}
record A(int x, int y) implements S {}

S o = new A(0, 1);

switch (o) {
  case A(int x, int y) when x >= 10 -> x;
  case A(int x, int y) when x <= 0 -> y;
  default -> -1;
}
```

## [JEP 443](https://openjdk.java.net/jeps/443): Unnamed Patterns and Variables (Preview)

```java

sealed interface S permits A{}
record A(int x, int y) implements S {}

S o = new A(0, 1);

if (o instanceof A(int x, int y)) {
    System.out.println(x + y);
}

if (o instanceof A(int x, int _)) {System.out.println(x);}

A[] as = new A[]{new A(0,0), new A(1,1)}

for (A a: as) {
  System.out.println(a.x()+a.y());
}

for (A _: as) {System.out.println("Hello");}

try {
  int i = Integer.parseInt("a");
} catch (NumberFormatException e) {
  System.out.println("Bad number");
}

try { int i = Integer.parseInt("a");} catch (NumberFormatException _) { System.out.println("Bad number");}
```

## [JEP 445](https://openjdk.java.net/jeps/445): Unnamed Classes and Instance Main Methods (Preview)

```java
public class HelloWorld {
  public static void main(String[] args) {
    System.out.println("Hello world!");
  }
}
```

```java
void main() {
  System.out.println("Hello world!");
}
```

```sh
javac -source 21 --enable-preview hello.java
java --enable-preview hello
rm hello.class

java --source 21 --enable-preview hello.java
```

## [JEP 430](https://openjdk.java.net/jeps/430): String Templates (Preview)

See https://openjdk.org/jeps/430

```java
int x = 10, y = 2;
String s = STR."\{x} + \{y} = \{x + y}";

String str = "\{x} + \{y} = \{x + y}";

import static java.lang.StringTemplate.RAW
StringTemplate str = RAW."\{x} + \{y} = \{x + y}";
String info = STR.process(str);

import static java.util.FormatProcessor.FMT;
String s = FMT."%03d\{x} + %03d\{y} = %03d\{x + y}";

```

- STR, FMT, and RAW are static instances of the default JDK provided
  StringFormatters which return String instances
- Slash for backward compability

> ResultSet rs = DB."SELECT * FROM Person p WHERE p.last_name = \{name}";

## [JEP 431](https://openjdk.java.net/jeps/431): Sequenced Collections

```java
interface SequencedCollection<E> extends Collection<E> {
    // new method
    SequencedCollection<E> reversed();
    // methods promoted from Deque
    void addFirst(E);
    void addLast(E);
    E getFirst();
    E getLast();
    E removeFirst();
    E removeLast();
}
```

```java
var list = List.of(1,2,3,4)

list.get(0)
list.getFirst()

list.get(list.size() - 1)
list.getLast()

list.reversed()

IntStream.rangeClosed(1, list.size()).forEach(i -> System.out.println("Value: " + list.get(list.size() - i)))
list.reversed().forEach(i -> System.out.println("Value: " + i))
```

```java
var map = Map.of("key1", "value1", "key2", "value2");

map.firstEntry()

var map = new LinkedHashMap<>();
map.put( "key1", "value1" );
map.put( "key2", "value2" );

map
map.firstEntry()
map
map.pollFirstEntry()
map
```

## [JEP 439](https://openjdk.java.net/jeps/439): Generational ZGC

> ZGC (JEP 333) is designed for low latency and high scalability.
> It has been available for production use since JDK 15 (JEP 377).

> Improve application performance by extending the Z Garbage Collector (ZGC) to
> maintain separate generations for young and old objects. This will allow ZGC
> to collect young objects - which tend to die young - more frequently.

## [JEP 442](https://openjdk.java.net/jeps/442): Foreign Function & Memory API (Third Preview)
## [JEP 448](https://openjdk.java.net/jeps/448): Vector API (Sixth Incubator)

Already covered in Java 20 (or before)

## [JEP 449](https://openjdk.java.net/jeps/449): Deprecate the Windows 32-bit x86 Port for Removal
## [JEP 451](https://openjdk.java.net/jeps/451): Prepare to Disallow the Dynamic Loading of Agents
## [JEP 452](https://openjdk.java.net/jeps/452): Key Encapsulation Mechanism API

## Resources

- [Java 21: sneak peek](https://blogs.oracle.com/javamagazine/post/java-21-sneak-peek)
- <https://sdkman.io/>
- <https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html>
- <https://www.jbang.dev/>

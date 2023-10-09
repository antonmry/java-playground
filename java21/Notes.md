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

Already covered in Java 20 (and before).

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

443:	Unnamed Patterns and Variables (Preview)

```java

sealed interface S permits A{}
record A(int x, int y) implements S {}

S o = new A(0, 1);

if (o instanceof A(int x, int y)) {
    System.out.println(x + y);
}

if (o instanceof A(int x, int 1)) {System.out.println(x);}

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

try { int i = Integer.parseInt(a);} catch (NumberFormatException _) { System.out.println("Bad number");}
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

446:	Scoped Values (Preview)

439:	Generational ZGC
449:	Deprecate the Windows 32-bit x86 Port for Removal
451:	Prepare to Disallow the Dynamic Loading of Agents
452:	Key Encapsulation Mechanism API
442:	Foreign Function & Memory API (Third Preview)
448:	Vector API (Sixth Incubator)

## [JEP 4](https://openjdk.java.net/jeps/4): 
## [JEP 4](https://openjdk.java.net/jeps/4): 
## [JEP 4](https://openjdk.java.net/jeps/4): 
## [JEP 4](https://openjdk.java.net/jeps/4): 
## [JEP 4](https://openjdk.java.net/jeps/4): 
## [JEP 4](https://openjdk.java.net/jeps/4): 
## [JEP 4](https://openjdk.java.net/jeps/4): 












## [JEP 433](https://openjdk.java.net/jeps/433): Pattern Matching for switch (Fourth Preview)

- An exhaustive switch (i.e., a switch expression or a pattern switch
  statement) over an enum class now throws MatchException rather than
  IncompatibleClassChangeError if no switch label applies at run time.
- The grammar for switch labels is simpler.
- Inference of type arguments for generic record patterns is now supported in
  switch expressions and statements, along with the other constructs that
  support patterns.

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
enum Color { RED, GREEN, BLUE; }
var c = Color.RED;
var result = switch(c) {
  case RED -> 0;
  case GREEN -> 1;
  case BLUE -> 2;
}
```

```java

sealed interface S permits A, B {}
record A(int x, int y) implements S {}
record B(int x, int y) implements S {}

S o = new A(0, 0);

switch (o) {
  case A(int x, int y) when x >= 10 -> System.out.println("A, positive x");
  case A(int x, int y) when x < 0 -> System.out.println("B, negative x");
  case B(int x, int y) -> System.out.println("B");
  //default -> System.out.println("Any of the previous options");
}
```

## [JEP 432](https://openjdk.java.net/jeps/432): Record Patterns (Second Preview)

- Add support for inference of type arguments of generic record patterns,
- Add support for record patterns to appear in the header of an enhanced for
  statement, and
- Remove support for named record patterns.

```java

```

## [JEP 436](https://openjdk.java.net/jeps/436): Virtual Threads (Second Preview)

- Minor changes since the first preview.

```java
Thread.ofPlatform().start(() -> System.out.println(Thread.currentThread()));
Thread.ofVirtual().start(() -> System.out.println(Thread.currentThread()));
```

## [JEP 437](https://openjdk.java.net/jeps/437): Structured Concurrency (Second Incubator)

- No changes since the first preview.

```java
String getUser() throws InterruptedException {
    Thread.sleep(3_000);
    return "Anton";
};

Integer getOrder() throws InterruptedException {
    Thread.sleep(2_000);
//    return 10;
    return 10/0;
};

String theUser  = getUser();
int theOrder = getOrder();
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
final static ThreadLocal<String> TL = new ThreadLocal<>();
TL.set("Hello");
TL.get();
Thread.ofVirtual().start(() -> System.out.println(TL.get()));
Thread.ofPlatform().start(() -> System.out.println(TL.get()));

final static InheritableThreadLocal<String> TL = new InheritableThreadLocal<>();
TL.set("Hello");
TL.get();
Thread.ofVirtual().start(() -> System.out.println(TL.get()));
Thread.ofPlatform().start(() -> System.out.println(TL.get()));

Thread.ofVirtual().start(() -> {
  TL.set("Hello from virtual");
  System.out.println(TL.get());})

TL.get();
```

Design flaws with ThreadLocal:

- Unconstrained mutability: variable's get() and set() methods can be called at
  any time.
- Unbounded lifetime: developers often forget to call ThreadLocal.remove().
- Expensive inheritance: the overhead of thread-local variables when utilizing
  a large number of threads.

```java
import jdk.incubator.concurrent.ScopedValue
static final ScopedValue<String> sv = ScopedValue.newInstance();
ScopedValue.where(sv, "anton", () -> System.out.println(sv.get()));
```

Advantages of Scoped Values:

- They are only valid during the lifetime of the Runnable passed to the where
  method.
- A scoped value is immutable - it can only be reset for a new scope by
  rebinding.
- The child threads created by StructuredTaskScope have access to the scoped
  value of the parent thread.

```java
import jdk.incubator.concurrent.StructuredTaskScope;

class Example {

  public static final ScopedValue<String> SV = ScopedValue.newInstance();

  static String getUser() {
    try {
      Thread.sleep(3_000);
    } catch (InterruptedException e) {}
    return SV.get();
  };

  static Integer getOrder() {
    try {
      Thread.sleep(2_000);
    } catch (InterruptedException e) {}
    return 1;
  };

  static void printMessage() {
    try {
      try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
       Future<String>  user  = scope.fork(() -> getUser());
       Future<Integer> order = scope.fork(() -> getOrder());
  
       scope.join();
       scope.throwIfFailed();
  
       System.out.println(user.resultNow() + ": " + order.resultNow());
     }
    } catch (Exception e) {}
  }
}

ScopedValue.where(Example.SV, "anton").run(() -> Example.printMessage())
```

Rebinding Scoped Values:

```java
class Example {

  public static final ScopedValue<String> SV = ScopedValue.newInstance();

  static String getUser() {
    try {
      Thread.sleep(3_000);
    } catch (InterruptedException e) {}

    // Rebinding the scoped value:
    ScopedValue.where(Example.SV, "Non Anton").run(() -> System.out.println(Example.SV.get()));
    return SV.get();
  };

  static Integer getOrder() {
    try {
      Thread.sleep(2_000);
    } catch (InterruptedException e) {}
    return 1;
  };

  static void printMessage() {
    try {
      try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
       Future<String>  user  = scope.fork(() -> getUser());
       Future<Integer> order = scope.fork(() -> getOrder());
  
       scope.join();
       scope.throwIfFailed();
  
       System.out.println(user.resultNow() + ": " + order.resultNow());
     }
    } catch (Exception e) {}
  }
}

ScopedValue.where(Example.SV, "anton"). run(() -> Example.printMessage())
```

- [Java API](https://download.java.net/java/early_access/loom/docs/api/jdk.incubator.concurrent/jdk/incubator/concurrent/ScopedValue.html)
- [JEP Cafe - Java 20 - From ThreadLocal to ScopedValue with Loom Full Tutorial](https://www.youtube.com/watch?v=fjvGzBFmyhM)
- [JEP 429: Extent-Local Variables to Promote Immutability in Java](https://www.infoq.com/news/2022/09/extent-local-variables-java/)
- [Scoped values in Java](https://www.happycoders.eu/java/scoped-values/)

## [JEP 434](https://openjdk.java.net/jeps/434): Foreign Function & Memory API (Second Preview)

```java
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

public long getStringLength(String content) throws Throwable {
    // 1. Get a lookup object for commonly used libraries
    SymbolLookup stdlib = Linker.nativeLinker().defaultLookup();

    // 2. Get a handle on the strlen function in the C standard library
    MethodHandle strlen = Linker.nativeLinker().downcallHandle(
            stdlib.find("strlen").orElseThrow(),
            FunctionDescriptor.of(JAVA_LONG, ADDRESS));
  
    long len = 0;

    // 3. Convert Java String to C string and store it in off-heap memory
    try (Arena offHeap = Arena.openConfined()) {
        MemorySegment str = offHeap.allocateUtf8String(content);
      
        // 4. Invoke the foreign function
        len = (long) strlen.invoke(str);
    }
    // 5. Off-heap memory is deallocated at the end of try-with-resources
    // 6. Return the length.
    return len;
}

getStringLength("Java 20 demo!")

```

## Resources

- [Java 21: sneak peek](https://blogs.oracle.com/javamagazine/post/java-21-sneak-peek)
- <https://sdkman.io/>
- <https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html>
- <https://www.jbang.dev/>

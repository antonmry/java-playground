# Java 18

https://en.wikipedia.org/wiki/Java_version_history
https://openjdk.java.net/projects/jdk/18/
https://blogs.oracle.com/javamagazine/post/java-project-amber-lambda-loom-panama-valhalla

## Install Java 18

```sh
sdk install java 18-open
sdk use java 18-open
jshell
```
JShell tutorial: https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html 

## [JEP 408](https://openjdk.java.net/jeps/408): Simple Web Server

```sh
jwebserver
```

```java
import java.net.InetSocketAddress;
import java.nio.file.Path;
import com.sun.net.httpserver.*;
import com.sun.net.httpserver.SimpleFileServer.*;

var s1 = SimpleFileServer.createFileServer(new InetSocketAddress(8000), Path.of(System.getProperty("user.dir")), OutputLevel.VERBOSE);
s1.start()
s1.stop(0)
```

```java
var s2 = HttpServer.create(new InetSocketAddress(8000), 0);
s2.start();

var handler = SimpleFileServer.createFileHandler(Path.of(System.getProperty("user.dir")));
s2.createContext("/browse/", handler)

s2.createContext("/", HttpHandlers.of(200, Headers.of("Allow", "GET"), "Hello World!"));

Predicate<Request> IS_GET = r -> r.getRequestMethod().equals("GET");
var handler = HttpHandlers.handleOrElse(IS_GET, HttpHandlers.of(200, Headers.of(), "It's GET"), HttpHandlers.of(200, Headers.of(), "It isn't GET"));
s2.createContext("/test", handler);

import java.net.http.*; 
var client = HttpClient.newHttpClient();
var request = HttpRequest.newBuilder(URI.create("http://localhost:8000/test")).GET().build();
var response = client.send(request, HttpResponse.BodyHandlers.ofString())
System.out.println(response.body())

var request = HttpRequest.newBuilder(URI.create("http://localhost:8000/test")).POST(HttpRequest.BodyPublishers.ofString("hello")).build();
var response = client.send(request, HttpResponse.BodyHandlers.ofString())
System.out.println(response.body())

s2.stop(0)

var filter = SimpleFileServer.createOutputFilter(System.out, OutputLevel.INFO);
var s3 = HttpServer.create(new InetSocketAddress(8000), 10, "/", HttpHandlers.of(200, Headers.of("Allow", "GET"), "Hello World!"), filter);
s3.start()

s3.stop(0)
```

## [JEP 413](https://openjdk.java.net/jeps/413): Code Snippets in Java API Documentation

```sh
javadoc -private -d ../docs/java18/ -sourcepath src/ example --snippet-path ./snippet-files

javac snippet-files/ShowOptional.java
```
## [JEP 400](https://openjdk.java.net/jeps/400): UTF-8 by Default

```sh
JAVA_TOOL_OPTIONS=-Dfile.encoding=x-windows-950 jshell
```

```java
import java.nio.charset.Charset;
Charset.defaultCharset()
Charset.availableCharsets()

var fileReader = new FileReader("test.txt");

int data = fileReader.read();
while(data != -1) {
  System.out.print((char) data);
  data = fileReader.read();
}
fileReader.close();
```

## [JEP 420](https://openjdk.java.net/jeps/420): Pattern Matching for switch (Second Preview)

```sh
jshell --enable-preview
```sh

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

Object test = "test!";
switch (test) {
  case String s                                 -> System.out.println("Hello " + s);
  case String s && s.equals("unreachable code") -> System.out.println("Hello " + s);
  default                                       -> System.out.println("Nop!");
}
```

```java

sealed interface S permits A, B, C {}
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

## [JEP 421](https://openjdk.java.net/jeps/421): Deprecate Finalization for Removal

```java
FileInputStream  input  = null;
FileOutputStream output = null;
try {
    input  = new FileInputStream("test.txt");
    output = new FileOutputStream("test2.txt");
    //... copy bytes from input to output ...
    output.close();  output = null;
    input.close();   input  = null;
} finally {
    if (output != null) output.close();
    if (input  != null) input.close();
}
```

```java

try (FileInputStream input = new FileInputStream("test.txt");
     FileOutputStream output = new FileOutputStream("test2.txt")) {
    //... copy bytes from input to output ...
}
```

```java

// Java 9: deprecated
// Java 18: deprecated for removal

class A {
  @Override
  public void finalize() {}
}
```

##  [JEP 416](https://openjdk.java.net/jeps/416): Reimplement Core Reflection with Method Handles

> Reimplement java.lang.reflect on top of method handles as the common underlying reflective mechanism of 
> the platform by replacing the bytecode-generating implementations of Method::invoke, Constructor::newInstance, 
> Field::get, and Field::set.

MethodHandles: https://www.baeldung.com/java-method-handles

```java
import java.lang.reflect.*;
import java.lang.invoke.*;

public record Demo(String s) {
  private String hello() {
    return s;
  }
}

MethodHandles.Lookup lookup = MethodHandles.publicLookup();
Method helloMethod = Demo.class.getDeclaredMethod("hello");
helloMethod.setAccessible(true);
MethodHandle demoMH = lookup.unreflect(helloMethod);

var demo = new Demo("Anton")
demo.hello()
(String) demoMH.invoke(demo)
```

## [JEP 418](https://openjdk.java.net/jeps/418): Internet-Address Resolution SPI

```sh
javac src/provider/impl/SimpleResolverProviderImpl.java
jar cvf simpleresolverprovider.jar -C src/provider/ .
```

```java
InetAddress.getByName("www.galiglobal.com")
/env -class-path simpleresolverprovider.jar
InetAddress.getByName("www.galiglobal.com")
```

## [JEP 417](https://openjdk.java.net/jeps/417): Vector API (Third Incubator)

See https://www.morling.dev/blog/fizzbuzz-simd-style/

```sh
jshell -v --add-modules jdk.incubator.vector
```

```java

void scalarComputation(float[] a, float[] b, float[] c) {
   for (int i = 0; i < a.length; i++) {
        c[i] = (a[i] * a[i] + b[i] * b[i]) * -1.0f;
   }
}

import jdk.incubator.vector.*;
static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

void vectorComputation(float[] a, float[] b, float[] c) {
    int i = 0;
    int upperBound = SPECIES.loopBound(a.length);
    for (; i < upperBound; i += SPECIES.length()) {
        // FloatVector va, vb, vc;
        var va = FloatVector.fromArray(SPECIES, a, i);
        var vb = FloatVector.fromArray(SPECIES, b, i);
        var vc = va.mul(va)
                   .add(vb.mul(vb))
                   .neg();
        vc.intoArray(c, i);
    }
    for (; i < a.length; i++) {
        c[i] = (a[i] * a[i] + b[i] * b[i]) * -1.0f;
    }
}

float[] a = new float[10_000_000];
float[] b = new float[10_000_000];
float[] r = new float[10_000_000];
Arrays.fill(a, 1L);
Arrays.fill(b, 2L);

int i;
long t0;
for(t0=System.nanoTime(),i=0; i<1000; ++i){
scalarComputation(a, b, r);
} long elapsed=System.nanoTime()-t0;

for(t0=System.nanoTime(),i=0; i<1000; ++i){
vectorComputation(a, b, r);
} long elapsed=System.nanoTime()-t0;

```


## [JEP 419](https://openjdk.java.net/jeps/419): Foreign Function & Memory API (Second Incubator)

See https://foojay.io/today/project-panama-for-newbies-part-1/

```sh
jshell -v --add-modules jdk.incubator.foreign
```

```java
import static jdk.incubator.foreign.ResourceScope.newConfinedScope;
import static jdk.incubator.foreign.SegmentAllocator.implicitAllocator;
import static org.unix.stdio_h.printf;

try (var scope = newConfinedScope()) {
   MemorySegment cString = implicitAllocator().allocateUtf8String("Hello World! Panama style\n");
   printf(cString);
   System.out.println(cString);
}
```

## Resources

- https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html
- https://www.jbang.dev/
- https://www.happycoders.eu/java/java-18-features/

# Java 18

https://openjdk.java.net/projects/jdk/18/

## Install Java 18

```sh
sdk install java 18-open
sdk use java 18-open
jshell
```
JShell tutorial: https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html 

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

```bash
javadoc -private -d javadocs/ -sourcepath src/ example --snippet-path ./snippet-files

```

## TODO

New Relic Java Agent? Comparisson?
jshell kung-fu? import libraries in advance?
  https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html 
  https://cr.openjdk.java.net/~rfield/tutorial/JShellTutorial.html
jbang?

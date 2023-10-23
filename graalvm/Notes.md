# Java 21

- <https://www.graalvm.org/>

> The first production-ready version, GraalVM 19.0, was released in May 2019

> Pure Java implementation.

> License: Community Edition: GPLv2. Enterprise Edition: Trialware

## Install GraalVM

```sh
sdk list java
sdk install java 21-graalce
sdk use java 21-graalce
java -version
```

## Graal JIT compiler

```sh
javac CountUppercase.java
java CountUppercase "Hello Codely!"

java -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler CountUppercase "Hello Codely!"
java -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler -Dgraal.PrintCompilation=true CountUppercase "Hello Codely!"
```

## Truffle Language Implementation Framework

- https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/LanguageTutorial/
- https://github.com/oracle/graaljs/releases/

```sh
wget https://github.com/oracle/graaljs/releases/download/graal-23.1.0/graaljs-23.1.0-macos-amd64.tar.gz
sudo xattr -r -d com.apple.quarantine graaljs-23.1.0-macos-amd64.tar.gz
tar -zxvf graaljs-23.1.0-macos-amd64.tar.gz
graaljs-23.1.0-macos-amd64/bin/js --version
graaljs-23.1.0-macos-amd64/bin/js
```

```javascript
var BigInteger = Java.type('java.math.BigInteger');
console.log(BigInteger.valueOf(200000).toString(16));
```

- https://github.com/graalvm/polyglot-embedding-demo/

```sh
mvn compile com.github.johnpoth:jshell-maven-plugin:1.4:run
```

```java
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

String JS_CODE = "(function myFun(param){console.log('hello '+param);})";

System.out.println("Hello Java!");
try (Context context = Context.create()) {
  Value value = context.eval("js", JS_CODE);
  value.execute("Hello Codely!");
}
```

```java
try (Context context = Context.create()) {
  Value value = context.eval("python", "print('Hello Python!')");
}
```

- https://github.com/graalvm/polyglot-embedding-demo/blob/main/src/main/java/org/example/embedding/Main.java

## GraalVM Native Image

```sh
javac CountUppercase.java
java CountUppercase "Hello Codely!"

native-image CountUppercase
ls -lah countuppercase

./countuppercase "Hello Codely"

./countuppercase "Hello Codely"

time java CountUppercase "Hello Codely!"
time ./countuppercase "Hello Codely"
```

- https://medium.com/graalvm/graalvm-for-jdk-21-is-here-ee01177dd12d
- https://graalvm.github.io/native-build-tools/latest/index.html

## Resources

- <https://sdkman.io/>
- <https://en.wikipedia.org/wiki/GraalVM>

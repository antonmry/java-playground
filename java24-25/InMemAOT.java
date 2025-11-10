import javax.tools.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import static java.util.stream.Collectors.*;

public class InMemAOT {
  // ----- in-memory source -----
  static class StringSource extends SimpleJavaFileObject {
    final String code;
    StringSource(String name, String code) {
      super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
      this.code = code;
    }
    @Override public CharSequence getCharContent(boolean ignore) { return code; }
  }
  // ----- in-memory class file -----
  static class MemClass extends SimpleJavaFileObject {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    MemClass(String name) {
      super(URI.create("mem:///" + name.replace('.','/') + Kind.CLASS.extension), Kind.CLASS);
    }
    @Override public OutputStream openOutputStream() { return bos; }
    byte[] bytes() { return bos.toByteArray(); }
  }
  // ----- file manager collecting bytecode -----
  static class MemFM extends ForwardingJavaFileManager<JavaFileManager> {
    final Map<String,MemClass> classes = new ConcurrentHashMap<>();
    MemFM(JavaFileManager fm) { super(fm); }
    @Override public JavaFileObject getJavaFileForOutput(Location l, String className, JavaFileObject.Kind k, FileObject sibling) {
      var mc = new MemClass(className);
      classes.put(className, mc);
      return mc;
    }
  }
  // ----- classloader that defines from memory -----
  static class MemLoader extends ClassLoader {
    Class<?> define(String name, byte[] bytes) { return defineClass(name, bytes, 0, bytes.length); }
  }

  public static void main(String[] args) throws Exception {
    int N = (args.length>0) ? Integer.parseInt(args[0]) : 5000;
    var jc = ToolProvider.getSystemJavaCompiler();
    var fm = new MemFM(jc.getStandardFileManager(null, null, null));

    // generate N tiny classes in package dyn: dyn.Ci#id()
    var sources = new ArrayList<JavaFileObject>(N);
    for (int i=0;i<N;i++) {
      var name = "dyn.C"+i;
      var code = "package dyn; public class C"+i+"{ public int id(){ return "+i+"; }}";
      sources.add(new StringSource(name, code));
    }

    // compile all to memory
    var opts = List.of("-g:none", "-proc:none");
    if (!jc.getTask(null, fm, null, opts, null, sources).call())
      throw new RuntimeException("compile failed");

    // load + link + invoke to force resolution
    var loader = new MemLoader();
    long sum = 0;
    for (int i=0;i<N;i++) {
      var name = "dyn.C"+i;
      var bytes = fm.classes.get(name).bytes();
      var c = loader.define(name, bytes);
      var o = c.getDeclaredConstructor().newInstance();
      sum += (int) c.getMethod("id").invoke(o);
    }
    System.out.println(sum); // prevent dead-code elimination
  }
}

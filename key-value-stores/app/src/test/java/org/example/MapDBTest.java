package org.example;

import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

/**
 * mvn test -Dtest=com.mycompany.app.MapDbTest3
 * output:
 */
public class MapDBTest {

    private static final String dbPath = "/tmp/mapdb";
    private static final String dbFilename = "/tmp/mapdb/file.db";
    private static final int TRANSACTION_SIZE = 100_000;
    private static DB db;
    private static ConcurrentMap<String, String> map;

    public static void openDB() {
        long start = System.currentTimeMillis();
        db = DBMaker
            .fileDB(dbFilename)
            .transactionEnable()
            .fileMmapEnable()
            .make();

        // it also has a ConcurrentNavigableMap that is backed by a treeMap
        // https://jankotek.gitbooks.io/mapdb/content/db/
        map = db.hashMap("my map", Serializer.STRING, Serializer.STRING) // you can also do db.treeMap to get a map backed by tree data structure instead of hashtable
            .createOrOpen();

        long end = System.currentTimeMillis();
        long duration = end - start;
        System.out.printf("Time: %d ms\n", duration);
    }

    @Test
    // https://gist.github.com/siddhsql/7d22b837001c910ac2dfe62b044ebaeb
    public void test1() {

        int N = 10_000_000;

        openDB();
        measureDBSize();
        insertDB(N);
        measureDBSize();
        closeDB();
        measureDBSize();

        openDB();
        measureDBSize();
        insertDB(1);
        measureDBSize();
        closeDB();
        measureDBSize();
    }

    private void measureDBSize() {
        var size = IOUtils.getDirectorySize(new File(dbPath));
        System.out.printf("DB Size = %d bytes\n", size);
    }

    private void insertDB(int N) {
        // insert 10 million entries
        long start = System.currentTimeMillis();

        for (var i = 0; i < N; i++) {
            if (i != 0 && i % 300_000 == 0) {
                System.out.printf("now at %d of %d\n", i, N);
            }
            String id = StringUtils.generate_random_string(16);
            String text = StringUtils.generate_random_string(100);
            map.put(id, text);
            if (i != 0 && i % TRANSACTION_SIZE == 0) {
                System.out.printf("commiting %d records ", TRANSACTION_SIZE);
                db.commit();
            }
        }
        long end = System.currentTimeMillis();
        long duration = end - start;
        System.out.printf("%d insert operations took %d ms\n", N, duration);
        System.out.printf("applying final commit\n");
        db.commit();
    }

    private void closeDB() {
        var start = System.currentTimeMillis();
        db.close();
        var end = System.currentTimeMillis();
        var duration = end - start;
        System.out.printf("closing the db took %d ms\n", duration);
    }
}
package org.example;

import org.junit.Test;
import org.rocksdb.OptimisticTransactionDB;
import org.rocksdb.Options;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDBException;
import org.rocksdb.Transaction;
import org.rocksdb.WriteOptions;

import java.io.File;

import static java.nio.charset.StandardCharsets.UTF_8;

// compile test classes from Maven by running: mvn test-compile
public class RockDBTest {
    static final String dbPath = "/tmp/rocksdb_10m_test";
    static OptimisticTransactionDB txnDb;
    static WriteOptions writeOptions;
    static ReadOptions readOptions;
    static Options options;

    @Test
    public void test1() throws RocksDBException {
        int N = 10_000;
        System.out.print("opening DB\n");
        openDB();
        measureDBSize();
        System.out.printf("inserting %d records\n", N);
        putRecords(N);
        measureDBSize();
        System.out.print("closing DB\n");
        closeDB();
        measureDBSize();

        System.out.print("opening DB\n");
        openDB();
        measureDBSize();
        System.out.printf("inserting %d records\n", 1);
        putRecords(1);
        measureDBSize();
        System.out.print("closing DB\n");
        closeDB();
        measureDBSize();
    }

    private void measureDBSize() {
        var size = IOUtils.getDirectorySize(new File(dbPath));
        System.out.printf("DB Size = %d bytes\n", size);
    }

    private void openDB() throws RocksDBException {
        long start = System.currentTimeMillis();
        options = new Options().setCreateIfMissing(true);
        txnDb = OptimisticTransactionDB.open(options, dbPath);
        writeOptions = new WriteOptions();
        readOptions = new ReadOptions();
        long end = System.currentTimeMillis();
        long duration = end - start;
        System.out.printf("Time: %d ms\n", duration);
    }

    private void putRecords(int count) throws RocksDBException {
        long start = System.currentTimeMillis();
        try (final Transaction txn = txnDb.beginTransaction(writeOptions)) {
            for (var i = 0; i < count; i++) {
                var key = StringUtils.generate_random_string(16);
                var value = StringUtils.generate_random_string(100);
                txn.put(key.getBytes(UTF_8), value.getBytes(UTF_8));
            }
            // Commit transaction - this is when data gets persisted to the DB
            txn.commit();
        }
        long end = System.currentTimeMillis();
        long duration = end - start;
        System.out.printf("Time: %d ms\n", duration);
    }

    private void closeDB() {
        long start = System.currentTimeMillis();
        writeOptions.close();
        readOptions.close();
        txnDb.close();
        options.close();
        long end = System.currentTimeMillis();
        long duration = end - start;
        System.out.printf("Time: %d ms\n", duration);
    }
}

// COHSize.java
public class COHSize {
    static class P { long a, b; }          // 16B payload -> 24B with COH, ~32B without
    static volatile long sink;             // defeats scalar replacement
    static Object keep;                    // keeps array alive

    static long used() {
        System.gc();
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public static void main(String[] args) {
        int N = (args.length > 0) ? Integer.parseInt(args[0]) : 8_000_000;

        long before = used();

        P[] arr = new P[N];
        for (int i = 0; i < N; i++) {
            P p = new P();
            p.a = i; p.b = i;
            arr[i] = p;
            sink += p.a;
        }
        keep = arr;                        // keep reachable

        long after = used();
        System.out.printf("Used delta: %.1f MB%n", (after - before) / 1048576.0);
    }
}

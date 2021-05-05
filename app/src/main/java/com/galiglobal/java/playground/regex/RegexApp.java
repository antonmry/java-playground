package com.galiglobal.java.playground.regex;

import com.gliwka.hyperscan.util.PatternFilter;
import org.apache.regexp.RE;
import org.ehcache.sizeof.SizeOf;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexApp {

    private static long totalHyperscan = 0;
    private static long totalApacheRegex = 0;
    private static long totalUtil = 0;
    private static long totalUtilComp = 0;
    private static long totalRe2 = 0;
    private static long totalRe2Comp = 0;

    public static void main(String[] args) throws Exception {

        // DISCLAIMER
        // This is a quick and dirty benchmark. Use JMH for better results
        // Results: https://excalidraw.com/#json=5109261076004864,b8cy0HOxaAn587sKrDhz6Q

        SizeOf sizeOf = SizeOf.newInstance();

        //long totalRecords = 10_000_000;
        long totalRecords = 100_000;

        Random random = new Random();
        var randoms = random
            .longs(totalRecords, 10, 1000000000)
            .mapToObj(String::valueOf)
            .collect(Collectors.toList());

        var regexps = Arrays.asList(
            "^[\\w\\-]+(\\.[\\w\\-]+)*@[\\w\\-]+(\\.[\\w\\-]+)*(\\.)[a-zA-Z]+$", // Email
            "(?s)^(\\.\\*\\??)?(.*)", // Bomb!
            "([0-9]{4})-?(1[0-2]|0[1-9])-?(3[01]|0[1-9]|[12][0-9])", // Date
            "[\\w\\.]+@[\\w\\.]+", // Email 2
            "([0-9]{3})-([0-9]{3})-([0-9]{4})", // Phone 2
            "($+((((($+((a+a*)+(b+c))*)((cc)(b+b))+a)+((b+c*)+(c+c)))+a)+(c*a+($+(c+c)b))))+c", // Random
            "[0-8][0-9]{2}-[0-9]{2}-[0-9]{4}", // Social
            "A[ZLRK]|C[TAO]|D[CE]|FL|GA|HI|I[ALND]|K[SY]|LA|M[ADEINOST]|"
                + "N[HCDEJMVY]|O[HKR]|PA|RI|S[CD]|T[XN]|UT|V[AT]|W[VAIY]" // States
        );

        //***************************************************************/
        //      Hyperscan: https://github.com/gliwka/hyperscan-java      /
        //***************************************************************/

        List<Pattern> patterns = regexps.stream().map(Pattern::compile).collect(Collectors.toList());

        //not thread-safe, create per thread
        final PatternFilter filter = new PatternFilter(patterns);

        long startTimeHyperScale = System.nanoTime();
        randoms.forEach(
            r -> filter.filter(r).forEach(m -> {
                if (m.find()) totalHyperscan++;
            })
        );

        long elapsedTimeHyperScale = System.nanoTime() - startTimeHyperScale;
        long hyperscaleShallowSize = sizeOf.sizeOf(filter);
        long hyperscaleDeepSize = sizeOf.deepSizeOf(filter);

        System.out.println("> hyperscan shallow size is: " + hyperscaleShallowSize);
        System.out.println("> hyperscan deep size is: " + hyperscaleDeepSize);
        System.out.println("> Hyperscale found " + totalHyperscan + " in " + elapsedTimeHyperScale / 1000000 + " ms.");

        //***************************************************************/
        //                     Apache Regex                              /
        //***************************************************************/

        List<RE> res = regexps.stream().map(r -> {
                try {
                    return new RE(r);
                } catch (Exception e) {
                    System.out.println("Apache Regex: 1 exception compiling one model");
                    return new RE("a");
                }
            }
        ).collect(Collectors.toList());

        long startApacheRegex = System.nanoTime();
        randoms.forEach(
            r -> res.forEach(re -> {
                if (re.match(r)) totalApacheRegex++;
            })
        );

        long elapsedApacheRegex = System.nanoTime() - startApacheRegex;
        long apacheRegexShallowSize = sizeOf.sizeOf(res);
        long apacheRegexDeepSize = sizeOf.deepSizeOf(res);

        System.out.println("> Apache Regex shallow size is: " + apacheRegexShallowSize);
        System.out.println("> Apache Regex deep size is: " + apacheRegexDeepSize);
        System.out.println("> Apache Regex found " + totalApacheRegex + " in " + elapsedApacheRegex / 1000000 + " ms.");

        //***************************************************************/
        //       java.util.regex without previous compilation            /
        //***************************************************************/

        long startUtil = System.nanoTime();
        randoms.forEach(
            r -> regexps.forEach(regexp -> {
                if (Pattern.matches(regexp, r)) totalUtil++;
            })
        );

        long elapsedUtil = System.nanoTime() - startUtil;
        long utilShallowSize = sizeOf.sizeOf(regexps);
        long utilDeepSize = sizeOf.deepSizeOf(regexps);

        System.out.println("> java.util.regex without compilation shallow size is: " + utilShallowSize);
        System.out.println("> java.util.regex without compilation deep size is: " + utilDeepSize);
        System.out.println("> java.util.regex without compilation found " + totalUtil + " in " + elapsedUtil / 1000000 + " ms.");

        //***************************************************************/
        //       java.util.regex with previous compilation            /
        //***************************************************************/

        long startUtilComp = System.nanoTime();
        randoms.forEach(
            r -> patterns.forEach(pu -> {
                if (pu.matcher(r).find()) totalUtilComp++;
            })
        );

        long elapsedUtilComp = System.nanoTime() - startUtilComp;
        // Note: we can't create patterns again, the JVM will optimize to use the same object...
        long utilCompShallowSize = hyperscaleShallowSize;
        long utilCompDeepSize = hyperscaleDeepSize;

        System.out.println("> java.util.regex with compilation shallow size is: " + utilCompShallowSize);
        System.out.println("> java.util.regex with compilation deep size is: " + utilCompDeepSize);
        System.out.println("> java.util.regex with compilation found " + totalUtilComp + " in " + elapsedUtilComp / 1000000 + " ms.");

        //***************************************************************/
        //                Google Re2 without compilation                 /
        //***************************************************************/

        //long totalRe2j = randoms.stream().filter(v -> com.google.re2j.Pattern.matches(regexp, v)).count();

        long startRe2 = System.nanoTime();
        randoms.forEach(
            r -> regexps.forEach(regexp -> {
                if (com.google.re2j.Pattern.matches(regexp, r)) totalRe2++;
            })
        );

        long elapsedRe2 = System.nanoTime() - startRe2;
        // Note: avoid JVM optimizations
        long re2ShallowSize = utilShallowSize;
        long re2DeepSize = utilShallowSize;

        System.out.println("> Google Re2 without compilation shallow size is: " + re2ShallowSize);
        System.out.println("> Google Re2 without compilation deep size is: " + re2DeepSize);
        System.out.println("> Google Re2 without compilation found " + totalRe2 + " in " + elapsedRe2 / 1000000 + " ms.");


        //***************************************************************/
        //           Google Re2 with previous compilation                /
        //***************************************************************/

        List<com.google.re2j.Pattern> patternsRe2 = regexps.stream().map(com.google.re2j.Pattern::compile).collect(Collectors.toList());

        long startRe2Comp = System.nanoTime();
        randoms.forEach(
            r -> patternsRe2.forEach(pu -> {
                if (pu.matcher(r).find()) totalRe2Comp++;
            })
        );

        long elapsedRe2Comp = System.nanoTime() - startRe2Comp;
        // Todo: is the JVM reusing part of patterns?
        long re2CompShallowSize = sizeOf.sizeOf(patternsRe2);
        long re2CompDeepSize = sizeOf.deepSizeOf(patternsRe2);

        System.out.println("> Google Re2 with compilation shallow size is: " + re2CompShallowSize);
        System.out.println("> Google Re2 with compilation deep size is: " + re2CompDeepSize);
        System.out.println("> Google Re2 with compilation found " + totalRe2Comp + " in " + elapsedRe2Comp / 1000000 + " ms.");

        //***************************************************************/
        //                     Print CSVs                               /
        //***************************************************************/

        System.out.println("--------------------");
        System.out.println("RegexEngine,ShallowSize " +  regexps.stream().count() + " regex (bytes)");
        System.out.println("Hyperscale," + hyperscaleShallowSize);
        System.out.println("ApacheRegex," + apacheRegexShallowSize);
        System.out.println("util," + utilShallowSize);
        System.out.println("util comp," + utilCompShallowSize);
        System.out.println("Re2," + re2ShallowSize);
        System.out.println("Re2Comp," + re2CompShallowSize);
        System.out.println("--------------------");
        System.out.println("RegexEngine,DeepSize " +  regexps.stream().count() + " regex (bytes)");
        System.out.println("Hyperscale," + hyperscaleDeepSize);
        System.out.println("ApacheRegex," + apacheRegexDeepSize);
        System.out.println("util," + utilDeepSize);
        System.out.println("utilComp," + utilCompDeepSize);
        System.out.println("Re2," + re2DeepSize);
        System.out.println("Re2Comp," + re2CompDeepSize);
        System.out.println("--------------------");
        System.out.println("RegexEngine,TotalTime for " + totalRecords + " strings (ms)");
        System.out.println("Hyperscale," + elapsedTimeHyperScale);
        System.out.println("ApacheRegex," + elapsedApacheRegex);
        System.out.println("util," + elapsedUtil);
        System.out.println("utilComp," + elapsedUtilComp);
        System.out.println("Re2," + elapsedRe2);
        System.out.println("Re2Comp," + elapsedRe2Comp);
    }
}

/**
 * Different ways to measure size:
 * <p>
 * System.out.println("> Google Re2j compiled found " + totalRe2jCompiled + " in " + elapsedTime5 / 1000000 + " ms.");
 * System.out.println("> Jol: regex string shallow size is: " + VM.current().sizeOf(regexp));
 * System.out.println("> Jol: java.util.regex.Pattern shallow size is: " + VM.current().sizeOf(pattern));
 * System.out.println("> Jol: com.google.re2j.Pattern shallow size is: " + VM.current().sizeOf(re2jPattern));
 * SizeOf sizeOf = SizeOf.newInstance();
 * System.out.println("> ehcache: regex string shallow size is: " + sizeOf.sizeOf(regexp));
 * System.out.println("> ehcache: java.util.regex.Pattern shallow size is: " + sizeOf.sizeOf(pattern));
 * System.out.println("> ehcache: com.google.re2j.Pattern shallow size is: " + sizeOf.sizeOf(re2jPattern));
 * System.out.println("> ehcache: java.util.regex.Pattern deep size is: " + sizeOf.deepSizeOf(pattern));
 * System.out.println("> ehcache: com.google.re2j.Pattern deep size is: " + sizeOf.deepSizeOf(re2jPattern));
 * MemoryMeter meter = MemoryMeter.builder().build();
 * System.out.println("> jamm: regex string shallow size is: " + meter.measure(regexp));
 * System.out.println("> jamm: java.util.regex.Pattern shallow size is: " + meter.measure(pattern));
 * System.out.println("> jamm: com.google.re2j.Pattern shallow size is: " + meter.measure(re2jPattern));
 * System.out.println("> jamm: java.util.regex.Pattern deep size is: " + meter.measureDeep(pattern));
 * System.out.println("> jamm: com.google.re2j.Pattern deep size is: " + meter.measureDeep(re2jPattern));
 */

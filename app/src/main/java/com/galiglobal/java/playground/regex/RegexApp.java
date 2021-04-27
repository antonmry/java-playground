package com.galiglobal.java.playground.regex;

import org.apache.regexp.RE;
import org.ehcache.sizeof.SizeOf;
import org.github.jamm.MemoryMeter;
import org.openjdk.jol.vm.VM;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexApp {

    public static void main(String[] args) throws Exception {

        long totalRecords = 1_000_000;
        Random random = new Random();
        var randoms = random
            .longs(totalRecords, 10, 1000000000)
            .mapToObj(v -> String.valueOf(v))
            .collect(Collectors.toList());

        var regexps = Arrays.asList(
            "^[\\w\\-]+(\\.[\\w\\-]+)*@[\\w\\-]+(\\.[\\w\\-]+)*(\\.)[a-zA-Z]+$", // Email
            "(?s)^(\\.\\*\\??)?(.*)",
            "([0-9]{4})-?(1[0-2]|0[1-9])-?(3[01]|0[1-9]|[12][0-9])", // Date
            "[\\w\\.]+@[\\w\\.]+", // Email 2
            "([0-9]{3})-([0-9]{3})-([0-9]{4})", // Phone 2
            "($+((((($+((a+a*)+(b+c))*)((cc)(b+b))+a)+((b+c*)+(c+c)))+a)+(c*a+($+(c+c)b))))+c", // Random
            "[0-8][0-9]{2}-[0-9]{2}-[0-9]{4}", // Social
            "A[ZLRK]|C[TAO]|D[CE]|FL|GA|HI|I[ALND]|K[SY]|LA|M[ADEINOST]|"
                + "N[HCDEJMVY]|O[HKR]|PA|RI|S[CD]|T[XN]|UT|V[AT]|W[VAIY]" // States
        );

        regexps.forEach(regexp -> {
            try {

                System.out.println("Regex expression: " + regexp);

                // Apache Regex
                RE re = new RE(regexp);
                long startTime = System.nanoTime();
                long totalApache = randoms.stream().filter(v -> re.match(v)).count();
                long elapsedTime = System.nanoTime() - startTime;
                System.out.println("> Apache Regex found " + totalApache + " in " + elapsedTime / 1000000 + " ms.");
            } catch (Exception e) {
                System.out.println("Apache Regex doesn't compile for this REGEX expression.");
            }

            // java.util.regex without previous compilation
            long startTime2 = System.nanoTime();
            long totalUtil = randoms.stream().filter(v -> Pattern.matches(regexp, v)).count();
            long elapsedTime2 = System.nanoTime() - startTime2;
            System.out.println("> Util Regex found " + totalUtil + " in " + elapsedTime2 / 1000000 + " ms.");

            // java.util.regex with previous compilation
            Pattern pattern = Pattern.compile(regexp);
            long startTime3 = System.nanoTime();
            long totalUtilCompiled = randoms.stream().filter(v -> pattern.matcher(v).find()).count();
            long elapsedTime3 = System.nanoTime() - startTime3;
            System.out.println("> Util Regex Compiled found " + totalUtilCompiled + " in " + elapsedTime3 / 1000000 + " ms.");

            // Google re2j without previous compilation
            long startTime4 = System.nanoTime();
            long totalRe2j = randoms.stream().filter(v -> com.google.re2j.Pattern.matches(regexp, v)).count();
            long elapsedTime4 = System.nanoTime() - startTime4;
            System.out.println("> Google Re2j found " + totalRe2j + " in " + elapsedTime4 / 1000000 + " ms.");

            // Google re2j with previous compilation
            com.google.re2j.Pattern re2jPattern = com.google.re2j.Pattern.compile(regexp);
            long startTime5 = System.nanoTime();
            long totalRe2jCompiled = randoms.stream().filter(v -> re2jPattern.matcher(v).find()).count();
            long elapsedTime5 = System.nanoTime() - startTime5;
            System.out.println("> Google Re2j compiled found " + totalRe2jCompiled + " in " + elapsedTime5 / 1000000 + " ms.");

            System.out.println("> Jol: regex string shallow size is: " + VM.current().sizeOf(regexp));
            System.out.println("> Jol: java.util.regex.Pattern shallow size is: " + VM.current().sizeOf(pattern));
            System.out.println("> Jol: com.google.re2j.Pattern shallow size is: " + VM.current().sizeOf(re2jPattern));
            SizeOf sizeOf = SizeOf.newInstance();
            System.out.println("> ehcache: regex string shallow size is: " + sizeOf.sizeOf(regexp));
            System.out.println("> ehcache: java.util.regex.Pattern shallow size is: " + sizeOf.sizeOf(pattern));
            System.out.println("> ehcache: com.google.re2j.Pattern shallow size is: " + sizeOf.sizeOf(re2jPattern));
            System.out.println("> ehcache: java.util.regex.Pattern deep size is: " + sizeOf.deepSizeOf(pattern));
            System.out.println("> ehcache: com.google.re2j.Pattern deep size is: " + sizeOf.deepSizeOf(re2jPattern));
            MemoryMeter meter = MemoryMeter.builder().build();
            System.out.println("> jamm: regex string shallow size is: " + meter.measure(regexp));
            System.out.println("> jamm: java.util.regex.Pattern shallow size is: " + meter.measure(pattern));
            System.out.println("> jamm: com.google.re2j.Pattern shallow size is: " + meter.measure(re2jPattern));
            System.out.println("> jamm: java.util.regex.Pattern deep size is: " + meter.measureDeep(pattern));
            System.out.println("> jamm: com.google.re2j.Pattern deep size is: " + meter.measureDeep(re2jPattern));
        });
    }
}

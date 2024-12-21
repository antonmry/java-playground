package com.galiglobal.java.playground.regex;

import com.gliwka.hyperscan.util.PatternFilter;
import com.gliwka.hyperscan.wrapper.CompileErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hyperscale {

    public static void main(String[] args) throws CompileErrorException {
        List<Pattern> patterns = Arrays.asList(
            Pattern.compile("The number is ([0-9]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("The color is (blue|red|orange)")
            //, Pattern.compile("^[\\w\\-]+(\\.[\\w\\-]+)*@[\\w\\-]+(\\.[\\w\\-]+)*(\\.)[a-zA-Z]+$"),
            , Pattern.compile("(?s)^(\\.\\*\\??)?(.*)") // It doesn't work well -> null
        );

        //not thread-safe, create per thread
        PatternFilter filter = new PatternFilter(patterns);

        //this list now only contains the probably matching patterns, in this case the first one
        //List<Matcher> matchers = filter.filter("The number is 7 the NUMber is 27");
        List<Matcher> matchers = filter.filter("email@email.com");

        //now we use the regular java regex api to check for matches - this is not hyperscan specific
        for (Matcher matcher : matchers) {
            while (matcher.find()) {
                // will print 7 and 27
                System.out.println(matcher.group(1));
            }
        }
    }
}

package com.agp.demo.pattern;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j
public class PTest {
    private static final String REGEX = "(\\$\\{[^\\}]+})";
    @Test
    public void testString(){
        String a="(${name}),${age}";
        Pattern pattern=Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(a);
        while (matcher.find()){
            String group = matcher.group();
            log.info("group:{}",group);
        }
    }
}

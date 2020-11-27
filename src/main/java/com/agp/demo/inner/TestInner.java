package com.agp.demo.inner;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

@Slf4j
public class TestInner {
    @Test
    public void test(){
        int a=20;
        int a1=20;
        int b=--a;
        int b1=a--;
       log.info("b:{}; b1:{}",b,b1);
    }
    @Test
    public void testSetIterator(){
        Set<String> strings=new HashSet<>();
        strings.add("aa");
        strings.add("aa1");
        strings.add("aa2");
        strings.add("aa3");
        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            System.out.println(next);
        }
    }

    @Test
    public void testTokenizer(){
        String a="aa  bb cc \t dd \f \n   aaa \t eee \n bbb";
        StringTokenizer stringTokenizer=new StringTokenizer(a);
        int i=0;
        while (stringTokenizer.hasMoreTokens()){
            String s = stringTokenizer.nextToken();
            System.out.println(i+s);
            ++i;
        }
    }
    @Test
    public void testBitAccumulate(){
        System.out.println(1 & ~2);
        System.out.println(~2 & 2);
        System.out.println(~4 & 4);
        System.out.println((1<<30)  + (1<<30)-1);
        System.out.println(0x7fffffff);
        System.out.println((1<<31) );
        System.out.println(-1 << 29 | 121);
        System.out.println(-1 << 28 | 121);
        System.out.println(-1 << 30 | 121);
        System.out.println();
        HashMap hashMap;
    }
    @Test
    public void testAutoIncDec(){
        int i=0,j=0;
        System.out.println(i--);  //0
        System.out.println(--j); //-1
        for (int it=0; it ++ < 4;){
            System.out.println(it);
        }
    }
    @Test
    public void testBreakFor(){
        outer:for (int i=0;i<10;i++){
            for (int j=100;j>i;--j){
                System.out.println(j);
                if (j==20){
                    break outer;
                }
            }
        }
    }
    @Test
    public void testEqual(){
      String a=null;
      String b=a;
      a="xxx";
        System.out.println(a);
        System.out.println(b);
    }


}

package com.grgbanking.grgacd;

import org.junit.Test;

import java.time.LocalDate;

/**
 * @author tjshan
 * @since 2019/6/11 14:15
 */
public class test {

    @Test
    public void test(){
        System.out.println(LocalDate.now());
        System.out.println(LocalDate.now().plusDays(-5));
        System.out.println(LocalDate.now().atStartOfDay());
        System.out.println(LocalDate.now().plusDays(1).atStartOfDay());
    }

    public static void main(String[] args) {
        System.out.println(LocalDate.now());
        System.out.println(LocalDate.now().atStartOfDay());
    }
}

package com.hanghae99.onit_be.utils;

import org.springframework.stereotype.Component;

@Component
public class Valid {

    public static boolean validWriter(boolean plan, String s) {
        if(plan){
            throw new IllegalArgumentException(s);
        }
        return false;
    }
}

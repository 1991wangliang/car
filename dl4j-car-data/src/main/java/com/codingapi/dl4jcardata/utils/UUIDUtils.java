package com.codingapi.dl4jcardata.utils;

import java.util.UUID;

public class UUIDUtils {


    public static String randomId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}

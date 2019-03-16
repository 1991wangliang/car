package com.codingapi.dl4jcardetection.utils;

import java.util.UUID;

public class UUIDUtils {


    public static String randomId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}

// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.common.encrypt;

import java.util.Base64;

/**
 * @author Kael He (kael.he@alo7.com)
 */
public class Base64Util {
    public static String encodeToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
    public static byte[] encode(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    public static byte[] decode(byte[] bytes) {
        return Base64.getDecoder().decode(bytes);
    }

    public static byte[] decode(String text) {
        return Base64.getDecoder().decode(text);
    }


}

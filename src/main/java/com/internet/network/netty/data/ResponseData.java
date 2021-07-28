// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.network.netty.data;

/**
 * @author Kael He(kael.he@alo7.com)
 * @since
 */
public class ResponseData {
    private int intValue;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "intValue=" + intValue +
                '}';
    }
}

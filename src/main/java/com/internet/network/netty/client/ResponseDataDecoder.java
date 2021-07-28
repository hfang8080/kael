// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.network.netty.client;

import com.internet.network.netty.data.ResponseData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author Kael He(kael.he@alo7.com)
 * @since
 */
public class ResponseDataDecoder extends ReplayingDecoder<ResponseData> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ResponseData responseData = new ResponseData();
        responseData.setIntValue(in.readInt());
        out.add(responseData);
    }
}

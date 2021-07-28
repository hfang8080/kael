// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.network.netty.server;

import com.internet.network.netty.data.RequestData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Kael He(kael.he@alo7.com)
 * @since
 */
public class RequestDataDecoder extends ReplayingDecoder<RequestData> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        RequestData requestData = new RequestData();
        requestData.setIntValue(in.readInt());
        requestData.setStringValue(in.readCharSequence(in.readInt(), Charset.forName("UTF-8")).toString());
        out.add(requestData);
    }
}

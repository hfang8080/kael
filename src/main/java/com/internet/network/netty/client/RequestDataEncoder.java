// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.network.netty.client;

import com.internet.network.netty.data.RequestData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * @author Kael He(kael.he@alo7.com)
 * @since
 */
public class RequestDataEncoder extends MessageToByteEncoder<RequestData> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RequestData msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getIntValue());
        out.writeInt(msg.getStringValue().length());
        out.writeCharSequence(msg.getStringValue(), Charset.forName("UTF-8"));
    }
}

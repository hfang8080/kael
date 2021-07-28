// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.network.netty.server;

import com.internet.network.netty.data.ResponseData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Kael He(kael.he@alo7.com)
 * @since
 */
public class ResponseDataEncoder extends MessageToByteEncoder<ResponseData> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseData msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getIntValue());
    }
}

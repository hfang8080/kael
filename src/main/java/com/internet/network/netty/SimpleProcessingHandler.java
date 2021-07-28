// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.network.netty;

import com.internet.network.netty.data.RequestData;
import com.internet.network.netty.data.ResponseData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Kael He(kael.he@alo7.com)
 * @since
 */
public class SimpleProcessingHandler extends ChannelInboundHandlerAdapter {

    ByteBuf tmp;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg;
        tmp.writeBytes(m);
        m.release();
        if (tmp.readableBytes() >= 4) {
            RequestData requestData = new RequestData();
            requestData.setIntValue(tmp.readInt());
            ResponseData responseData = new ResponseData();
            responseData.setIntValue(requestData.getIntValue() * 2);
            ChannelFuture future = ctx.writeAndFlush(responseData);
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Handler added");
        tmp  = ctx.alloc().buffer(4);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Handler removed");
        tmp.release();
        tmp = null;
    }
}

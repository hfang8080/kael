// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.network.netty.client;

import com.internet.network.netty.data.RequestData;
import com.internet.network.netty.data.ResponseData;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Kael He(kael.he@alo7.com)
 * @since
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        RequestData requestData = new RequestData();
        requestData.setIntValue(123);
        requestData.setStringValue("all work and no play makes jack a dull boy");
        ChannelFuture channelFuture = ctx.writeAndFlush(requestData);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println((ResponseData)msg);
        ctx.close();
    }
}

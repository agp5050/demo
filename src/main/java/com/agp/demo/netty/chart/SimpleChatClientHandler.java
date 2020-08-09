package com.agp.demo.netty.chart;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端 channel
 * 
 * @author waylau.com
 * @date 2015-2-26
 */
public class SimpleChatClientHandler extends SimpleChannelInboundHandler<String> {
	protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
		System.out.println("Client Received from Server: "+s);
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
		channelRead0(ctx,msg);
	}
}

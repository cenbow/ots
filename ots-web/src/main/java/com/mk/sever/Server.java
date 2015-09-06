package com.mk.sever;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(Server.class);

	private String ip = null;

	private int port = 0;

	public Server(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public void run() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.handler(new InnerChannelHandler());

			ChannelFuture channelFuture = bootstrap.connect(this.getHostAddress()).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			Server.logger.error("can not connect synserver {}:{}", this.getIp(), this.getPort());
		} finally {
			group.shutdownGracefully();
			this.reConnect();
		}
	}

	private void reConnect() {
		try {
			TimeUnit.SECONDS.sleep(5);
			new Thread(new Server(this.getIp(), this.getPort())).start();
		} catch (InterruptedException e) {
			Server.logger.error(e.getMessage(), e);
		}
	}

	private class InnerChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
			pipeline.addLast(new ObjectEncoder());

			pipeline.addLast(new ServerHandler());
		}
	}

	private InetSocketAddress getHostAddress() {
		return new InetSocketAddress(this.getIp(), this.getPort());
	}

	private String getIp() {
		return this.ip;
	}

	private int getPort() {
		return this.port;
	}

}

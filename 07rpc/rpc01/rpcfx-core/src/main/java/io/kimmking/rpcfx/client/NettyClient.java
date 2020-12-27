package io.kimmking.rpcfx.client;

import io.kimmking.rpcfx.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyClient {

    private final String host;

    private final Integer port;

    private final String req;


    public NettyClient(String host, Integer port, String req) {
        this.host = host;
        this.port = port;
        this.req = req;
    }

    public String start() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        String resp;
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.
                    group(eventLoopGroup).
                    channel(NioSocketChannel.class).
                    remoteAddress(host, port).
                    handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ClientHandler(req));
                        }
                    });
            ChannelFuture future = bootstrap.connect().sync();
            resp = future.channel().alloc().buffer().toString(CharsetUtil.UTF_8);
            System.out.println(resp);
            future.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
        return resp;
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyClient("127.0.0.1", 8888, "hello netty").start();
    }

}

package com.neu.p2pConnectionGroup.nettyClient;

import com.neu.liveNodeList.LiveNodeList;
import com.neu.protocol.TransmitProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Netty client uses to actively connect to a remote peer.
 */
@Slf4j
public class NettyClient {

    private final Bootstrap bootstrap;
    private final EventLoopGroup group;

    private final LiveNodeList nodeList;

    public NettyClient(LiveNodeList nodeList) {
        this.nodeList = nodeList;

        log.info("Netty client started ");
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                // nio connection
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        // add byte encoder and decoder
                        p.addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(TransmitProtocol.class.getClassLoader())));
                        p.addLast(new ObjectEncoder());
                        // This is our custom client handler which will have logic for chat.
//                            p.addLast(new ChatClientHandler());
                    }
                });

    }


    /**
     * Asynchronously Connect to a remote peer with given hostname and port.
     * If success the io channel of the connection will be added to live node list,
     * otherwise, it will retry once after 3 seconds delay.
     *
     * @param hostname hostname
     * @param port port
     */
    public void connectTo(String hostname, int port) {
        bootstrap.connect(hostname, port).addListener((ChannelFutureListener) channelFuture -> {
            // the listener of the connection thread to monitor the result of the connection
            if (channelFuture.isSuccess()) {
                log.info("Successfully connect to the host: " + hostname + ", port: " + port);
                // add to live node list
                // TODO



            } else {
                log.error("Failed to connect to the host: " + hostname + ", port: " + port);
                log.info("Retrying the connection after 3 seconds");
                // get the io thread and retry
                channelFuture.channel().eventLoop().schedule(() -> connectTo(hostname, port), 3, TimeUnit.SECONDS);
            }
        });
    }

    /**
     * Call when client shutdown.
     */
    public void onClose() {
        group.shutdownGracefully();
        log.info("Netty client shutdown");
    }


}

package io.nadron;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * A simple test class for connecting Nad client using Google's Protocol Buffers
 * Protocol to a remote nadron server. This does
 * not have any game logic and will just print out events coming from the
 * server.
 * 
 * @author Saurabh Shukul
 * 
 */
public final class ProtobufTestClient {

    static final boolean SSL = System.getProperty("ssl") == "true";
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "18090"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ProtobufTestClientInitializer(sslCtx));

            // Make a new connection.
            ChannelFuture f = b.connect(HOST, PORT).sync();

            // Get the handler instance to retrieve the answer.
            ProtobufTestClientHandler handler =
                (ProtobufTestClientHandler) f.channel().pipeline().last();

            while (true) {
                // Print out the result
                System.err.format("Received event: %s", handler.getEvent());
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}

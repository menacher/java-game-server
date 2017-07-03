
package io.nadron;

import com.google.protobuf.Any;
import io.nadron.networking.NadronMessages.NadronEvent;
import io.nadron.networking.NadronMessages.ConnectionConfig;
import io.nadron.networking.NadronMessages.EventType;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handler for a client-side channel.  This handler maintains stateful
 * information which is specific to a certain channel using member variables.
 * Therefore, an instance of this handler can cover only one channel.  You have
 * to create a new handler instance whenever you create a new channel and insert
 * this handler to avoid a race condition.
 */
public class ProtobufTestClientHandler extends ChannelInboundHandlerAdapter {

    final BlockingQueue<NadronEvent> events = new LinkedBlockingQueue<>();

    public NadronEvent getEvent() {
        boolean interrupted = false;
        try {
            for (;;) {
                try {
                    return events.take();
                } catch (InterruptedException ignore) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ConnectionConfig connectionConfig = ConnectionConfig.newBuilder()
                .setUser("user")
                .setPass("pass")
                .setConnectionKey("BattleRoom")
                .build();

        NadronEvent event = NadronEvent.newBuilder()
                .setEventType(EventType.LOG_IN)
                .setSource(Any.pack(connectionConfig))
                .setTimeStamp(new Date().getTime())
                .build();
        ctx.writeAndFlush(event);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NadronEvent m = (NadronEvent) msg;
        // Offer the attack result after closing the connection.
        ctx.channel().close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                boolean offered = events.offer(m);
                assert offered;
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

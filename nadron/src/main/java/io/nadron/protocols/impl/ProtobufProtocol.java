package io.nadron.protocols.impl;

import io.nadron.app.PlayerSession;
import io.nadron.event.impl.DefaultEvent;
import io.nadron.handlers.netty.DefaultToServerHandler;
import io.nadron.handlers.netty.ProtobufEventToEventDecoder;
import io.nadron.networking.NadronMessages.NadronEvent;
import io.nadron.protocols.AbstractNettyProtocol;
import io.nadron.util.NettyUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtobufProtocol extends AbstractNettyProtocol {

    private static final Logger LOG = LoggerFactory.getLogger(ProtobufProtocol.class);

    public ProtobufProtocol()
    {
        super("PROTOBUF_PROTOCOL");
    }

    private boolean gzipCompression = false;

    @Override
    public void applyProtocol(PlayerSession playerSession) {
        LOG.debug("Going to apply {} on session: {}", getProtocolName(),
                playerSession);

        ChannelPipeline pipeline = NettyUtils.getPipeLineOfConnection(playerSession);
        NettyUtils.clearPipeline(pipeline);

        if (gzipCompression) {
            pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
            pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
        }

        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(NadronEvent.getDefaultInstance()));
        pipeline.addLast(new ProtobufEventToEventDecoder(new DefaultEvent()));
        pipeline.addLast("eventHandler",new DefaultToServerHandler(playerSession));

        // Downstream handlers
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());
    }

    public void setGzipCompression(boolean gzipCompression) {
        this.gzipCompression = gzipCompression;
    }
}

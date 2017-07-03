package io.nadron.handlers.netty;

import io.nadron.event.impl.DefaultEvent;
import io.nadron.networking.NadronMessages.NadronEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.springframework.beans.factory.annotation.Autowired;

import static io.nadron.event.Events.LOG_IN;
import static io.nadron.event.Events.RECONNECT;

/**
 * Created by saurabhshukul on 13/02/17.
 */
public class ProtobufLoginProtocol implements LoginProtocol {

    @Autowired
    private ProtobufLoginHandler loginHandler;

    private boolean gzipCompression = false;

    @Override
    public boolean applyProtocol(ByteBuf buffer, ChannelPipeline pipeline) {
        boolean isThisProtocol = false;
        short opcode = buffer.getUnsignedByte(buffer.readerIndex() + 2);

        if(this.isProtobufProtocol(opcode)) {
            if (gzipCompression) {
                pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
                pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
            }

            pipeline.addLast(new ProtobufVarint32FrameDecoder());
            pipeline.addLast(new ProtobufDecoder(NadronEvent.getDefaultInstance()));
            pipeline.addLast(new ProtobufEventToEventDecoder(new DefaultEvent()));
            pipeline.addLast(LOGIN_HANDLER_NAME, loginHandler);

            // Downstream handlers
            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
            pipeline.addLast(new ProtobufEncoder());
            isThisProtocol = true;
        }

        return isThisProtocol;
    }

    protected boolean isProtobufProtocol(int magic1)
    {
        return (magic1 == LOG_IN || magic1 == RECONNECT);
    }

    public ProtobufLoginHandler getLoginHandler()
    {
        return loginHandler;
    }

    public void setLoginHandler(ProtobufLoginHandler loginHandler)
    {
        this.loginHandler = loginHandler;
    }

    public void setGzipCompression(boolean gzipCompression) {
        this.gzipCompression = gzipCompression;
    }
}

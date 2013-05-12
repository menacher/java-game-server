package org.menacheri.jetclient.handlers.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import org.menacheri.jetclient.app.Session;
import org.menacheri.jetclient.communication.MessageBuffer;
import org.menacheri.jetclient.event.Event;

/**
 * This pipeline factory can be considered the default 'protocol' for client
 * side communication with jetserver. For other protocols, different
 * {@link ChannelInitializer}s, with different encoders and decoders in its
 * pipeline should be used to connect to remote jetserver.
 * 
 * @author Abraham Menacherry.
 * 
 */
public class TCPPipelineFactory extends ChannelInitializer<SocketChannel>
{
	/**
	 * Prepends the length of transmitted message before sending to remote
	 * jetserver.
	 */
	private static final LengthFieldPrepender LENGTH_FIELD_PREPENDER = new LengthFieldPrepender(
			2);
	/**
	 * Decodes incoming messages from remote jetserver to {@link MessageBuffer}
	 * type, puts this as the payload for an {@link Event} and passes this
	 * {@link Event} instance to the next decoder/handler in the chain.
	 */
	private static final MessageBufferEventDecoder EVENT_DECODER = new MessageBufferEventDecoder();
	/**
	 * Decodes incoming messages from remote jetserver to {@link MessageBuffer}
	 * type, puts this as the payload for an {@link Event} and passes this
	 * {@link Event} instance to the next decoder/handler in the chain.
	 */
	private static final MessageBufferEventEncoder EVENT_ENCODER = new MessageBufferEventEncoder();
	/**
	 * Used to transmit the message to {@link Session}.
	 */
	private final DefaultToClientHandler defaultToClientHandler;

	public TCPPipelineFactory(Session session)
	{
		this.defaultToClientHandler = new DefaultToClientHandler(session);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(
				Integer.MAX_VALUE, 0, 2, 0, 2));
		pipeline.addLast("eventDecoder", EVENT_DECODER);
		pipeline.addLast(DefaultToClientHandler.getName(),
				defaultToClientHandler);
		
		// Down stream handlers are added now. Note that the last one added to
		// pipeline is actually the first encoder in the pipeline.
		pipeline.addLast("lengthFieldPrepender", LENGTH_FIELD_PREPENDER);
		pipeline.addLast("eventEncoder", EVENT_ENCODER);
		
	}
}

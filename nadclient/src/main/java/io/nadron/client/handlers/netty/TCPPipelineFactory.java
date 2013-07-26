package io.nadron.client.handlers.netty;

import io.nadron.client.app.Session;
import io.nadron.client.communication.MessageBuffer;
import io.nadron.client.event.Event;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;


/**
 * This pipeline factory can be considered the default 'protocol' for client
 * side communication with nadron server. For other protocols, different
 * {@link ChannelInitializer}s, with different encoders and decoders in its
 * pipeline should be used to connect to remote nadron server.
 * 
 * @author Abraham Menacherry.
 * 
 */
public class TCPPipelineFactory extends ChannelInitializer<SocketChannel>
{
	/**
	 * Prepends the length of transmitted message before sending to remote
	 * nadron server.
	 */
	private static final LengthFieldPrepender LENGTH_FIELD_PREPENDER = new LengthFieldPrepender(
			2);
	/**
	 * Decodes incoming messages from remote nadron server to {@link MessageBuffer}
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
		pipeline.addLast("eventDecoder", new MessageBufferEventDecoder());
		pipeline.addLast(DefaultToClientHandler.getName(),
				defaultToClientHandler);
		
		// Down stream handlers are added now. Note that the last one added to
		// pipeline is actually the first encoder in the pipeline.
		pipeline.addLast("lengthFieldPrepender", LENGTH_FIELD_PREPENDER);
		pipeline.addLast("eventEncoder", EVENT_ENCODER);
		
	}
}

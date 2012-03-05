package org.menacheri.jetclient.handlers.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.menacheri.jetclient.app.ISession;
import org.menacheri.jetclient.communication.IMessageBuffer;
import org.menacheri.jetclient.event.IEvent;

/**
 * This pipeline factory can be considered the default 'protocol' for client
 * side communication with jetserver. For other protocols, different
 * {@link ChannelPipelineFactory}, with different encoders and decoders in its
 * pipeline should be used to connect to remote jetserver.
 * 
 * @author Abraham Menacherry.
 * 
 */
public class TCPPipelineFactory implements ChannelPipelineFactory
{
	/**
	 * Prepends the length of transmitted message before sending to remote
	 * jetserver.
	 */
	private static final LengthFieldPrepender LENGTH_FIELD_PREPENDER = new LengthFieldPrepender(
			2);
	/**
	 * Decodes incoming messages from remote jetserver to {@link IMessageBuffer}
	 * type, puts this as the payload for an {@link IEvent} and passes this
	 * {@link IEvent} instance to the next decoder/handler in the chain.
	 */
	private static final MessageBufferEventDecoder EVENT_DECODER = new MessageBufferEventDecoder();
	/**
	 * Decodes incoming messages from remote jetserver to {@link IMessageBuffer}
	 * type, puts this as the payload for an {@link IEvent} and passes this
	 * {@link IEvent} instance to the next decoder/handler in the chain.
	 */
	private static final MessageBufferEventEncoder EVENT_ENCODER = new MessageBufferEventEncoder();
	/**
	 * Used to transmit the message to {@link ISession}.
	 */
	private final DefaultToClientHandler defaultToClientHandler;

	public TCPPipelineFactory(ISession session)
	{
		this(new DefaultToClientHandler(session));
	}

	public TCPPipelineFactory(
			final DefaultToClientHandler defaultToClientHandler)
	{
		this.defaultToClientHandler = defaultToClientHandler;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(
				Integer.MAX_VALUE, 0, 2, 0, 2));
		pipeline.addLast("eventDecoder", EVENT_DECODER);
		pipeline.addLast(DefaultToClientHandler.getName(),
				defaultToClientHandler);
		
		// Down stream handlers are added now. Note that the last one added to
		// pipeline is actually the first encoder in the pipeline.
		pipeline.addLast("lengthFieldPrepender", LENGTH_FIELD_PREPENDER);
		pipeline.addLast("eventEncoder", EVENT_ENCODER);
		
		return pipeline;
	}
}

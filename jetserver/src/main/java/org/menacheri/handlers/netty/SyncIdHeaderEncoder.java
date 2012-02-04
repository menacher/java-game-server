package org.menacheri.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.menacheri.service.IGameStateManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This encoder will add state related information to the output message. It is
 * responsible for adding the AMF3 byte array and synchronization key in the
 * state manager to the message.
 * 
 * @author Abraham Menacherry
 * 
 */
public class SyncIdHeaderEncoder extends OneToOneEncoder
{
	private static final Logger LOG = LoggerFactory.getLogger(SyncIdHeaderEncoder.class);
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception
	{
		if (!(msg instanceof IGameStateManagerService))
		{
			LOG.error("Incoming message type: not of "
					+ "IGameStateManagerService type.", msg.getClass()
					.getName());
			return msg;
		}

		// Get the state manager instance.
		IGameStateManagerService stateManager = (IGameStateManagerService) msg;
		
		// Get the synchronization key 
		Integer syncKey = (Integer)stateManager.getSyncKey();
		LOG.trace("Synckey sent from server: {}", syncKey);
		// write the sync integer key to a buffer.
		ChannelBuffer syncHeader = ChannelBuffers.buffer(4);
		syncHeader.writeInt(syncKey);

		// write the amf3 data to buffer.
		ChannelBuffer amf3Buf = ChannelBuffers
				.wrappedBuffer(stateManager
						.getSerializedByteArray());
		
		// wrap the header and body together.
		ChannelBuffer buffer = ChannelBuffers
				.wrappedBuffer(syncHeader, amf3Buf);
		
		return buffer;
	}
}

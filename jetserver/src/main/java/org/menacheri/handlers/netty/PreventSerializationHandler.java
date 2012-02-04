package org.menacheri.handlers.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.menacheri.handlers.IStateAware;
import org.menacheri.service.IGameStateManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This handler is setup in the pipeline to prevent unnecessary serialization
 * and de-serialization from AMF3 to Java and back. It will will check if the
 * incoming message was able to set the synchronization key, if not the message
 * will be discarded.
 * 
 * @author Abraham Menacherry
 * 
 */
public class PreventSerializationHandler extends SimpleChannelUpstreamHandler implements IStateAware
{
	private static final Logger LOG = LoggerFactory.getLogger(PreventSerializationHandler.class);
	private IGameStateManagerService gameStateManagerService;
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		ChannelBuffer buffer = (ChannelBuffer)e.getMessage();
		int syncKey = buffer.readInt();
		
		if(isFurtherProcessingRequired(syncKey))
		{
			LOG.trace("isFurtherProcessingRequired "
					+ "for syncKey: {}, returned true", syncKey);
			buffer.discardReadBytes();
			// No change, just forward it along the pipeline.
			super.messageReceived(ctx, e);
		}
		else
		{
			LOG.trace("isFurtherProcessingRequired "
					+ "for syncKey: {}, returned false", syncKey);
			e.getChannel().write(false);
		}
	}
	
	public boolean isFurtherProcessingRequired(int syncKey)
	{
		if(null != gameStateManagerService)
		{
			// Need to compare and set as soon as possible before another session
			// does it
			if(gameStateManagerService.compareAndSetSyncKey(syncKey))
			{
				// This incoming message is able to set the state, hence further
				// processing is necessary.
				return true;
			}
			else
			{
				// Incoming user session is unable to set state, no further
				// processing is necessary, just broadcast
				return false;
			}
		}
		else
		{
			// Cannot check state, hence further processing is necessary
			return true;
		}
	}

	@Override
	public IGameStateManagerService getGameStateManagerService()
	{
		return gameStateManagerService;
	}

	@Override
	public void setGameStateManagerService(
			IGameStateManagerService gameStateManagerService)
	{
		this.gameStateManagerService = gameStateManagerService;
	}
}

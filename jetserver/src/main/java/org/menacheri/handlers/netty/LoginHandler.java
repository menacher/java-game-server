package org.menacheri.handlers.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayer;
import org.menacheri.app.IPlayerSession;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
import org.menacheri.server.netty.NettyServer;
import org.menacheri.service.ILookupService;
import org.menacheri.service.ISessionRegistryService;
import org.menacheri.util.Credentials;
import org.menacheri.util.ICredentials;
import org.menacheri.util.NettyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Sharable
public class LoginHandler extends SimpleChannelUpstreamHandler
{
	private static final Logger LOG = LoggerFactory
			.getLogger(LoginHandler.class);

	private ILookupService lookupService;
	ISessionRegistryService sessionRegistryService;
	/**
	 * Used for book keeping purpose. It will count all open channels. Currently
	 * closed channels will not lead to a decrement.
	 */
	private static final AtomicInteger CHANNEL_COUNTER =  new AtomicInteger(0);
	
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
			throws Exception
	{
		final IEvent event = (IEvent)e.getMessage();
		final ChannelBuffer buffer = (ChannelBuffer) event.getSource();
		final Channel channel = e.getChannel();
		if(event.getType() == Events.LOG_IN)
		{
			LOG.trace("Login attempt from {}",channel.getRemoteAddress());
			IPlayer player = lookupPlayer(buffer, channel);
			handleLogin(player,channel);
			handleGameRoomJoin(player, channel,buffer);
		}
		else
		{
			LOG.error("Invalid event {} sent from remote address {}. "
					+ "Going to close channel {}",
					new Object[] { event.getType(), channel.getRemoteAddress(),
							channel.getId() });
			closeChannelWithLoginFailure(channel);
		}
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		NettyServer.ALL_CHANNELS.add(e.getChannel());
		LOG.debug("Added Channel with id: {} as the {}th open channel", e
				.getChannel().getId(), CHANNEL_COUNTER.incrementAndGet());
	}
	
	public IPlayer lookupPlayer(final ChannelBuffer buffer, final Channel channel)
	{
		ICredentials credentials = new Credentials(buffer);
		IPlayer player = lookupService.playerLookup(credentials);
		if(null == player){
			LOG.error("Invalid credentials provided by user: {}",credentials);
		}
		return player;
	}
	
	public void handleLogin(IPlayer player,Channel channel)
	{
		if (null != player)
		{
			channel.write(NettyUtils
					.createBufferForOpcode(Events.LOG_IN_SUCCESS));
		}
		else
		{
			// Write future and close channel
			closeChannelWithLoginFailure(channel);
		}
	}
	
	/**
	 * Helper method which will close the channel after writing
	 * {@link Events#LOG_IN_FAILURE} to remote connection.
	 * 
	 * @param channel
	 *            The tcp connection to remote machine that will be closed.
	 */
	private void closeChannelWithLoginFailure(Channel channel)
	{
		ChannelFuture future = channel.write(NettyUtils
				.createBufferForOpcode(Events.LOG_IN_FAILURE));
		future.addListener(ChannelFutureListener.CLOSE);
	}
	
	public void handleGameRoomJoin(IPlayer player, Channel channel, ChannelBuffer buffer)
	{
		String refKey = NettyUtils.readString(buffer);
		
		IGameRoom gameRoom = lookupService.gameRoomLookup(refKey);
		if(null != gameRoom)
		{
			IPlayerSession playerSession = gameRoom.createPlayerSession();
			playerSession.setConnectParameter(NettyUtils.NETTY_CHANNEL,
					channel);
			gameRoom.onLogin(playerSession);
			LOG.trace("Sending GAME_ROOM_JOIN_SUCCESS to channel {}", channel.getId());
			ChannelFuture future = channel.write(NettyUtils.createBufferForOpcode(Events.GAME_ROOM_JOIN_SUCCESS));
			connectToGameRoom(gameRoom, playerSession, future);
			loginUdp(playerSession,buffer);
		}
		else
		{
			// Write failure and close channel.
			ChannelFuture future = channel.write(NettyUtils.createBufferForOpcode(Events.GAME_ROOM_JOIN_FAILURE));
			future.addListener(ChannelFutureListener.CLOSE);
			LOG.error("Invalid ref key provided by client: {}. Channel {} will be closed",refKey,channel.getId());
		}
	}
	
	public void connectToGameRoom(final IGameRoom gameRoom, final IPlayerSession playerSession, ChannelFuture future)
	{
		future.addListener(new ChannelFutureListener()
		{
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception
			{
				Channel channel = future.getChannel();
				LOG.trace("Sending GAME_ROOM_JOIN_SUCCESS to channel {} completed", channel.getId());
				if (future.isSuccess())
				{
					LOG.trace("Going to clear pipeline");
					// Clear the existing pipeline
					NettyUtils.clearPipeline(channel.getPipeline());
					// Connect the pipeline to the game room.
					gameRoom.connectSession(playerSession);
					// Send the connect event to session so that it can create its message sender
					playerSession.onEvent(Events.event(channel, Events.CONNECT_TCP));
					// Add a data out listener so that it can write back stuff.
					//playerSession.addHandler(new NettyDataOutTCPListener(channel));
				}
				else
				{
					LOG.error("GAME_ROOM_JOIN_SUCCESS message sending to client was failure, channel will be closed");
					channel.close();
				}
			}
		});
	}
	
	protected void loginUdp(IPlayerSession playerSession,ChannelBuffer buffer)
	{
		InetSocketAddress remoteAdress = NettyUtils.readSocketAddress(buffer);
		if(null != remoteAdress)
		{
			sessionRegistryService.putSession(remoteAdress, playerSession);
		}
	}
	
	public ILookupService getLookupService()
	{
		return lookupService;
	}

	public void setLookupService(ILookupService lookupService)
	{
		this.lookupService = lookupService;
	}

	public ISessionRegistryService getSessionRegistryService()
	{
		return sessionRegistryService;
	}

	public void setSessionRegistryService(
			ISessionRegistryService sessionRegistryService)
	{
		this.sessionRegistryService = sessionRegistryService;
	}

}

package org.menacheri.handlers.netty;

import java.net.InetSocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayer;
import org.menacheri.app.IPlayerSession;
import org.menacheri.event.Events;
import org.menacheri.event.IEvent;
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
	
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		IEvent event = (IEvent)e.getMessage();
		ChannelBuffer buffer = (ChannelBuffer) event.getSource();
		Channel channel = e.getChannel();
		switch(event.getType())
		{
		case Events.LOG_IN:
			LOG.trace("Login attempt from {}",channel.getRemoteAddress());
			IPlayer player = lookupPlayer(buffer, channel);
			handleLogin(buffer, channel,player);
			break;
		}
	}

	public IPlayer lookupPlayer(ChannelBuffer buffer, Channel channel)
	{
		ICredentials credentials = new Credentials(buffer);
		IPlayer player = lookupService.playerLookup(credentials);
		if (null != player)
		{
			channel.write(NettyUtils
					.createBufferForOpcode(Events.LOG_IN_SUCCESS));
		}
		else
		{
			// Write future and close channel
			ChannelFuture future = channel.write(NettyUtils
					.createBufferForOpcode(Events.LOG_IN_FAILURE));
			future.addListener(ChannelFutureListener.CLOSE);
			LOG.error("Invalid credentials provided by user: {}",credentials);
		}
		return player;
	}
	
	public void handleLogin(ChannelBuffer buffer, Channel channel,IPlayer player)
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
			loginUdp(buffer, playerSession);
		}
		else
		{
			// Write failure and close channel.
			ChannelFuture future = channel.write(NettyUtils.createBufferForOpcode(Events.GAME_ROOM_JOIN_FAILURE));
			future.addListener(ChannelFutureListener.CLOSE);
			LOG.error("Invalid ref key provided by client: {}",refKey);
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
	
	protected void loginUdp(ChannelBuffer buffer,IPlayerSession playerSession)
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

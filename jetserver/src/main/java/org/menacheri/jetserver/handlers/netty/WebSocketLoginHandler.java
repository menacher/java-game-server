package org.menacheri.jetserver.handlers.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.app.Player;
import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.communication.NettyTCPMessageSender;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.impl.DefaultEvent;
import org.menacheri.jetserver.event.impl.ReconnetEvent;
import org.menacheri.jetserver.service.LookupService;
import org.menacheri.jetserver.service.UniqueIDGeneratorService;
import org.menacheri.jetserver.service.impl.ReconnectSessionRegistry;
import org.menacheri.jetserver.util.Credentials;
import org.menacheri.jetserver.util.JetConfig;
import org.menacheri.jetserver.util.SimpleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * This login handler will parse incoming login events to get the
 * {@link Credentials} and lookup {@link Player} and {@link GameRoom} objects.
 * It kicks of the session creation process and will then send the
 * {@link Events#START} event object to websocket client.
 * 
 * @author Abraham Menacherry
 * 
 */
@Sharable
public class WebSocketLoginHandler extends ChannelInboundMessageHandlerAdapter<TextWebSocketFrame>
{
	private static final Logger LOG = LoggerFactory
			.getLogger(WebSocketLoginHandler.class);

	private LookupService lookupService;
	protected ReconnectSessionRegistry reconnectRegistry;
	protected UniqueIDGeneratorService idGeneratorService;
	
	private Gson gson;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame frame)
			throws Exception
	{
		Channel channel = ctx.channel();
		String data = frame.text();
		LOG.trace("From websocket: " + data);
		Event event = gson.fromJson(data, DefaultEvent.class);
		int type = event.getType();
		if (Events.LOG_IN == type)
		{
			LOG.trace("Login attempt from {}", channel.remoteAddress());
			List<String> credList = null;
			credList = (List) event.getSource();
			Player player = lookupPlayer(credList.get(0), credList.get(1));
			handleLogin(player, channel);
			handleGameRoomJoin(player, channel, credList.get(2));
		}
		else if (type == Events.RECONNECT)
		{
			LOG.debug("Reconnect attempt from {}", channel.remoteAddress());
			PlayerSession playerSession = lookupSession((String)event.getSource());
			handleReconnect(playerSession, channel);
		}
		else
		{
			LOG.error(
					"Invalid event {} sent from remote address {}. "
							+ "Going to close channel {}",
					new Object[] { event.getType(),
							channel.remoteAddress(), channel.id() });
			closeChannelWithLoginFailure(channel);
		}
	}

	public PlayerSession lookupSession(final String reconnectKey)
	{
		PlayerSession playerSession = (PlayerSession)reconnectRegistry.getSession(reconnectKey);
		if(null != playerSession)
		{
			synchronized(playerSession){
				// if its an already active session then do not allow a
				// reconnect. So the only state in which a client is allowed to
				// reconnect is if it is "NOT_CONNECTED"
				if(playerSession.getStatus() == Session.Status.NOT_CONNECTED)
				{
					playerSession.setStatus(Session.Status.CONNECTING);
				}
				else
				{
					playerSession = null;
				}
			}
		}
		return playerSession;
	}
	
	protected void handleReconnect(PlayerSession playerSession, Channel channel)
	{
		if (null != playerSession)
		{
			channel.write(eventToFrame(Events.LOG_IN_SUCCESS, null));
			GameRoom gameRoom = playerSession.getGameRoom();
			gameRoom.disconnectSession(playerSession);
			if (null != playerSession.getTcpSender())
				playerSession.getTcpSender().close();

			handleReJoin(playerSession, gameRoom, channel);
		}
		else
		{
			// Write future and close channel
			closeChannelWithLoginFailure(channel);
		}
	}
	
	protected void handleReJoin(PlayerSession playerSession, GameRoom gameRoom, Channel channel)
	{
		// Set the tcp channel on the session. 
		NettyTCPMessageSender sender = new NettyTCPMessageSender(channel);
		playerSession.setTcpSender(sender);
		// Connect the pipeline to the game room.
		gameRoom.connectSession(playerSession);
		channel.write(Events.GAME_ROOM_JOIN_SUCCESS, null);//assumes that the protocol applied will take care of event objects.
		playerSession.setWriteable(true);// TODO remove if unnecessary. It should be done in start event
		// Send the re-connect event so that it will in turn send the START event.
		playerSession.onEvent(new ReconnetEvent(sender));
	}
	
	public Player lookupPlayer(String username, String password)
	{
		Credentials credentials = new SimpleCredentials(username, password);
		Player player = lookupService.playerLookup(credentials);
		if (null == player)
		{
			LOG.error("Invalid credentials provided by user: {}", credentials);
		}
		return player;
	}

	public void handleLogin(Player player, Channel channel)
	{
		if (null != player)
		{
			channel.write(eventToFrame(Events.LOG_IN_SUCCESS, null));
		}
		else
		{
			// Write future and close channel
			closeChannelWithLoginFailure(channel);
		}
	}

	protected void closeChannelWithLoginFailure(Channel channel)
	{
		// Close the connection as soon as the error message is sent.
		channel.write(eventToFrame(Events.LOG_IN_FAILURE, null)).addListener(
				ChannelFutureListener.CLOSE);
	}

	public void handleGameRoomJoin(Player player, Channel channel, String refKey)
	{
		GameRoom gameRoom = lookupService.gameRoomLookup(refKey);
		if (null != gameRoom)
		{
			PlayerSession playerSession = gameRoom.createPlayerSession(player);
			gameRoom.onLogin(playerSession);
			String reconnectKey = (String)idGeneratorService
					.generateFor(playerSession.getClass());
			playerSession.setAttribute(JetConfig.RECONNECT_KEY, reconnectKey);
			playerSession.setAttribute(JetConfig.RECONNECT_REGISTRY, reconnectRegistry);
			LOG.trace("Sending GAME_ROOM_JOIN_SUCCESS to channel {}",
					channel.id());
			ChannelFuture future = channel.write(eventToFrame(
					Events.GAME_ROOM_JOIN_SUCCESS, reconnectKey));
			connectToGameRoom(gameRoom, playerSession, future);
		}
		else
		{
			// Write failure and close channel.
			ChannelFuture future = channel.write(eventToFrame(
					Events.GAME_ROOM_JOIN_FAILURE, null));
			future.addListener(ChannelFutureListener.CLOSE);
			LOG.error(
					"Invalid ref key provided by client: {}. Channel {} will be closed",
					refKey, channel.id());
		}
	}

	public void connectToGameRoom(final GameRoom gameRoom,
			final PlayerSession playerSession, ChannelFuture future)
	{
		future.addListener(new ChannelFutureListener()
		{
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception
			{
				Channel channel = future.channel();
				LOG.trace(
						"Sending GAME_ROOM_JOIN_SUCCESS to channel {} completed",
						channel.id());
				if (future.isSuccess())
				{
					// Set the tcp channel on the session.
					NettyTCPMessageSender sender = new NettyTCPMessageSender(
							channel);
					playerSession.setTcpSender(sender);
					// Connect the pipeline to the game room.
					gameRoom.connectSession(playerSession);
					// Send the connect event so that it will in turn send the
					// START event.
					playerSession.onEvent(Events.connectEvent(sender));
				}
				else
				{
					LOG.error("Sending GAME_ROOM_JOIN_SUCCESS message to client was failure, channel will be closed");
					channel.close();
				}
			}
		});
	}

	protected TextWebSocketFrame eventToFrame(byte opcode, Object payload)
	{
		Event event = Events.event(payload, opcode);
		return new TextWebSocketFrame(gson.toJson(event));
	}

	public LookupService getLookupService()
	{
		return lookupService;
	}

	public void setLookupService(LookupService lookupService)
	{
		this.lookupService = lookupService;
	}

	public Gson getGson()
	{
		return gson;
	}

	public void setGson(Gson gson)
	{
		this.gson = gson;
	}

	public ReconnectSessionRegistry getReconnectRegistry()
	{
		return reconnectRegistry;
	}

	public void setReconnectRegistry(ReconnectSessionRegistry reconnectRegistry)
	{
		this.reconnectRegistry = reconnectRegistry;
	}

	public UniqueIDGeneratorService getIdGeneratorService()
	{
		return idGeneratorService;
	}

	public void setIdGeneratorService(UniqueIDGeneratorService idGeneratorService)
	{
		this.idGeneratorService = idGeneratorService;
	}

}

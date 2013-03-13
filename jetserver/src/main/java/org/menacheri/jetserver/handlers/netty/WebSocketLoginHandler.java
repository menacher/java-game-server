package org.menacheri.jetserver.handlers.netty;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.app.Player;
import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.communication.NettyTCPMessageSender;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.impl.DefaultEvent;
import org.menacheri.jetserver.service.LookupService;
import org.menacheri.jetserver.service.UniqueIDGeneratorService;
import org.menacheri.jetserver.service.impl.ReconnectSessionRegistry;
import org.menacheri.jetserver.util.Credentials;
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
public class WebSocketLoginHandler extends SimpleChannelUpstreamHandler
{
	private static final Logger LOG = LoggerFactory
			.getLogger(WebSocketLoginHandler.class);

	private LookupService lookupService;
	protected ReconnectSessionRegistry reconnectRegistry;
	protected UniqueIDGeneratorService idGeneratorService;
	
	private Gson gson;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		Channel channel = ctx.getChannel();
		if (e.getMessage() instanceof TextWebSocketFrame)
		{
			TextWebSocketFrame frame = (TextWebSocketFrame) e.getMessage();
			String data = frame.getText();
			LOG.trace("From websocket: " + data);
			Event event = gson.fromJson(data, DefaultEvent.class);

			if (Events.LOG_IN == event.getType())
			{
				LOG.trace("Login attempt from {}", channel.getRemoteAddress());
				List<String> credList = null;
				credList = (List) event.getSource();
				Player player = lookupPlayer(credList.get(0), credList.get(1));
				handleLogin(player, channel);
				handleGameRoomJoin(player, channel, credList.get(2));
			}
			else
			{
				LOG.error(
						"Invalid event {} sent from remote address {}. "
								+ "Going to close channel {}",
						new Object[] { event.getType(),
								channel.getRemoteAddress(), channel.getId() });
				closeChannelWithLoginFailure(channel);
			}
		}
		else
		{
			closeChannelWithLoginFailure(channel);
		}
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
			LOG.trace("Sending GAME_ROOM_JOIN_SUCCESS to channel {}",
					channel.getId());
			ChannelFuture future = channel.write(eventToFrame(
					Events.GAME_ROOM_JOIN_SUCCESS, null));
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
					refKey, channel.getId());
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
				Channel channel = future.getChannel();
				LOG.trace(
						"Sending GAME_ROOM_JOIN_SUCCESS to channel {} completed",
						channel.getId());
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

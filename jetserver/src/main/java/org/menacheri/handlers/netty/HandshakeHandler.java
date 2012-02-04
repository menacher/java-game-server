package org.menacheri.handlers.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;
import org.menacheri.app.IGameRoom;
import org.menacheri.app.IPlayerSession;
import org.menacheri.handlers.IHandshakeHandler;
import org.menacheri.protocols.UnknownProtocolException;
import org.menacheri.server.netty.HandshakePipelineFactory;
import org.menacheri.service.IHandshakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * This class is the default handshake handler which will be configured into the
 * Netty pipeline by default. The expectation is that incoming connections would
 * provide a valid reference key to this handler, which would used it to "place"
 * the connection to the right game or application. Once the reference key is
 * validated, the class sends back an ack message to client to see if writing
 * back to the connection has any issue. The client is supposed to send the same
 * ack back with only the protocol character changed if necessary.
 * 
 * @author Abraham Menacherry
 * 
 */
public class HandshakeHandler extends SimpleChannelUpstreamHandler implements
		IHandshakeHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(HandshakeHandler.class);
	
	// Constants
	private static final int MAX_IDLE_SECONDS = 60;
	private static final String IDLE_STATE_HANDLER = "idleStateHandler";
	private static final String IDLE_STATE_CHECK = "idleStateCheck";
	
	// Services that are used by handshake handler
	private IHandshakeService handshakeSerivce;

	private Timer timer;
	private IdleStateAwareChannelHandler idleCheckHandler;


	// TODO all the following objects can be off loaded to a map in the
	// HandshakeService object. In which case this HandshakeHandler can be a
	// singleton.
	private String expectedAck;
	private boolean isValidated = false;
	private IPlayerSession playerSession;
	private Channel channel = null;
	
	public HandshakeHandler()
	{
		expectedAck = null;
		playerSession = null;
		isValidated = false;
	}

	public HandshakeHandler(IHandshakeService handshakeSerivce, Timer timer,
			IdleStateAwareChannelHandler idleCheckHandler)
	{
		super();
		this.handshakeSerivce = handshakeSerivce;
		this.timer = timer;
		this.idleCheckHandler = idleCheckHandler;
		playerSession = null;
		expectedAck = null;
		isValidated = false;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		String message = (String) e.getMessage();
		
		// Check if this is a validated connection.
		if (isValidated)
		{
			LOG.debug("In handshakehandler instance: " + this
							+ " playersession id: "
							+ playerSession.getId());
			// Session is already validated, ack needs to be validated.
			manageAckFromClient(playerSession, message);
		}
		else
		{
			// Session is not validated, the following method will
			// validate it and set the isValidated flag.
			manageIncomingSessionFromClient(channel, message);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
	{
		LOG.error("Exception in HandshakeHandler class: {}.",e);
		e.getChannel().close();
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception
	{
		channel = e.getChannel();
		super.channelConnected(ctx, e);
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception
	{
		// TODO send disconnect event to session.
		super.channelClosed(ctx, e);
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception
	{
		// TODO send disconnect event to session.
		super.channelDisconnected(ctx, e);
	}
	
	public void manageAckFromClient(IPlayerSession playerSession,
			String message) throws UnknownProtocolException
	{
		String incomingAck = message;
		boolean isAcknowledged = handshakeSerivce.validateAck(playerSession,
				incomingAck, expectedAck);
		if (isAcknowledged)
		{
			// ALL CONDITIONS GO. 
			// Remove the handshake handlers.
			removeHandshakeHandlers(playerSession);
			ChannelPipeline pipeline = channel.getPipeline();
			removeIdleStateHandlers(pipeline);
			//Now connect the channel to the game room.
			connectToGame(playerSession,incomingAck);
		}
		else
		{
			// Client sent us an invalid ack, send reply and disconnect.
			sendInvalidHandshakeMessage(playerSession, "INVALID_ACK");
			playerSession.close();
		}
	}

	public void manageIncomingSessionFromClient(
			Object nativeConnection, String gameContextKey)
	{
		Channel channel = (Channel) nativeConnection;
		playerSession = handshakeSerivce
				.validateCredentialsAndCreateSession(gameContextKey);
		
		if(null != playerSession){
			isValidated = true;
		}
		
		if (isValidated)
		{
			// It got validated, add idle state handlers;
			ChannelPipeline pipeline = channel.getPipeline();
			addIdleStateHandlers(pipeline);
			// The ack string also contains the default protocol i.e. AMF3_STRING
			// added to it.
			expectedAck = handshakeSerivce.generateAck(playerSession);
			channel.write(expectedAck);
		}
		else
		{
			// Client sent us an invalid key, send reply and disconnect.
			sendInvalidHandshakeMessage(playerSession, "INVALID_SERVER_KEY");
		}
	}

	/**
	 * Once the first message is received from client, server sends back an ack
	 * message. If client does not reply back to the ack message, we need to
	 * disconnect. The idle handlers are added for that purpose.
	 * 
	 * @param pipeline
	 */
	public void addIdleStateHandlers(ChannelPipeline pipeline)
	{
		// Add some idle state handlers to the pipeline in order to
		// detect if the ack that is sent has any reply. Else disconnect.
		// If it is idle for 1 minute disconnect. Since possibility of
		// receiving ack is minimal now.
		// TODO make this idle time configurable.
		pipeline.addLast(IDLE_STATE_HANDLER, new IdleStateHandler(timer, 0, 0,
				MAX_IDLE_SECONDS));
		pipeline.addLast(IDLE_STATE_CHECK, idleCheckHandler);
	}

	/**
	 * Removes from the pipeline the idle check handlers. This is done after the
	 * ack has been validated and the incoming session is going to be
	 * connected to a game.
	 * 
	 * @param pipeline
	 */
	public void removeIdleStateHandlers(ChannelPipeline pipeline)
	{
		pipeline.remove(IDLE_STATE_HANDLER);
		pipeline.remove(IDLE_STATE_CHECK);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.menacheri.handlers.netty.Temp#removeHandshakeHandlers(com.menacheri
	 * .game.IUserConnection)
	 */
	public void removeHandshakeHandlers(IPlayerSession playerSession)
	{
		ChannelPipeline pipeline = channel.getPipeline();
		HandshakePipelineFactory.removeHandlers(pipeline);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.menacher.handlers.netty.Temp#connectToGame(org.menacheri.app.
	 * IPlayerSession)
	 */
	public void connectToGame(IPlayerSession playerSession,
			String gameProtocolKey)
	{
		// Get the parent game room for the player session.
		IGameRoom gameRoom = playerSession.getGameRoom();
		gameRoom.connectSession(playerSession, gameProtocolKey, channel);
	}

	public void sendInvalidHandshakeMessage(IPlayerSession session,
			String message)
	{
		ChannelFuture writeFuture = channel.write(message);
		writeFuture.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public IHandshakeService getHandshakeSerivce()
	{
		return handshakeSerivce;
	}

	@Override
	@Required
	public void setHandshakeSerivce(IHandshakeService handshakeSerivce)
	{
		this.handshakeSerivce = handshakeSerivce;
	}

	public Timer getTimer()
	{
		return timer;
	}

	@Required
	public void setTimer(Timer timer)
	{
		this.timer = timer;
	}

	public IdleStateAwareChannelHandler getIdleCheckHandler()
	{
		return idleCheckHandler;
	}

	@Required
	public void setIdleCheckHandler(
			IdleStateAwareChannelHandler idleCheckHandler)
	{
		this.idleCheckHandler = idleCheckHandler;
	}

}

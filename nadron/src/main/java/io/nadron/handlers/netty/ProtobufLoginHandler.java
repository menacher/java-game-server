package io.nadron.handlers.netty;

import com.google.protobuf.Any;
import io.nadron.app.GameRoom;
import io.nadron.app.Player;
import io.nadron.app.PlayerSession;
import io.nadron.app.Session;
import io.nadron.communication.NettyTCPMessageSender;
import io.nadron.event.Event;
import io.nadron.event.Events;
import io.nadron.event.impl.ReconnectEvent;
import io.nadron.networking.NadronMessages.ConnectionConfig;
import io.nadron.networking.NadronMessages.EventType;
import io.nadron.networking.NadronMessages.NadronEvent;
import io.nadron.service.LookupService;
import io.nadron.service.SessionRegistryService;
import io.nadron.service.UniqueIDGeneratorService;
import io.nadron.service.impl.ReconnectSessionRegistry;
import io.nadron.util.Credentials;
import io.nadron.util.NadronConfig;
import io.nadron.util.NettyUtils;
import io.nadron.util.SimpleCredentials;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by saurabhshukul on 13/02/17.
 */
@ChannelHandler.Sharable
public class ProtobufLoginHandler extends SimpleChannelInboundHandler<Event> {

    private static final Logger LOG = LoggerFactory
            .getLogger(ProtobufLoginHandler.class);

    protected LookupService lookupService;
    protected SessionRegistryService<SocketAddress> udpSessionRegistry;
    protected ReconnectSessionRegistry reconnectRegistry;
    protected UniqueIDGeneratorService idGeneratorService;

    private int currentUserId = 1;

    /**
     * Used for book keeping purpose. It will count all open channels. Currently
     * closed channels will not lead to a decrement.
     */
    private static final AtomicInteger CHANNEL_COUNTER = new AtomicInteger(0);

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             Event event) throws Exception
    {
        final ConnectionConfig connectionConfig = ((Any) event.getSource()).unpack(ConnectionConfig.class);
        final Channel channel =  ctx.channel();
        int type = event.getType();
        if (Events.LOG_IN == type)
        {
            LOG.debug("Login attempt from {}", channel.remoteAddress());
            Player player = lookupPlayer(connectionConfig, channel);
            handleLogin(player, ctx, connectionConfig);
        }
        else if (Events.RECONNECT == type)
        {
            LOG.debug("Reconnect attempt from {}", channel.remoteAddress());
            String reconnectKey = connectionConfig.getConnectionKey();
            PlayerSession playerSession = lookupSession(reconnectKey);
            handleReconnect(playerSession, ctx, connectionConfig);
        }
        else
        {
            LOG.error("Invalid event {} sent from remote address {}. "
                            + "Going to close channel {}",
                    new Object[] { event.getType(), channel.remoteAddress(),
                            channel});
            closeChannelWithLoginFailure(channel);
        }
    }

    public Player lookupPlayer(final ConnectionConfig connectionConfig, final Channel channel)
    {
        Credentials credentials = new SimpleCredentials(connectionConfig.getUser(),
                connectionConfig.getPass());
        Player player = lookupService.playerLookup(credentials);
        // FIXME: The player ID is hacked in for now, we should replace the
        // LookupService in SpringConfig with a real DB based user lookup service.
        player.setId(currentUserId++);
        if(null == player){
            LOG.error("Invalid credentials provided by user: {}",credentials);
        }
        return player;
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

    public void handleLogin(Player player, ChannelHandlerContext ctx, ConnectionConfig connectionConfig)
    {
        if (null != player)
        {
            ctx.channel().writeAndFlush(NadronEvent.newBuilder()
                    .setEventType(EventType.LOG_IN_SUCCESS)
                    .build());
            handleGameRoomJoin(player, ctx, connectionConfig);
        }
        else
        {
            // Write future and close channel
            closeChannelWithLoginFailure(ctx.channel());
        }
    }

    protected void handleReconnect(PlayerSession playerSession, ChannelHandlerContext ctx, ConnectionConfig connectionConfig)
    {
        if (null != playerSession)
        {
            ctx.write(NadronEvent.newBuilder()
                    .setEventType(EventType.LOG_IN_SUCCESS)
                    .build());
            GameRoom gameRoom = playerSession.getGameRoom();
            gameRoom.disconnectSession(playerSession);
            if (null != playerSession.getTcpSender())
                playerSession.getTcpSender().close();

            if (null != playerSession.getUdpSender())
                playerSession.getUdpSender().close();

            handleReJoin(playerSession, gameRoom, ctx.channel(), connectionConfig);
        }
        else
        {
            // Write future and close channel
            closeChannelWithLoginFailure(ctx.channel());
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
        ChannelFuture future = channel.writeAndFlush(NadronEvent.newBuilder()
                .setEventType(EventType.LOG_IN_FAILURE)
                .build());
        future.addListener(ChannelFutureListener.CLOSE);
    }

    public void handleGameRoomJoin(Player player, ChannelHandlerContext ctx, ConnectionConfig connectionConfig)
    {
        String refKey = connectionConfig.getConnectionKey();
        Channel channel = ctx.channel();
        GameRoom gameRoom = lookupService.gameRoomLookup(refKey);
        if(null != gameRoom)
        {
            PlayerSession playerSession = gameRoom.createPlayerSession(player);
            String reconnectKey = (String)idGeneratorService
                    .generateFor(playerSession.getClass());
            playerSession.setAttribute(NadronConfig.RECONNECT_KEY, reconnectKey);
            playerSession.setAttribute(NadronConfig.RECONNECT_REGISTRY, reconnectRegistry);
            LOG.trace("Sending GAME_ROOM_JOIN_SUCCESS to channel {}", channel);
            ChannelFuture future = channel.writeAndFlush(NadronEvent.newBuilder()
                    .setEventType(EventType.GAME_ROOM_JOIN_SUCCESS)
                    .setSource(Any.pack(ConnectionConfig.newBuilder().
                            setConnectionKey(reconnectKey).build()))
                    .build());
            connectToGameRoom(gameRoom, playerSession, future);
            loginUdp(playerSession, connectionConfig);
        }
        else
        {
            // Write failure and close channel.
            ChannelFuture future = channel.writeAndFlush(NettyUtils.createBufferForOpcode(Events.GAME_ROOM_JOIN_FAILURE));
            future.addListener(ChannelFutureListener.CLOSE);
            LOG.error("Invalid ref key provided by client: {}. Channel {} will be closed",refKey,channel);
        }
    }

    protected void handleReJoin(PlayerSession playerSession, GameRoom gameRoom, Channel channel,
                                ConnectionConfig connectionConfig)
    {
        LOG.trace("Going to clear pipeline");
        // Clear the existing pipeline
        NettyUtils.clearPipeline(channel.pipeline());
        // Set the tcp channel on the session.
        NettyTCPMessageSender sender = new NettyTCPMessageSender(channel);
        playerSession.setTcpSender(sender);
        // Connect the pipeline to the game room.
        gameRoom.connectSession(playerSession);
        playerSession.setWriteable(true);// TODO remove if unnecessary. It should be done in start event
        // Send the re-connect event so that it will in turn send the START event.
        playerSession.onEvent(new ReconnectEvent(sender));
        loginUdp(playerSession, connectionConfig);
    }

    public void connectToGameRoom(final GameRoom gameRoom, final PlayerSession playerSession, ChannelFuture future)
    {
        future.addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future)
                    throws Exception
            {
                Channel channel = future.channel();
                LOG.trace("Sending GAME_ROOM_JOIN_SUCCESS to channel {} completed", channel);
                if (future.isSuccess())
                {
                    LOG.trace("Going to clear pipeline");
                    // Clear the existing pipeline
                    NettyUtils.clearPipeline(channel.pipeline());

                    // Set the tcp channel on the session.
                    NettyTCPMessageSender tcpSender = new NettyTCPMessageSender(channel);
                    playerSession.setTcpSender(tcpSender);
                    // Connect the pipeline to the game room.
                    gameRoom.connectSession(playerSession);
                    // send the start event to remote client.
                    tcpSender.sendMessage(NadronEvent.newBuilder()
                            .setEventType(EventType.START)
                            .build());
                    gameRoom.onLogin(playerSession);
                }
                else
                {
                    LOG.error("GAME_ROOM_JOIN_SUCCESS message sending to client was failure, channel will be closed");
                    channel.close();
                }
            }
        });
    }

    /**
     * This method adds the player session to the
     * {@link SessionRegistryService}. The key being the remote udp address of
     * the client and the session being the value.
     *
     * @param playerSession
     * @param connectionConfig
     *            Used to check if the incoming connection is for UDP or TCP
     */
    protected void loginUdp(PlayerSession playerSession, ConnectionConfig connectionConfig)
    {
        if (connectionConfig.getNadronUdpHostName() != null) {
            InetSocketAddress remoteAddress = new InetSocketAddress(connectionConfig.getNadronUdpHostName(),
                    connectionConfig.getUdpPort());
            udpSessionRegistry.putSession(remoteAddress, playerSession);
        }
    }

    public LookupService getLookupService()
    {
        return lookupService;
    }

    public void setLookupService(LookupService lookupService)
    {
        this.lookupService = lookupService;
    }

    public UniqueIDGeneratorService getIdGeneratorService() {
        return idGeneratorService;
    }

    public void setIdGeneratorService(UniqueIDGeneratorService idGeneratorService) {
        this.idGeneratorService = idGeneratorService;
    }

    public SessionRegistryService<SocketAddress> getUdpSessionRegistry()
    {
        return udpSessionRegistry;
    }

    public void setUdpSessionRegistry(
            SessionRegistryService<SocketAddress> udpSessionRegistry)
    {
        this.udpSessionRegistry = udpSessionRegistry;
    }

    public ReconnectSessionRegistry getReconnectRegistry()
    {
        return reconnectRegistry;
    }

    public void setReconnectRegistry(ReconnectSessionRegistry reconnectRegistry)
    {
        this.reconnectRegistry = reconnectRegistry;
    }
}

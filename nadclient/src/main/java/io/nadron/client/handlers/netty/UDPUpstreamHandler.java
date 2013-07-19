package io.nadron.client.handlers.netty;

import io.nadron.client.NettyUDPClient;
import io.nadron.client.app.Session;
import io.nadron.client.event.Event;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;


/**
 * This upstream handler handles <b>ALL</b> UDP events. It will lookup the
 * appropriate session from {@link NettyUDPClient#CLIENTS} map and then transmit
 * the event to that {@link Session}. <b>Note</b> If this class cannot find the
 * appropriate session to transmit this event to, then the event is
 * <b>silently</b> discarded.
 * 
 * @author Abraham Menacherry.
 * 
 */
@Sharable
public class UDPUpstreamHandler extends SimpleChannelInboundHandler<DatagramPacket>
{
	private final MessageBufferEventDecoder decoder;
	
	public UDPUpstreamHandler()
	{
		super();
		decoder = new MessageBufferEventDecoder();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx,
			DatagramPacket packet) throws Exception
	{
		Session session = NettyUDPClient.CLIENTS.get(ctx.channel().localAddress());
		if (null != session)
		{
			Event event = (Event)decoder.decode(null, packet.content());
			// Pass the event on to the session
			session.onEvent(event);
		}
	}
	
}

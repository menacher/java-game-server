package io.nadron.handlers.netty;

import static io.nadron.event.Events.LOG_IN;
import static io.nadron.event.Events.PROTCOL_VERSION;
import static io.nadron.event.Events.RECONNECT;
import io.nadron.event.Events;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.List;


/**
 * Applies a protocol to the incoming pipeline which will handle login.
 * Subsequent protocol may also be manipulated by these login handlers.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface LoginProtocol
{
	String LOGIN_HANDLER_NAME = "loginHandler";
	/**
	 * Apply a protocol on the pipeline to handle login. Implementations will
	 * first "search" if the incoming bytes correspond to the implementations
	 * protocol, only if they match, the correspoinding protocol will be
	 * applied.
	 * 
	 * @param buffer
	 *            The incoming buffer, by default around 5 bytes will be read
	 *            and passed on to detect the protocol
	 * @param pipeline
	 *            The channelpipeline on which the login protocol handlers need
	 *            to be set.
	 * @return Returs true if the protocol was applied, else false.
	 */
	public boolean applyProtocol(ByteBuf buffer, ChannelPipeline pipeline);

	/**
	 * Searches the incoming bytes of a client connection to determine if its an
	 * HTTP connection, in which case Websocket or HTTP related handlers will be
	 * applied on the piepline.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public static class HTTPProtocol implements LoginProtocol
	{
		private WebSocketLoginHandler webSocketLoginHandler;
		@Override
		public boolean applyProtocol(ByteBuf buffer,
				ChannelPipeline pipeline)
		{
			boolean isThisProtocol = false;
			final int magic1 = buffer.getUnsignedByte(buffer.readerIndex());
			final int magic2 = buffer.getUnsignedByte(buffer.readerIndex() + 1);
			if (isHttp(magic1, magic2))
			{
				pipeline.addLast("decoder", new HttpRequestDecoder());
		        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
		        pipeline.addLast("encoder", new HttpResponseEncoder());
		        pipeline.addLast("handler", new WebSocketServerProtocolHandler("/nadsocket"));
		        pipeline.addLast(LOGIN_HANDLER_NAME, webSocketLoginHandler);
		        isThisProtocol = true;
			}
			return isThisProtocol;
		}

		/**
		 * Method which checks if the first 2 incoming parameters are G, E or
		 * similar combiantions which signal that its an HTTP protocol, since
		 * some protocols like nadron's default protocol send the length
		 * first (which is 2 arbitrary bytes), its better if this protocol is
		 * searched last to avoid switching to HTTP protocol prematurely.
		 * 
		 * @param magic1
		 * @param magic2
		 * @return true if the two incoming bytes match any of the first two
		 *         letter of HTTP headers like GET, POST etc.
		 */
		protected boolean isHttp(int magic1, int magic2)
		{
			return magic1 == 'G' && magic2 == 'E' || // GET
					magic1 == 'P' && magic2 == 'O' || // POST
					magic1 == 'P' && magic2 == 'U' || // PUT
					magic1 == 'H' && magic2 == 'E' || // HEAD
					magic1 == 'O' && magic2 == 'P' || // OPTIONS
					magic1 == 'P' && magic2 == 'A' || // PATCH
					magic1 == 'D' && magic2 == 'E' || // DELETE
					magic1 == 'T' && magic2 == 'R' || // TRACE
					magic1 == 'C' && magic2 == 'O'; // CONNECT
		}

		public WebSocketLoginHandler getWebSocketLoginHandler()
		{
			return webSocketLoginHandler;
		}

		public void setWebSocketLoginHandler(WebSocketLoginHandler webSocketLoginHandler)
		{
			this.webSocketLoginHandler = webSocketLoginHandler;
		}
	}

	/**
	 * This is the default protocol of nadron. If incoming event is of type
	 * LOG_IN and also has appropriate protocol version as defined in the
	 * {@link Events} class, then this protocol will be applied. The 3rd and 4th
	 * bytes of the incoming transmission are searched to get this information.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public static class DefaultNadProtocol implements LoginProtocol
	{

		private int frameSize = 1024;
		private EventDecoder eventDecoder;
		private LoginHandler loginHandler;
		private LengthFieldPrepender lengthFieldPrepender;

		@Override
		public boolean applyProtocol(ByteBuf buffer,
				ChannelPipeline pipeline)
		{
			boolean isThisProtocol = false;
			final int opcode = buffer.getUnsignedByte(buffer.readerIndex() + 2);
			final int protocolVersion = buffer.getUnsignedByte(buffer
					.readerIndex() + 3);
			if (isNadProtocol(opcode, protocolVersion))
			{
				pipeline.addLast("framer", createLengthBasedFrameDecoder());
				pipeline.addLast("eventDecoder", eventDecoder);
				pipeline.addLast(LOGIN_HANDLER_NAME, loginHandler);
				pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
				isThisProtocol = true;
			}
			return isThisProtocol;
		}

		protected boolean isNadProtocol(int magic1, int magic2)
		{
			return ((magic1 == LOG_IN || magic1 == RECONNECT) && magic2 == PROTCOL_VERSION);
		}

		public ChannelHandler createLengthBasedFrameDecoder()
		{
			return new LengthFieldBasedFrameDecoder(frameSize, 0, 2, 0, 2);
		}

		public int getFrameSize()
		{
			return frameSize;
		}

		public void setFrameSize(int frameSize)
		{
			this.frameSize = frameSize;
		}

		public EventDecoder getEventDecoder()
		{
			return eventDecoder;
		}

		public void setEventDecoder(EventDecoder eventDecoder)
		{
			this.eventDecoder = eventDecoder;
		}

		public LoginHandler getLoginHandler()
		{
			return loginHandler;
		}

		public void setLoginHandler(LoginHandler loginHandler)
		{
			this.loginHandler = loginHandler;
		}

		public LengthFieldPrepender getLengthFieldPrepender()
		{
			return lengthFieldPrepender;
		}

		public void setLengthFieldPrepender(
				LengthFieldPrepender lengthFieldPrepender)
		{
			this.lengthFieldPrepender = lengthFieldPrepender;
		}
	}

	public static class CompositeProtocol implements LoginProtocol
	{
		private List<LoginProtocol> protocols;

		@Override
		public boolean applyProtocol(ByteBuf buffer,
				ChannelPipeline pipeline)
		{
			if (null != protocols)
			{
				for (LoginProtocol protocol : protocols)
				{
					if (protocol.applyProtocol(buffer, pipeline))
					{
						return true;
					}
				}
			}
			return false;
		}

		public List<LoginProtocol> getProtocols()
		{
			return protocols;
		}

		public void setProtocols(List<LoginProtocol> protocols)
		{
			this.protocols = protocols;
		}
	}
}

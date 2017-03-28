package io.nadron.server.netty;

import io.nadron.handlers.netty.LoginProtocol;
import io.nadron.handlers.netty.ProtocolMultiplexerDecoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;


public class ProtocolMultiplexerChannelInitializer extends
	ChannelInitializer<SocketChannel>
{
	// TODO make this configurable from spring.
	private static final int MAX_IDLE_SECONDS = 60;
	private int bytesForProtocolCheck;
	private LoginProtocol loginProtocol;

	private String sslHost;
	private int sslPort = 0;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception 
	{
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast("idleStateCheck", new IdleStateHandler(
				MAX_IDLE_SECONDS, MAX_IDLE_SECONDS, MAX_IDLE_SECONDS));

		if (sslHost != null && sslPort != 0) {
			final SslContext sslCtx;
			// TODO: replace with proper secure certificate
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
					.clientAuth(ClientAuth.OPTIONAL).build();
			pipeline.addLast(sslCtx.newHandler(ch.alloc(), sslHost, sslPort));
		}

		pipeline.addLast("multiplexer", createProtcolMultiplexerDecoder());
	}

	protected ChannelHandler createProtcolMultiplexerDecoder()
	{
		return new ProtocolMultiplexerDecoder(bytesForProtocolCheck, loginProtocol);
	}

	public int getBytesForProtocolCheck()
	{
		return bytesForProtocolCheck;
	}

	public void setBytesForProtocolCheck(int bytesForProtocolCheck)
	{
		this.bytesForProtocolCheck = bytesForProtocolCheck;
	}

	public LoginProtocol getLoginProtocol()
	{
		return loginProtocol;
	}

	public void setLoginProtocol(LoginProtocol loginProtocol)
	{
		this.loginProtocol = loginProtocol;
	}

	public void setSslHost(String sslHost) {
		this.sslHost = sslHost;
	}

	public void setSslPort(int sslPort) {
		this.sslPort = sslPort;
	}
}

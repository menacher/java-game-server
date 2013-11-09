package io.nadron.handlers.netty;

import io.nadron.util.SmallFileReader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.CharsetUtil;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="http://www.waywardmonkeys.com/">Bruce Mitchener</a>
 */
public class FlashPolicyServerHandler extends SimpleChannelInboundHandler<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(FlashPolicyServerHandler.class);
	
	private static ByteBuf policyFile;
	private final String portNumber;
	private static final String NEWLINE = "\r\n";
	
	public String getPortNumber()
	{
		return portNumber;
	}

	static
	{
		policyFile = null;
		String filePath = System.getProperty("flash_policy_file_path");
		if (null != filePath)
		{
			try
			{
				String fileContents = SmallFileReader.readSmallFile(filePath);
				policyFile = Unpooled.copiedBuffer(fileContents
						.getBytes());
			}
			catch (IOException e)
			{
				LOG.error("Unable to open flash policy file", e);
			}
		}
	}

	public FlashPolicyServerHandler(String portNum)
	{
		this.portNumber = portNum;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx,
			Object msg) throws Exception
	{
		ChannelFuture f = null;

		if (null != policyFile)
		{
			f = ctx.channel().writeAndFlush(policyFile);
		}
		else
		{
			f = ctx.channel().writeAndFlush(this.getPolicyFileContents());
		}
		f.addListener(ChannelFutureListener.CLOSE);
	}
	
    public ByteBuf getPolicyFileContents() throws Exception {
    	
        return Unpooled.copiedBuffer(
            "<?xml version=\"1.0\"?>" + NEWLINE +
            "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">" + NEWLINE +
            "" + NEWLINE +
            "<!-- Policy file for xmlsocket://socks.example.com -->" + NEWLINE +
            "<cross-domain-policy> " + NEWLINE +
            "" + NEWLINE +
            "   <!-- This is a master socket policy file -->" + NEWLINE +
            "   <!-- No other socket policies on the host will be permitted -->" + NEWLINE +
            "   <site-control permitted-cross-domain-policies=\"master-only\"/>" + NEWLINE +
            "" + NEWLINE +
            "   <!-- Instead of setting to-ports=\"*\", administrator's can use ranges and commas -->" + NEWLINE +
            "   <allow-access-from domain=\"*\" to-ports=\"" + portNumber + "\" />" + NEWLINE +
            "" + NEWLINE +
            "</cross-domain-policy>" + NEWLINE,
            CharsetUtil.US_ASCII);
    }

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception
	{
		if (cause instanceof ReadTimeoutException)
		{
			LOG.error("Connection timed out. Going to close channel");
		}
		else
		{
			LOG.error("Exception in FlashPolicyFileHanlder", cause);
		}
		ctx.channel().close();
    }

}

package org.menacheri.jetserver.server.netty;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.menacheri.jetserver.server.Server;

/**
 * An interface specific to the JBoss Netty implementation. It will be
 * implemented by a class that will start up a Netty server at a specified port.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface NettyServer extends Server
{
	/**
	 * Creates a {@link ServerBootstrap} object which is used to start a server.
	 * 
	 * @return Returns the created {@link ServerBootstrap}.
	 */
	public Bootstrap createServerBootstrap();

	/**
	 * If thread pools or TCP/IP parameters or the pipeline factory need to be
	 * modified then it is this method that needs to be overriden.
	 * 
	 * @param optionsList
	 *            Used to set tcp ip options like noDelay etc.
	 */
	public void configureServerBootStrap(String[] optionsList);

	/**
	 * createServerBootstrap will create a pipeline factory and save it as a
	 * class variable. This method can then be used to retrieve that value.
	 * 
	 * @return Returns the channel pipeline factory that is associated with this
	 *         netty server.
	 */
	public ChannelPipelineFactory getPipelineFactory();

	/**
	 * Method can be used to set the pipeline factory that is to be used by the
	 * netty server.
	 * 
	 * @param factory
	 *            The factory which will create a pipeline on each incoming
	 *            connection.
	 */
	public void setPipelineFactory(ChannelPipelineFactory factory);

	/**
	 * @return Returns the created server bootstrap object.
	 */
	public Bootstrap getServerBootstrap();

	/**
	 * Sets the server bootstrap, could be TCP, UDP bootstrap.
	 * 
	 * @param serverBootstrap
	 */
	public void setServerBootstrap(Bootstrap serverBootstrap);
}

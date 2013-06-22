package io.nadron.context;

import io.nadron.app.GameStartListener;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Simple wrapper class for the spring application context. It defines constants
 * configured in the spring xml configuration. This class can be used to
 * retrieve application beans using a String name.
 * 
 * @author Abraham Menacherry
 * 
 */
public class AppContext implements ApplicationContextAware
{
	// App context
	public static final String APP_CONTEXT = "appContext";
	
	public static final String APP_SESSION = "appSession";
	
	// Servers
	public static final String SERVER_MANAGER = "serverManager";
	public static final String TCP_SERVER = "tcpServer";
	public static final String UDP_SERVER = "udpServer";
	public static final String FLASH_POLICY_SERVER = "flashPolicyServer";
	
	// Services with default implementations
	public static final String HANDSHAKE_SERVICE = "handshakeSerivce";
	public static final String CONNECTION_AND_CONFIGURATION_SERVICE = "connectionAndConfigurationService";
	public static final String LOOKUP_SERVICE = "lookupService";
	public static final String GAME_ADMIN_SERVICE = "gameAdminService";
	public static final String TASK_MANAGER_SERVICE = "taskManagerService";
	public static final String SESSION_REGISTRY_SERVICE = "sessionRegistryService";
	
	// Server default protocols
	public static final String SIMPLE_BYTE_ARRAY_PROTOCOL = "simpleByteArrayProtocol";
	public static final String AMF3_EXT_INT_SYNC_PROTOCOL = "amf3ExternalIntSyncProtocol";
	public static final String AMF3_PROTOCOL = "amf3Protocol";
	public static final String AMF3_STRING_PROTOCOL = "amf3StringProtocol";
	public static final String STRING_PROTOCOL = "stringProtocol";
	public static final String PROTOCOL_FACTORY = "protocolFactory"; // protocol factory.
	
	// Netty default handlers
	public static final String HANDSHAKE_HANDLER = "handshakeHandler";
	public static final String HASHED_WHEEL_TIMER = "hashedWheelTimer";
	public static final String STRING_DECODER = "stringDecoder";
	public static final String STRING_ENCODER = "stringEncoder";
	public static final String BASE_64_ENCODER = "base64Encoder";
	public static final String BASE_64_DECODER = "base64Decoder";
	public static final String NUL_ENCODER = "nulEncoder";
	public static final String BYTE_ARRAY_TO_CHANNEL_BUFFER_ENCODER="byteArrayToChannelBufferEncoder";
	public static final String LENGTH_FIELD_PREPENDER = "lengthFieldPrepender";
	public static final String LENGTH_FIELD_BASED_FRAME_DECODER = "lengthFieldBasedFrameDecoder";
	public static final String IDLE_CHECK_HANDLER = "idleCheckHandler";
	public static final String BYTE_ARRAY_STREAM_DECODER = "byteArrayStreamDecoder";
	public static final String BYTE_ARRAY_DECODER = "byteArrayDecoder";
	public static final String FLASH_POLICY_SERVER_DECODER = "flashPolicyServerDecoder";
	public static final String FLASH_POLICY_SERVER_HANDLER = "flashPolicyServerHandler";
	public static final String AMF3_TO_JAVA_DECODER = "AMF3ToJavaObjectDecoder";
	public static final String JAVA_TO_AMF3_ENCODER = "javaToAMF3Encoder";
	public static final String PREVENT_SERIALIZATION_HANDLER = "preventSerializationHandler";
	public static final String SYNC_ID_HEADER_ENCODER = "syncIdHeaderEncoder";
	public static final String UDP_UPSTREAM_HANDLER = "udpUpstreamHandler";
	
	// Other Netty specific beans
	public static final String DEFAULT_PIPELINE_FACTORY = "defaultPipeLineFactory";
	public static final String HANDSHAKE_PIPELINE_FACTORY = "handshakePipelineFactory";
	public static final String NETTY_CHANNEL_GROUP = "defaultChannelGroup";
	
	
	// The spring application context.
	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException
	{
		AppContext.applicationContext = applicationContext;
	}
	
	/**
	 * This method is used to retrieve a bean by its name. Note that this may
	 * result in new bean creation if the scope is set to "prototype" in the
	 * bean configuration file.
	 * 
	 * @param beanName
	 *            The name of the bean as configured in the spring configuration
	 *            file.
	 * @return The bean if it is existing or null.
	 */
	public static Object getBean(String beanName)
	{
		if (null == beanName)
		{
			return null;
		}
		return applicationContext.getBean(beanName);
	}
	
	/**
	 * Called from the main method once the application is initialized. This
	 * method is advised by aspectj which will in turn call the start method on
	 * the {@link GameStartListener} instance.
	 */
	public void initialized()
	{

	}
	
}

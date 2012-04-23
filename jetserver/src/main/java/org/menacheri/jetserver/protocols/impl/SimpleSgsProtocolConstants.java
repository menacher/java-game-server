package org.menacheri.jetserver.protocols.impl;


/**
 * SGS Protocol constants.
 * <p>
 * A protocol message is constructed as follows:
 * <ul>
 * <li> (unsigned short) payload length, not including this field
 * <li> (byte) operation code
 * <li> optional content, depending on the operation code.
 * </ul>
 * <p>
 * A {@code ByteArray} is encoded in a context dependent fashion. If the
 * ByteArray is the only content, or if the ByteArray is the last piece of
 * content (that is, if the length of the ByteArray can be determined by the
 * payload length and the length of what has come before), the ByteArray is
 * encoded as
 * <ul>
 * <li> (byte[]) the bytes in the array
 * </ul>
 * If there is other content that follows the ByteArray, then the ByteArray is
 * encoded as
 * <ul>
 * <li> (unsigned short) number of bytes in the array
 * <li> (byte[]) content
 * </ul>
 * <p>
 * A {@code String} is encoded as follows:
 * <ul>
 * <li> (unsigned short) number of bytes of modified UTF-8 encoded String
 * <li> (byte[]) String encoded in modified UTF-8 as described in
 * {@link java.io.DataInput}
 * </ul>
 * Note that these encodings only apply to those data items that are specified
 * explicitly in the protocol. Application data, passed as a ByteArray, may
 * contain any information, but will need to be parsed (and, if necessary,
 * converted to and from a network representation) by the application or client.
 * <p>
 * The total length of a message must not be greater than 65535 bytes; given the
 * header information this means that the payload of a message cannot be greater
 * than 65532 bytes. If a message larger than this must be sent, it is the
 * responsibility of the sender to break the message into pieces and of the
 * receiver to re-assemble those pieces. 
 * <p>
 * Behavior not specified in this document is left as an implementation 
 * decision for the particular client and server. Information on the
 * implementation characteristics of the RedDwarf server can be found
 * in the <a href="../../../../../overview-summary.html"> overview</a> for the 
 * implementation.
 */
public class SimpleSgsProtocolConstants{
    
    /**
     * This class should not be instantiated.
     */
    protected SimpleSgsProtocolConstants() {
    }

    /**
     * The maximum length of a protocol message:
     * {@value #MAX_MESSAGE_LENGTH} bytes.
     */
    public static final int MAX_MESSAGE_LENGTH = 65535;

    /**
     * The maximum payload length:
     * {@value #MAX_PAYLOAD_LENGTH} bytes.
     */
    public static final int MAX_PAYLOAD_LENGTH = 65532;

    /** The version number, currently {@code 0x05}. */
    public static final byte VERSION = 0x05;

    /**
     * Login request from a client to a server. This message should only be sent
     * to a server; if received by a client it should be ignored. <br>
     * Opcode: {@code 0x10} <br>
     * Payload:
     * <ul>
     * <li>(byte) protocol version
     * <li>(String) name
     * <li>(String) password
     * </ul>
     * The {@code protocol version} will be checked by the server to insure that
     * the client and server are using the same protocol or versions of the
     * protocol that are compatible. If the server determines that the protocol
     * version used by the sender and the protocol version or versions required
     * by the server are not compatible, the server will disconnect the client.
     * In cases where the protocols being used are not compatible, no other
     * communication between the client and the server is guaranteed to be
     * understood.
     * <p>
     * The {@code name} and {@code password} strings are passed to the server's
     * authentication mechanism. After the server processes the login request,
     * the server sends one of the following acknowledgments to the client:
     * <ul>
     * <li> {@link #LOGIN_SUCCESS}, if user authentication succeeds and
     * invoking the {@code loggedIn}' method on the application's
     * {@code AppListener} with the user's {@code ClientSession} returns a
     * non-null, serializable {@code ClientSessionListener};
     * <li>{@link #LOGIN_REDIRECT}, if user authentication succeeds, but the
     * server requests that the client redirect the login request to another
     * node; or
     * <li>{@link #LOGIN_FAILURE}, if user authentication fails, or if the
     * user is already logged in and the server is configured to reject new
     * logins for the same user, or if invoking the {@code loggedIn} method on
     * the application's {@code AppListener} with the user's
     * {@code ClientSession} returns a null, or non-serializable
     * {@code ClientSessionListener} or the method does not complete
     * successfully.
     * </ul>
     * <p>
     * If a client is currently logged in, the result of receiving a
     * LOGIN_REQUEST is not defined by the protocol, but is an
     * implementation-dependent detail of the server. 
     */
    public static final byte LOGIN_REQUEST = 0x10;

    /**
     * Login success. Server response to a client's {@link #LOGIN_REQUEST}.
     * <br>
     * Opcode: {@code 0x11} <br>
     * Payload:
     * <ul>
     * <li> (ByteArray) reconnectionKey
     * </ul>
     * The {@code reconnectionKey} is an opaque reference that can be held by
     * the client for use in case the client is disconnected and wishes to
     * reconnect to the server with the same identity using a
     * {@link #RECONNECT_REQUEST}.
     */
    public static final byte LOGIN_SUCCESS = 0x11;

    /**
     * Login failure. Server response to a client's {@link #LOGIN_REQUEST}.
     * <br>
     * Opcode: {@code 0x12} <br>
     * Payload:
     * <ul>
     * <li> (String) reason
     * </ul>
     * This message indicates that the server rejects the {@link #LOGIN_REQUEST}
     * for some reason, for example
     * <ul>
     * <li> user authentication failure,
     * <li> failure during application processing of the client session, or
     * <li> a user with the same identity is already logged in, and the server
     * is configured to reject new logins for clients who are currently logged
     * in
     * </ul>
     * 
     */
    public static final byte LOGIN_FAILURE = 0x12;

    /**
     * Login redirect. Server response to a client's {@link #LOGIN_REQUEST}.
     * <br>
     * Opcode: {@code 0x13} <br>
     * Payload:
     * <ul>
     * <li> (String) hostname
     * <li> (int) port
     * </ul>
     * This message indicates a redirection from the node to which the
     * {@link #LOGIN_REQUEST} was sent to another node. The client receiving
     * this request should shut down the connection to the original node and
     * establish a connection to the node indicated by the {@code hostname} and
     * {@code port} in the payload. The client should then attempt to log in to
     * the node to which it has been redirected by sending a
     * {@link #LOGIN_REQUEST} to that node.
     */
    public static final byte LOGIN_REDIRECT = 0x13;

    /**
     * Suspend messages notification. Server to client notification.
     * <br>
     * Opcode: {@code 0x14} <br>
     * Payload: (none) <p>
     *
     * This message notifies a client to suspend sending messages to the
     * server until it receives further instruction (such as {@link
     * #RELOCATE_NOTIFICATION} or {@link #RESUME_MESSAGES}). The client
     * should send the acknowledgment {@link #SUSPEND_MESSAGES_COMPLETE} to
     * the server when it has suspended sending messages.  After the server
     * sends a {@code SUSPEND_MESSAGES} notification to the client, the
     * server may decide to drop messages from the client if it does not
     * receive the {@link #SUSPEND_MESSAGES_COMPLETE} acknowledgment in a
     * timely fashion. <p>
     *
     * This opcode was introduced in protocol version {@code 0x05}.
     */
    public static final byte SUSPEND_MESSAGES = 0x14;

    /**
     * Acknowledgment of {@link #SUSPEND_MESSAGES} notification. Client to
     * server notification. 
     * <br>
     * Opcode: {@code 0x15} <br>
     * Payload: (none) <p>
     *
     * This message notifies the server that the client has received the
     * {@link #SUSPEND_MESSAGES} notification.  Any messages received by the
     * server after this notification will be dropped, unless the server
     * has instructed the client to either resume messages or relocate its
     * client session to another node. <p>
     *
     * This opcode was introduced in protocol version {@code 0x05}.
     */
    public static final byte SUSPEND_MESSAGES_COMPLETE = 0x15;

    /**
     * Resume messages notification. Server to client notification. 
     * <br>
     * Opcode: {@code 0x16} <br>
     * Payload: (none) <p>
     *
     * This message notifies the client that it can resume sending messages
     * to the server. <p>
     *
     * This opcode was introduced in protocol version {@code 0x05}.
     */
    public static final byte RESUME_MESSAGES = 0x16;

    /**
     * Relocate session notification. Server to client notification.
     * <br>
     * Opcode: {@code 0x17} <br>
     * Payload:
     * <ul>
     * <li> (String) hostname
     * <li> (int) port
     * <li> (ByteArray) relocationKey
     * </ul>
     *
     * This message notifies a client to relocate its session on the
     * current node to a new node. The client receiving this request should
     * shut down the connection to the original node and establish a
     * connection to the node indicated by the {@code hostname} and {@code
     * port} in the payload. The client should then attempt to reestablish
     * the client session with the server (without logging in) using the
     * {@code relocationKey} specified in the payload. <p>
     *
     * This opcode was introduced in protocol version {@code 0x05}.
     */
    public static final byte RELOCATE_NOTIFICATION = 0x17;

    /**
     * Relocation request. Client requesting relocation to a server. <br>
     * Opcode: {@code 0x18} <br>
     * Payload:
     * <ul>
     * <li> (byte) protocol version
     * <li> (ByteArray) relocationKey
     * </ul>
     *
     * This message requests that the client's existing client session be
     * relocated to (and re-established with) the server. The {@code
     * relocationKey} must match the one that the client received in the
     * previous {@link #RELOCATE_NOTIFICATION} message.  If relocation is
     * successful, the server acknowledges the request with a {@link
     * #RELOCATE_SUCCESS} message containing a {@code reconnectionKey} for
     * reconnecting to the server. If relocation is not successful, a
     * {@link #RELOCATE_FAILURE} message is sent to the client.  If the
     * client receives a {@code RELOCATE_FAILURE} message, the client
     * should disconnect from the server. <p>
     *
     * This opcode was introduced in protocol version {@code 0x05}.
     */
    public static final byte RELOCATE_REQUEST = 0x18;

    /**
     * Relocation success. Server response to a client's {@link
     * #RELOCATE_REQUEST}.
     * <br>
     * Opcode: {@code 0x19} <br>
     * Payload:
     * <ul>
     * <li> (ByteArray) reconnectionKey
     * </ul>
     * The {@code reconnectionKey} is an opaque reference that can be held by
     * the client for use in case the client is disconnected and wishes to
     * reconnect to the server with the same identity using a
     * {@link #RECONNECT_REQUEST}. <p>
     *
     * This opcode was introduced in protocol version {@code 0x05}.
     */
    public static final byte RELOCATE_SUCCESS = 0x19;

    /**
     * Relocate failure. Server response to a client's {@link
     * #RELOCATE_REQUEST}.
     * <br>
     * Opcode: {@code 0x1a} <br>
     * Payload:
     * <ul>
     * <li> (String) reason
     * </ul>
     * This message indicates that the server rejects the {@link
     * #RELOCATE_REQUEST} for some reason, for example
     * <ul>
     * <li> session not relocating to the server
     * <li> relocation key mismatch
     * <li> a user with the same identity is already logged in
     * </ul> <p>
     *
     * This opcode was introduced in protocol version {@code 0x05}.
     */
    public static final byte RELOCATE_FAILURE = 0x1a;

    /**
     * Reconnection request. Client requesting reconnect to a server. <br>
     * Opcode: {@code 0x20} <br>
     * Payload:
     * <ul>
     * <li> (byte) protocol version
     * <li> (ByteArray) reconnectionKey
     * </ul>
     * This message requests that the client be reconnected to an existing
     * client session with the server. The {@code reconnectionKey} must match
     * the one that the client received in the previous {@link #LOGIN_SUCCESS}
     * or {@link #RECONNECT_SUCCESS} message (if reconnection was performed
     * subsequent to login). If reconnection is successful, the server
     * acknowledges the request with a {@link #RECONNECT_SUCCESS} message
     * containing a new {@code reconnectionKey}. If reconnection is not
     * successful, a {@link #RECONNECT_FAILURE} message is sent to the client.
     * If the client receives a {@code RECONNECT_FAILURE} message, the client
     * should disconnect from the server.
     */
    public static final byte RECONNECT_REQUEST = 0x20;

    /**
     * Reconnect success. Server response to a client's
     * {@link #RECONNECT_REQUEST}. <br>
     * Opcode: {@code 0x21} <br>
     * Payload:
     * <ul>
     * <li> (ByteArray) reconnectionKey
     * </ul>
     * Indicates that a {@link #RECONNECT_REQUEST} has been successful. The
     * message will include a {@code reconnectionKey} that can be used in a
     * subsequent reconnect requests from the client. Reciept of this message
     * indicates that the client session has been re-established. 
     */
    public static final byte RECONNECT_SUCCESS = 0x21;

    /**
     * Reconnect failure. Server response to a client's
     * {@link #RECONNECT_REQUEST}. 
     * <br>
     * Opcode: {@code 0x22} 
     * <br>
     * Payload:
     * <ul>
     * <li> (String) reason
     * </ul>
     * This response indicates that a reconnect request could not be honored by
     * the server. This could be because of an invalid reconnect key, or
     * because too much time has elapsed between the session disconnection and
     * the reconnect request (which, in turn, may cause the server to
     * discard the session state). The string returned details the reason for
     * the denial of reconnection. 
     */
    public static final byte RECONNECT_FAILURE = 0x22;

    /**
     * Session message. May be sent by the client or the server. Maximum length
     * is {@value #MAX_PAYLOAD_LENGTH} bytes. Larger messages require
     * fragmentation and reassembly above this protocol layer. 
     * <br>
     * Opcode: {@code 0x30} 
     * <br>
     * Payload:
     * <ul>
     * <li> (ByteArray) message
     * </ul>
     * This message allows information to be sent between the client and the
     * server. The content of the message is application dependent, and the
     * mechanisms for constructing and parsing these messages is an
     * application-level task. 
     */
    public static final byte SESSION_MESSAGE = 0x30;

    /**
     * Logout request from a client to a server. 
     * <br>
     * Opcode: {@code 0x40} 
     * <br>
     * No payload. 
     * <br>
     * This message will cause the client to be logged out of the server. The
     * server will remove all of the client's channel memberships. Any message
     * (other than {@link #LOGIN_REQUEST}) sent by the client after sending this
     * message will be ignored, and any message will need to be sent on a new
     * connection to the server.
     */
    public static final byte LOGOUT_REQUEST = 0x40;

    /**
     * Logout success.  Server response to a client's {@link #LOGOUT_REQUEST}.
     * <br>
     * Opcode: {@code 0x41}
     * <br>
     * No payload.
     * <br>
     * This message is sent from the server to the client to indicate that a
     * {@link #LOGOUT_REQUEST} has been received and that the client has been
     * logged out of the current session. On receipt of this message, the client
     * should shut down any networking resources that are used to communicate
     * with the server.
     */
    public static final byte LOGOUT_SUCCESS = 0x41;

    /**
     * Channel join.  Server notifying a client that it has joined a channel.
     * <br>
     * Opcode: {@code 0x50}
     * <br>
     * Payload:
     * <ul>
     * <li> (String) channel name
     * <li> (ByteArray) channel ID
     * </ul>
     * This message is sent from the server to the client to indicate that the 
     * client has been added to the channel identified by the {@code channel 
     * name} and {@code channel ID} contained in the message. 
     */
    public static final byte CHANNEL_JOIN = 0x50;

    /**
     * Channel leave. Server notifying a client that the client has left a
     * channel. 
     * <br>
     * Opcode: {@code 0x51} 
     * <br>
     * Payload:
     * <ul>
     * <li> (ByteArray) channel ID
     * </ul>
     * This message is sent from the server indicating to the client that the
     * client has been removed from the channel with the indicated {@code 
     * channel ID}. The client can no longer send messages on the channel.
     */
    public static final byte CHANNEL_LEAVE = 0x51;

    /**
     * Channel message. May be sent by the client or the server. Maximum length
     * is {@value #MAX_PAYLOAD_LENGTH} bytes minus the sum of the {@code 
     * channel ID} size and two bytes (the size of the unsigned short indicating
     * the {@code channel Id} size). Larger messages require fragmentation and 
     * reassembly above this protocol layer. 
     * <br>
     * Opcode: {@code 0x52} 
     * <br>
     * Payload:
     * <ul>
     * <li> (unsigned short) channel ID size
     * <li> (ByteArray) channel ID
     * <li> (ByteArray) message
     * </ul>
     * This message requests that the specified message be sent to all members
     * of the specified channel. If the client sending the request is not a
     * member of the channel, the message will be rejected by the server. The
     * server may also refuse to send the message, or alter the message, because
     * of application-specific logic.
     */
    public static final byte CHANNEL_MESSAGE = 0x52;
}

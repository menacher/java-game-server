package org.menacheri.jetclient.protocol.impl 
{
	import flash.net.Socket;
	import org.menacheri.jetclient.app.Session;
	import org.menacheri.jetclient.codecs.CodecChain;
	import org.menacheri.jetclient.codecs.impl.DefaultCodecChain;
	import org.menacheri.jetclient.codecs.impl.LengthFieldBasedFrameDecoder;
	import org.menacheri.jetclient.codecs.impl.LengthFieldPrepender;
	import org.menacheri.jetclient.codecs.impl.MessagBufferEventDecoder;
	import org.menacheri.jetclient.codecs.impl.MessageBufferEventEncoder;
	import org.menacheri.jetclient.communication.DefaultMessageSender;
	import org.menacheri.jetclient.communication.MessageSender;
	import org.menacheri.jetclient.protocol.Protocol;
	/**
	 * A default protocol which is of the form <length><opcode><payload>.
	 * @author Abraham Menacherry
	 */
	public class MessageBufferProtocol implements Protocol
	{
		public static const name:String = "MessageBufferProtocol";
		private var lengthFieldPrepender:LengthFieldPrepender;
		private var messageBufferEventEncoder:MessageBufferEventEncoder;
		private var messagBufferEventDecoder:MessagBufferEventDecoder;
		
		public function MessageBufferProtocol() 
		{
			lengthFieldPrepender = new LengthFieldPrepender();
			messageBufferEventEncoder = new MessageBufferEventEncoder();
			messagBufferEventDecoder = new MessagBufferEventDecoder();
		}
		
		public function getName():String
		{
			return name;
		}
		
		public function applyProtocol(session:Session):void 
		{
			var inCodec:CodecChain = new DefaultCodecChain();
			inCodec.add(new LengthFieldBasedFrameDecoder());
			inCodec.add(messagBufferEventDecoder);
			session.setInCodecs(inCodec);
		}
		
		public function createMessageSender(socket:Socket):MessageSender
		{
			var outCodec:CodecChain = new DefaultCodecChain();
			outCodec.add(messageBufferEventEncoder);
			outCodec.add(lengthFieldPrepender);
			var messageSender:MessageSender = new DefaultMessageSender(socket, outCodec);
			return messageSender;
		}
	}

}
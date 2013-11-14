package io.nadron.protocol.impl 
{
	import flash.net.Socket;
	import io.nadron.app.Session;
	import io.nadron.codecs.CodecChain;
	import io.nadron.codecs.impl.DefaultCodecChain;
	import io.nadron.codecs.impl.LengthFieldBasedFrameDecoder;
	import io.nadron.codecs.impl.LengthFieldPrepender;
	import io.nadron.codecs.impl.MessagBufferEventDecoder;
	import io.nadron.codecs.impl.MessageBufferEventEncoder;
	import io.nadron.communication.DefaultMessageSender;
	import io.nadron.communication.MessageSender;
	import io.nadron.protocol.Protocol;
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
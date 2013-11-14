package io.nadron.protocol.impl 
{
	import flash.net.Socket;
	import io.nadron.app.Session;
	import io.nadron.codecs.CodecChain;
	import io.nadron.codecs.impl.AMFDeserializer;
	import io.nadron.codecs.impl.AMFSerializer;
	import io.nadron.codecs.impl.DefaultCodecChain;
	import io.nadron.codecs.impl.LengthFieldBasedFrameDecoder;
	import io.nadron.codecs.impl.LengthFieldPrepender;
	import io.nadron.communication.DefaultMessageSender;
	import io.nadron.communication.MessageSender;
	import io.nadron.protocol.Protocol;
	
	/**
	 * This protocol has encoders and decoders which do AMF3 (de)serialization while 
	 * transmitting and receiving messages to remote Nadron server.
	 * @author Abraham Menacherry
	 */
	public class AMF3Protocol implements Protocol
	{
		public static const name:String = "AMF3Protocol";
		private var lengthFieldPrepender:LengthFieldPrepender;
		private var amfSerializer:AMFSerializer;
		private var amfDeSerializer:AMFDeserializer;
		
		
		public function AMF3Protocol() 
		{
			lengthFieldPrepender = new LengthFieldPrepender();
			amfSerializer = new AMFSerializer();
			amfDeSerializer = new AMFDeserializer();
		}
		
		public function getName():String
		{
			return name;
		}
		
		public function applyProtocol(session:Session):void 
		{
			var inCodec:CodecChain = new DefaultCodecChain();
			inCodec.add(new LengthFieldBasedFrameDecoder());
			inCodec.add(amfDeSerializer);
			session.setInCodecs(inCodec);
		}
		
		public function createMessageSender(socket:Socket):MessageSender
		{
			var outCodec:CodecChain = new DefaultCodecChain();
			outCodec.add(amfSerializer);
			outCodec.add(lengthFieldPrepender);
			var messageSender:MessageSender = new DefaultMessageSender(socket, outCodec);
			return messageSender;
		}
		
	}

}
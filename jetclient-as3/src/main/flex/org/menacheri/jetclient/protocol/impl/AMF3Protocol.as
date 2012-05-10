package org.menacheri.jetclient.protocol.impl 
{
	import flash.net.Socket;
	import org.menacheri.jetclient.app.Session;
	import org.menacheri.jetclient.codecs.CodecChain;
	import org.menacheri.jetclient.codecs.impl.AMFDeserializer;
	import org.menacheri.jetclient.codecs.impl.AMFSerializer;
	import org.menacheri.jetclient.codecs.impl.DefaultCodecChain;
	import org.menacheri.jetclient.codecs.impl.LengthFieldBasedFrameDecoder;
	import org.menacheri.jetclient.codecs.impl.LengthFieldPrepender;
	import org.menacheri.jetclient.communication.DefaultMessageSender;
	import org.menacheri.jetclient.communication.MessageSender;
	import org.menacheri.jetclient.protocol.Protocol;
	
	/**
	 * This protocol has encoders and decoders which do AMF3 (de)serialization while 
	 * transmitting and receiving messages to remote jetserver.
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
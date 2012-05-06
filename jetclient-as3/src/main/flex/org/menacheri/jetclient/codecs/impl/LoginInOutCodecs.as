package org.menacheri.jetclient.codecs.impl 
{
	import org.menacheri.jetclient.codecs.CodecChain;
	import org.menacheri.jetclient.codecs.impl.DefaultCodecChain;
	import org.menacheri.jetclient.codecs.InAndOutCodecChain;
	
	/**
	 * The logging in process requires both writing to remote jetserver as well as reading from it. 
	 * This set of in and out codecs will take care of transforming the events to be written and read 
	 * from remote server.
	 * @author Abraham Menacherry
	 */
	public class LoginInOutCodecs implements InAndOutCodecChain
	{
		private var inCodecs:CodecChain;
		private var outCodecs:CodecChain;
		
		public function LoginInOutCodecs() 
		{
			this.inCodecs = new DefaultCodecChain();
			this.outCodecs = new DefaultCodecChain();
			outCodecs.add(new MessageBufferEventEncoder());
			outCodecs.add(new LengthFieldPrepender());
			inCodecs.add(new LengthFieldBasedFrameDecoder());
			inCodecs.add(new MessagBufferEventDecoder());
		}
		
		public function getInCodecs():CodecChain
		{
			return inCodecs;
		}
		
		public function setInCodecs(inCodecs:CodecChain):void
		{
			this.inCodecs = inCodecs;
		}
		
		public function getOutCodecs():CodecChain
		{
			return outCodecs;
		}
		
		public function setOutCodecs(outCodecs:CodecChain):void
		{
			this.outCodecs = outCodecs;
		}
		
	}

}
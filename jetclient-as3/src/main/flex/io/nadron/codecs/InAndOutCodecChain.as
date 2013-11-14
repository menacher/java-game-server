package io.nadron.codecs 
{
	
	/**
	 * A utility interface which has methods to set and get the codec chains.
	 * @author Abraham Menacherry
	 */
	public interface InAndOutCodecChain 
	{
		function getInCodecs():CodecChain; 
		function setInCodecs(inCodecs:CodecChain):void 
		function getOutCodecs():CodecChain; 
		function setOutCodecs(outCodecs:CodecChain):void;
	}
	
}
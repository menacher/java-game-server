package io.nadron.codecs 
{
	
	/**
	 * This interface represents a chain of transformers. 
	 * It will store them in a chain i.e. an Array. Can be 
	 * considered like a composite transform which wraps other 
	 * transforms inside it.
	 * @author Abraham Menacherry
	 */
	public interface CodecChain extends Transform
	{
		function add(next:Transform):void;
		function remove(next:Transform):void;
	}
	
}
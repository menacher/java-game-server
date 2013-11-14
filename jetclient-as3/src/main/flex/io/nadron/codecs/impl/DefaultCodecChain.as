package io.nadron.codecs.impl 
{
	import io.nadron.codecs.CodecChain;
	import io.nadron.codecs.Transform;
	
	/**
	 * Default implementation of a codecchain which uses an array 
	 * to store the list of transformers.
	 * @author Abraham Menacherry
	 */
	public class DefaultCodecChain implements CodecChain 
	{
		private var chain:Array;
		
		public function DefaultCodecChain() 
		{
			chain = new Array();
		}
		
		public function add(next:Transform):void
		{
			chain.push(next);
		}
		
		public function remove(next:Transform):void
		{
			chain.splice(chain.indexOf(next),1);
		}
		
		/**
		 * Can be considered to be a composite method, which will call transform on all the 
		 * component transformers in the array and then return the final decoded or encoded object.
		 * 
		 * @param	input An object that needs to be transformed to another.
		 * @return The transformed object. If any transformer within the chain cannot transform the 
		 * 			object passed in to it, the it will return null and the method will also return null.
		 */
		public function transform(input:Object):Object
		{
			var len:uint = chain.length;
			for (var i:uint = 0; i < len; i++) {
				var output:Object = chain[i].transform(input);
				if (null == output) {
					return null;
				}else {
					input = output;
				}
			}
			
			return input;
		}
		
		public function getChain():Array
		{
			return chain;
		}
	}

}
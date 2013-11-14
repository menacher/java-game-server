package io.nadron.communication 
{
	import flash.utils.ByteArray;
	
	/**
	 * Not completed yet! A thin wrapper over ByteArray which will provide utility methods for writing and reading multiple Strings.
	 * @author Abraham Menacherry
	 */
	public class MessageBuffer
	{
		private var buffer:ByteArray;
		
		public function MessageBuffer(byteArray:ByteArray) 
		{
			if (null == byteArray) {
				buffer = new ByteArray();
			} else{
				buffer = byteArray;
			}
		}
		
		/**
		 * Writes multiple Strings to the underlying ByteArray. Each string is written as <length><bytes of string> 
		 * to the array since JetServer protocol expects it in this way.
		 * @param	... args
		 */
		public function writeMultiStrings(... args):void {
			for (var i:int = 0; i < args.length; i++) {
				writeString(args[i])
			}
		}
		
		/**
		 * Each string is written as <length><bytes of string> to the array since Nadron Server protocol expects it in this way
		 * @param	theString
		 */
		public function writeString(theString:String):void {
			var bytes:ByteArray = new ByteArray();
			bytes.writeUTFBytes(theString);
			return writeBytes(bytes);
		}
		
		/**
		 * Reads the length and then the actual string of that length from the underlying buffer.
		 * @return
		 */
		public function readString():String {
			var utfBytes:ByteArray = readBytes();
			var str:String = null;
			if (utfBytes != null) {
				str = utfBytes.readUTF();
			}
			return str;
		}
		
		/**
		 * Writes bytes to the underlying buffer, with the length of the bytes prepended.
		 * @param	bytes
		 */
		public function writeBytes(bytes:ByteArray):void {
			buffer.writeShort(bytes.length);
			buffer.writeBytes(bytes);
		}
		
		public function readBytes():ByteArray {
			var length:int = buffer.readShort();
			var bytes:ByteArray = null;
			if ((length > 0) && buffer.bytesAvailable >= length) {
				bytes = new ByteArray();
				buffer.readBytes(bytes);
			}
			return bytes;
		}
		
		public function getBuffer():ByteArray {
			return buffer;
		}
	}

}
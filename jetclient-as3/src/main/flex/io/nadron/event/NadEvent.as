package io.nadron.event 
{
	
	/**
	 * ...
	 * @author Abraham Menacherry
	 */
	public interface NadEvent 
	{
		function getType():int
		function setType(type:int):void
		function getSource():Object
		function setSource(source:Object):void;
		function getTimestamp():Number
		function setTimestamp(timestamp:Number):void
	}
	
}
package org.menacheri.jetclient.event 
{
	
	/**
	 * ...
	 * @author Abraham Menacherry
	 */
	public interface JetEvent 
	{
		function getType():int
		function setType(type:int):void
		function getSource():Object
		function setSource(source:Object):void;
		function getTimestamp():Number
		function setTimestamp(timestamp:Number):void
	}
	
}
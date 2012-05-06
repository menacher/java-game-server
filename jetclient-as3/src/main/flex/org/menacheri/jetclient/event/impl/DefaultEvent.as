package org.menacheri.jetclient.event.impl 
{
	import flash.events.Event;
	import org.menacheri.jetclient.event.JetEvent;
	
	/**
	 * ...
	 * @author Abraham Menacherry
	 */
	public class DefaultEvent extends Event implements JetEvent
	{
		private var jetEventType:int;
		private var source:Object;
		private var timestamp:Number;
		
		public function DefaultEvent(eventType:String) 
		{
			super(eventType);
		}
		
		public function getType():int  
		{
			return jetEventType;
		}
		
		public function setType(type:int):void 
		{
			this.jetEventType = type;
		}
		
		public function getSource():Object
		{
			return source;	
		}
		
		public function setSource(eventSource:Object):void
		{
			this.source = eventSource;
		}
		
		public function getTimestamp():Number
		{
			return timestamp;
		}
		
		public function setTimestamp(eventTimestamp:Number):void
		{
			this.timestamp = eventTimestamp;
		}
	}

}
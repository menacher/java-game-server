package io.nadron.event.impl 
{
	import flash.events.Event;
	import io.nadron.event.NadEvent;
	
	/**
	 * ...
	 * @author Abraham Menacherry
	 */
	public class DefaultEvent extends Event implements NadEvent
	{
		private var nEventType:int;
		private var source:Object;
		private var timestamp:Number;
		
		public function DefaultEvent(eventType:String) 
		{
			super(eventType);
		}
		
		public function getType():int  
		{
			return nEventType;
		}
		
		public function setType(type:int):void 
		{
			this.nEventType = type;
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
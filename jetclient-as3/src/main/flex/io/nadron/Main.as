package io.nadron
{
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	import io.nadron.app.impl.SessionFactory;
	import io.nadron.app.Session;
	import io.nadron.communication.MessageBuffer;
	import io.nadron.event.Events;
	import io.nadron.event.NadEvent;
	import io.nadron.util.LoginHelper;
	
	/**
	 * ...
	 * @author Abraham Menacherry
	 */
	public class Main extends Sprite 
	{
		private var sessions:Array = new Array();
		private var runOnce:Boolean = false;
		
		public function Main():void 
		{
			if (stage) init();
			else addEventListener(Event.ADDED_TO_STAGE, init);
		}
		
		private function init(e:Event = null):void 
		{
			removeEventListener(Event.ADDED_TO_STAGE, init);
			// entry point
			var loginHelper:LoginHelper = new LoginHelper("user", "pass", "Zombie_ROOM_1", "localhost", 18090);
			var sessionFactory:SessionFactory = new SessionFactory(loginHelper);
			for (var i:uint = 0; i < 50; i++)
			{
				var session:Session = sessionFactory.createAndConnectSession();
				session.addEventListener(Events.SESSION_MESSAGE_EVENT, traceData);
				sessions.push(session);
			}
		}
		
		private function traceData(event:Event):void
		{
			var nEvent:NadEvent = event as NadEvent;
			var messageBuffer:MessageBuffer = nEvent.getSource() as MessageBuffer;
			var buffer:ByteArray = messageBuffer.getBuffer();
			trace("Remaining Human Population: " + buffer.readInt());
			if (false == runOnce) 
			{
				runOnce = true;
				var timer:Timer = new Timer(200);
				timer.addEventListener(TimerEvent.TIMER, writeData);
				timer.start();
			}
		}
		
		private function writeData(event:TimerEvent):void
		{
			var len:uint = sessions.length;
			for (var i:uint = 0; i < len; i++) 
			{
				var session:Session = sessions[i];
				
				var bytes:ByteArray = new ByteArray();
				if ((i % 2) == 0) 
				{
					bytes.writeInt(2); //Defender
					bytes.writeInt(1); // operation = shot gun.
				}
				else 
				{
					bytes.writeInt(1); //Zombie
					bytes.writeInt(2); // operation = eat brains
				}
				var buffer:MessageBuffer = new MessageBuffer(bytes);
				var nEvent:NadEvent = Events.event(Events.NETWORK_MESSAGE, buffer);
				session.sendToServer(nEvent);
			}
			
		}
	}
	
}
package org.menacheri.lostdecade;

import java.util.HashSet;
import java.util.Set;

import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.app.impl.GameRoomSession;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.SessionEventHandler;
import org.menacheri.jetserver.event.impl.DefaultEventContext;
import org.menacheri.jetserver.event.impl.DefaultSessionEventHandler;
import org.menacheri.jetserver.service.GameStateManagerService;

public class LDRoom extends GameRoomSession
{
	private static final int canvasWidth = 512;
	private static final int canvasHeight = 480;

	public LDRoom(GameRoomSessionBuilder builder)
	{
		super(builder);
		addHandler(new GameSessionHandler(this));
	}

	@Override
	public void onLogin(final PlayerSession playerSession)
	{
		// Add a session handler to player session. So that it can receive
		// events.
		playerSession.addHandler(new DefaultSessionEventHandler(playerSession)
		{
			@Override
			protected void onDataIn(Event event)
			{
				if (null != event.getSource())
				{
					// Pass the player session in the event context so that the
					// game room knows which player session send the message.
					event.setEventContext(new DefaultEventContext(playerSession, null));
					// pass the event to the game room
					playerSession.getGameRoom().send(event);
				}
			}
			
		});
		
		
		Entity hero = createHero(playerSession);
		LDGameState state = (LDGameState)getStateManager().getState();
		state.getEntities().add(hero);
		// We do broadcast instead of send since all connected players need to
		// know about new players arrival
		sendBroadcast(Events.networkEvent(new LDGameState(state.getEntities(),
				state.getMonster(), hero)));
	}

	private Entity createHero(final PlayerSession playerSession) {
		Entity hero = new Entity();
		hero.setId((String) playerSession.getId());
		hero.score = 0;
		hero.setType(Entity.HERO);
		hero.setY(canvasHeight / 2);
		hero.setX(canvasWidth / 2);
		return hero;
	}

	private static Entity createMonster()
	{
		Entity monster =  new Entity();
		monster.setType(Entity.MONSTER);
		monster.setX(getRandomPos(canvasWidth));
		monster.setY(getRandomPos(canvasHeight));
		return monster;
	}
	
	private static int getRandomPos(int axisVal)
	{
		long round = Math.round(32 + (Math.random() * (axisVal - 64)));
		return (int) round;
	}
	
	public static class GameSessionHandler implements SessionEventHandler
	{
		Entity monster;
		GameRoom room;// not really required. It can be accessed as getSession()
						// also.
		
		public GameSessionHandler(GameRoomSession session)
		{
			this.room = session;
			GameStateManagerService manager = room.getStateManager();
			LDGameState state = (LDGameState) manager.getState();
			// Initialize the room state.
			state = new LDGameState();
			state.setEntities(new HashSet<Entity>());
			state.setMonster(createMonster());
			manager.setState(state); // set it back on the room
			this.monster = state.getMonster();
		}

		@Override
		public void onEvent(Event event)
		{
			Entity hero = ((LDGameState) event.getSource()).getHero();
			Session session = event.getEventContext()
			.getSession();
			update(hero, session);
		}

		@Override
		public int getEventType()
		{
			return Events.SESSION_MESSAGE;
		}

		@Override
		public Session getSession()
		{
			return (Session)room;
		}

		@Override
		public void setSession(Session session)
				throws UnsupportedOperationException
		{
		}
		
		private void update(Entity hero, Session session)
		{
			boolean isTouching = (hero.getX() <= monster.getX() + 32)
					&& (hero.getY() <= monster.getY() + 32)
					&& (monster.getX() <= hero.getX() + 32)
					&& (monster.getY() <= hero.getY() + 32);
			
			LDGameState state = (LDGameState) room.getStateManager().getState();
			if (isTouching)
			{
				hero.score += 1;
				state.addEntitiy(hero);
				reset();
			}
			else
			{
				state.addEntitiy(hero);
			}
			
			hero.setId((String) session.getId());
			// The state of only one hero is updated, no need to send every hero's state.
			room.sendBroadcast(Events.networkEvent(new LDGameState(null,
					monster, hero)));
		}

		private void reset()
		{
			Set<Entity> entities = ((LDGameState) room.getStateManager()
					.getState()).getEntities();
			for (Entity entity : entities)
			{
				entity.setY(canvasHeight / 2);
				entity.setX(canvasWidth / 2);
				monster.setX(getRandomPos(canvasWidth));
				monster.setY(getRandomPos(canvasHeight));
			}
			// no need to send the entities here since client will do resetting.
			LDGameState ldGameState = new LDGameState(null, monster, null);
			ldGameState.setReset(true);
			room.sendBroadcast(Events.networkEvent(ldGameState));
		}
		
	}
}

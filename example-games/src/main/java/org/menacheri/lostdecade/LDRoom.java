package org.menacheri.lostdecade;

import java.util.HashSet;
import java.util.Set;

import org.menacheri.jetserver.app.GameRoom;
import org.menacheri.jetserver.app.Player;
import org.menacheri.jetserver.app.PlayerSession;
import org.menacheri.jetserver.app.Session;
import org.menacheri.jetserver.app.impl.DefaultPlayerSession.PlayerSessionBuilder;
import org.menacheri.jetserver.app.impl.GameRoomSession;
import org.menacheri.jetserver.event.Event;
import org.menacheri.jetserver.event.Events;
import org.menacheri.jetserver.event.SessionEventHandler;
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
	public void onLogin(PlayerSession playerSession)
	{
	}

	@Override
	public void afterSessionConnect(PlayerSession playerSession)
	{
		GameStateManagerService manager = getStateManager();
		LDGameState state = (LDGameState) manager.getState();
		if(null == state){
			state = new LDGameState();
			manager.setState(state);
		}
		Entity hero = new Entity();
		hero.setId((String) playerSession.getId());
		hero.score = 0;
		hero.setType(Entity.HERO);
		hero.setY(canvasHeight / 2);
		hero.setX(canvasWidth / 2);
		if (null == state.getEntities())
		{
			HashSet<Entity> list = new HashSet<Entity>();
			list.add(hero);
			state.setEntities(list);
		}
		else
		{
			state.addEntitiy(hero);
		}
		// TODO this might cause synchronization problems. Since netty is async.
		// State needs to be made immutable.
		sendBroadcast(Events.networkEvent(new LDGameState(state.getEntities(),
				state.getMonster(), hero)));
	}

	@Override
	public PlayerSession getSessionInstance(Player player)
	{
		PlayerSessionBuilder playerSessionBuilder = new PlayerSessionBuilder()
				.parentGameRoom(this).player(player);
		return new LDPlayerSession(playerSessionBuilder);
	}

	public static class GameSessionHandler implements SessionEventHandler
	{
		private final Session session;
		Entity monster;
		GameRoom room;// not really required can be accessed as getSession()
						// also.

		public GameSessionHandler(GameRoomSession session)
		{
			this.session = session;
			room = session;
			monster = new Entity();
			monster.setType(Entity.MONSTER);
			LDGameState state = (LDGameState) room.getStateManager().getState();
			if(null == state){
				state = new LDGameState();
				room.getStateManager().setState(state);
			}
			state.setMonster(monster);
		}

		private void update(Entity entity, LDPlayerSession session)
		{
			Entity hero = session.getEntity();
			hero.setX(entity.getX());
			hero.setY(entity.getY());

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
			
			entity.setId((String) session.getId());
			// The state of only one hero is updated, no need to send every hero's state.
			room.sendBroadcast(Events.networkEvent(new LDGameState(null,
					entity, monster)));
		}

		private void reset()
		{
			Set<PlayerSession> playerSessions = room.getSessions();
			for (PlayerSession s : playerSessions)
			{
				LDPlayerSession session = (LDPlayerSession) s;
				Entity entity = session.getEntity();
				entity.setY(canvasHeight / 2);
				entity.setX(canvasWidth / 2);
				monster.setX(getRandomPos(canvasWidth));
				monster.setY(getRandomPos(canvasHeight));
			}
			LDGameState ldGameState = new LDGameState(null, monster, null);
			ldGameState.setReset(true);
			room.sendBroadcast(Events.networkEvent(ldGameState));
		}

		private int getRandomPos(int axisVal)
		{
			long round = Math.round(32 + (Math.random() * (axisVal - 64)));
			return (int) round;
		}

		@Override
		public void onEvent(Event event)
		{
			Entity hero = ((LDGameState) event.getSource()).getHero();
			LDPlayerSession session = (LDPlayerSession) event.getEventContext()
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
			return session;
		}

		@Override
		public void setSession(Session session)
				throws UnsupportedOperationException
		{
			
		}
	}
}

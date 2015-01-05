package xenar47.bukkit.mygames.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameUpdateEvent extends Event {
private static final HandlerList handlers = new HandlerList();
	public enum GameEventType {PLAYER_JOIN, PLAYER_LEAVE, WORLD_CHANGE, GAME_START, GAME_END};
	
	private String game;
	private GameEventType type;
	
	public GameUpdateEvent(String game, GameEventType type) {
		this.game = game;
		this.type = type;
	}
	
	public String getGame() {
		return game;
	}
	
	public GameEventType getEventType() {
		return type;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}

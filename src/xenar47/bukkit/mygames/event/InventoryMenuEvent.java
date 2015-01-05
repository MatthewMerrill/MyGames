package xenar47.bukkit.mygames.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class InventoryMenuEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private Player player;
	private String menuName;
	private ItemStack choice;
	
	public InventoryMenuEvent(Player player, String menuName, ItemStack choice) {
		this.player = player;
		this.menuName = menuName;
		this.choice = choice;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public String getMenuName(){
		return menuName;
	}
	
	public ItemStack getChoice(){
		return choice;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}

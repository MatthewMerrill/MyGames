package xenar47.bukkit.mygames.inventorymenu;

import org.bukkit.inventory.ItemStack;

public class MenuItem {
	
	private ItemStack icon;
	private MenuItemListener listener;
	
	public MenuItem(ItemStack icon, MenuItemListener listener) {
		this.icon = icon;
		this.listener = listener;
	}
	
	public ItemStack getIcon(){
		return icon;
	}
	
	public MenuItemListener getListener() {
		return listener;
	}
}

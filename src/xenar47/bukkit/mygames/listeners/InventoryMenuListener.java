package xenar47.bukkit.mygames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import xenar47.bukkit.mygames.inventorymenu.InventoryMenu;

public class InventoryMenuListener implements Listener {
	
	public InventoryMenuListener() {
		
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		InventoryMenu.inventoryClosed(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (InventoryMenu.inventoryClicked(event.getWhoClicked().getUniqueId(), event.getSlot()))
			event.getWhoClicked().closeInventory();
	}

}

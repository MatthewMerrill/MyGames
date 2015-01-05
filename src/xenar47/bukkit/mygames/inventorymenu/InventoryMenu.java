package xenar47.bukkit.mygames.inventorymenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryMenu {

	private static HashMap<UUID, InventoryMenu> currentInventories = new HashMap<UUID, InventoryMenu>();
	
	private String name;
	private ArrayList<MenuItem> items = new ArrayList<MenuItem>();
	
	private HashMap<Integer, MenuItem> layout = null;
	private Inventory inventory = null;
	
	public InventoryMenu(String name, ArrayList<MenuItem> items) {
		this.name = name;
		this.items = items;
	}
	
	private Inventory constructInventory(){
		
		if (inventory != null)
			return inventory;

		layout = new HashMap<Integer, MenuItem>();
		
		int itemRows = (int)(-(Math.floor(items.size() / -7.0)));
		
		boolean needsScroll = itemRows > 4;
		boolean useFull = needsScroll && (items.size() <= 54);	
		
		if (!needsScroll) {
			inventory = Bukkit.createInventory(null, itemRows * 9, name);
			
			int rowLength = (useFull)?9:7;
			
			for (int i = 0; i < items.size(); i++) {
				
				int rowIndex = i/rowLength;
				int posInRow = i%rowLength;
				
				int sideBuffer = (9 - rowLength)/2;
				
				boolean noodlyRow = (items.size() - i) < rowLength;
				if (noodlyRow) {
					sideBuffer = (9 - (items.size() - i)) / 2; 
				}
				
				int slot = (rowIndex) + (posInRow) + (sideBuffer);
				
				inventory.setItem(slot, items.get(i).getIcon());
				layout.put(slot, items.get(i));
			}
		}
		
		return inventory;
	}
	
	public static void openMenu(Player player, InventoryMenu menu) {
		Inventory inventory = menu.constructInventory();
		
		player.openInventory(inventory);
		currentInventories.put(player.getUniqueId(), menu);
	}
	
	public static boolean inventoryClicked(UUID uniqueId, int slot) {
		if (!currentInventories.containsKey(uniqueId))
			return false;
		
		InventoryMenu menu = currentInventories.get(uniqueId);
		MenuItem item = menu.layout.get(slot);
		item.getListener().onClick(Bukkit.getPlayer(uniqueId), menu, item);
		
		inventoryClosed(uniqueId);
		return true;
	}

	public static void inventoryClosed(UUID uniqueId) {
		//Player player = Bukkit.getPlayer(uniqueId);
		//if (player != null)
		//	player.closeInventory();
		
		currentInventories.remove(uniqueId);
	}
	
	

}

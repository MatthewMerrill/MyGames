package xenar47.bukkit.mygames.inventorymenu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Submenu extends MenuItem {

	private ItemStack icon;
	
	public Submenu(ItemStack icon, final InventoryMenu submenu) {
		
		super(icon, new MenuItemListener(){
			
			@Override
			public void onClick(Player player, InventoryMenu menu,
					MenuItem item) {
				InventoryMenu.openMenu(player, submenu);
			}
			
		});
		
		this.icon = icon;
	}
	
	@Override
	public ItemStack getIcon() {
		return icon;
	}	
}

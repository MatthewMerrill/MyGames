package xenar47.bukkit.mygames.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import xenar47.bukkit.mygames.MetadataManager;
import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.inventorymenu.InventoryMenu;
import xenar47.bukkit.mygames.world.WorldConfigManager;

public class WorldConfigListener implements Listener {

	MyGames mygames;
	WorldConfigManager wcm;
	MetadataManager mm;

	public WorldConfigListener(MyGames mygames, WorldConfigManager wcm) {

		this.mygames = mygames;
		this.wcm = wcm;
		this.mm = mygames.getMetaMgr();

	}

	/*@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (setBlock(event.getPlayer(), event.getItemInHand(), event.getBlock())) {
			event.setCancelled(true);
		}
	}*/

	@EventHandler
	public void onPlayerClickTool(PlayerInteractEvent event) {
		Block clicked = event.getClickedBlock();
		Block relative = clicked.getRelative(event.getBlockFace());
		
		if (setBlock(event.getPlayer(), event.getItem(), relative.getLocation()))
			event.setCancelled(true);
	}
	
	
	public boolean setBlock(Player player, ItemStack itemInHand, Location location) {

		if (mm.getMode(player) != MetadataManager.SETUP)
			return false;
		
		if (WorldConfigManager.isConfigMenuItem(itemInHand)) {
			InventoryMenu.openMenu(player, wcm.getConfigMenu());
			return false;
		}

		try {
			String locationKey = WorldConfigManager.getKeyFromTool(itemInHand);
			
			if (locationKey == null)
				return false;
			
			wcm.setLocation(player.getWorld().getName(), locationKey,
					location);
			
			player.sendMessage("Attached location to " + locationKey);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}

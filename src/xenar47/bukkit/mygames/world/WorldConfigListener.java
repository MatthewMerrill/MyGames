package xenar47.bukkit.mygames.world;

import xenar47.bukkit.mygames.MetadataManager;
import xenar47.bukkit.mygames.TeamWorld;
import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.world.WorldConfigManager.LOCATIONS;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Wool;

public class WorldConfigListener implements Listener {

	MyGames mygames;
	WorldConfigManager wcm;
	MetadataManager mm;

	public WorldConfigListener(MyGames mygames, WorldConfigManager wcm) {

		this.mygames = mygames;
		this.wcm = wcm;
		this.mm = mygames.getMetaMgr();

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (setBlock(event.getPlayer(), event.getBlock())) {
			event.getPlayer().sendMessage("Set Location");
			event.setCancelled(true);
		}
	}

	/*
	 * @EventHandler public void onBlockBreak(BlockBreakEvent event) { if
	 * (setBlock(event.getPlayer(), event.getBlock())) {
	 * event.getPlayer().sendMessage("Oops! "); event.setCancelled(true); } }
	 */

	public boolean setBlock(Player player, Block block) {

		if (mm.getMode(player) != mm.SETUP)
			return false;

		// ItemStack is =
		// player.getInventory().getItem(player.getInventory().getHeldItemSlot());
		if (block.getType() == Material.WOOL) {
			try {
				Wool wool = (Wool) block.getState().getData();
				DyeColor color = wool.getColor();

				LOCATIONS l;
				if (color.equals(DyeColor.RED))
					l = LOCATIONS.RED;
				else if (color.equals(DyeColor.BLUE))
					l = LOCATIONS.BLUE;
				else if (color.equals(DyeColor.GREEN))
					l = LOCATIONS.GREEN;
				else if (color.equals(DyeColor.YELLOW))
					l = LOCATIONS.YELLOW;
				else if (color.equals(DyeColor.WHITE))
					l = LOCATIONS.BOUNDS1;
				else if (color.equals(DyeColor.BLACK))
					l = LOCATIONS.BOUNDS2;
				else
					return false;

				TeamWorld.setLocation(wcm, player.getWorld(), l,
						block.getLocation());
				player.sendMessage("Attached location to " + l.toString());
				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}

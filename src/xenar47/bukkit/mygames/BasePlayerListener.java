package xenar47.bukkit.mygames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class BasePlayerListener implements Listener {

	MyGames plugin;
	MetadataManager mm;

	public BasePlayerListener(MyGames plugin) {
		this.plugin = plugin;
		mm = plugin.getMetaMgr();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		plugin.toLobby(event.getPlayer());
		event.setJoinMessage(ChatColor.YELLOW
				+ ChatColor.stripColor(event.getPlayer().getDisplayName())
				+ ChatColor.YELLOW + " has entered the arena!");
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (mm.getMode(event.getPlayer()) == mm.LOBBY) {

			event.setCancelled(true);
			sendErrorMessage(event.getPlayer());
		}
	}

	@EventHandler
	public void onInvOpen(InventoryOpenEvent event) {
		Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
		if (mm.getMode(player) != mm.SETUP) {

			event.getPlayer().closeInventory();
			event.setCancelled(true);
			if (player != null)
				sendErrorMessage(player);
		}
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent event) {
		Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
		if (mm.getMode(player) != mm.SETUP) {

			event.getWhoClicked().closeInventory();
			event.setCancelled(true);
			if (player != null)
				sendErrorMessage(player);
		}
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (mm.getMode(event.getPlayer()) != mm.SETUP) {

			event.setCancelled(true);
			sendErrorMessage(event.getPlayer());
		}
	}

	/*
	 * @EventHandler public void onBlockDamageEvent(BlockDamageEvent event) { if
	 * (mm.getMode(event.getPlayer()) != mm.SETUP) {
	 * 
	 * event.setCancelled(true); sendErrorMessage(event.getPlayer()); } }
	 */

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (mm.getMode(event.getPlayer()) != mm.SETUP) {

			event.setCancelled(true);
			sendErrorMessage(event.getPlayer());
		}
	}

	public void sendErrorMessage(Player player) {
		player.sendMessage(ChatColor.GRAY.toString()
				+ ChatColor.ITALIC.toString() + " I'm sorry "
				+ ChatColor.stripColor(player.getDisplayName())
				+ ", but I'm afraid I can't let you do that.");
	}
}

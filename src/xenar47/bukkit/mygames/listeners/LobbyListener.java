package xenar47.bukkit.mygames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import xenar47.bukkit.mygames.MetadataManager;
import xenar47.bukkit.mygames.MyGames;

public class LobbyListener implements Listener {

	MyGames plugin;
	MetadataManager mm;

	public LobbyListener(MyGames plugin) {
		this.plugin = plugin;
		mm = plugin.getMetaMgr();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		plugin.toLobby(event.getPlayer());
		event.setJoinMessage(plugin.getChatManager().joinServer(event.getPlayer()));
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (mm.getMode(event.getPlayer()) == MetadataManager.LOBBY) {

			event.setCancelled(true);
			sendErrorMessage(event.getPlayer());
		}
	}

	@EventHandler
	public void onInvOpen(InventoryOpenEvent event) {
		Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
		if (mm.getMode(player) == MetadataManager.LOBBY) {

		event.getPlayer().closeInventory();
			event.setCancelled(true);
			if (player != null)
				sendErrorMessage(player);
		}
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent event) {
		Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
		if (mm.getMode(player) == MetadataManager.LOBBY) {

			event.getWhoClicked().closeInventory();
			event.setCancelled(true);
			if (player != null)
				sendErrorMessage(player);
		}
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (mm.getMode(event.getPlayer()) == MetadataManager.LOBBY) {

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
		if (mm.getMode(event.getPlayer()) == MetadataManager.LOBBY) {

			event.setCancelled(true);
			sendErrorMessage(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onRespawnEvent(PlayerRespawnEvent event) {
		if (mm.getMode(event.getPlayer()) == MetadataManager.LOBBY) {
			event.setRespawnLocation(plugin.lobbyLocation());
		}
	}

	public void sendErrorMessage(Player player) {
		player.sendMessage(plugin.getChatManager().actionNotAllowed());
	}
}

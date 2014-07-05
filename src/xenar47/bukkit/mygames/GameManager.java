package xenar47.bukkit.mygames;

import java.util.ArrayList;
import java.util.UUID;

import xenar47.bukkit.mygames.api.Game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * @author Xenarthran47
 * 
 */
public class GameManager {

	private MyGames plugin;
	private Class<? extends Game> gameClass;
	private String gameName;
	private String[] aliases;
	
	private ArrayList<UUID> players = new ArrayList<UUID>();
	
	private Game game;

	public GameManager(MyGames plugin, String gameName, Class<? extends Game> gameClass, String... aliases) {

		this.plugin = plugin;
		this.gameClass = gameClass;
		this.gameName = gameName;
		this.aliases = aliases;
			
		//this.world = plugin.getWorldMgr().getRandomWorld();
		
	}
	
	public String getName() {
		return gameName;
	}
	
	public String[] getAliases() {
		return aliases;
	}

	public boolean canJoin(Player player) {
		return !players.contains(player.getName());
	}

	public void joinGame(Player player) {
		if (!canJoin(player))
			return;

		players.add(player.getUniqueId());

		ScoreboardManager.waitingList(player, this);

		player.sendMessage(ChatColor.GRAY + "You have joined the waiting list.");

	}

	public ArrayList<UUID> getPlayers() {
		return players;
	}

	public void startGame() {
		
		if (game == null) {
			try {
				game = gameClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
		}
		
		if (game.isRunning())
			return;

		game.setPlayers(players);
		game.setWorld(plugin.getWorldMgr().getWorld(
				plugin.getWorldMgr().getRandomWorld()));

		prepareGame();
		// teleportPlayers();
		// pregame(game);

		game.startGame(this);

	}

	private void prepareGame() {
		if (game != null)
			game.prepareGame();
	}

	/*
	 * private void teleportPlayers() {
	 * 
	 * for (String name : game.getPlayers()) { Player player =
	 * Bukkit.getPlayer(name);
	 * 
	 * plugin.getMetaMgr().setInGame(player);
	 * 
	 * player.teleport(game.getTeleportLocation(player)); } }
	 */

	public void stopGame() {
		
		if (game == null)
			return;
		
		if (game.isRunning())
			game.stopGame();
		
		if (game.getPlayers() != null){		
			for (UUID id : game.getPlayers()) {
				Player player = Bukkit.getPlayer(id);
				plugin.toLobby(player);
			}
		}

		HandlerList.unregisterAll(game);
		
		game = null;
		// plugin.getWorldMgr().rollback(world);
	}

}

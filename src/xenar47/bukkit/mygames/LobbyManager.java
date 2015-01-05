package xenar47.bukkit.mygames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import xenar47.bukkit.mygames.api.Game;

public class LobbyManager implements Listener {
	
	private MyGames mygames;
	
	private ArrayList<String> gameKeys;
	private HashMap<String, Game> games;
	private HashMap<String, String> aliases;
	
	public HashMap<UUID, String> lobbys = new HashMap<UUID, String>();
	
	public LobbyManager(MyGames mygames) {		
		this.mygames = mygames;
		
		gameKeys = new ArrayList<String>();
		games = new HashMap<String, Game>();
		aliases = new HashMap<String, String>();
		
		Bukkit.getPluginManager().registerEvents(this, mygames);
	}
	
	public void joinedGame(Player player, Game game) {
		String gameName = lobbys.get(player.getUniqueId());
		if ((gameName != null) && (getGame(gameName) != null)) {
			if (!game.getName().equalsIgnoreCase(gameName))			
				getGame(gameName).leaveGame(player);
		}
		
		lobbys.put(player.getUniqueId(), game.getName());
	}
	
	public void leftGame(Player player, Game game) {
		if (lobbys.get(player.getUniqueId()).equalsIgnoreCase(game.getName())) {
			lobbys.remove(player.getUniqueId());
		}
	}
	
	public String getCurrentGame(UUID uuid) {
		if (!lobbys.containsKey(uuid))
				return null;
		
		return lobbys.get(uuid);
	}
	
	@Deprecated
	public String getCurrentGame(Player player) {
		return getCurrentGame(player.getUniqueId());
	}
	
	public boolean join(Player player, String gameName) {

		Game game = getGame(gameName);
		if (game == null) {
			player.sendMessage(mygames.getChatManager().gameNotFound(gameName));
			return false;
		}
		else
			game.joinGame(player);

		return true;
	}
	public boolean start(String gameName) {
		
		Game game = getGame(gameName);
		if (game == null) {
			return false;
		}
		
		game.startGame();
		return true;
	}
	
	public Game getGame(String game) {
		if (aliases.containsKey(game.toLowerCase()))
			game = aliases.get(game.toLowerCase());
		
		if (games.containsKey(game.toLowerCase()))
			return games.get(game.toLowerCase());
		return null;
	}
	
	public void addGame(Game game) {
		if (!gameKeys.contains(game.getName()))
			gameKeys.add(game.getName());
		
		games.put(game.getUniqueName().toLowerCase(), game);
		
		//this.getLogger().info("Added game: "+gm.getName());
		
		String[] aliases = game.getAliases();
		if (aliases != null)
			for (String alias : aliases)
				this.aliases.put(alias.toLowerCase() + game.getId(), game.getUniqueName().toLowerCase());
		
		if (!this.aliases.containsKey(game.getName())) {
			this.aliases.put(game.getName().toLowerCase(), game.getUniqueName().toLowerCase());
			
			if (aliases != null)
				for (String alias : aliases)
					this.aliases.put(alias.toLowerCase(), game.getUniqueName().toLowerCase());
		}
	}
	
	public void removeGame(Game gm) {
		if (games.containsKey(gm.getName().toLowerCase()))
			games.remove(gm.getName().toLowerCase());
	}
	
	public boolean hasGame(String game) {
		return (games.containsKey(game.toLowerCase()) || aliases.containsKey(game.toLowerCase()));
	}
	
	public ArrayList<String> getGames() {
		
		ArrayList<String> names = new ArrayList<String>();
		for (String string : games.keySet()) {
			//Bukkit.getLogger().info(string);
			if (string != null && !string.trim().equalsIgnoreCase("")){
				names.add(games.get(string).getColoredName());
			}
		}
		
		return names;
	}

	public ArrayList<String> getGameKeys() {
		return gameKeys;
	}

	public void stopAll() {
		for (Game game : games.values()) {
			game.stopGame();
		}
		games.clear();
	}

}

package xenar47.bukkit.mygames;

import java.util.ArrayList;
import java.util.HashMap;

import xenar47.bukkit.mygames.games.DMGame;
import xenar47.bukkit.mygames.games.GrabGame;
import xenar47.bukkit.mygames.games.TDMGame;
import xenar47.bukkit.mygames.games.TagGame;
import xenar47.bukkit.mygames.world.WorldConfigManager;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

/**
 * @author Xenarthran47
 * 
 */
public class MyGames extends JavaPlugin {

	public static final int METADATA_GAME = 0;

	MetadataManager mm;
	WorldConfigManager wcm;
	GameConfigManager gcm;

	BasePlayerListener ll;

	HashMap<String, GameManager> games;
	HashMap<String, String> aliases;
	
	public static MyGames getInstance() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("MyGames");
		
		if (plugin != null && plugin instanceof MyGames) {	
			return (MyGames)plugin;
		}
		
		return null;
	}

	public void onEnable() {
		mm = new MetadataManager(this);
		wcm = new WorldConfigManager(this);
		wcm.loadWorlds();
		gcm = new GameConfigManager(this);

		ll = new BasePlayerListener(this);
		Bukkit.getPluginManager().registerEvents(ll, this);

		games = new HashMap<String, GameManager>();
		aliases = new HashMap<String, String>();
		
		addGame(new GameManager(this, "DeathMatch", 	DMGame.class, "DM"));
		addGame(new GameManager(this, "TeamDeathMatch", TDMGame.class, "TDM"));
		addGame(new GameManager(this, "Tag", 			TagGame.class));
		addGame(new GameManager(this, "Grab", 			GrabGame.class));

		for (Player player : Bukkit.getOnlinePlayers()) {
			toLobby(player);
			player.sendMessage(ChatColor.GRAY.toString()
					+ ChatColor.ITALIC.toString()
					+ "Welcome back to MyGames; the plugin is being enabled.");
		}
	}

	public void onDisable() {

		for (Player player : Bukkit.getOnlinePlayers()) {
			toLobby(player);
			ScoreboardManager.remove(player);
			player.sendMessage(ChatColor.GRAY.toString()
					+ ChatColor.ITALIC.toString()
					+ "Sorry for any inconvenience; the plugin is being disabled.");
		}

		wcm.saveWorlds();
		gcm.saveConfig();
		
		for (GameManager gm : games.values()) {
			gm.stopGame();
		}
		games.clear();
		
		HandlerList.unregisterAll(this);
	}

	public GameManager getGameManager(String game) {
		
		if (aliases.containsKey(game.toLowerCase()))
			game = aliases.get(game.toLowerCase());
		
		if (games.containsKey(game.toLowerCase()))
			return games.get(game.toLowerCase());
		return null;
	}
	
	public void addGame(GameManager gm) {
		if (gcm.getGameEnabled(gm.getName())) {
			games.put(gm.getName().toLowerCase(), gm);
			
			this.getLogger().info("Added game: "+gm.getName());
			
			String[] aliases = gm.getAliases();
			if (aliases != null)
				for (String alias : aliases)
					this.aliases.put(alias.toLowerCase(), gm.getName().toLowerCase());
		}
	}
	
	public void removeGame(GameManager gm) {
		if (games.containsKey(gm.getName().toLowerCase()))
			games.remove(gm.getName().toLowerCase());
	}
	
	public boolean hasGame(String game) {
		return (games.containsKey(game.toLowerCase()) || aliases.containsKey(game.toLowerCase()));
	}
	
	public ArrayList<String> getGames() {
		
		ArrayList<String> names = new ArrayList<String>();
		for (String string : games.keySet()) {
			Bukkit.getLogger().info(string);
			if (string != null && !string.trim().equalsIgnoreCase("")){
				//Bukkit.getLogger().info(string);
				names.add(games.get(string).getName());
			}
		}
		
		return names;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if (label.equalsIgnoreCase("join")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED
						+ "You must be a player to use this command.");
				return true;
			}

			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "/join [game]");
				sender.sendMessage(ChatColor.RED
						+ "What games are running? /list");
				return true;
			}

			Player player = (Player) sender;

			GameManager gm = getGameManager(args[0]);
			if (gm == null) {
				sender.sendMessage(ChatColor.RED + "Could not find game \""
						+ ChatColor.GREEN + args[0] + ChatColor.RED + "\"!");
				sender.sendMessage(ChatColor.RED
						+ "What games are running? /list");
			}
			else
				gm.joinGame(player);

			return true;
		} else if (label.equalsIgnoreCase("start")) {

			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "/start [game]");
				sender.sendMessage(ChatColor.RED
						+ "What games are running? /list");
				return true;
			}

			GameManager gm = getGameManager(args[0]);
			if (gm == null) {
				sender.sendMessage(ChatColor.RED + "Could not find game \""
						+ ChatColor.GREEN + args[0] + ChatColor.RED + "\"!");
				sender.sendMessage(ChatColor.RED
						+ "What games are running? /list");
			}
			else
				gm.startGame();

			return true;
		} else if (label.equalsIgnoreCase("setup")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED
						+ "You must be a player to use this command.");
				return true;
			}

			Player player = (Player) sender;

			if (!(player.isOp())) {
				sender.sendMessage(ChatColor.RED
						+ "Need OP for that. Plea nerf.");
				return true;
			}

			toSetup(player);

			return true;
		}else if (label.equalsIgnoreCase("play")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED
						+ "You must be a player to use this command.");
				return true;
			}

			Player player = (Player) sender;

			if (!(player.isOp())) {
				sender.sendMessage(ChatColor.RED
						+ "Need OP for that. Plea nerf.");
				return true;
			}

			toLobby(player);

			return true;
		} else if (label.equalsIgnoreCase("setworld")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED
						+ "You must be a player to use this command.");
				return true;
			}

			Player player = (Player) sender;
			if (!(player.isOp())) {
				sender.sendMessage(ChatColor.RED
						+ "Need OP for that. Plea nerf.");
				return true;
			}

			try {
				player.teleport(Bukkit.getWorld(args[0]).getSpawnLocation());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		} else if (label.equalsIgnoreCase("prepworld")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED
						+ "You must be a player to use this command.");
				return true;
			}
			if (!(((Player) sender).isOp())) {
				sender.sendMessage(ChatColor.RED
						+ "Need OP for that. Plea nerf.");
				return true;
			}

			World world = ((Player) sender).getWorld();
			wcm.setWorldOptions(world);

			return true;
		} else if (label.equalsIgnoreCase("addworld")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED
						+ "You must be a player to use this command.");
				return true;
			}
			if (!(((Player) sender).isOp())) {
				sender.sendMessage(ChatColor.RED
						+ "Need OP for that. Plea nerf.");
				return true;
			}
			World world = ((Player) sender).getWorld();
			wcm.addToList(world.getName());

			return true;
		} else if (label.equalsIgnoreCase("saveworld")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED
						+ "You must be a player to use this command.");
				return true;
			}
			if (!(((Player) sender).isOp())) {
				sender.sendMessage(ChatColor.RED
						+ "Need OP for that. Plea nerf.");
				return true;
			}
			World world = ((Player) sender).getWorld();
			world.save();

			return true;
		} else if (label.equalsIgnoreCase("list")) {
			ArrayList<String> games = getGames();
			String gameList = Utils.list(games, ChatColor.GRAY, ChatColor.GREEN);
			
			if (gameList == null || gameList.equalsIgnoreCase(""))
				gameList = "None!";
			else
				gameList += ChatColor.GRAY + ".";
			
			sender.sendMessage(ChatColor.GRAY + "Games running: " + gameList);
			return true;
		}

		return false;
	}

	public MetadataManager getMetaMgr() {
		return mm;
	}

	public WorldConfigManager getWorldMgr() {
		return wcm;
	}

	public void toLobby(Player player) {
		player.setHealth(20);
		player.setFoodLevel(20);

		player.getInventory().setArmorContents(null);
		player.getInventory().clear();

		player.setGameMode(GameMode.CREATIVE);
		player.setAllowFlight(false);
		player.setCanPickupItems(false);

		player.setFireTicks(-1);
		player.getInventory().clear();
		for (PotionEffect pe : player.getActivePotionEffects())
			player.removePotionEffect(pe.getType());

		player.teleport(lobbyLocation());
		try {
			mm.setInLobby(player);
		} catch (Exception e) {
		}
		ScoreboardManager.remove(player);
	}

	public void toSetup(Player player) {
		player.setHealth(20);
		player.setFoodLevel(20);

		player.getInventory().setArmorContents(null);
		player.getInventory().clear();

		player.setGameMode(GameMode.CREATIVE);
		player.setAllowFlight(true);
		player.setCanPickupItems(true);

		player.setFireTicks(-1);

		PlayerInventory inv = player.getInventory();
		inv.setItem(0, WorldConfigManager.getSpawnTool(DyeColor.RED));
		inv.setItem(1, WorldConfigManager.getSpawnTool(DyeColor.BLUE));
		inv.setItem(2, WorldConfigManager.getSpawnTool(DyeColor.GREEN));
		inv.setItem(3, WorldConfigManager.getSpawnTool(DyeColor.YELLOW));
		inv.setItem(4, WorldConfigManager.getBoundsTool(1));
		inv.setItem(5, WorldConfigManager.getBoundsTool(2));

		try {
			mm.setInSetup(player);
		} catch (Exception e) {
		}
		ScoreboardManager.remove(player);
	}

	public Location lobbyLocation() {
		return Bukkit.getWorlds().get(0).getSpawnLocation();
	}
}

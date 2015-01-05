package xenar47.bukkit.mygames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import xenar47.bukkit.mygames.api.Game;
import xenar47.bukkit.mygames.chat.ChatManager;
import xenar47.bukkit.mygames.chat.DefaultChatManager;
import xenar47.bukkit.mygames.command.CommandMap;
import xenar47.bukkit.mygames.command.MyGamesCommandMap;
import xenar47.bukkit.mygames.impl.games.DMGame;
import xenar47.bukkit.mygames.impl.games.GrabGame;
import xenar47.bukkit.mygames.impl.games.TDMGame;
import xenar47.bukkit.mygames.impl.games.TagGame;
import xenar47.bukkit.mygames.listeners.InventoryMenuListener;
import xenar47.bukkit.mygames.listeners.LobbyListener;
import xenar47.bukkit.mygames.listeners.SignListener;
import xenar47.bukkit.mygames.world.WorldConfigManager;
import xenar47.bukkit.mygames.world.WorldManager;

/**
 * @author Xenarthran47
 * 
 */
public class MyGames extends JavaPlugin {

	public static final int METADATA_GAME = 0;

	MetadataManager mm;
	WorldManager wm;
	ConfigManager cm;
	ChatManager chatManager;
	
	LobbyManager lm;
	LobbyListener ll;
	
	InventoryMenuListener iml;
	SignListener sl;
	
	CommandMap commandMap;

	
	public static MyGames getInstance() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("MyGames");
		
		if (plugin != null && plugin instanceof MyGames) {	
			return (MyGames)plugin;
		}
		
		return null;
	}
	/*@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if (args.length == 0) {
			emptyArgs(sender);
		} else {
			CommandExecutor exec = executors.get(args[0]);
			if (exec != null) {
				
				
				
				return (exec.onCommand(sender, new PluginCommand(args[0], mygames), args[0], args2));
			}
			return false;
		}
		else if (args[0].equalsIgnoreCase("setup")) {
			setup(sender, args);
		}
		else if (args[0].equalsIgnoreCase("join")) {
			
		}

		if (label.equalsIgnoreCase("setworld")) {

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

	//		World world = ((Player) sender).getWorld();
//			wm.setWorldOptions(world);

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
			mygames.getWorldMgr().addToList(world.getName());

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
		}

		return false;
	}*/

	public void onEnable() {
		mm = new MetadataManager(this);
		wm = new WorldManager(this);
		cm = new ConfigManager(this);
		lm = new LobbyManager(this);
		chatManager = new DefaultChatManager();
		
		commandMap = new MyGamesCommandMap(this);
		
		iml = new InventoryMenuListener();
		Bukkit.getPluginManager().registerEvents(iml, this);
		
		if (cm.isLobbyListenerEnabled()) {
			ll = new LobbyListener(this);
			Bukkit.getPluginManager().registerEvents(ll, this);
		}
		if (cm.isSignListenerEnabled()) {
			sl = new SignListener(this);
			Bukkit.getPluginManager().registerEvents(sl, this);
		}
		
		Bukkit.getPluginCommand("MyGames").setExecutor(commandMap);
		/*Bukkit.getPluginCommand("join").setExecutor(lm);
		Bukkit.getPluginCommand("start").setExecutor(lm);
		Bukkit.getPluginCommand("play").setExecutor(lm);
		Bukkit.getPluginCommand("setup").setExecutor(lm);
		Bukkit.getPluginCommand("listGames").setExecutor(lm);
		*/
		addGame(new DMGame());
		addGame(new TDMGame());
		addGame(new TagGame());
		addGame(new GrabGame());		
		

		if (cm.isLobbyListenerEnabled()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				toLobby(player);
				player.sendMessage(ChatColor.GRAY.toString()
						+ ChatColor.ITALIC.toString()
						+ "Welcome back to MyGames; the plugin is being enabled.");
			}
		}
	}

	public void onDisable() {
		
		if (cm.isLobbyListenerEnabled()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				toLobby(player);
				//ScoreboardManager.remove(player);
				player.sendMessage(ChatColor.GRAY.toString()
						+ ChatColor.ITALIC.toString()
						+ "Sorry for any inconvenience; the plugin is being disabled.");
			}
		}

		wm.onDisable();
		cm.saveConfig();
		
		lm.stopAll();
		
		HandlerList.unregisterAll(this);
	}
	/*
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if (label.equalsIgnoreCase("setworld")) {

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

	//		World world = ((Player) sender).getWorld();
//			wm.setWorldOptions(world);

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
			wm.addToList(world.getName());

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
		}

		return false;
	}*/
	
	public void addGame(Game game) {
		if (game.getName().length() > 13) {
			getLogger().severe("Unable to enable game:"+game.getName()
					+". Base name should not exceed 13 characters.");
			return;
		}
		if (cm.isGameEnabled(game.getName())) {
			lm.addGame(game);
		}
	}
	public void removeGame(Game game) {
		lm.removeGame(game);
	}
	
	public Game getGame(String game) {
		return lm.getGame(game);
	}

	/*
	 * <Managers>
	 */
	
	public MetadataManager getMetaMgr() {
		return mm;
	}

	public WorldManager getWorldMgr() {
		return wm;
	}
	
	public LobbyManager getLobbyMgr() {
		return lm;
	}
	
	public ChatManager getChatManager() {
		return chatManager;
	}
	
	public void setChatManager(ChatManager chatManager) {
		this.chatManager = chatManager;
	}
	
	/*
	 * </Managers>
	 * 
	 * <PlayerManagement>
	 */

	public void toLobby(Player player) {
		player.setHealth(20);
		player.setFoodLevel(20);

		player.getInventory().setArmorContents(null);
		player.getInventory().clear();

		if (cm.isLobbyListenerEnabled()) {
			player.setGameMode(GameMode.CREATIVE);
			player.setAllowFlight(false);
			player.setCanPickupItems(false);
		}

		player.setFireTicks(-1);
		player.getInventory().clear();
		for (PotionEffect pe : player.getActivePotionEffects())
			player.removePotionEffect(pe.getType());

		player.teleport(lobbyLocation());
		try {
			mm.setInLobby(player);
		} catch (Exception e) {
		}
		//ScoreboardManager.remove(player);
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
		
		player.getInventory().setItem(0, WorldConfigManager.getConfigMenuItem());
		
		//TODO: Setup Inventory Menu
/*
		PlayerInventory inv = player.getInventory();
		inv.setItem(0, WorldConfigManager.getSpawnTool(DyeColor.RED));
		inv.setItem(1, WorldConfigManager.getSpawnTool(DyeColor.BLUE));
		inv.setItem(2, WorldConfigManager.getSpawnTool(DyeColor.GREEN));
		inv.setItem(3, WorldConfigManager.getSpawnTool(DyeColor.YELLOW));
		inv.setItem(4, WorldConfigManager.getBoundsTool(1));
		inv.setItem(5, WorldConfigManager.getBoundsTool(2));
*/
		try {
			mm.setInSetup(player);
		} catch (Exception e) {
		}
		//ScoreboardManager.remove(player);
	}

	public Location lobbyLocation() {
		return cm.getSpawnLocation();
		//return Bukkit.getWorlds().get(0).getSpawnLocation();
	}
	
	/*
	 * </PlayerManagement>
	 */
	
}

package xenar47.bukkit.mygames.listeners;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import xenar47.bukkit.mygames.MetadataManager;
import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.api.Game;
import xenar47.bukkit.mygames.event.GameUpdateEvent;

public class SignListener implements Listener {
	
	private MyGames mygames;
	private HashMap<String, ArrayList<Location>> infoSigns;

	public static final String PATH_LOCATIONS = "locations";
	
	private FileConfiguration config;
	private File configFile;
	
	public SignListener(MyGames mygames) {
		this.mygames = mygames;
		infoSigns = new HashMap<String, ArrayList<Location>>();
		
		updateAll();
	}
	
	private void updateAll() {
		ArrayList<Location> locs = getSignLocations();
		
		for (Location loc : locs) {
			try {
			Block block = loc.getBlock();
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
			BlockState blockState = block.getState();
			Sign sign = (Sign) blockState;
			String game = ChatColor.stripColor(sign.getLine(0));
			//Bukkit.broadcastMessage(game);
				
				if (!infoSigns.containsKey(game))
					infoSigns.put(game, new ArrayList<Location>());
				
				infoSigns.get(game).add(loc);
				
				updateInfo(sign);
			} else {
				//Bukkit.("This ain't a sign");
			}
			} catch (Exception e) {}
		}
	}
	
	private void saveLocations() {
		ArrayList<Location> all = new ArrayList<Location>();
		for (ArrayList<Location> locs : infoSigns.values())
			for (Location loc : locs)
				all.add(loc);
		
		setSignLocations(all);
	}
	
	@SuppressWarnings("deprecation")
	private void reloadConfig() {

		if (configFile == null) {
			configFile = new File(mygames.getDataFolder(), "signs.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);

		// Look for defaults in the jar
		InputStream defConfigStream = mygames.getResource("signs.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}

	public void saveConfig() {
		if (config == null || configFile == null) {
			return;
		}
		try {			
			config.save(configFile);
		} catch (IOException e) {
			mygames.getLogger().log(Level.SEVERE,
					"Could not save config to " + configFile, e);
		}
	}

	private FileConfiguration getConfig() {
		if (config == null) {
			reloadConfig();
		}
		return config;
	}
	
	public ArrayList<Location> getSignLocations() {
		ArrayList<Location> locs = new ArrayList<Location>();
		
		ConfigurationSection section = getConfig().getConfigurationSection(PATH_LOCATIONS);
		
		if (section != null) {
			for (String key : section.getKeys(false)) {
				List<String> strings = section.getStringList(key);
				for (String string : strings) {
					String[] sub = string.split(";");
					double[] d = new double[3];
					
					for (int i = 0; i < 3; i++)
						d[i] = Double.valueOf(sub[i]);
					
					Location loc = new Location(Bukkit.getWorld(key), d[0], d[1], d[2]);
					locs.add(loc);
				}
			}
		}
		
		return locs;
	}
	
	private void setSignLocations(ArrayList<Location> locs) {
		
		config.set(PATH_LOCATIONS, null);
		ConfigurationSection section = config.createSection(PATH_LOCATIONS);
		
		for (Location loc : locs) {
			
			String worldName = loc.getWorld().getName();
			
			List<String> list;
			if (section.contains(worldName))
				list = section.getStringList(worldName);
			else
				list = new ArrayList<String>();
			
			list.add(loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() );
			section.set(worldName, list);
		}
		
		saveConfig();
	}
	
	@EventHandler
	public void onSignCreate(SignChangeEvent event) {
		
		Player player = event.getPlayer();
		if ((player != null) && (!player.hasPermission("mygames.sign.edit"))) {
			event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission for this!");
			event.setCancelled(true);
			return;
		}
		
		String gameName = event.getLine(0);
		String line1 = event.getLine(1);

		Game gm = mygames.getGame(gameName);
		if (gm == null) {
			return;
		}
		gameName = gm.getName();
		
		if ((line1 != null) && (line1.equalsIgnoreCase("join"))) {
			
			String[] lines = getLines(gm);
			for (int i = 0; i < lines.length; i++)
				event.setLine(i, lines[i]);
			
			if (!infoSigns.containsKey(gameName))
				infoSigns.put(gameName, new ArrayList<Location>());
			
			infoSigns.get(gameName).add(event.getBlock().getLocation());
			saveLocations();
		}
	}
	
	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		
		if (!event.getPlayer().hasPermission("mygames.sign.use"))
			return;
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			BlockState b = event.getClickedBlock().getState();
			if (b instanceof Sign) {
				Sign sign = (Sign)b;
				String[] lines = sign.getLines();
				
				Game gm = mygames.getGame(ChatColor.stripColor(lines[0]));
				
				if (ChatColor.stripColor(lines[1]).equalsIgnoreCase("[refresh]")) {
					if (infoSigns.containsKey(ChatColor.stripColor(lines[0]))) {
						if (!updateInfo(sign))
							event.getPlayer().sendMessage(ChatColor.RED + "Could not attach to game manager. Is it enabled?");
						return;
					}
				}

				if (gm == null)
					return;
				
				if ((!gm.isRunning()) && (gm.canJoin(event.getPlayer()))
						&& mygames.getMetaMgr().getMode(event.getPlayer()) != MetadataManager.INGAME) {
					gm.joinGame(event.getPlayer());
				}
				//}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		BlockState b = event.getBlock().getState();
		if (b instanceof Sign) {
			Sign sign = (Sign)b;
			String[] lines = sign.getLines();
			
			String gameName = ChatColor.stripColor(lines[0]);
			
			if (infoSigns.containsKey(gameName)) {
				if (infoSigns.get(gameName).contains(event.getBlock().getLocation())) {
					if (event.getPlayer().hasPermission("mygames.sign.edit")) {
						infoSigns.get(gameName).remove(event.getBlock().getLocation());
						saveLocations();
					} else {
						event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission for this!");
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onGameEvent(GameUpdateEvent event) {
		//Bukkit.broadcastMessage("got event");
		if (!infoSigns.containsKey(event.getGame())){
			//Bukkit.broadcastMessage("dont see that key.");
			//for (String string : infoSigns.keySet())
				//Bukkit.broadcastMessage("but i do have:"+string);
			return;
		}
		for (Location loc : infoSigns.get(event.getGame())) {
			//Bukkit.broadcastMessage("updating");
			Sign sign = (Sign)loc.getBlock().getState();
			updateInfo(sign);
		}
	}
	
	public boolean updateInfo(Sign sign) {
		
		String line0 = sign.getLine(0);
		if ((line0 == null)||(line0.equalsIgnoreCase("")))
			return false;
		
		String gameName = ChatColor.stripColor(line0);

		Game gm = mygames.getGame(gameName);
		if (gm == null) {
			sign.setLine(0, ChatColor.RED + gameName);
			sign.setLine(1, ChatColor.RED + "[refresh]");
			sign.setLine(2, ChatColor.RED + "?");
			sign.setLine(3, ChatColor.RED + "?");
			return false;
		}
		
		String[] lines = getLines(gm);
		for (int i = 0; i < lines.length; i++)
			sign.setLine(i, lines[i]);
		
		sign.update();
		
		return true;
	}
	
	int calls = 1;
	public String[] getLines(Game gm) {		
		calls += 1;
		String[] line = new String[4];
		line[0] = gm.getColoredName();
		line[1] = (gm.isRunning())?(ChatColor.RED+"[Running]"):(ChatColor.GREEN+"[join]");
		line[2] = ChatColor.YELLOW + Integer.toString(gm.getPlayers().size())
				+ ChatColor.BLUE + "/" + ChatColor.YELLOW
				+ gm.getMinPlayers() + "-" + gm.getMaxPlayers();
		line[3] = ChatColor.YELLOW + gm.getWorldKey();
		return line;
	}
}

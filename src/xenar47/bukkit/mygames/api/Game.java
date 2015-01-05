package xenar47.bukkit.mygames.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import xenar47.bukkit.mygames.MetadataManager;
import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.event.GameUpdateEvent;
import xenar47.bukkit.mygames.event.GameUpdateEvent.GameEventType;
import xenar47.bukkit.mygames.listeners.GameListener;
import xenar47.bukkit.mygames.task.CountdownTask;
import xenar47.bukkit.mygames.task.GameTimer;
import xenar47.bukkit.mygames.world.location.WorldLocation;

/**
 * @author Xenarthran47
 * 
 */
interface IGame {

	public String getName();
	public String[] getAliases();
	
	public DyeColor[] getSpawnColors();
	public WorldLocation[] getLocationTypes();
	
	public boolean doFallDamage();
	public boolean doDrownDamage();
	
	public int getMinPlayers();
	public int getMaxPlayers();
	
	public boolean allowJoinInProgress();

	public void prepareGame();
	public void preparePlayer(Player player);

	//public void startGame(GameManager gameManager);
	//public boolean isRunning();
	//public void stopGame();
	
	public int getPlayTime();
	public int getWarmupTime();
	public double getSecsPerTick();
	
	public void warmupTick();
	public void tick();

	/**
	 * return true if player should be damaged, false if attack should be
	 * cancelled.
	 */
	public boolean playerDamagePlayer(Player attacker, Player victim);
	public void playerKilled(Player player);
	
	//public boolean shouldEnd();
	public ArrayList<String> getWinners();
	
	/**
	 * Return array of scores containing desired ScoreBoard display.
	 * Maximum 8 lines will be displayed.
	 * 
	 * Return null to hide score board.
	 * 
	 * @return ArrayList of scores for side display slot
	 */
	public ArrayList<GameScore> getSideScores();
	public ArrayList<GameScore> getPlayerScores();
	public GameScore getPlayerScore(Player player);
}

public abstract class Game implements IGame {

	protected final MyGames mygames;
	private GameListener listener;
	
	private ArrayList<UUID> players = new ArrayList<UUID>();
	private HashMap<UUID, Integer> scores = new HashMap<UUID, Integer>();
	
	SidebarManager sbm;
	
	//private static final int ticksPerSec = 20;
	
//	private final int maxPlayTime;
//	private final int warmupTime;
//	private final double secsPerTick;
	
	private CountdownTask countdownTask;
	private GameTimer gameTimer;
	
	private static HashMap<String, Integer> indeces = new HashMap<String, Integer>();
	private final int id;
	
	public Game() {		
		this.mygames = MyGames.getInstance();
		
		sbm = new SidebarManager(this);
		
		if (!indeces.containsKey(getName())) {
			indeces.put(getName(), 1);
		}
		
		id = indeces.put(getName(), indeces.get(getName()) + 1);
		
//		maxPlayTime = getPlayTime();
//		warmupTime = getWarmupTime();
//		secsPerTick = getSecsPerTick();
		
		countdownTask = new CountdownTask(mygames, this, 40);		
		gameTimer = new GameTimer(mygames, this);
	}
	
	public final int getId() {
		return id;
	}
	
	public final String getUniqueName() {
		return getName() + id;
	}
	
	/**
	 * Convenience method. Returns the UniqueName colored Green/Blue/Red
	 * Green: Game not started yet.
	 * Blue: In progress, can join.
	 * Red: In progress, can not join.
	 */
	public String getColoredName() {
		ChatColor cc = ChatColor.GREEN;
		
		if (isRunning())
			cc = (allowJoinInProgress())?ChatColor.BLUE:ChatColor.RED;
		
		return cc + getUniqueName();
	}

	/****************************************************
	 * 
	 * <GameManagement>
	 * 
	 ***************************************************/
	
	public abstract void prepareGame();

	public final void startGame() {
		
		if (worldKey == null) {
			if (hasWorlds()) {
				findRandomWorld();
				loadWorld();
			} else {
				sendMessageToPlayers(mygames.getChatManager().gameHasNoWorlds(this));
				return;
			}
		}
		
		prepareGame();
		
		for (UUID uuid : getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);

			player.getInventory().clear();
			player.setGameMode(GameMode.SURVIVAL);
			player.setHealth(20);
			player.teleport(getSpawnLocation(player));
			
			player.sendMessage(mygames.getChatManager().gameStart(this));
		}
		
		listener = new GameListener(this, mygames);
		Bukkit.getPluginManager().registerEvents(listener, mygames);
		
		gameTimer.start();
	}
	
	public final boolean isRunning() {
		return gameTimer.isRunning();
	}
	
	public final boolean isWarmup() {
		return gameTimer.isWarmup();
	};
	
	public final void stopGame() {
		
		countdownTask.stop();
		gameTimer.stop();
		
		sbm.hide();
		
		Bukkit.broadcastMessage(mygames.getChatManager().gameOver(this, getWinners()));
		
		unregisterListeners();
		
		for (UUID uuid : players) {
			mygames.toLobby(Bukkit.getPlayer(uuid));
		}
		players.clear();
	}
	
	/**
	 * If you override this, make sure to call super.unregisterListeners()!!!
	 */
	public void unregisterListeners() {
		HandlerList.unregisterAll(listener);
	}
	
	@Override
	public int getPlayTime() {
		return 120;
	}
	
	@Override
	public int getWarmupTime() {
		return 30;
	}
	
	@Override
	public double getSecsPerTick() {
		return 5;
	}

	/****************************************************
	 * 
	 * </GameManagement>
	 * 
	 * <World>
	 * 
	 ***************************************************/
	
	protected String worldKey;
	protected World world;
	
	public boolean hasWorlds() {
		return mygames.getWorldMgr().getCompatibleWorlds(this).size() > 0;
	}
	public final void findRandomWorld() {
		String key = mygames.getWorldMgr().getRandomKey(this);
		if (key != null)
			setWorldKey(key);
	}
	
	public final void setWorldKey(String key) {		
		if (!isRunning()) {
			this.worldKey = key;
		}
	}
	public final String getWorldKey() {
		return worldKey;
	}
	public final World getWorld() {
		return world;
	}
	
	public final void loadWorld() {
		if (world == null)
			world = mygames.getWorldMgr().getWorldCopy(worldKey);
	}
	
	public final void unloadWorld() {	
		if (world == null)
			return;
		
		//TODO: Change worlds without returning to lobby
		for (Player player : world.getEntitiesByClass(Player.class))
			mygames.toLobby(player);
		
		/**
		 * Don't change this. Will delete world contents if changed incorrectly.
		 */
		mygames.getWorldMgr().destroyCopy(world.getName());
	}

	public Location getSpawnLocation(Player player) {
		return mygames.getWorldMgr().getRandomSpawn(world);
	}

	/****************************************************
	 * 
	 * </World>
	 * 
	 * <Players>
	 * 
	 ***************************************************/

	public boolean canJoin(Player player) {
		
		if (players.contains(player.getUniqueId())) {
			player.sendMessage(mygames.getChatManager().joinLobbyAlreadyJoined(this));
			return false;
		}
		
		if (players.size() >= getMaxPlayers()){
			player.sendMessage(mygames.getChatManager().joinLobbyFull(this));
			return false;
		}
		
		int mode = mygames.getMetaMgr().getMode(player);
		if (mode == MetadataManager.INGAME || mode == MetadataManager.OTHER_GAME){
			player.sendMessage(mygames.getChatManager().joinLobbyIngame(this));
			return false;
		}
		
		if (isRunning() && !allowJoinInProgress()) {
			player.sendMessage(mygames.getChatManager().joinLobbyInProgress(this));
			return false;
		}
		
		return true;
	}

	public void joinGame(Player player) {
		if (!canJoin(player))
			return;

		players.add(player.getUniqueId());
		mygames.getLobbyMgr().joinedGame(player, this);
		
		if (getPlayersNeeded() > 0) {
			sbm.playersNeeded();
		} else if (!countdownTask.isRunning()) {
			countdownTask.start();
		}
		
		if (isRunning()) {
			preparePlayer(player);
		}
		else {
			//TODO: Is this a good idea? :/
			/*if (players.size() == getMaxPlayers()) {
				countdownTask.stop();
				startGame();
			}*/
		}
		
		player.sendMessage(mygames.getChatManager().joinLobbySuccess(this));

		GameUpdateEvent gue = new GameUpdateEvent(this.getName(), GameEventType.PLAYER_JOIN);
		Bukkit.getServer().getPluginManager().callEvent(gue);
	}
	
	public void leaveGame(Player player) {
		if (!players.contains(player.getUniqueId()))
			return;
		
		players.remove(player.getUniqueId());
		mygames.getLobbyMgr().leftGame(player, this);

		player.sendMessage(mygames.getChatManager().leaveLobby(this));
		
		if (getPlayersNeeded() > 0) {
			sbm.playersNeeded();
			
			if (countdownTask.isRunning()) {
				countdownTask.stop();
			}
		} else if (!countdownTask.isRunning()) {
			countdownTask.start();
		}

		GameUpdateEvent gue = new GameUpdateEvent(this.getName(), GameEventType.PLAYER_LEAVE);
		Bukkit.getServer().getPluginManager().callEvent(gue);
	}
	
	public int getPlayersNeeded() {
		int size = getPlayers().size();
		return (size >= getMinPlayers())?(0):(getMinPlayers() - size);
	}
	
	public final ArrayList<UUID> getPlayers() {
		return players;
	}

	public final boolean hasPlayer(Player player) {
		if (player == null)
			return false;
		return players.contains(player.getUniqueId());
	}

	/*public final void removePlayer(Player player) {
		players.remove(player.getUniqueId());
		sbm.removeEntry(player.getUniqueId());
		mygames.toLobby(player);
	}*/
	
	public final void sendMessageToPlayers(String string) {
		for (UUID uuid : this.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null)
				player.sendMessage(string);
		}
	}
	
	@Override
	public void preparePlayer(Player player) {
		PlayerClass.setupPlayer(player);		
	}
	
	public void updateEffects() {
		for (UUID uuid : this.getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				for (PotionEffect pe : player.getActivePotionEffects()) {
					if (pe.getDuration() > 6666)
						player.removePotionEffect(pe.getType());
				}
				
				HashMap<PotionEffectType, Integer> effects = PlayerClass.getLastingEffects(player);
				for (PotionEffectType type : effects.keySet()) {
					player.addPotionEffect(new PotionEffect(type, 9999, effects.get(type)), true);
				}
			}
		}
	}
	
	public void respawnPlayer(Player player) {
		
		//player.setNoDamageTicks(60);
				
		if (this.hasPlayer(player.getKiller()))
			sendMessageToPlayers(mygames.getChatManager().playerDeath(player, player.getKiller()));
		else
			sendMessageToPlayers(mygames.getChatManager().playerDeath(player));
		
		//Utils.fakeDeath(player.getLocation());
		/*Packet205ClientCommand packet = new Packet205ClientCommand();
        packet.a = 1;
        ((CraftPlayer) player).getHandle().netServerHandler.a(packet);
		//*/
		player.getInventory().clear();
		
		Location spawnLoc = getSpawnLocation(player);
		
		if (spawnLoc.distanceSquared(player.getLocation())>1) {
			sendMessageToPlayers("Sending " + player.getDisplayName() + " to respawn point.");
			player.teleport(player.getLocation().add(0, -32, 0), TeleportCause.PLUGIN);
			player.teleport(spawnLoc, TeleportCause.PLUGIN);
		}

		this.playerKilled(player);
		
		//if (shouldEnd())
		//	stopGame();

		player.setHealth(player.getMaxHealth());
		player.setNoDamageTicks(0);

		preparePlayer(player);
	}


	/****************************************************
	 * 
	 * </Players>
	 * 
	 * <Scoreboard>
	 * 
	 ***************************************************/

	public final void setScore(UUID uuid, Integer score) {
		scores.put(uuid, score);
	}
	
	public final void addPoints(UUID uuid, Integer points) {
		Integer prev = scores.get(uuid);
		scores.put(uuid, prev + points);
	}
	
	public final void removePoints(UUID uuid, Integer points) {
		Integer prev = scores.get(uuid);
		scores.put(uuid, prev - points);
	}
	
	public GameScore getPlayerScore(Player player) {
		return new GameScore(player.getName(), scores.get(player.getUniqueId()));
	}
	
	@Override
	public ArrayList<GameScore> getPlayerScores() {
		ArrayList<GameScore> gameScores = new ArrayList<GameScore>();
		for (UUID uuid : scores.keySet()) {
			gameScores.add(getPlayerScore(Bukkit.getPlayer(uuid)));
		}
		return gameScores;
	}
	
	/**
	 * Orders the scores from highest to lowest
	 * @return
	 */
	public static ArrayList<GameScore> orderHighest(ArrayList<GameScore> scores) {
		ArrayList<GameScore> ordered = new ArrayList<GameScore>();
		
		for (GameScore score : scores) {
			
			if (ordered.size() == 0) {
				ordered.add(score);
				continue;
			}
			
			for (int i = 0; i < ordered.size(); i++) {
				if (score.score > ordered.get(i).score) {
					ordered.add(i, score);
					break;
				} else if (i == ordered.size() - 1) {
					ordered.add(score);
					break;
				}
			}
		}
		
		return ordered;
	}
	
	/**
	 * Orders the scores from lowest to highest
	 * @return
	 */
	public static ArrayList<GameScore> orderLowest(ArrayList<GameScore> scores) {
		ArrayList<GameScore> ordered = new ArrayList<GameScore>();
		
		for (GameScore score : scores) {
			
			if (ordered.size() == 0) {
				ordered.add(score);
				continue;
			}
			
			for (int i = 0; i < ordered.size(); i++) {
				if (score.score < ordered.get(i).score) {
					ordered.add(i, score);
					break;
				} else if (i == ordered.size() - 1) {
					ordered.add(score);
					break;
				}
			}
		}
		
		return ordered;
	}
	
	public static ArrayList<String> highestScorers(HashMap<UUID, Integer> scores){
		
		ArrayList<String> winners = new ArrayList<String>();
		
		if (scores.size() < 1) {
			return null;
		} else if (scores.size() == 1) {
			winners.add(Bukkit.getPlayer((UUID)scores.keySet().toArray()[0]).getName());
			return winners;
		}
		
		ArrayList<UUID> top = new ArrayList<UUID>();
		for (UUID uuid : scores.keySet()) {
			if (top.size() <= 0) {
				top.add(uuid);
				continue;
			}
			
			if (scores.get(top.get(0)) < scores.get(uuid)) {
				top.clear();
				top.add(uuid);
			} else if (scores.get(top.get(0)) == scores.get(uuid)) {
				top.add(uuid);
			}
		}
		
		for (UUID uuid : top)
			winners.add(Bukkit.getPlayer(uuid).getName());
		
		return winners;
	}
	
	public static ArrayList<String> lowestScorers(HashMap<UUID, Integer> scores){

		ArrayList<String> winners = new ArrayList<String>();
		
		if (scores.size() < 1) {
			return null;
		} else if (scores.size() == 1) {
			winners.add(Bukkit.getPlayer((UUID)scores.keySet().toArray()[0]).getName());
			return winners;
		}
		
		ArrayList<UUID> top = new ArrayList<UUID>();
		for (UUID uuid : scores.keySet()) {
			if (top.size() <= 0) {
				top.add(uuid);
				continue;
			}
			
			if (scores.get(top.get(0)) > scores.get(uuid)) {
				top.clear();
				top.add(uuid);
			} else if (scores.get(top.get(0)) == scores.get(uuid)) {
				top.add(uuid);
			}
		}
		
		for (UUID uuid : top)
			winners.add(Bukkit.getPlayer(uuid).getName());
		
		return winners;
	}

	public SidebarManager getSidebarManager() {
		return sbm;
	}


	/****************************************************
	 * 
	 * </Scoreboard>
	 * 
	 ***************************************************/

}

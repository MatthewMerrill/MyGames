package xenar47.bukkit.mygames.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import xenar47.bukkit.mygames.*;
import xenar47.bukkit.mygames.ScoreboardManager.GameScore;
import xenar47.bukkit.mygames.world.WorldConfigManager.LOCATIONS;

import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Xenarthran47
 * 
 */
interface IGame {

	public String getName();

	public void prepareGame();

	public void preparePlayer(Player player);

	public void startGame(GameManager gameManager);
	public boolean isRunning();
	public void stopGame();
	
	
	public int getPlayTime();
	public int getWarmupTime();
	public int getSecsPerTick();
	
	public void warmupTick();
	public void tick();

	/**
	 * return true if player should be damaged, false if attack should be
	 * cancelled.
	 */
	public boolean playerDamagePlayer(Player attacker, Player victim);
	public void playerKilled(Player player);
	
	public boolean shouldEnd();
	public ArrayList<Player> getWinners();
	
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
}

public abstract class Game implements IGame, Listener {

	protected final MyGames mygames;
	private ArrayList<UUID> players = new ArrayList<UUID>();

	ScoreboardManager sbm;
	GameManager gm;
	
	private static final int ticksPerSec = 20;
	
	private final int maxPlayTime;
	private final int warmupTime;
	private final int secsPerTick;
	
	private int secondsPlayed = 0;
	
	boolean showHealth = true;

	public Game() {
		
		this.mygames = MyGames.getInstance();
		sbm = new ScoreboardManager(mygames, this);
		
		maxPlayTime = getPlayTime();
		warmupTime = getWarmupTime();
		secsPerTick = getSecsPerTick();
	}

	/****************************************************
	 * 
	 * <GameManagement>
	 * 
	 ***************************************************/
	
	@Override
	public void prepareGame() {
		ArrayList<UUID> players = this.getPlayers();
		for (UUID uuid : players) {
			Player player = Bukkit.getPlayer(uuid);
			
			player.getInventory().clear();
			player.setGameMode(GameMode.SURVIVAL);
			player.setHealth(20);
			preparePlayer(player);
		}
	}

	@Override
	public void startGame(GameManager gm) {

		if (showHealth)
			sbm.showHealth();
		sbm.show();

		this.gm = gm;
		this.isRunning = true;

		for (UUID uuid : getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			player.teleport(getSpawnLocation(player));
		}
		
		Bukkit.getPluginManager().registerEvents(this, mygames);
		startTimer();
	}
	
	BukkitTask task;
	BukkitRunnable runnable;

	public void startTimer() {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				secondsPlayed += secsPerTick;
				
				if ( secondsPlayed < warmupTime ) {
					warmupTick();
				} else {
					tick();
				}
				
				if ((shouldEnd()) || ((maxPlayTime != -1) && (secondsPlayed >= warmupTime + maxPlayTime)) ) {
					Bukkit.broadcastMessage("stopping");
					stopGame();
				}

				updateScores();
			}
		};
		
		task = Bukkit.getScheduler().runTaskTimerAsynchronously(mygames, runnable, 0, ticksPerSec * secsPerTick);
	}

	private boolean isRunning;

	@Override
	public boolean isRunning() {
		return isRunning;
	}
	
	public boolean isWarmup() {
		return (secondsPlayed < warmupTime);
	};
	
	public int getSecondsPlayed() {
		return secondsPlayed;
	}

	@Override
	public void stopGame() {
		this.isRunning = false;
		
		if (task != null)
			task.cancel();
		
		sbm.hide();

		for (UUID uuid : getPlayers())
			sbm.removeEntry(uuid);

		ArrayList<Player> winners = getWinners();
		if (winners != null) {		
			if (winners.size() >= 1) {
				if (winners.size() == 1) {
					Bukkit.broadcastMessage(ChatColor.GREEN
							+ "[" + getName()+ ChatColor.GREEN + "]"
							+ ChatColor.BLUE + Bukkit.getPlayer(getPlayers().get(0))
									.getDisplayName() + ChatColor.GRAY + " takes the victory!");
				} else if (winners.size() == 2) {
					Bukkit.broadcastMessage(ChatColor.GREEN + winners.get(0).getDisplayName() + ChatColor.GRAY + " and "
							+ ChatColor.GREEN + winners.get(1).getDisplayName() + ChatColor.GRAY + " take the victory!");
				} else {
					String message = "";
					for (int i = 0; i < winners.size(); i++) {
						message +=  ChatColor.GRAY + ((i==0)?"":", ")
								+ ((i==winners.size()-1)?"and ":"") 
								+ ChatColor.GREEN + winners.get(i);
					}
					Bukkit.broadcastMessage(message);
				}
			} else {
				Bukkit.broadcastMessage(ChatColor.GRAY
						+ "All participants have died... No Winner!");
			}
		} else {
			Bukkit.broadcastMessage(ChatColor.GRAY
					+ "Game ended in mysterious circumstances... No Winner!");
		}
		
		if (gm != null)
			gm.stopGame();
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
	public int getSecsPerTick() {
		return 5;
	}

	/****************************************************
	 * 
	 * </GameManagement>
	 * 
	 * <World>
	 * 
	 ***************************************************/
	
	protected World world;
	public void setWorld(World world) {
		this.world = world;
	}

	public Location getSpawnLocation(Player player) {

		LOCATIONS l = LOCATIONS.RED;

		Random random = new Random();
		int i = random.nextInt(4);

		switch (i) {
		case (0): {
			l = LOCATIONS.RED;
			break;
		}
		case (1): {
			l = LOCATIONS.BLUE;
			break;
		}
		case (2): {
			l = LOCATIONS.GREEN;
			break;
		}
		default: {
			l = LOCATIONS.YELLOW;
			break;
		}
		}

		return (TeamWorld.getLocation(mygames.getWorldMgr(), world, l));
	}

	/****************************************************
	 * 
	 * </World>
	 * 
	 * <Players>
	 * 
	 ***************************************************/

	public void setPlayers(ArrayList<UUID> players) {
		this.players = players;
	}

	public final ArrayList<UUID> getPlayers() {
		return players;
	}

	public final boolean hasPlayer(Player player) {
		return players.contains(player.getUniqueId());
	}

	public void removePlayer(Player player) {
		players.remove(player.getUniqueId());
		sbm.removeEntry(player.getUniqueId());
		mygames.toLobby(player);
	}

	/****************************************************
	 * 
	 * </Players>
	 * 
	 * <Scoreboard>
	 * 
	 ***************************************************/

	public void setShowHealth(boolean b) {
		showHealth = b;
	}
	
	public void updateScores() {
		sbm.update();
	}
	
	/**
	 * Orders the scores from highest to lowest
	 * @return
	 */
	public ArrayList<GameScore> orderHighest(ArrayList<GameScore> scores) {
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
	public ArrayList<GameScore> orderLowest(ArrayList<GameScore> scores) {
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
	
	public ArrayList<Player> highestScorers(HashMap<UUID, Integer> scores){
		
		ArrayList<Player> winners = new ArrayList<Player>();
		
		if (scores.size() < 1) {
			return null;
		} else if (scores.size() == 1) {
			winners.add(Bukkit.getPlayer((UUID)scores.keySet().toArray()[0]));
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
			winners.add(Bukkit.getPlayer(uuid));
		
		return winners;
	}
	
	public ArrayList<Player> lowestScorers(HashMap<UUID, Integer> scores){

		ArrayList<Player> winners = new ArrayList<Player>();
		
		if (scores.size() < 1) {
			return null;
		} else if (scores.size() == 1) {
			winners.add(Bukkit.getPlayer((UUID)scores.keySet().toArray()[0]));
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
			winners.add(Bukkit.getPlayer(uuid));
		
		return winners;
	}

	/****************************************************
	 * 
	 * </Scoreboard>
	 * 
	 * <Listeners>
	 * 
	 ***************************************************/
	
	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event) {
		Player player = Bukkit.getPlayer(event.getEntity().getUniqueId());
		if (hasPlayer(player)) {
			event.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public void onItemPickupEvent(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		if (item instanceof Arrow) {
			event.setCancelled(true);
			item.remove();
		}
	}

	@EventHandler
	public void onPlayerMelee(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {

			Player damager = (Player) event.getDamager();
			Player victim = (Player) event.getEntity();

			if (players.contains(damager.getUniqueId())
					&& players.contains(victim.getUniqueId())) {

				Weapon weapon = Weapon.parseWeapon(damager.getItemInHand());
				if (weapon != null) {
					event.setDamage(weapon.melee(this, damager, victim));
				}
				event.setCancelled(!playerDamagePlayer(damager, victim));
			}
		}
	}

	@EventHandler
	public void onPlayerClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (players.contains(player.getUniqueId())
				&& Weapon.isWeapon(event.getItem())) {
			
			Weapon weapon = (Weapon) Weapon.parseWeapon(event.getItem());
			Action action = event.getAction();

			if (action == Action.LEFT_CLICK_AIR
					|| action == Action.LEFT_CLICK_BLOCK) {
				weapon.primary(this, player);
			} else if (action == Action.RIGHT_CLICK_AIR
					|| action == Action.RIGHT_CLICK_BLOCK) {
				weapon.secondary(this, player);
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (players.contains(player.getUniqueId())) {
			
			Weapon weapon = Weapon.parseWeapon(player.getItemInHand());
			if (weapon != null) {
				event.setCancelled(!weapon.interact(this, player,
						event.getRightClicked()));
			}
			
			/*
			if (player.getItemInHand().getData() instanceof WeaponData) {
				WeaponData weapon = (WeaponData) player.getItemInHand().getData();
				event.setCancelled(!weapon.interact(this, player,
						event.getRightClicked()));
			}*/
		}
	}

	@EventHandler
	public void onPlayerReload(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (hasPlayer(player)) {//players.contains(player.getUniqueId())) {
				//&& player.getItemInHand().getData() instanceof WeaponData) {
			//((WeaponData) player.getItemInHand().getData()).reload(this, player);
			Weapon weapon = Weapon.parseWeapon(player.getItemInHand());
			if (weapon != null) {
				weapon.reload(this, player);
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (!hasPlayer(player))
			return;

		if (player.getHealth() - event.getDamage() <= 0) {

			Utils.fakeDeath(player.getLocation());
			
			player.teleport(getSpawnLocation(player));
			preparePlayer(player);

			this.playerKilled(player);
			
			if (shouldEnd())
				stopGame();

			event.setCancelled(true);
			event.setDamage(0);

			player.setHealth(player.getMaxHealth());
		}

	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!hasPlayer(event.getPlayer()))
			return;

		event.setRespawnLocation(getSpawnLocation(event.getPlayer()));
	}

	/*@EventHandler
	public void onArrowHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();

			// Weapons system not working - just make all explosive to look
			// fancy
			 if (arrow.getMetadata("Explosive").contains(true)) {
			ProjectileSource ps = arrow.getShooter();

			if (ps instanceof Player) {

				Player player = (Player) ps;
				if (!hasPlayer(player) || !isRunning())
					return;

				Location loc = arrow.getLocation();
				arrow.getWorld().createExplosion(loc.getX(), loc.getY(),
						loc.getZ(), .75f, false, false);
				arrow.remove();
			}
			 }
		}
	}*/
	

	/****************************************************
	 * 
	 * </Listeners>
	 * 
	 ***************************************************/

}

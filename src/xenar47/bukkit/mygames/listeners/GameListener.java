package xenar47.bukkit.mygames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.api.Game;
import xenar47.bukkit.mygames.api.Weapon;

public class GameListener implements Listener {
	
	private MyGames mygames;
	private Game game;
	
	public GameListener(Game game, MyGames mygames) {
		this.game = game;
		this.mygames = mygames;
	}
	
	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		Game gm = mygames.getLobbyMgr().getGame(mygames.getLobbyMgr().getCurrentGame(event.getPlayer().getUniqueId()));
		if (gm != null)
			gm.leaveGame(event.getPlayer());
	}
	
	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event) {
		Player player = Bukkit.getPlayer(event.getEntity().getUniqueId());
		if (game.hasPlayer(player)) {
			event.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public final void onItemPickupEvent(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		if (item instanceof Arrow) {
			event.setCancelled(true);
			item.remove();
		}
	}

	@EventHandler
	public final void onPlayerMelee(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {

			Bukkit.broadcastMessage(event.getCause().name());
			if (event.getCause() == DamageCause.CUSTOM)
				return;
			
			Player damager = (Player) event.getDamager();
			Player victim = (Player) event.getEntity();

			if (game.hasPlayer(damager) && game.hasPlayer(victim)) {
				
				if (game.playerDamagePlayer(damager, victim)) {
					Weapon weapon = Weapon.parseWeapon(damager.getItemInHand());
					if (weapon != null) {
						event.setDamage(weapon.melee(game, damager, victim));
					}
				} else {
					event.setCancelled(true);
				}
			}
		} else if (event.getDamager() instanceof TNTPrimed
				&& event.getEntity() instanceof Player) {
			
			TNTPrimed tnt = (TNTPrimed) event.getDamager();
			Player victim = (Player) event.getEntity();
			
			if (tnt.getMetadata("Owner").size() >= 1) {
				String owner = tnt.getMetadata("Owner").get(0).asString();
				@SuppressWarnings("deprecation")
				Player player = Bukkit.getPlayer(owner);
				
				if (player != null) {
					if (game.hasPlayer(player)) {
						if (game.playerDamagePlayer(player, victim)) {
							double damage = event.getDamage();
							Bukkit.broadcastMessage("damaging " + victim + " " + damage);
							victim.setLastDamageCause(new EntityDamageEvent(victim, DamageCause.ENTITY_EXPLOSION, damage));
							victim.damage(damage, player);
						}
						event.setCancelled(true);
					}
				}				
			}
			
		}
	}

	@EventHandler
	public final void onPlayerClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (game.hasPlayer(player)
				&& Weapon.isWeapon(event.getItem())) {
			
			Weapon weapon = (Weapon) Weapon.parseWeapon(event.getItem());
			Action action = event.getAction();

			if (action == Action.LEFT_CLICK_AIR
					|| action == Action.LEFT_CLICK_BLOCK) {
				event.setCancelled(weapon.primary(game, player));
			} else if (action == Action.RIGHT_CLICK_AIR
					|| action == Action.RIGHT_CLICK_BLOCK) {
				event.setCancelled(weapon.secondary(game, player));
			}
		}
	}

	@EventHandler
	public final void onPlayerInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (game.hasPlayer(player)) {
			
			Weapon weapon = Weapon.parseWeapon(player.getItemInHand());
			if (weapon != null) {
				event.setCancelled(!weapon.interact(game, player,
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
	public final void onPlayerReload(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (game.hasPlayer(player)) {//players.contains(player.getUniqueId())) {
				//&& player.getItemInHand().getData() instanceof WeaponData) {
			//((WeaponData) player.getItemInHand().getData()).reload(this, player);
			Weapon weapon = Weapon.parseWeapon(player.getItemInHand());
			if (weapon != null) {
				weapon.reload(game, player);
			}
		}
	}

	@EventHandler
	public final void onPlayerDamage(EntityDamageEvent event) {

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (!game.hasPlayer(player))
			return;
		
		boolean cancel = false;
		
		cancel = ((!game.doFallDamage()) && event.getCause() == DamageCause.FALL)?true:cancel;
		cancel = game.isWarmup()?true:cancel;
		
		if (cancel) {
			event.setDamage(0.0);
			event.setCancelled(true);
			return;
		}

		/*if (player.getHealth() - event.getDamage() < 1) {

			game.respawnPlayer(player);
			event.setDamage(0);
			event.setCancelled(true);
		}*/
	}
	
	@EventHandler
	public final void onPlayerDeath(PlayerDeathEvent event) {
		
		if (!game.hasPlayer(event.getEntity()))
			return;
		
		event.setDroppedExp(0);
		event.setKeepLevel(true);
		event.setDeathMessage(null);
		
		event.getEntity().setHealth(20);
		game.respawnPlayer(event.getEntity());
	}

	@EventHandler
	public final void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!game.hasPlayer(event.getPlayer()))
			return;

		event.setRespawnLocation(game.getSpawnLocation(event.getPlayer()));
		game.preparePlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onInvOpen(InventoryOpenEvent event) {
		Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
		if (game.hasPlayer(player)) {

		event.getPlayer().closeInventory();
			event.setCancelled(true);
			if (player != null)
				player.sendMessage(mygames.getChatManager().actionNotAllowed());
		}
	}

	@EventHandler
	public final void onInvClick(InventoryClickEvent event) {
		Player player = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
		if (game.hasPlayer(player)) {

			event.getWhoClicked().closeInventory();
			event.setCancelled(true);
			if (player != null)
				player.sendMessage(mygames.getChatManager().actionNotAllowed());
		}
	}

	@EventHandler
	public final void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (game.hasPlayer(event.getPlayer())) {

			event.setCancelled(true);
			event.getPlayer().sendMessage(mygames.getChatManager().actionNotAllowed());
		}
	}

	/*
	 * @EventHandler public void onBlockDamageEvent(BlockDamageEvent event) { if
	 * (mm.getMode(event.getPlayer()) != mm.SETUP) {
	 * 
	 * event.setCancelled(true); sendErrorMessage(event.getPlayer()); } }
	 */

	@EventHandler
	public final void onBlockBreakEvent(BlockBreakEvent event) {
		if (game.hasPlayer(event.getPlayer())) {

			event.setCancelled(true);
			event.getPlayer().sendMessage(mygames.getChatManager().actionNotAllowed());
		}
	}
	
	@EventHandler
	public final void onExplosion(EntityExplodeEvent event){
		if (game.getWorld() == event.getEntity().getWorld())
			event.blockList().clear();
	}

}

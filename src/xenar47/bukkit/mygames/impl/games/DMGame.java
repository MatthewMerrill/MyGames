/**
 * 
 */
package xenar47.bukkit.mygames.impl.games;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import xenar47.bukkit.mygames.api.Game;
import xenar47.bukkit.mygames.api.GameScore;
import xenar47.bukkit.mygames.world.location.WorldLocation;

public class DMGame extends Game {

	//private HashMap<UUID, Integer> playerKills;

	public DMGame() {
		//playerKills = new HashMap<UUID, Integer>();
	}

	@Override
	public String getName() {
		return "DeathMatch";
	}

	@Override
	public void preparePlayer(Player player) {
		//playerKills.put(player.getUniqueId(), 0);

		PlayerInventory pi = player.getInventory();
		pi.clear();

		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		pi.setItem(0, sword);

		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta im = bow.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Explosive Bow");
		bow.setItemMeta(im);
		pi.setItem(1, bow);

		ItemStack arrows = new ItemStack(Material.ARROW);
		arrows.setAmount(64);
		pi.setItem(2, arrows);

		ItemStack leatherHelmet = new ItemStack(Material.LEATHER_HELMET);
		ItemStack leatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack leatherLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack leatherBoots = new ItemStack(Material.LEATHER_BOOTS);

		pi.setArmorContents(new ItemStack[] { leatherHelmet, leatherChestplate,
				leatherLeggings, leatherBoots });

	}

	@Override
	public boolean playerDamagePlayer(Player attacker, Player victim) {
		return true;
	}

	@Override
	public void playerKilled(Player player) {

		if (!hasPlayer(player))
			return;

		//mygames.toLobby(player);
		//playerKills.remove(player);
		//removePlayer(player);

		Player killer = player.getKiller();
		if (killer != null) {
			if (killer.getUniqueId() == player.getUniqueId()) {
				this.removePoints(killer.getUniqueId(), 1);
			} else {
				this.addPoints(killer.getUniqueId(), 1);
			}
			//int kills = playerKills.get(killer.getUniqueId());
			//playerKills.put(killer.getUniqueId(), kills+1);
		}
		
		//Bukkit.broadcastMessage(player.getDisplayName() + " has perished.");

		return;
	}
	
	/*@Override
	public boolean shouldEnd(){
		return getPlayers().size() <= 1;
	}*/
	
	@Override
	public ArrayList<String> getWinners() {
		if (getPlayers().size() != 1)
			return null;

		ArrayList<String> winner = new ArrayList<String>();
		winner.add(Bukkit.getPlayer(getPlayers().get(0)).getName());
		return winner;
	}

	@Override
	public ArrayList<GameScore> getSideScores() {
		return orderHighest(getPlayerScores());
	}

	/*@Override
	public ArrayList<GameScore> getPlayerScores() {
		ArrayList<GameScore> s = new ArrayList<GameScore>();
		for (UUID uuid : this.playerKills.keySet()) {
			s.add(new GameScore(Bukkit.getPlayer(uuid).getName(),
					playerKills.get(uuid)));
		}
		return s;
	}*/

	@Override
	public void warmupTick() {
		
	}

	@Override
	public void tick() {
		
	}

	@Override
	public String[] getAliases() {
		return new String[]{"DM"};
	}

	@Override
	public DyeColor[] getSpawnColors() {
		return null;
	}

	@Override
	public WorldLocation[] getLocationTypes() {
		return null;
	}

	@Override
	public boolean doFallDamage() {
		return true;
	}

	@Override
	public boolean doDrownDamage() {
		return true;
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 8;
	}

	@Override
	public boolean allowJoinInProgress() {
		return false;
	}

	@Override
	public void prepareGame() {
	}


}

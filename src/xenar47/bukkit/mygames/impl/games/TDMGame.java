package xenar47.bukkit.mygames.impl.games;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Team;

import xenar47.bukkit.mygames.api.GameScore;
import xenar47.bukkit.mygames.api.TeamGame;
import xenar47.bukkit.mygames.world.location.WorldLocation;

public class TDMGame extends TeamGame {

	public TDMGame() {
		super(true, true);
	}

	@Override
	public String getName() {
		return "TeamDM";
	}

	@Override
	public void preparePlayer(Player player, Team team) {

		PlayerInventory pi = player.getInventory();
		pi.clear();

		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		pi.setItem(0, sword);

		ItemStack bow = new ItemStack(Material.BOW);
		//ItemMeta im = bow.getItemMeta();
		//im.setDisplayName(ChatColor.RED + "Explosive Bow");
		//bow.setItemMeta(im);
		pi.setItem(1, bow);

		ItemStack arrows = new ItemStack(Material.ARROW);
		arrows.setAmount(16);
		pi.setItem(2, arrows);
	}

	@Override
	public boolean playerDamagePlayer(Player attacker, Player victim) {
		return true;
	}

	@Override
	public void playerKilled(Player player) {
		
		if (!hasPlayer(player))
			return;
		
		this.addPoints(getTeam(player), 1);

	}
	
	/*@Override
	public boolean shouldEnd() {
		return getPopulatedTeams().size() <= 1;
	}*/

	@Override
	public ArrayList<GameScore> getSideScores() {
		
		ArrayList<Team> teams = getTeams();
		ArrayList<GameScore> scores = new ArrayList<GameScore>();
		
		for (Team team : teams) {
			scores.add(new GameScore(team.getDisplayName(), team.getSize()));
		}
		
		return scores;
	}

	@Override
	public ArrayList<GameScore> getPlayerScores() {
		return null;
	}

	@Override
	public ArrayList<String> getWinners() {
		ArrayList<String> winners = new ArrayList<String>();
		
		for (Team team : getPopulatedTeams()) {
			for (OfflinePlayer player : team.getPlayers()) {
				winners.add(player.getName());
			}
		}
		
		return winners;
	}

	@Override
	public void warmupTick() {
	}

	@Override
	public void tick() {
	}

	@Override
	public String[] getAliases() {
		return new String[]{"TDM", "TeamDeathMatch"};
	}

	@Override
	public WorldLocation[] getLocationTypes() {
		return null;
	}

	@Override
	public boolean doFallDamage() {
		return false;
	}

	@Override
	public boolean doDrownDamage() {
		return true;
	}

	@Override
	public int getMinPlayers() {
		return 4;
	}

	@Override
	public int getMaxPlayers() {
		return 24;
	}

	@Override
	public boolean allowJoinInProgress() {
		return false;
	}

	@Override
	public void prepareGame() {
	}

}

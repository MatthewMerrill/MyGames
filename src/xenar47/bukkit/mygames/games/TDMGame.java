package xenar47.bukkit.mygames.games;

import java.util.ArrayList;

import xenar47.bukkit.mygames.ScoreboardManager.GameScore;
import xenar47.bukkit.mygames.api.TeamGame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;

public class TDMGame extends TeamGame {

	public TDMGame() {
		super(true, true);
	}

	@Override
	public String getName() {
		return "TeamDeathMatch";
	}

	@Override
	public void preparePlayer(Player player, Team team) {

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
	}

	@Override
	public boolean playerDamagePlayer(Player attacker, Player victim) {
		return true;
	}

	@Override
	public void playerKilled(Player player) {
		
		if (!hasPlayer(player))
			return;

		mygames.toLobby(player);
		removePlayer(player);
		
		Bukkit.broadcastMessage(player.getDisplayName() + " has perished.");
	}
	
	@Override
	public boolean shouldEnd() {
		if (getPopulatedTeams().size() == 1) {// if (playerLives.size() <= 1) {
			if (getPopulatedTeams().size() == 1) {
				Bukkit.broadcastMessage(ChatColor.BLUE
						+ Bukkit.getPlayer(getPlayers().get(0))
								.getDisplayName() + ChatColor.GRAY
						+ " takes the victory!");
				return true;
			} else if (getPopulatedTeams().size() <= 0) {
				Bukkit.broadcastMessage(ChatColor.GRAY
						+ "All participants have died... No Winner!");
				return true;
			}
		}

		return false;
	}

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
	public ArrayList<Player> getWinners() {
		ArrayList<Player> winners = new ArrayList<Player>();
		
		for (Team team : getPopulatedTeams()) {
			for (OfflinePlayer player : team.getPlayers()) {
				winners.add(Bukkit.getPlayer(player.getUniqueId()));
			}
		}
		
		return winners;
	}

	@Override
	public void warmupTick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

}

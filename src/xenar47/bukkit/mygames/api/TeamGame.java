package xenar47.bukkit.mygames.api;

import java.util.ArrayList;
import java.util.HashMap;

import xenar47.bukkit.mygames.*;
import xenar47.bukkit.mygames.ScoreboardManager.GameScore;
import xenar47.bukkit.mygames.world.WorldConfigManager.LOCATIONS;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Team;

public abstract class TeamGame extends Game {

	Team red;
	Team blue;
	Team green;
	Team yellow;

	HashMap<String, Color> teamColors;
	HashMap<String, Integer> teamPoints;

	private boolean autoAssign;
	private boolean autoArmor;

	public TeamGame(boolean autoAssign, boolean autoArmor) {
		teamColors = new HashMap<String, Color>();

		red = sbm.getTeam("RED");
		teamColors.put(red.getName(), Color.RED);

		blue = sbm.getTeam("BLUE");
		teamColors.put(blue.getName(), Color.BLUE);

		green = sbm.getTeam("GREEN");
		teamColors.put(green.getName(), Color.GREEN);

		yellow = sbm.getTeam("YELLOW");
		teamColors.put(yellow.getName(), Color.YELLOW);
	}
	
	@Override
	public Location getSpawnLocation(Player player){
		Team team = this.getTeam(player);
		LOCATIONS l = LOCATIONS.RED;
		
		if (team != null) {
			if (team.getName() == blue.getName()) {
				l = LOCATIONS.BLUE;
			} else if (team.getName() == green.getName()) {
				l = LOCATIONS.GREEN;
			} else if (team.getName() == yellow.getName()) {
				l = LOCATIONS.YELLOW;
			}
		}
		
		return TeamWorld.getLocation(mygames.getWorldMgr(), world, l);
	}

	/**
	 * For Team-based games, use preparePlayer(Player, Team).
	 */
	@Override
	public final void preparePlayer(Player player) {
		if (autoAssign) {
			Team team = getSmallestTeam();
			setTeam(player, team);
			preparePlayer(player, team);
		} else {
			preparePlayer(player, null);
		}
	}

	/**
	 * If you are assigning teams, make sure to use .setTeam(Player player, Team
	 * team).
	 * 
	 * @param player
	 *            = player to prepare.
	 * @param team
	 *            = assigned team. Will be null if autoAssign = false.
	 */
	public abstract void preparePlayer(Player player, Team team);

	public void setTeam(Player player, Team team) {
		if (!team.getPlayers().contains(player))
			team.addPlayer(player);

		if (autoArmor) {
			PlayerInventory pi = player.getInventory();
			pi.clear();

			Wool w = new Wool();
			w.setColor(DyeColor.getByColor(getTeamColor(team)));
			pi.setHelmet(w.toItemStack());

			ItemStack lChest = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta lam = (LeatherArmorMeta) lChest.getItemMeta();
			lam.setColor(getTeamColor(team));
			lChest.setItemMeta(lam);
			pi.setChestplate(lChest);

			ItemStack lLegs = new ItemStack(Material.LEATHER_LEGGINGS);
			lam = (LeatherArmorMeta) lLegs.getItemMeta();
			lam.setColor(getTeamColor(team));
			lLegs.setItemMeta(lam);
			pi.setLeggings(lLegs);

			ItemStack lBoots = new ItemStack(Material.LEATHER_BOOTS);
			lam = (LeatherArmorMeta) lBoots.getItemMeta();
			lam.setColor(getTeamColor(team));
			lBoots.setItemMeta(lam);
			pi.setBoots(lBoots);
		}
	}

	public ArrayList<Team> getTeams() {
		ArrayList<Team> teams = new ArrayList<Team>();

		teams.add(red);
		teams.add(blue);
		teams.add(green);
		teams.add(yellow);

		return teams;
	}

	public ArrayList<Team> getPopulatedTeams() {
		ArrayList<Team> teams = new ArrayList<Team>();
		if (red.getSize() > 0)
			teams.add(red);
		if (blue.getSize() > 0)
			teams.add(blue);
		if (green.getSize() > 0)
			teams.add(green);
		if (yellow.getSize() > 0)
			teams.add(yellow);
		return teams;
	}
	
	public Team getTeam(Player player) {
		ArrayList<Team> teams = getPopulatedTeams();
		
		for (Team team : teams){ 
			for (OfflinePlayer p : team.getPlayers()) {
				if (p.getUniqueId() == player.getUniqueId())
						return team;
			}
		}
		
		return null;
	}

	public Team getSmallestTeam() {
		int minimum = Math.min(Math.min(red.getSize(), blue.getSize()),
				Math.min(green.getSize(), yellow.getSize()));

		if (red.getSize() == minimum)
			return red;
		else if (blue.getSize() == minimum)
			return blue;
		else if (green.getSize() == minimum)
			return green;
		else
			return yellow;
	}

	public void setTeamColor(Team team, Color color) {
		teamColors.put(team.getName(), color);
	}

	public Color getTeamColor(Team team) {
		return teamColors.get(team.getName());
	}

	public void setTeamPoints(Team team, int points) {
		teamPoints.put(team.getName(), points);
	}

	public int getTeamPoints(Team team) {
		return teamPoints.get(team.getName());
	}
	
	public ArrayList<GameScore> getTeamPoints() {
		ArrayList<GameScore> scores = new ArrayList<GameScore>();
		for (String key : teamPoints.keySet()) {
			scores.add(new GameScore(key, teamPoints.get(key)));
		}
		return scores;
	}

	public void setAutoAssign(boolean autoAssign) {
		this.autoAssign = autoAssign;
	}

	public void setAutoArmor(boolean autoArmor) {
		this.autoArmor = autoArmor;
	}

	public boolean getAutoAssign() {
		return autoAssign;
	}

	public boolean getAutoArmor() {
		return autoArmor;
	}
}

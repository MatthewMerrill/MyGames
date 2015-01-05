package xenar47.bukkit.mygames.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Team;

import xenar47.bukkit.mygames.world.location.SpawnLocation;

public abstract class TeamGame extends Game implements Listener {

	Team red = null;
	Team blue = null;
	Team green = null;
	Team yellow = null;

	HashMap<String, DyeColor> teamColors;
	HashMap<String, Integer> teamPoints;

	private boolean autoAssign;
	private boolean autoHelmet;

	public TeamGame(boolean autoAssign, boolean autoHelmet) {
		this.autoAssign = autoAssign;
		this.autoHelmet = autoHelmet;
		
		createTeams(true, true, true, true);
	}
	
	public TeamGame(boolean autoAssign, boolean autoHelmet, boolean useRed, boolean useBlue, boolean useGreen, boolean useYellow) {
		this.autoAssign = autoAssign;
		this.autoHelmet = autoHelmet;
		
		createTeams(useRed, useBlue, useGreen, useYellow);
	}
	
	private Team getTeam(String name){
		if (sbm.scoreboard.getTeam(name) == null)
			sbm.scoreboard.registerNewTeam(name);
		
		return sbm.scoreboard.getTeam(name);
	}
	
	private void createTeams(boolean useRed, boolean useBlue, boolean useGreen, boolean useYellow) {
		//TODO: Let colors be defined by the map or something. This is a poor way of doing this.
		
		teamColors = new HashMap<String, DyeColor>();
		if (useRed) {
			red = getTeam("RED");
			teamColors.put(red.getName(), DyeColor.RED);
		}

		if (useBlue) {
			blue = getTeam("BLUE");
			teamColors.put(blue.getName(), DyeColor.BLUE);
		}

		if (useGreen) {
			green = getTeam("GREEN");
			teamColors.put(green.getName(), DyeColor.GREEN);
		}

		if (useYellow) {
			yellow = getTeam("YELLOW");
			teamColors.put(yellow.getName(), DyeColor.YELLOW);
		}
	}
	
	@Override
	public DyeColor[] getSpawnColors() {
		return new DyeColor[]{DyeColor.RED, DyeColor.BLUE, DyeColor.GREEN, DyeColor.YELLOW};
	}
	
	@Override
	public Location getSpawnLocation(Player player){
		Team team = this.getTeam(player);
		
		try {
			SpawnLocation l = new SpawnLocation(teamColors.get(team.getName()));
			return mygames.getWorldMgr().getLocation(world, l.configKey());
		} catch (Exception e) {
			mygames.getLogger().severe("Unable to find spawns for TeamGame \"" + getName() + "\" in world \"" + world.getName() + "\"? Giving random spawn.");
			return mygames.getWorldMgr().getRandomSpawn(world);
		}
	}

	/**
	 * For Team-based games, use preparePlayer(Player, Team).
	 */
	@Override
	public final void preparePlayer(Player player) {
		if (autoAssign) {
			Team team = getSmallestTeam();
			preparePlayer(player, team);
			setTeam(player, team);
		} else {
			preparePlayer(player, null);
		}
	}

	/**
	 * Sets player's class.
	 * If you are assigning teams, make sure to use .setTeam(Player player, Team
	 * team).
	 * 
	 * @param player
	 *= player to prepare.
	 * @param team
	 *= assigned team. Will be null if autoAssign = false.
	 */
	public void preparePlayer(Player player, Team team) {
		super.preparePlayer(player);
	}

	public void setTeam(Player player, Team team) {
		if (!team.getPlayers().contains(player))
			team.addPlayer(player);

		if (autoHelmet) {
			PlayerInventory pi = player.getInventory();
			pi.clear();

			Wool w = new Wool();
			w.setColor(getTeamDyeColor(team));

			pi.setHelmet(w.toItemStack());
		}
	}

	public ArrayList<Team> getTeams() {
		ArrayList<Team> teams = new ArrayList<Team>();

		if (red != null)
			teams.add(red);
		if (blue != null)
			teams.add(blue);
		if (green != null)
			teams.add(green);
		if (yellow != null)
			teams.add(yellow);

		return teams;
	}

	public ArrayList<Team> getPopulatedTeams() {
		ArrayList<Team> teams = new ArrayList<Team>();
		for (Team team : getTeams()) {
			if (team != null && team.getSize() > 0)
				teams.add(team);
		}
		return teams;
	}
	
	public Team getTeam(OfflinePlayer offlinePlayer) {
		ArrayList<Team> teams = getPopulatedTeams();
		
		for (Team team : teams){ 
			for (OfflinePlayer p : team.getPlayers()) {
				if (p.getUniqueId() == offlinePlayer.getUniqueId())
						return team;
			}
		}
		
		return null;
	}

	public Team getSmallestTeam() {
		Team smallest = null;
		for (Team team : getTeams()) {
			if (smallest == null) {
				smallest = team;
				continue;
			}
			if (team.getSize() < smallest.getSize())
				smallest = team;
		}
		return smallest;
	}

	public void setTeamDyeColor(Team team, DyeColor color) {
		teamColors.put(team.getName(), color);
	}

	public DyeColor getTeamDyeColor(Team team) {
		return teamColors.get(team.getName());
	}
	
	public Color getTeamColor(Team team) {
		return teamColors.get(team.getName()).getColor();
	}

	public void setPoints(Team team, int points) {
		teamPoints.put(team.getName(), points);
	}
	
	/**
	 * Adding a negative will remove.
	 */
	public void addPoints(Team team, int points) {
		setPoints(team, getPoints(team) + points);
	}
	
	/**
	 * Removing a negative does not add.
	 */
	public void removePoints(Team team, int points) {
		setPoints(team, getPoints(team) - Math.abs(points));
	}

	public int getPoints(Team team) {
		return teamPoints.get(team.getName());
	}
	
	public ArrayList<GameScore> getTeamPoints() {
		ArrayList<GameScore> scores = new ArrayList<GameScore>();
		for (String key : teamPoints.keySet()) {
			scores.add(new GameScore(key, teamPoints.get(key)));
		}
		return scores;
	}
	
	@Override
	public boolean playerDamagePlayer(Player attacker, Player victim) {
		Team team = getTeam(attacker);
		
		if (team.allowFriendlyFire())
			return true;
		
		return (team.hasPlayer(victim));
	}

	public void setAutoAssign(boolean autoAssign) {
		this.autoAssign = autoAssign;
	}

	public void setAutoArmor(boolean autoArmor) {
		this.autoHelmet = autoArmor;
	}

	public boolean getAutoAssign() {
		return autoAssign;
	}

	public boolean getAutoArmor() {
		return autoHelmet;
	}
	
}

package xenar47.bukkit.mygames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import xenar47.bukkit.mygames.api.Game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager {

	static org.bukkit.scoreboard.ScoreboardManager manager = Bukkit
			.getScoreboardManager();

	public static void remove(OfflinePlayer player) {
		Player online = Bukkit.getPlayer(player.getUniqueId());
		if (online != null) {
			online.setScoreboard(manager.getNewScoreboard());
		}
	}

	public static void waitingList(Player player, GameManager gm) {
		Scoreboard scoreboard = manager.getNewScoreboard();

		Objective obj = scoreboard.registerNewObjective("waitingList", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(ChatColor.GRAY + "Waiting List:");

		obj.getScore(player.getName())
				.setScore(gm.getPlayers().indexOf(player.getUniqueId()) + 1);

		player.setScoreboard(scoreboard);
	}

	MyGames plugin;
	HashMap<String, Team> teams;
	Scoreboard board;
	Game game;

	public ScoreboardManager(MyGames plugin, Game game) {
		teams = new HashMap<String, Team>();
		board = manager.getNewScoreboard();

		this.plugin = plugin;
		this.game = game;
	}

	private boolean visible = false;

	public void show() {
		for (UUID id : game.getPlayers()) {
			Player player = Bukkit.getPlayer(id);
			player.setHealth(player.getHealth()); // Update their health
			player.setScoreboard(board);
		}
		visible = true;

		update();
	}

	public void hide() {
		for (UUID id : game.getPlayers()) {
			removeEntry(id);
		}
		visible = false;
		
		for (DisplaySlot ds : DisplaySlot.values())
			board.clearSlot(ds);
	}

	public void update() {

		if (visible) {
			
			ArrayList<GameScore> scores = game.getSideScores();
			
			if (game.isWarmup()) {
				scores = new ArrayList<GameScore>();
				scores.add(new GameScore(ChatColor.YELLOW + "Warmup", game.getWarmupTime() - game.getSecondsPlayed()));
			}
			
			board.clearSlot(DisplaySlot.SIDEBAR);
			if (scores != null && scores.size() > 0) {
				
				Objective o = board.getObjective("scores");
				
				if (o != null) {
					o.unregister();
				}
				
				o = board.registerNewObjective("scores", "dummy");
				
				o.setDisplaySlot(DisplaySlot.SIDEBAR);
				o.setDisplayName(ChatColor.DARK_AQUA + game.getName());
				
				for (int i = scores.size()-1; i >= 0; i--) {
					GameScore score = scores.get(i);
					o.getScore(score.name).setScore(score.score);
				}
			}

			for (UUID id : game.getPlayers()) {
				Player player = Bukkit.getPlayer(id);
				//player.setHealth(player.getHealth());
				player.setScoreboard(board);
			}
		}
	}

	public Scoreboard getBoard() {
		return board;
	}

	public Team getTeam(String name) {
		if (board.getTeam(name) != null)
			return teams.get(name);

		Team team = board.registerNewTeam(name);
		teams.put(name, team);
		return team;
	}

	public void showHealth() {
		Objective health = board.registerNewObjective("showhealth", "health");
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		health.setDisplayName("/ 20");

		for (UUID id : game.getPlayers()) {
			Player player = Bukkit.getPlayer(id);
			player.setHealth(player.getHealth()); // Update their health
		}

		update();
	}

	public void hideHealth() {
		Objective health = board.getObjective("health");
		if (health != null)
			health.unregister();

		update();
	}
/*
	public void showTeamPoints() {

		if (!(game instanceof TeamGame))
			return;

		TeamGame teamGame = (TeamGame) this.game;

		Objective teamPoints = board
				.registerNewObjective("scores", "dummy");
		teamPoints.setDisplayName("Team Points:");
		teamPoints.setDisplaySlot(DisplaySlot.SIDEBAR);

		for (Team team : board.getTeams()) {
			teamPoints.getScore(team.getDisplayName()).setScore(
					teamGame.getTeamPoints(team));
		}

		update();
	}
	*/
	
	public void setScores(ArrayList<GameScore> scores){
		for (GameScore score : scores) {
			board.getObjective("scores").getScore(score.name).setScore(score.score);
		}
	}

	public void removeEntry(UUID id) {
		board.resetScores(Bukkit.getPlayer(id).getName());
		remove(Bukkit.getOfflinePlayer(id));

		Team playerTeam = board.getPlayerTeam(Bukkit.getOfflinePlayer(id));
		if (playerTeam != null) {
			playerTeam.removePlayer(Bukkit.getOfflinePlayer(id));
		}

		update();
	}
	
	public static class GameScore {
		public String name;
		public int score;
		
		public GameScore(String name, int score) {
			this.name = name;
			this.score = score;
		}
	}
}

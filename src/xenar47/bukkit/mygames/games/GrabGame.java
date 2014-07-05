package xenar47.bukkit.mygames.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import xenar47.bukkit.mygames.ScoreboardManager.GameScore;
import xenar47.bukkit.mygames.api.Game;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GrabGame extends Game {

	private HashMap<UUID, Integer> scores;
	
	private UUID it = null;

	@Override
	public String getName() {
		return "Grab";
	}

	/**
	 * @param mygames
	 */
	public GrabGame() {
		scores = new HashMap<UUID, Integer>();
		
	}

	@Override
	public void preparePlayer(Player player) {
		player.getInventory().clear();

		Wool w = new Wool();
		w.setColor(DyeColor.RED);

		player.getInventory().setHelmet(w.toItemStack());
	}

	public void prepareIt(Player player) {
		player.getInventory().clear();

		Wool w = new Wool();
		w.setColor(DyeColor.BLUE);

		player.getInventory().setHelmet(w.toItemStack());
	}

	@Override
	public void prepareGame() {
		super.prepareGame();

		Random r = new Random();
		int index = r.nextInt(getPlayers().size());
		Player p = Bukkit.getPlayer(getPlayers().get(index));
		setIt(p);
	}

	@Override
	public void warmupTick() {
		for (UUID uuid : getPlayers()) {
			if (uuid == it)
				continue;
			
			Player p = Bukkit.getPlayer(uuid);
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 255), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 400, 255), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 400, 255), true);
		}

		Player p = Bukkit.getPlayer(it);
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, 3), true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 2), true);
	}

	@Override
	public void tick() {

		int score = 0;
		if (scores.containsKey(it))
			score = scores.get(it);
		scores.put(it, score + getSecsPerTick());
		
		for (UUID uuid : getPlayers()) {
			if (uuid == it)
				continue;
			Player p = Bukkit.getPlayer(uuid); 
			
			p.removePotionEffect(PotionEffectType.BLINDNESS);
			p.removePotionEffect(PotionEffectType.CONFUSION);
			p.removePotionEffect(PotionEffectType.SLOW);
		}
		
		Player p = Bukkit.getPlayer(it);
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, 3), false);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 2), false);
	}
	
	public void setIt(Player player) {

		if (it != null) {
			Player oldIt = Bukkit.getPlayer(it);
			preparePlayer(oldIt);
		}

		it = player.getUniqueId();
		prepareIt(player);
	}

	@Override
	public boolean playerDamagePlayer(Player attacker, Player victim) {

		if (it == victim.getUniqueId() && (!this.isWarmup())) {
			setIt(attacker);
		}

		return false;
	}

	@Override
	public void playerKilled(Player player) {
		return;
	}

	@Override
	public ArrayList<Player> getWinners(){
		return lowestScorers(scores);
	}
	
	@Override
	public ArrayList<GameScore> getSideScores() {
		return orderHighest(getPlayerScores());
	}

	@Override
	public ArrayList<GameScore> getPlayerScores() {
		ArrayList<GameScore> s = new ArrayList<GameScore>();
		for (UUID uuid : this.scores.keySet()) {
			s.add(new GameScore(Bukkit.getPlayer(uuid).getName(), scores.get(uuid)));
		}
		return s;
	}

	@Override
	public boolean shouldEnd() {
		return false;
	}
}

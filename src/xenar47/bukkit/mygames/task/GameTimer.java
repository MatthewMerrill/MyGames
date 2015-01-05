package xenar47.bukkit.mygames.task;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import xenar47.bukkit.mygames.MyGames;
import xenar47.bukkit.mygames.api.Game;

public class GameTimer implements Runnable {
	
	private final MyGames mygames;
	private final Game game;
	
	private boolean running = false;
	private BukkitTask task;
	
	private boolean showTime = false;
	private int ticksSinceToggle = 0;
	
	private int tickDelay;
	private int ticks = 0;
	
	boolean weaponsGiven;
	
	public GameTimer(MyGames mygames, Game game) {
		this.mygames = mygames;
		this.game = game;
		
		tickDelay = (int)(20 * game.getSecsPerTick());
	}
	
	public void start() {
		ticks = 0;
		if (!running) {
			task = Bukkit.getScheduler().runTaskTimer(mygames, this, 0, tickDelay);
			running = true;
		}
	}
	
	/**
	 * Do not call unless you know what you are doing
	 */
	public void stop() {
		if (running) {
			Bukkit.getScheduler().cancelTask(task.getTaskId());
			this.running = false;
		}
	}
	
	/**
	 * Same as stop()
	 */
	public void cancel() {
		stop();
	}
	
	@Override
	public void run() {
		ticks += tickDelay;
		
		int secondsPassed = (ticks / 20) - game.getWarmupTime();
		if (secondsPassed < 0) {
			game.warmupTick();
		} else if (secondsPassed < game.getPlayTime()){
			if (!weaponsGiven) {
				for (UUID uuid : game.getPlayers())
					game.preparePlayer(Bukkit.getPlayer(uuid));
				weaponsGiven = true;
			}
			
			ticksSinceToggle += tickDelay;
			if (ticksSinceToggle >= 20) {
				showTime = !showTime;
				ticksSinceToggle = 0;
			}
			
			if (showTime)
				game.getSidebarManager().timeRemaining(game.getPlayTime() - secondsPassed);
			else
				game.getSidebarManager().scores();
			
			game.tick();
		} else {
			stop();
			game.stopGame();
		}
		
		game.updateEffects();
		//game.updateScores();
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isWarmup() {
		return ((ticks / 20) > game.getWarmupTime());
	}
}

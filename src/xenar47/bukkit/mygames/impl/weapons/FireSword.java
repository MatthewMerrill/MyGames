package xenar47.bukkit.mygames.impl.weapons;

import xenar47.bukkit.mygames.api.Game;
import xenar47.bukkit.mygames.api.Weapon;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FireSword extends Weapon {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "FireSword";
	}

	@Override
	public boolean primary(Game game, Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean secondary(Game game, Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int melee(Game game, Player player, Player victim) {
		victim.setFireTicks(20 * 5);
		return 0;
	}

	@Override
	public boolean interact(Game game, Player player, Entity target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reload(Game game, Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemStack getBaseItem() {
		return new ItemStack(Material.DIAMOND_SWORD);
	}

	@Override
	public void registerNecessaryListeners() {
	}

}

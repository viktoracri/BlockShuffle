package dev.acri.blockshuffle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BlockShuffle extends JavaPlugin{
	
	private static BlockShuffle instance;
	
	public BukkitTask runnable;
	public boolean blockShuffleEnabled = false;
	
	private Random random = new Random();
	
	private long previousShuffle = -1;
	private final int SHUFFLE_TIME_MINUTES = 5;
	//private int previousCountdown = -1;
	
	private boolean countdownEnabled = false;
	private BukkitRunnable countdown = new BukkitRunnable() {
		int count = 10;
		@Override
		public void run() {
			if(count < 1)this.cancel();
			if(!countdownEnabled)this.cancel();
			Bukkit.broadcastMessage("§c§lYou have " + count + " seconds to stand on your block");
			count--;
		};
	};
	
	public HashMap<Player, PlayerState> playersInGame = new HashMap<Player, PlayerState>();
	
	public List<Material> allowedMaterials = new ArrayList<Material>(Arrays.asList( new Material[]{
			Material.COBBLESTONE,
			Material.STONE,
			Material.GRASS_BLOCK,
			Material.DIRT,
			Material.OAK_LOG,
			Material.OAK_LEAVES,
			Material.OAK_WOOD,
			Material.SPRUCE_LOG,
			Material.SPRUCE_LEAVES,
			Material.SPRUCE_WOOD,
			Material.BIRCH_LOG,
			Material.BIRCH_LEAVES,
			Material.BIRCH_WOOD,
			Material.SNOW_BLOCK,
			Material.SAND,
			Material.STONE_BRICKS,
			Material.CRACKED_STONE_BRICKS,
			Material.BRICKS,
			Material.MELON,
			Material.OBSIDIAN,
			Material.BOOKSHELF,
			Material.SANDSTONE,
			Material.COAL_BLOCK,
			Material.SOUL_SAND,
			Material.NETHER_BRICKS,
			Material.NETHERRACK,
			Material.GLOWSTONE,
			Material.DIAMOND_ORE,
			Material.GOLD_ORE,
			Material.IRON_ORE,
			Material.COAL_ORE,
			Material.TNT,
			Material.DISPENSER,
			Material.NOTE_BLOCK,
			Material.REDSTONE_BLOCK,
			Material.HOPPER,
			Material.COMPOSTER,
			Material.BEDROCK,
			Material.STONE_SLAB,
			Material.COBBLESTONE_SLAB,
			Material.STONE_BRICK_SLAB,
			Material.GLASS,
			Material.PISTON,
			Material.WATER,
			Material.LAVA,
			Material.HAY_BLOCK,
			Material.BELL,
			Material.BLACK_WOOL,
			Material.BLUE_WOOL,
			Material.CYAN_WOOL,
			Material.RED_WOOL,
			Material.YELLOW_WOOL,
			Material.GREEN_WOOL,
			Material.LIME_WOOL,
			Material.WHITE_WOOL,
			Material.MAGENTA_WOOL,
			Material.ORANGE_WOOL
	}));
	
	
	@Override
	public void onEnable() {
		BlockShuffle.instance = this;
		this.getCommand("blockshuffle").setExecutor(new CommandBlockShuffle());
		this.getCommand("blockshuffle").setTabCompleter(new CommandBlockShuffle());
		for(Material mat : Material.values()) {
			if(mat.name().startsWith("OAK") && mat.name().contains("BOAT"))
				allowedMaterials.add(mat);
			else if(mat.name().contains("DIORITE"))
				allowedMaterials.add(mat);
			else if(mat.name().contains("ANDESITE"))
				allowedMaterials.add(mat);
			else if(mat.name().contains("GRANITE"))
				allowedMaterials.add(mat);
		}

		startRunnable();
		
		
		
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	
	public void startRunnable() {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(blockShuffleEnabled) {
					if(System.currentTimeMillis() - previousShuffle > SHUFFLE_TIME_MINUTES * 60000){
						// When a new round starts
						previousShuffle = System.currentTimeMillis();
						
						List<Player> failed = new ArrayList<Player>();
						for(Player all : playersInGame.keySet()) {
							if(!playersInGame.get(all).hasFound) {
								// If a player fails to find their block
								Bukkit.broadcastMessage("§4§l" + all.getName() + " failed to find their block!");
								failed.add(all);
							}
						}
						
						for(Player p : failed) playersInGame.remove(p);
						
						if(playersInGame.size() == 1 && Bukkit.getOnlinePlayers().size() > 1) {
							blockShuffleEnabled = false;
							for(Player p : playersInGame.keySet()) {
								Bukkit.broadcastMessage("§a§l" + p.getName() + " won BlockShuffle!");
								break;
							}
							return;
						}else if(playersInGame.size() == 0) {
							Bukkit.broadcastMessage("§4§lNobody managed to find their block. Game over!");
							blockShuffleEnabled = false;
						}
						
						
						
						
						for(Player all : playersInGame.keySet()) {
							Material mat = generateNewMaterial();
							playersInGame.get(all).setMaterial(mat);
							playersInGame.get(all).setFound(false);
							
							
							
							all.sendMessage("§aYou must find and stand on a §b" + mat.name().toLowerCase().replaceAll("_", " "));
							
					
						}
					}else if(System.currentTimeMillis() - previousShuffle > (SHUFFLE_TIME_MINUTES * 60000) - 10000 && !countdownEnabled) {
						startCountdown();
						countdownEnabled = true;
					}
					
					boolean found = true;
					for(Player all : playersInGame.keySet()) {
						if(!playersInGame.get(all).hasFound)
							if(all.getLocation().add(0, -0.75, 0).getBlock().getType() == playersInGame.get(all).getMaterial()) {
								// When their block is found
								playersInGame.get(all).setFound(true);
								Bukkit.broadcastMessage("§6§l" + all.getName() + " has found their block!");
								all.playSound(all.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
							}else
								found = false;
					}
					
					if(found) {
						previousShuffle = -1;
						countdownEnabled = false;
					}
					
					
					
				}
			}}.runTaskTimer(this, 0, 5);
	}
	
	public void startGame() {
		playersInGame.clear();
		for(Player all : Bukkit.getOnlinePlayers())
			playersInGame.put(all, new PlayerState());
		blockShuffleEnabled = true;
		previousShuffle = -1;
	}
	
	public void startCountdown() {
		
		countdown.runTaskTimer(this, 0, 20);
		
	}
	
	public Material generateNewMaterial() {
		
		
		return allowedMaterials.get(random.nextInt(allowedMaterials.size() - 1));
	}
	
	public static BlockShuffle getInstance() {
		return BlockShuffle.instance;
	}
	
	public class PlayerState{
		Material material = null;
		boolean hasFound = true;
		
		
		public void setMaterial(Material mat) {
			this.material = mat;
		}
		
		public void setFound(boolean found) {
			this.hasFound = found;
		}
		
		public Material getMaterial() {
			return this.material;
		}
		
		public boolean hasFound() {
			return this.hasFound;
		}

		
		
	}
}

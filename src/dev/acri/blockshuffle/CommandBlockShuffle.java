package dev.acri.blockshuffle;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandBlockShuffle implements TabCompleter, CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!sender.hasPermission("blockshuffle.admin")) {
			sender.sendMessage("§4You do not have permission.");
			return true;
		}
		
		if(BlockShuffle.getInstance().blockShuffleEnabled) {
			BlockShuffle.getInstance().blockShuffleEnabled = false;
			BlockShuffle.getInstance().playersInGame.clear();
			Bukkit.broadcastMessage("§8[§6§lB§e§lS§8] §aBlockShuffle has been disabled");
			
		}else {
			BlockShuffle.getInstance().startGame();
			Bukkit.broadcastMessage("§8[§6§lB§e§lS§8] §aBlockShuffle has been enabled");
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		
		return new ArrayList<String>();
	}
}

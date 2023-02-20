package me.dkz.artemis.plugins.command;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import me.dkz.artemis.plugins.ArtemisArmazem;
import me.dkz.artemis.plugins.inventory.StorageInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StorageCommand implements CommandExecutor {
    private final ArtemisArmazem plugin = ArtemisArmazem.getInstance();
    private final PlotAPI plotAPI = plugin.getPlotAPI();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage("§cApenas jogadores podem executar este comando.");
            return false;
        }

        Player player = (Player) sender;
        if(!player.hasPermission("artemis.armazem")){
            player.sendMessage("§cVocê não tem permissão para executar este comando.");
            return false;
        }

        if(plotAPI.getPlot(player.getLocation()) == null){
            player.sendMessage("§cVocê não está em um plot.");
            return false;
        }

        Plot plot = plotAPI.getPlot(player.getLocation());

        if(!plot.isAdded(player.getUniqueId())){
            player.sendMessage("§cVocê não é membro deste plot.");
            return false;
        }


        StorageInventory storageInventory = new StorageInventory(plot);
        storageInventory.openInventory(player);


        return false;
    }
}

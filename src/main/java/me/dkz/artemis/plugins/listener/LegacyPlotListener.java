package me.dkz.artemis.plugins.listener;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import me.dkz.artemis.plugins.ArtemisArmazem;
import me.dkz.artemis.plugins.storage.PlotItem;
import me.dkz.artemis.plugins.storage.PlotStorage;
import me.lemonypancakes.bukkit.api.actionbar.ActionBarAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class LegacyPlotListener implements Listener {
    private ArtemisArmazem plugin = ArtemisArmazem.getInstance();
    private PlotStorage plotStorage = plugin.getPlotStorage();

    private PlotAPI plotAPI = plugin.getPlotAPI();


    @EventHandler
    void onItemSpawn(ItemSpawnEvent e) {

        ItemStack stack = e.getEntity().getItemStack();
        String material = stack.getType().toString().toUpperCase();
        if (plotStorage.getPlotItem(material) == null) return;
        Location location = e.getLocation();
        if (plotAPI.getPlot(location) == null) return;
        Plot plot = plotAPI.getPlot(location);
        if (!plot.hasOwner()) return;
        System.out.println("Has owner");
        int amount = stack.getAmount();

        UUID plotId = plot.getOwners().stream().findFirst().get();
        PlotItem plotItem = plotStorage.getPlotItem(material);
        plotStorage.addToStorage(plotId, material, amount);
        e.setCancelled(true);
        if(plugin.getConfig().getBoolean("ActionBar.Ativo")) {sendActionText(plot, plotItem, amount);}


    }

    private void sendActionText(Plot plot, PlotItem plotItem, int amount) {
        plot.getOwners().forEach(uuid -> {
            if (plugin.getServer().getPlayer(uuid) != null) {
                Player player = plugin.getServer().getPlayer(uuid);
                if (!plotStorage.getActionBarMessage().containsKey(player)) plotStorage.getActionBarMessage().put(player, amount);
                plotStorage.getActionBarMessage().put(player, plotStorage.getActionBarMessage().get(player) + amount);
                String message = plugin.getConfig().getString("ActionBar.Mensagem")
                        .replaceAll("@quantidade", String.valueOf(amount))
                        .replaceAll("@item", plotItem.getName())
                        .replaceAll("&", "§");
               ActionBarAPI.sendMessage(player, message);
            }
        });
    }

}

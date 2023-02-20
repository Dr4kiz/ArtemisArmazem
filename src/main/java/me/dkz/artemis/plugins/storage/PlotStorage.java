package me.dkz.artemis.plugins.storage;

import com.intellectualcrafters.plot.api.PlotAPI;
import me.dkz.artemis.plugins.ArtemisArmazem;
import me.dkz.artemis.plugins.dao.SQLite;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlotStorage {

    private final ArtemisArmazem plugin = ArtemisArmazem.getInstance();
    private final PlotAPI plotAPI = plugin.getPlotAPI();
    private final SQLite sqLite = plugin.getSQLite();

    private final HashMap<Player, Integer> actionBarMessage = new HashMap<>();

    private final HashMap<UUID, Set<PlotItem>> plotList = new HashMap<>();

    {
        if(plugin.getConfig().getBoolean("ActionBar.Ativo")){
            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, actionBarMessage::clear, 0L, 20L*4);
        }
    }

    public Set<PlotItem> getPlotItems(UUID uuid){
        if(!plotList.containsKey(uuid)) plotList.put(uuid, getDefaultItems());
        return plotList.get(uuid);
    }

    private Set<PlotItem> getDefaultItems() {
        Set<PlotItem> plotItems = new HashSet<>();

        plugin.getConfig().getConfigurationSection("Itens").getKeys(false).forEach(item ->{
            String path = "Itens."+item;
            PlotItem plotItem = PlotItem.PlotItemBuilder.builder()
                    .name(plugin.getConfig().getString(path+".Nome"))
                    .lore(plugin.getConfig().getStringList(path+".Desc"))
                    .type(item)
                    .amount(0)
                    .build();
            plotItems.add(plotItem);
        });
        return plotItems;
    }


    public PlotItem getPlotItem(String type){
        if(validItem(type)) return null;
        return getDefaultItems().stream().filter(item -> item.getType().equalsIgnoreCase(type)).findFirst().get();
    }

    public void save(){
        plotList.forEach((uuid, items) ->{
            if(uuid == null || items == null) return;
            sqLite.save(uuid, items);
        });
    }

    public boolean validItem(String material){
        return plugin.getConfig().getConfigurationSection("Itens").getKeys(false).contains(material);
    }

    public HashMap<Player, Integer> getActionBarMessage() {
        return actionBarMessage;
    }

    public void addToStorage(UUID plotId, String material, int amount) {
        PlotItem plotItem = getPlotItems(plotId).stream().filter(p -> p.getType().equals(material)).findFirst().get();
        plotItem.add(amount);
    }


}

package me.dkz.artemis.plugins;

import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.intellectualcrafters.plot.api.PlotAPI;
import me.dkz.artemis.plugins.command.StorageCommand;
import me.dkz.artemis.plugins.dao.SQLite;
import me.dkz.artemis.plugins.listener.LegacyPlotListener;
import me.dkz.artemis.plugins.storage.PlotStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ArtemisArmazem extends JavaPlugin {

    private PlotStorage plotStorage;
    private PlotAPI plotAPI;
    private SQLite sqLite;


    public static ArtemisArmazem getInstance() {
        return getPlugin(ArtemisArmazem.class);
    }

    @Override
    public void onEnable() {
        InventoryManager.enable(this);
        saveDefaultConfig();

        if (getServer().getPluginManager().getPlugin("PlotSquared") == null) {
            getLogger().log(Level.SEVERE, "PlotSquared não encontrado, desativando plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        plotAPI = new PlotAPI();
        plotStorage = new PlotStorage();
        sqLite = new SQLite();

        getServer().getPluginManager().registerEvents(new LegacyPlotListener(), this);
        getCommand("armazem").setExecutor(new StorageCommand());
        sqLite.load();

    }

    @Override
    public void onDisable() {
        getLogger().info("Salvando o banco de dados...");
        plotStorage.save();
    }

    public PlotStorage getPlotStorage() {
        return plotStorage;
    }

    public PlotAPI getPlotAPI() {
        return plotAPI;
    }

    public SQLite getSQLite() {
        return sqLite;
    }
}

package me.dkz.artemis.plugins.inventory;

import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.intellectualcrafters.plot.object.Plot;
import me.dkz.artemis.plugins.ArtemisArmazem;
import me.dkz.artemis.plugins.storage.PlotItem;
import me.dkz.artemis.plugins.storage.PlotStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class StorageInventory extends PagedInventory {

    private final ArtemisArmazem plugin = ArtemisArmazem.getInstance();
    private final PlotStorage plotStorage = plugin.getPlotStorage();
    private Plot plot;

    public StorageInventory(Plot plot) {
        super("me.dkz.artemis.sinv", "Armazém", 9 * 3);
        this.plot = plot;

        configuration(configuration -> {
            configuration.secondUpdate(1);
        });
    }


    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {
        List<InventoryItemSupplier> itemSuppliers = new LinkedList<>();

        UUID plotID = plot.getOwners().stream().findFirst().get();
        plotStorage.getPlotItems(plotID).forEach(item -> {
            ItemStack itemStack = item.build();

            itemSuppliers.add(() ->
                    (InventoryItem.of(itemStack))
                    .callback(ClickType.LEFT, (i) -> addItem(viewer.getPlayer(), item, Integer.MAX_VALUE))
                    .callback(ClickType.RIGHT, (i) -> addItem(viewer.getPlayer(), item, 1))
                    .callback(ClickType.SHIFT_LEFT, (i) -> addItem(viewer.getPlayer(), item, 64))
            );
        });

        return itemSuppliers;
    }

    private void addItem(Player player, PlotItem plotItem, int requiredAmount) {

        int inventorySizeAmount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                inventorySizeAmount += 64;
            } else if (item.getType().toString().toUpperCase().equals(plotItem.getType())) {
                inventorySizeAmount += (64 - item.getAmount());

            }
        }

        if (inventorySizeAmount == 0) {
            player.sendMessage("§cNão há espaço no inventário para adicionar este item.");
            return;
        }

        int storageItemAmount = plotItem.getAmount();
        if (storageItemAmount == 0) {
            player.sendMessage("§cNão há este item no armazém.");
            return;
        }


        int possibleRemoveAmount = Math.min(storageItemAmount, inventorySizeAmount);
        int addToInvAmount = Math.min(possibleRemoveAmount, requiredAmount);


        ItemStack item = new ItemStack(plotItem.build().getType());
        item.setAmount(addToInvAmount);
        player.getInventory().addItem(item);

        plotItem.remove(addToInvAmount);

        player.sendMessage("§aVocê adicionou " + addToInvAmount + " " + plotItem.getName() + " §aao seu inventário.");

    }
}

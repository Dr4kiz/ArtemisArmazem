package me.dkz.artemis.plugins.storage;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class PlotItem {

    private int amount;
    private String name;
    private String type;
    private transient List<String> lore;


    public String getType() {
        return type;
    }

    public void add(int amount) {
        this.amount += amount;
    }

    public void remove(int amount) {
        this.amount -= amount;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name.replaceAll("&", "§");
    }

    public List<String> getLore() {
        return lore.stream().map(s ->
                s.replaceAll("@quantia", String.valueOf(amount))
                .replaceAll("&", "§"))
                .collect(Collectors.toList());
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(Material.valueOf(getType()));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(getName());
        meta.setLore(getLore());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static final class PlotItemBuilder {
        private int amount;
        private String name;
        private transient List<String> lore;
        private String type;

        private PlotItemBuilder() {
        }

        public static PlotItemBuilder builder() {
            return new PlotItemBuilder();
        }

        public PlotItemBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public PlotItemBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PlotItemBuilder type(String type) {
            this.type = type;
            return this;
        }

        public PlotItemBuilder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public PlotItem build() {
            PlotItem plotItem = new PlotItem();
            plotItem.lore = this.lore;
            plotItem.name = this.name;
            plotItem.amount = this.amount;
            plotItem.type = this.type;
            return plotItem;
        }
    }

    @Override
    public String toString() {
        return "PlotItem{" +
                "amount=" + amount +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}


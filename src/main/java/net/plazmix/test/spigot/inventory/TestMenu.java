package net.plazmix.test.spigot.inventory;

import net.plazmix.core.api.spigot.SpigotCoreApi;
import net.plazmix.core.api.spigot.inventory.icon.Icon;
import net.plazmix.core.api.spigot.inventory.paginator.GlobalPaginator;
import net.plazmix.core.api.spigot.inventory.paginator.PaginatorType;
import net.plazmix.core.api.spigot.inventory.view.GlobalViewInventory;
import net.plazmix.core.api.spigot.inventory.view.PersonalViewInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TestMenu implements Listener {

    public static void registerMenus(SpigotCoreApi api) {
        TestMenu menu = new TestMenu(api);
        api.getPlugin().getServer().getPluginManager().registerEvents(menu, api.getPlugin());
    }

    private final GlobalViewInventory globalViewInventory;
    private final PersonalViewInventory personalViewInventory;

    public TestMenu(SpigotCoreApi api) {
        this.globalViewInventory = api.newGlobalViewInventory()
                .setTitle("Global inventory")
                .setOpeningAction(data -> {
                    data.getIssuer().sendMessage("Opening global inventory...");
                })
                .setType(InventoryType.HOPPER)
                .withIcon(2, Icon.of(new ItemStack(Material.APPLE), click -> {
                    click.getIssuer().playSound(click.getIssuer().getLocation(), Sound.EAT, 1F, 1F);
                }))
                .build();

        this.personalViewInventory = api.newPersonalViewInventory()
                .setTitleApplier((player, inventory) -> player.getName() + "'s inventory")
                .setChestRows(3)
                // Adding default EXIT item
                .withGlobalIcon(26, Icon.of(new ItemStack(Material.REDSTONE_BLOCK), click -> click.getIssuer().closeInventory()))

                // Setting paginator scheme slots
                .setPaginatorScheme(PaginatorType.GLOBAL, "3 4 5")

                .setOpeningAction(data -> {
                    ItemStack item = new ItemStack(Material.PAPER);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.YELLOW + data.getIssuer().getName());
                    item.setItemMeta(meta);

                    PersonalViewInventory inventory = (PersonalViewInventory) data.getInventoryBase();
                    // Adding player's personal item
                    inventory.setPersonalIcon(data.getIssuer(), 0, Icon.of(item));

                    GlobalPaginator paginator = (GlobalPaginator) inventory.getPaginator();

                    // Adding items
                    paginator.addContents(Arrays.asList("Item1", "Item2", "Item3", "Item4")
                    .stream()
                    .map(str -> {
                        ItemStack contentItem = new ItemStack(Material.BREAD);
                        ItemMeta contentMeta = contentItem.getItemMeta();
                        contentMeta.setDisplayName(ChatColor.RED + str);
                        contentItem.setItemMeta(contentMeta);
                        return Icon.of(contentItem);
                    })
                    .collect(Collectors.toList()));

                    // Adding page switcher items
                    inventory.setGlobalIcon(1, Icon.of(new ItemStack(Material.ARROW), click -> {
                        if (paginator.previousPage(click.getIssuer()))
                            paginator.refresh(click.getIssuer());
                        else
                            click.getIssuer().playSound(click.getIssuer().getLocation(), Sound.VILLAGER_NO, 1F, 1F);
                    }));

                    inventory.setGlobalIcon(8, Icon.of(new ItemStack(Material.ARROW), click -> {
                        if (paginator.nextPage(click.getIssuer()))
                            paginator.refresh(click.getIssuer());
                        else
                            click.getIssuer().playSound(click.getIssuer().getLocation(), Sound.VILLAGER_NO, 1F, 1F);
                    }));

                    // Refreshing for updates
                    paginator.refresh(data.getIssuer());
                })
                .build();
    }

    @EventHandler
    private void on(PlayerJoinEvent event) {
        event.getPlayer().getInventory().addItem(new ItemStack(Material.PAPER));
        event.getPlayer().getInventory().addItem(new ItemStack(Material.BOOK));
    }

    @EventHandler
    private void on(PlayerInteractEvent event) {
        if (event.getItem() == null)
            return;

        if (event.getItem().getType() == Material.PAPER)
            globalViewInventory.open(event.getPlayer());
        else if (event.getItem().getType() == Material.BOOK)
            personalViewInventory.open(event.getPlayer());
    }
}

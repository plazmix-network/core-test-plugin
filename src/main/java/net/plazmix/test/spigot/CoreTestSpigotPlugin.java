package net.plazmix.test.spigot;

import net.plazmix.core.api.Core;
import net.plazmix.core.api.spigot.SpigotCoreApi;
import net.plazmix.test.spigot.command.TestCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreTestSpigotPlugin extends JavaPlugin {

    private SpigotCoreApi coreApi;

    @Override
    public void onLoad() {
        this.coreApi = (SpigotCoreApi) Core.getApi();
    }

    @Override
    public void onEnable() {
        TestCommand.createAndRegister(coreApi);
    }
}
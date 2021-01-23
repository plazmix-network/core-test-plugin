package net.plazmix.test.spigot.command;

import lombok.experimental.UtilityClass;
import net.plazmix.core.api.common.command.Command;
import net.plazmix.core.api.spigot.SpigotCoreApi;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@UtilityClass
public class TestCommand {

    public static void createAndRegister(SpigotCoreApi api) {
        Command command = api.newCommand("test")
                .withDescription("My test command!")
                .withPermission("test.permission", "You do not have permission yeah")
                .registerAliases("t", "testo")
                .withExecutor((sender, params) -> {
                    Player player = sender.resolveAs(Player.class).orElse(null);
                    if (player == null) {
                        sender.sendMessage("This command can be executed by players only!");
                        return;
                    }
                    player.setExp(player.getExp() + 10F);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
                })

                .addArgument(api.newCommandArgument("testarg")
                        .registerAliases("ta", "testoargo")
                        .withDescription("My test command argument!")
                        .withPermission("test.permission.arg", "You do not have permission to use this subcommand!")
                        .withExecutor((sender, params) -> {
                            CommandSender commandSender = sender.resolveAs(CommandSender.class).get();
                            commandSender.sendMessage("Hello! You found a secret argument of this command =)");
                        }))

                .build();

        command.register();
    }
}

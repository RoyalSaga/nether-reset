package net.royalsaga.minecraft.netherreset.commands;

import net.royalsaga.minecraft.netherreset.NetherReset;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class NetherResetCommand implements CommandExecutor, TabCompleter {

    private static final String PERMISSION = "netherreset.admin";

    private final NetherReset plugin;

    public NetherResetCommand(NetherReset plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage("NR | No permission!");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("NR | The plugin has been reloaded.");
            plugin.reload();
            return true;
        }

        final var nether = Bukkit.getWorld("world_nether");

        if (nether == null) {
            sender.sendMessage("NR | Could not find world_nether.");
            return true;
        }

        nether.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        nether.getWorldBorder().setCenter(plugin.getBorderCenterX(), plugin.getBorderCenterZ());
        nether.getWorldBorder().setSize(plugin.getBorderWidth());
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "chunkmaster setCenter world_nether %d %d".formatted((int) plugin.getBorderCenterX(), (int) plugin.getBorderCenterZ())
        );
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "chunkmaster generate world_nether %d".formatted(plugin.getRadius())
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Collections.singletonList("reload");
        }

        return Collections.emptyList();
    }

}

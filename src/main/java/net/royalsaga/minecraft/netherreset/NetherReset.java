package net.royalsaga.minecraft.netherreset;

import net.royalsaga.minecraft.netherreset.commands.NetherResetCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class NetherReset extends JavaPlugin {

    private double borderCenterX;
    private double borderCenterZ;
    private double borderWidth;
    private int radius;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reload();

        final var pluginCommand = getCommand("netherreset");

        if (pluginCommand != null) {
            final var command = new NetherResetCommand(this);
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }
    }

    public void reload() {
        reloadConfig();
        this.borderCenterX = getConfig().getDouble("border.center.x", 0.0);
        this.borderCenterZ = getConfig().getDouble("border.center.z", 0.0);
        this.borderWidth = getConfig().getDouble("border.width", 3333.0);
        this.radius = getConfig().getInt("radius", 1700);
    }

    public double getBorderCenterX() {
        return borderCenterX;
    }

    public double getBorderCenterZ() {
        return borderCenterZ;
    }

    public double getBorderWidth() {
        return borderWidth;
    }

    public int getRadius() {
        return radius;
    }

}

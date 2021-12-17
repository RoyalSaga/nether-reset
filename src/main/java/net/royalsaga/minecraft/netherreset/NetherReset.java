package net.royalsaga.minecraft.netherreset;

import com.google.common.primitives.Ints;
import net.royalsaga.minecraft.netherreset.commands.NetherResetCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetherReset extends JavaPlugin {

    private double borderCenterX;
    private double borderCenterZ;
    private double borderWidth;
    private int radius;
    private List<String> commands;
    private final Map<GameRule<Boolean>, Boolean> booleanGameRules = new HashMap<>();
    private final Map<GameRule<Integer>, Integer> integerGameRules = new HashMap<>();

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

        final var placeholders = new String[] {
                "%center_x%", "%center_x_fixed%", "%center_x_centered%",
                "%center_z%", "%center_z_fixed%", "%center_z_centered%",
                "%radius%"
        };

        final var values = new String[] {
                Double.toString(this.borderCenterX), Integer.toString((int) this.borderCenterX), Double.toString(this.borderCenterX + 0.5),
                Double.toString(this.borderCenterZ), Integer.toString((int) this.borderCenterZ), Double.toString(this.borderCenterZ + 0.5),
                Double.toString(this.radius)
        };

        this.commands = getConfig().getStringList("commands").stream()
                .map(it -> StringUtils.replaceEach(it, placeholders, values))
                .collect(Collectors.toList());

        this.booleanGameRules.clear();
        this.integerGameRules.clear();

        var invalidGameRule = false;

        for (final var it : getConfig().getStringList("gameRules")) {
            final var parts = it.split(" ", 2);

            if (parts.length != 2) {
                getLogger().warning("Rule " + parts[0] + " is missing a value.");
                continue;
            }

            final var rule = GameRule.getByName(parts[0]);

            if (rule == null) {
                getLogger().warning("Unknown rule " + parts[0]);

                if (!invalidGameRule) {
                    invalidGameRule = true;
                    getLogger().info("Valid rules: " + Arrays.stream(GameRule.values()).map(GameRule::getName).collect(Collectors.joining(", ")));
                }
                continue;
            }

            if (rule.getType().equals(Boolean.class)) {
                this.booleanGameRules.put((GameRule<Boolean>) rule, Boolean.parseBoolean(parts[1]));
                continue;
            }

            if (rule.getType().equals(Integer.class)) {
                final var value = Ints.tryParse(parts[1]);

                if (value == null) {
                    getLogger().warning("Value " + parts[1] + " for rule " + rule.getName() + " is not an integer.");
                    continue;
                }

                this.integerGameRules.put((GameRule<Integer>) rule, value);
                continue;
            }

            getLogger().warning("Unsupported value type " + rule.getType() + " if rule " + rule.getName());
        }
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

    public List<String> getCommands() {
        return commands;
    }

    public Map<GameRule<Boolean>, Boolean> getBooleanGameRules() {
        return booleanGameRules;
    }

    public Map<GameRule<Integer>, Integer> getIntegerGameRules() {
        return integerGameRules;
    }

}

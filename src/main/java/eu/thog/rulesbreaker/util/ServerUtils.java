package eu.thog.rulesbreaker.util;

import com.google.common.collect.Lists;
import eu.thog.rulesbreaker.RulesBreaker;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.*;

/**
 * Server access utilities
 * Created by Thog the 27/06/2016
 */
public class ServerUtils
{
    private static List<String> DEFAULT_WORLDS = Arrays.asList("Overworld", "Nether", "End");
    /**
     * Get a World by name
     * @param server The MinecraftServer instance
     * @param name The world name
     * @return The target world for the given name or the default overworld instance
     */
    public static World getServer(MinecraftServer server, String name)
    {
        World[] worlds = server.worldServers;
        if (DEFAULT_WORLDS.contains(name))
        {
            for (int i = 0; i < 3; i++)
                if (DEFAULT_WORLDS.get(i).equals(name))
                    return server.worldServers[i];
        }
        for (World world : worlds)
        {
            if (world.getWorldInfo() != null && world.getWorldInfo().getWorldName().equalsIgnoreCase(name))
                return world;
        }

        // If not found, fallback to the overworld
        return server.worldServers[0];
    }

    /**
     * Replace a already registered command
     * @param server the MinecraftServer instance
     * @param newCommand the replacement of the old command
     */
    public static void replaceCommand(MinecraftServer server, ICommand newCommand)
    {
        // Overriding rules command
        ICommandManager handler = server.getCommandManager();

        byte patched = 3;
        // Check if we can patch it
        if (handler instanceof CommandHandler)
        {
            // Yes we can, remove the original /gamerule command
            Map<String, ICommand> commandsMap = handler.getCommands();
            ICommand command = commandsMap.remove(newCommand.getCommandName());
            if (command == null)
                RulesBreaker.LOGGER.warn("The %s command doesn't exits! This is not supposed to happened!", newCommand.getCommandName());
            else
            {
                patched = 2;
                try
                {
                    Set<ICommand> commandSet = ObfuscationReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) handler, "commandSet", "field_71561_b", "c");
                    commandSet.remove(command);
                    patched = 1;
                } catch (RuntimeException e)
                {
                    RulesBreaker.LOGGER.error(String.format("An exception occurred during Rules Breaker setup of /%s command", newCommand.getCommandName()), e);
                }
            }
            ((CommandHandler) handler).registerCommand(newCommand);
        }

        if (patched != 1)
            RulesBreaker.LOGGER.warn("The %s command cannot be patch, hook disabled!" + (patched == 2 ? "Custom command available" : ""), newCommand.getCommandName());

    }

    /**
     * Get all worlds name in a List
     * @param server The MinecraftServer instance
     * @return a list containing all worlds name
     */
    public static List<String> getAllWorldsName(MinecraftServer server)
    {
        World[] worlds = server.worldServers;
        List<String> worldsNameList = Lists.newArrayList(DEFAULT_WORLDS);
        for (int i = 0; i < worlds.length; i++)
        {
            if (i > 2 && worlds[i] != null)
                worldsNameList.add(worlds[i].getWorldInfo().getWorldName());
        }
        worldsNameList.add("all");
        return worldsNameList;
    }

    /**
     * Get a game rule as a double
     * @param rules The GameRules instance
     * @param key The game rule key name to get
     * @param defaultValue The default value to return if we get an exception from the parsing system
     * @return The value of the game rule as float or the defaultValue if an exception occurred
     */
    public static double getGameRuleAsDouble(GameRules rules, String key, double defaultValue)
    {
        try
        {
            return Double.parseDouble(rules.getString(key));
        }
        catch (NumberFormatException ignored)
        {
        }
        return defaultValue;
    }

    /**
     * Get a game rule as a float
     * @param rules The GameRules instance
     * @param key The game rule key name to get
     * @param defaultValue The default value to return if we get an exception from the parsing system
     * @return The value of the game rule as float or the defaultValue if an exception occurred
     */
    public static float getGameRuleAsFloat(GameRules rules, String key, float defaultValue)
    {
        try
        {
            return Float.parseFloat(rules.getString(key));
        }
        catch (NumberFormatException ignored)
        {
        }
        return defaultValue;
    }

    /**
     * Ugly hack to set the correct ValueType to a already defined game rule or register it normally
     * @param rules The GameRules instance that will receive the game rule
     * @param ruleName The name of the rule
     * @param defaultValue The default value of the game rule
     * @param type The type of the game rule value
     */
    @SuppressWarnings("unchecked")
    public static void registerGameRule(GameRules rules, String ruleName, String defaultValue, GameRules.ValueType type)
    {
        String value = rules.getString(ruleName);
        TreeMap<String, Object> theGameRules = (TreeMap<String, Object>) ReflectionUtils.getValue(rules, ReflectionHelper.findField(GameRules.class, MappingUtils.getObfuscatedName("field_82771_a"), "field_82771_a", "theGameRules"));

        // Remove the old entry that maybe incorrect, thanks to Mojang NBT deserialization
        theGameRules.remove(ruleName);

        // Register the game rule with the correct ValueType
        rules.addGameRule(ruleName, defaultValue, type);

        // We have an entry so we reuse the correct value
        if (!value.isEmpty())
            rules.setOrCreateGameRule(ruleName, value);
    }
}

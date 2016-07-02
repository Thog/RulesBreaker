package eu.thog.rulesbreaker.commands;

import eu.thog.rulesbreaker.util.ServerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * A rewrite of the vanilla weather command to support multi-worlds
 * Created by Thog the 27/06/2016
 */
@SuppressWarnings("NullableProblems")
public class WeatherCommand extends net.minecraft.command.CommandWeather
{
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isInteger(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe)
        {
            return false;
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 1)
        {
            boolean isSecondArgDelay = args.length >= 2 && isInteger(args[1]);

            String worldName = args.length > 2 || (args.length > 1 && !isSecondArgDelay) ? buildString(args, isSecondArgDelay ? 2 : 1) : server.worldServers[0].getWorldInfo().getWorldName();
            boolean isGlobal = worldName.equals("all");

            int delay = (300 + (new Random()).nextInt(600)) * 20;

            if (args.length >= 2 && isSecondArgDelay)
                delay = parseInt(args[1], 1, 1000000) * 20;

            if (isGlobal)
                for (World world : server.worldServers)
                    changeWeather(world, args[0], delay);
            else
                changeWeather(ServerUtils.getServer(server, worldName), args[0], delay);
            notifyCommandListener(sender, this, "commands.weather." + args[0].toLowerCase() + ".custom" + (isGlobal ? ".allworld" : ""), worldName);
        } else
        {
            throw new WrongUsageException("commands.weather.usage.custom");
        }
    }

    private void changeWeather(World world, String type, int delay) throws CommandException
    {
        WorldInfo worldinfo = world.getWorldInfo();

        if ("clear".equalsIgnoreCase(type))
        {
            worldinfo.setCleanWeatherTime(delay);
            worldinfo.setRainTime(0);
            worldinfo.setThunderTime(0);
            worldinfo.setRaining(false);
            worldinfo.setThundering(false);
        } else if ("rain".equalsIgnoreCase(type))
        {
            worldinfo.setCleanWeatherTime(0);
            worldinfo.setRainTime(delay);
            worldinfo.setThunderTime(delay);
            worldinfo.setRaining(true);
            worldinfo.setThundering(false);
        } else if ("thunder".equalsIgnoreCase(type))
        {
            worldinfo.setCleanWeatherTime(0);
            worldinfo.setRainTime(delay);
            worldinfo.setThunderTime(delay);
            worldinfo.setRaining(true);
            worldinfo.setThundering(true);
        } else
            throw new WrongUsageException("commands.weather.usage");
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.weather.usage.custom";
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if ((args.length == 3 && isInteger(args[1])) || (args.length == 2 && !isInteger(args[1])))
            return getListOfStringsMatchingLastWord(args, ServerUtils.getAllWorldsName(server));
        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}

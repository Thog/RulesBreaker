package eu.thog.rulesbreaker.commands;

import eu.thog.rulesbreaker.util.ServerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A rewrite of the vanilla toggle downfall command to support multi-worlds
 * Created by Thog the 29/06/2016
 */
@SuppressWarnings("NullableProblems")
public class ToggleDownfallCommand extends net.minecraft.command.CommandToggleDownfall
{
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.downfall.usage.custom";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String worldName = args.length >= 1 ? buildString(args, 0) : server.worldServers[0].getWorldInfo().getWorldName();
        boolean isGlobal = worldName.equals("all");
        if (!isGlobal)
            this.toggleRainfall(ServerUtils.getServer(server, worldName));
        else
            for (World world : server.worldServers)
                this.toggleRainfall(world);
        notifyCommandListener(sender, this, "commands.downfall.success.custom" + (isGlobal ? ".allworld" : ""), worldName);
    }

    private void toggleRainfall(World world)
    {
        WorldInfo worldinfo = world.getWorldInfo();
        worldinfo.setRaining(!worldinfo.isRaining());
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, ServerUtils.getAllWorldsName(server)) : super.getTabCompletionOptions(server, sender, args, pos);
    }
}

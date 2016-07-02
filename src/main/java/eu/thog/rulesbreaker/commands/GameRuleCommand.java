package eu.thog.rulesbreaker.commands;

import eu.thog.rulesbreaker.util.ServerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A rewrite of the vanilla gamerule command to support multi-worlds
 * Created by Thog the 27/06/2016
 */
@SuppressWarnings("NullableProblems")
public class GameRuleCommand extends net.minecraft.command.CommandGameRule
{
    /**
     * Rules that aren't related to the world
     */
    private List<String> SERVER_RULES = Arrays.asList("logAdminCommands", "reducedDebugInfo", "sendCommandFeedback");

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String s = args.length > 0 ? args[0] : "";
        String s1 = args.length > 1 ? args[1] : "";
        String worldName = args.length > 2 ? buildString(args, 2) : server.worldServers[0].getWorldInfo().getWorldName();
        GameRules gamerules = ServerUtils.getServer(server, worldName).getGameRules();

        switch (args.length)
        {
            case 0:
                sender.addChatMessage(new TextComponentString(joinNiceString(gamerules.getRules())));
                break;
            case 1:

                if (!gamerules.hasRule(s))
                    throw new CommandException("commands.gamerule.norule", s);

                String s2 = gamerules.getString(s);
                sender.addChatMessage((new TextComponentString(s)).appendText(" = ").appendText(s2));
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, gamerules.getInt(s));
                break;
            default:
                if (gamerules.areSameType(s, GameRules.ValueType.BOOLEAN_VALUE) && !"true".equals(s1) && !"false".equals(s1))
                    throw new CommandException("commands.generic.boolean.invalid", s1);
                boolean isServerRule = SERVER_RULES.contains(s);
                if (isServerRule)
                    s = server.worldServers[0].getWorldInfo().getWorldName();
                if (!isServerRule && worldName.equals("all"))
                {
                    for (World world : server.worldServers)
                    {
                        // Not supposed to happen but hey modders love dirty hacks...
                        if (world.getGameRules() != null)
                        {
                            // Secure it to avoid setting incorrect value
                            if (world.getGameRules().areSameType(s, GameRules.ValueType.NUMERICAL_VALUE) && !NumberUtils.isNumber(s1))
                                throw new NumberInvalidException("commands.generic.num.invalid", s1);
                            world.getGameRules().setOrCreateGameRule(s, s1);
                        }
                    }
                    notifyCommandListener(sender, this, "commands.gamerule.success.allworld", s, s1);
                } else
                {
                    // Secure it to avoid setting incorrect value
                    if (gamerules.areSameType(s, GameRules.ValueType.NUMERICAL_VALUE) && !NumberUtils.isNumber(s1))
                        throw new NumberInvalidException("commands.generic.num.invalid", s1);
                    gamerules.setOrCreateGameRule(s, s1);
                    notifyCommandListener(sender, this, "commands.gamerule.success" + (isServerRule ? "" : ".custom"), s, s1, worldName);
                }
                notifyGameRuleChange(gamerules, s, server);

        }
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.gamerule.usagecustom";
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if (args.length == 3 && !SERVER_RULES.contains(args[1]))
            return getListOfStringsMatchingLastWord(args, ServerUtils.getAllWorldsName(server));
        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Collections.singletonList("gr");
    }
}

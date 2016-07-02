package eu.thog.rulesbreaker;

import eu.thog.rulesbreaker.commands.GameRuleCommand;
import eu.thog.rulesbreaker.commands.ToggleDownfallCommand;
import eu.thog.rulesbreaker.commands.WeatherCommand;
import eu.thog.rulesbreaker.util.Constants;
import eu.thog.rulesbreaker.util.MappingUtils;
import eu.thog.rulesbreaker.util.ReflectionUtils;
import eu.thog.rulesbreaker.util.ServerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;


@Mod(modid = "rulesbreaker", version = "DEV_BUILD")
public class RulesBreaker
{

    private static Field aiArrowAttackField, fuseTimeField;
    @Mod.Instance(value = "rulesbreaker")
    public static RulesBreaker instance;

    public static final Logger LOGGER = LogManager.getLogger("rulesbreaker");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event)
    {
        // Replace some commands to provide a multi world support
        ServerUtils.replaceCommand(event.getServer(), new WeatherCommand());
        ServerUtils.replaceCommand(event.getServer(), new GameRuleCommand());
        ServerUtils.replaceCommand(event.getServer(), new ToggleDownfallCommand());

        int[] dimensions = DimensionManager.getDimensions(DimensionType.OVERWORLD);
        for (int dimensionID : dimensions)
        {
            WorldServer world = event.getServer().worldServerForDimension(dimensionID);
            // Not supposed to happen but hey modders love dirty hacks...
            if (world == null || world.getWorldInfo() == null || world.getWorldInfo().getGameRulesInstance() == null)
            {
                LOGGER.warn(String.format("Dimension with id %d is unavailable! Rules Breaker will not be available on it!", dimensionID));
                continue;
            }

            try
            {
                // Override the world provider instance to get a full control of the weather update system
                ReflectionUtils.setFinalField(ReflectionHelper.findField(World.class, MappingUtils.getObfuscatedName("field_73011_w"), "field_73011_w", "provider"), world, new RuledWorldProvider(world));
                GameRules rules = world.getWorldInfo().getGameRulesInstance();
                ServerUtils.registerGameRule(rules, Constants.RAIN_CYCLE_RULE, "true", GameRules.ValueType.BOOLEAN_VALUE);
                ServerUtils.registerGameRule(rules, Constants.CREEPER_FUSE_RULE, "30", GameRules.ValueType.NUMERICAL_VALUE);
                ServerUtils.registerGameRule(rules, Constants.SKELETON_FIRESPEED_RULE, "20", GameRules.ValueType.NUMERICAL_VALUE);
                ServerUtils.registerGameRule(rules, Constants.SKELETON_ATTACKRANGE_RULE, "15", GameRules.ValueType.NUMERICAL_VALUE);
                ServerUtils.registerGameRule(rules, Constants.SKELETON_MOVESPEED_RULE, "1", GameRules.ValueType.NUMERICAL_VALUE);
            } catch (RuntimeException e)
            {
                LOGGER.error(String.format("An exception occurred during Rules Breaker setup on world %s", world.getWorldInfo().getWorldName()), e);
            }
        }
    }

    @SubscribeEvent
    public void onSpawn(LivingSpawnEvent event)
    {
        this.manageSpawn(event.getEntity());
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event)
    {
        this.manageSpawn(event.getEntity());
    }


    /**
     * Internal hook for ruled entities
     * @param entity A possible hookable entity
     */
    private void manageSpawn(Entity entity)
    {
        if (!entity.worldObj.isRemote)
        {
            if (aiArrowAttackField == null)
                aiArrowAttackField = ReflectionHelper.findField(EntitySkeleton.class, MappingUtils.getObfuscatedName("field_85037_d"), "field_85037_d", "aiArrowAttack");
            if (fuseTimeField == null)
                fuseTimeField = ReflectionHelper.findField(EntityCreeper.class, MappingUtils.getObfuscatedName("field_82225_f"), "field_82225_f", "fuseTime");
            if (entity instanceof EntityCreeper && (Integer)ReflectionUtils.getValue(entity, fuseTimeField) == 30)
                ReflectionUtils.setValue(entity, fuseTimeField, entity.worldObj.getGameRules().getInt(Constants.CREEPER_FUSE_RULE));
            else if (entity instanceof EntitySkeleton)
            {
                EntitySkeleton skeleton = ((EntitySkeleton) entity);

                GameRules rules = skeleton.worldObj.getGameRules();
                final int newAttackCooldown = rules.getInt(Constants.SKELETON_FIRESPEED_RULE);
                final double moveSpeed = ServerUtils.getGameRuleAsDouble(rules, Constants.SKELETON_MOVESPEED_RULE, 1.0D);
                final float attackRange = ServerUtils.getGameRuleAsFloat(rules, Constants.SKELETON_ATTACKRANGE_RULE, 15.0F);

                EntityAIAttackRangedBow ai = new EntityAIAttackRangedBow(skeleton, moveSpeed, newAttackCooldown, attackRange)
                {
                    @Override
                    public void setAttackCooldown(int attackCooldown)
                    {
                        // Hard mode? dot 2
                        if (attackCooldown == 40)
                            attackCooldown = newAttackCooldown * 2;
                        else
                            attackCooldown = newAttackCooldown;
                        super.setAttackCooldown(attackCooldown);
                    }
                };

                // remove old arrow attack instance from the task system
                skeleton.tasks.removeTask((EntityAIBase) ReflectionUtils.getValue(skeleton, aiArrowAttackField));

                // override AI by our custom one
                ReflectionUtils.setFinalField(aiArrowAttackField, skeleton, ai);

                // Force task to be actualized
                skeleton.setCombatTask();
            }
        }
    }
}

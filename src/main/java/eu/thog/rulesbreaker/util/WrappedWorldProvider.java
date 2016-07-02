package eu.thog.rulesbreaker.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Global Wrapper to the world provider
 * Created by Thog the 27/06/2016
 */
@SuppressWarnings("NullableProblems")
public class WrappedWorldProvider extends WorldProvider
{
    private final WorldProvider provider;
    private Method generateLightBrightnessTable, createBiomeProvider;

    public WrappedWorldProvider(World world) throws ReflectionHelper.UnableToFindMethodException
    {
        this.worldObj = world;
        this.provider = world.provider;
        generateLightBrightnessTable = ReflectionHelper.findMethod(WorldProvider.class, provider, new String[]{"a", "func_76556_a", "generateLightBrightnessTable"});
        createBiomeProvider = ReflectionHelper.findMethod(WorldProvider.class, provider, new String[]{"b", "func_76572_b", "createBiomeProvider"});
    }

    @Override
    public DimensionType getDimensionType()
    {
        return provider.getDimensionType();
    }

    @Override
    protected void generateLightBrightnessTable()
    {
        try
        {
            generateLightBrightnessTable.invoke(provider);
        } catch (IllegalAccessException | InvocationTargetException ignored)
        {

        }
    }

    @Override
    protected void createBiomeProvider()
    {
        try
        {
            createBiomeProvider.invoke(provider);
        } catch (IllegalAccessException | InvocationTargetException ignored)
        {

        }
    }

    @Override
    public IChunkGenerator createChunkGenerator()
    {
        return provider.createChunkGenerator();
    }

    @Override
    public WorldBorder createWorldBorder()
    {
        return provider.createWorldBorder();
    }

    @Override
    public boolean canRespawnHere()
    {
        return provider.canRespawnHere();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IRenderHandler getCloudRenderer()
    {
        return provider.getCloudRenderer();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setCloudRenderer(IRenderHandler renderer)
    {
        provider.setCloudRenderer(renderer);
    }

    @Override
    public boolean canDoLightning(Chunk chunk)
    {
        return provider.canDoLightning(chunk);
    }

    @Override
    public boolean canDoRainSnowIce(Chunk chunk)
    {
        return provider.canDoRainSnowIce(chunk);
    }

    @Override
    public int getActualHeight()
    {
        return provider.getActualHeight();
    }

    @Override
    public int getAverageGroundLevel()
    {
        return provider.getAverageGroundLevel();
    }

    @Override
    public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful)
    {
        provider.setAllowedSpawnTypes(allowHostile, allowPeaceful);
    }

    @Override
    public BlockPos getRandomizedSpawnPoint()
    {
        return provider.getRandomizedSpawnPoint();
    }

    @Override
    public boolean canBlockFreeze(BlockPos pos, boolean byWater)
    {
        return provider.canBlockFreeze(pos, byWater);
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return provider.canCoordinateBeSpawn(x, z);
    }

    @Override
    public BlockPos getSpawnCoordinate()
    {
        return provider.getSpawnCoordinate();
    }

    @Override
    public boolean canDropChunk(int x, int z)
    {
        return provider.canDropChunk(x, z);
    }

    @Override
    public boolean canMineBlock(EntityPlayer player, BlockPos pos)
    {
        return provider.canMineBlock(player, pos);
    }

    @Override
    public BlockPos getSpawnPoint()
    {
        return provider.getSpawnPoint();
    }

    @Override
    public void setSpawnPoint(BlockPos pos)
    {
        provider.setSpawnPoint(pos);
    }

    @Override
    public boolean getHasNoSky()
    {
        return provider.getHasNoSky();
    }

    @Override
    public boolean isDaytime()
    {
        return provider.isDaytime();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z)
    {
        return provider.doesXZShowFog(x, z);
    }

    @Override
    public boolean canSnowAt(BlockPos pos, boolean checkLight)
    {
        return provider.canSnowAt(pos, checkLight);
    }

    @Override
    public boolean doesWaterVaporize()
    {
        return provider.doesWaterVaporize();
    }

    @Override
    public boolean isBlockHighHumidity(BlockPos pos)
    {
        return provider.isBlockHighHumidity(pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isSkyColored()
    {
        return provider.isSkyColored();
    }

    @Override
    public boolean isSurfaceWorld()
    {
        return provider.isSurfaceWorld();
    }

    @Override
    public boolean shouldMapSpin(String entity, double x, double y, double z)
    {
        return provider.shouldMapSpin(entity, x, y, z);
    }

    @Override
    public double getMovementFactor()
    {
        return provider.getMovementFactor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getVoidFogYFactor()
    {
        return provider.getVoidFogYFactor();
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
        return provider.calculateCelestialAngle(worldTime, partialTicks);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getCloudHeight()
    {
        return provider.getCloudHeight();
    }

    @Override
    public float getCurrentMoonPhaseFactor()
    {
        return provider.getCurrentMoonPhaseFactor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1)
    {
        return provider.getStarBrightness(par1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float par1)
    {
        return provider.getSunBrightness(par1);
    }

    @Override
    public float getSunBrightnessFactor(float par1)
    {
        return provider.getSunBrightnessFactor(par1);
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    @Override
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks)
    {
        return provider.calcSunriseSunsetColors(celestialAngle, partialTicks);
    }

    @Override
    public float[] getLightBrightnessTable()
    {
        return provider.getLightBrightnessTable();
    }

    @Override
    public int getMoonPhase(long worldTime)
    {
        return provider.getMoonPhase(worldTime);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IRenderHandler getSkyRenderer()
    {
        return provider.getSkyRenderer();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setSkyRenderer(IRenderHandler skyRenderer)
    {
        provider.setSkyRenderer(skyRenderer);
    }

    @Override
    public String getDepartMessage()
    {
        return provider.getDepartMessage();
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player)
    {
        return provider.getRespawnDimension(player);
    }

    @Override
    public void calculateInitialWeather()
    {
        provider.calculateInitialWeather();
    }

    @Override
    public String getWelcomeMessage()
    {
        return provider.getWelcomeMessage();
    }

    @Override
    public String getSaveFolder()
    {
        return provider.getSaveFolder();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IRenderHandler getWeatherRenderer()
    {
        return provider.getWeatherRenderer();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setWeatherRenderer(IRenderHandler renderer)
    {
        provider.setWeatherRenderer(renderer);
    }

    @Override
    public void onPlayerAdded(EntityPlayerMP player)
    {
        provider.onPlayerAdded(player);
    }

    @Override
    public void onPlayerRemoved(EntityPlayerMP player)
    {
        provider.onPlayerRemoved(player);
    }

    @Override
    public void onWorldSave()
    {
        provider.onWorldSave();
    }

    @Override
    public void updateWeather()
    {
        provider.updateWeather();
    }

    @Override
    public void onWorldUpdateEntities()
    {
        provider.onWorldUpdateEntities();
    }

    @Override
    public Biome getBiomeForCoords(BlockPos pos)
    {
        return provider.getBiomeForCoords(pos);
    }

    @Override
    public BiomeProvider getBiomeProvider()
    {
        return provider.getBiomeProvider();
    }

    @Override
    public double getHorizon()
    {
        return provider.getHorizon();
    }

    @Override
    public int getDimension()
    {
        return provider.getDimension();
    }

    @Override
    public void setDimension(int dim)
    {
        provider.setDimension(dim);
    }

    @Override
    public long getWorldTime()
    {
        return provider.getWorldTime();
    }

    @Override
    public void setWorldTime(long time)
    {
        provider.setWorldTime(time);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float var1, float var2)
    {
        return provider.getFogColor(var1, var2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getCloudColor(float partialTicks)
    {
        return provider.getCloudColor(partialTicks);
    }

    @Override
    public void resetRainAndThunder()
    {
        provider.resetRainAndThunder();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
    {
        return provider.getSkyColor(cameraEntity, partialTicks);
    }

    @Override
    public long getSeed()
    {
        return provider.getSeed();
    }

    @Override
    public int getHeight()
    {
        return provider.getHeight();
    }
}

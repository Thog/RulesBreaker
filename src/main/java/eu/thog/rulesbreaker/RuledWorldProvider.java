package eu.thog.rulesbreaker;

import eu.thog.rulesbreaker.util.Constants;
import eu.thog.rulesbreaker.util.WrappedWorldProvider;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Desc...
 * Created by Thog the 27/06/2016
 */
public class RuledWorldProvider extends WrappedWorldProvider
{
    private int prevWeatherClean, prevWeatherRain, prevWeatherThunder, forceWeatherUpdate;

    public RuledWorldProvider(World world) throws ReflectionHelper.UnableToFindMethodException
    {
        super(world);
    }

    @Override
    public void updateWeather()
    {
        WorldInfo info = worldObj.getWorldInfo();
        boolean isWeatherCycleEnabled = worldObj.getWorldInfo().getGameRulesInstance().getBoolean(Constants.RAIN_CYCLE_RULE);
        if (isWeatherCycleEnabled || forceWeatherUpdate > 0)
        {
            if (!isWeatherCycleEnabled)
                this.forceWeatherUpdate--;
            super.updateWeather();
        } else
        {
            // FIXME: Real value for commands update
            if (info.getCleanWeatherTime() != prevWeatherClean)
                this.forceWeatherUpdate = 100;
            else if (info.getRainTime() != prevWeatherRain)
                this.forceWeatherUpdate = 60;
            else if (info.getThunderTime() != prevWeatherThunder)
                this.forceWeatherUpdate = 60;
        }

        this.prevWeatherClean = info.getCleanWeatherTime();
        this.prevWeatherRain = info.getRainTime();
        this.prevWeatherThunder = info.getThunderTime();

    }
}

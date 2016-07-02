package eu.thog.rulesbreaker.util;

import com.google.common.collect.Maps;
import net.minecraftforge.common.ForgeVersion;

import java.util.Map;

/**
 * Provide utilities to support multiple mcp mappings
 * Created by Thog the 02/07/2016
 */
public class MappingUtils
{
    private static final Map<String, String> DATA_MAPPED = Maps.newHashMap();

    static
    {
        String mcVersion = ForgeVersion.mcVersion;
        DATA_MAPPED.put("field_82771_a", "a"); // theGameRules
        DATA_MAPPED.put("field_85037_d", "c"); // aiArrowAttack
        DATA_MAPPED.put("field_73011_w", "s"); // provider
        switch (mcVersion)
        {
            case "1.10":
            case "1.10.1":
            case "1.10.2":
            {
                DATA_MAPPED.put("field_82225_f", "bz"); // fuseTime
                break;
            }
            case "1.9.4":
            {
                DATA_MAPPED.put("field_82225_f", "by"); // fuseTime
                break;
            }
            default:
                throw new RuntimeException("RulesBreaker: Unsupported minecraft version!");
        }
    }

    public static String getObfuscatedName(String seargeName)
    {
        return DATA_MAPPED.get(seargeName);
    }
}

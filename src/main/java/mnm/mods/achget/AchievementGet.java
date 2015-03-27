package mnm.mods.achget;

import java.io.File;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AchievementGet.MODID, name = "Achievement Get", version = AchievementGet.VERSION, acceptableRemoteVersions = "*")
public class AchievementGet {

    public static final String MODID = "achget";
    public static final String VERSION = "1.0";

    public static Logger logger;
    @Instance
    public static AchievementGet instance;
    @SidedProxy(clientSide = "mnm.mods.achget.ClientProxy", serverSide = "mnm.mods.achget.CommonProxy")
    public static CommonProxy proxy;

    public StatAchievement[] achievements;

    @EventHandler
    public void init(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        File f = new File(event.getModConfigurationDirectory(), MODID + ".json");
        proxy.init(f);
    }

}

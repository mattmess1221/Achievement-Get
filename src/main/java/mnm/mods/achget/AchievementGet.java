package mnm.mods.achget;

import java.io.File;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = AchievementGet.MODID, name = "Achievement Get", version = AchievementGet.VERSION, acceptableRemoteVersions = "*")
public class AchievementGet {

    public static final String MODID = "achget";
    public static final String VERSION = "1.0";

    public static Logger logger;
    @Instance
    public static AchievementGet instance;
    @SidedProxy(clientSide = "mnm.mods.achget.ClientProxy", serverSide = "mnm.mods.achget.CommonProxy")
    public static CommonProxy proxy;

    public Map<String, StatAchievement> achievements = Maps.newHashMap();

    @EventHandler
    public void init(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        File f = new File(event.getModConfigurationDirectory(), "Achievements.json");
        proxy.init(f);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLiving(LivingUpdateEvent event) {
        // disconnect is an event (or something)
        if (event.entity == null)
            return;
        if (event.entity instanceof EntityPlayerMP) {
            checkStats((EntityPlayerMP) event.entity);
        }
    }

    private void checkStats(EntityPlayerMP player) {
        for (StatAchievement sa : achievements.values()) {
            StatHandler handler = new StatHandler(sa);
            if (handler.shouldAward(player)) {
                handler.giveAchievement(player);
            }
        }
    }
}

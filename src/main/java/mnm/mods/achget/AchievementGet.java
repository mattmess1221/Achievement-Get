package mnm.mods.achget;

import java.io.File;
import java.util.Map;

import mnm.mods.achget.net.AchSyncPacket;
import mnm.mods.achget.net.AchievementChannel;
import mnm.mods.achget.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod(modid = AchievementGet.MODID, name = "Achievement Get", version = AchievementGet.VERSION, acceptableRemoteVersions = "*")
public class AchievementGet {

    public static final String MODID = "achget";
    public static final String VERSION = "1.0";

    public static Logger logger;
    @Instance
    public static AchievementGet instance;
    @SidedProxy(clientSide = "mnm.mods.achget.proxy.ClientProxy", serverSide = "mnm.mods.achget.proxy.CommonProxy")
    public static CommonProxy proxy;
    public AchievementChannel network = new AchievementChannel();

    /**
     * Mirror to {@link StatList#oneShotStats}.
     */
    public Map<String, StatBase> oneShotStats;
    public String jsonAch;
    public Map<String, StatAchievement> achievements = Maps.newHashMap();
    private File config;

    @EventHandler
    public void init(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        this.config = new File(event.getModConfigurationDirectory(), "achievements.json");
        this.oneShotStats = StatListEx.getOneShotStats();
        MinecraftForge.EVENT_BUS.register(this);
        network.init();
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent start) {
        proxy.init(config);
    }

    @SubscribeEvent
    public void join(PlayerLoggedInEvent join) {
        network.sendTo((EntityPlayerMP) join.player, new AchSyncPacket(jsonAch));
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

    public void registerAchivements(String json) {
        proxy.register(json);
    }

    private void checkStats(EntityPlayerMP player) {
        for (StatAchievement sa : achievements.values()) {
            StatHandler handler = new StatHandler(sa);
            if (handler.shouldAward(player)) {
                handler.giveAchievement(player);
            }
        }
    }

    /**
     * Class to access protected field.
     */
    private static class StatListEx extends StatList {
        @SuppressWarnings("unchecked")
        private static Map<String, StatBase> getOneShotStats() {
            return StatList.oneShotStats;
        }
    }

}

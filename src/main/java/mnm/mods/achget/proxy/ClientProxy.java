package mnm.mods.achget.proxy;

import mnm.mods.achget.AchievementGet;
import mnm.mods.achget.StatAchievement;

public class ClientProxy extends CommonProxy {

    @Override
    public void unregisterAchievements() {
        for (StatAchievement sa : AchievementGet.instance.achievements.values()) {
            sa.unregisterStat();
        }
    }
}

package mnm.mods.achget;

import net.minecraft.entity.player.EntityPlayerMP;

public class StatHandler {

    private final StatAchievement achievement;

    public StatHandler(StatAchievement achievement) {
        this.achievement = achievement;
    }

    public int getStat(EntityPlayerMP player) {
        return player.getStatFile().readStat(achievement.getStat());
    }

    public boolean hasAchievement(EntityPlayerMP player) {
        return player.getStatFile().hasAchievementUnlocked(achievement);
    }

    public void giveAchievement(EntityPlayerMP player) {
        player.triggerAchievement(achievement);
    }

    public boolean shouldAward(EntityPlayerMP player) {
        return !hasAchievement(player) && getStat(player) >= achievement.getCount();
    }
}

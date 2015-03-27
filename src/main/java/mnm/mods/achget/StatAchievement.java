package mnm.mods.achget;

import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class StatAchievement extends Achievement {

    private StatBase stat;
    private IChatComponent name;
    private int count;

    public StatAchievement(JsonAchievement json) {
        super(json.id, "", json.xPos, json.yPos, Items.apple, null);
        this.stat = StatList.getOneShotStat(json.stat);
        this.name = json.name;
        this.count = json.count;
        this.setStatStringFormatter(new Formatter(json.desc));
        this.registerStat();
    }

    public StatBase getStat() {
        return stat;
    }

    public int getCount() {
        return count;
    }

    @Override
    public IChatComponent getStatName() {
        IChatComponent ichatcomponent = this.name.createCopy();
        ChatStyle style = ichatcomponent.getChatStyle();
        style.setColor(EnumChatFormatting.GRAY);
        style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.statId)));
        return ichatcomponent;
    }

    private class Formatter implements IStatStringFormat {

        final String desc;

        public Formatter(String desc) {
            this.desc = desc;
        }

        @Override
        public String formatString(String p_74535_1_) {
            return desc;
        }
    }

    static class JsonAchievement {

        private String id;
        private IChatComponent name;
        private String desc;
        private String stat;
        private int count;
        private int xPos;
        private int yPos;

    }
}
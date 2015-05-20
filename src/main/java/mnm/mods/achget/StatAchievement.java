package mnm.mods.achget;

import net.minecraft.event.HoverEvent;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class StatAchievement extends Achievement {

    private StatBase stat;
    private IChatComponent name;
    private String description;
    private int count;

    private EnumChatFormatting color;

    public StatAchievement(JsonAchievement json) throws ParentNotLoadedException, StatNotFoundException {
        super(json.id, "", json.xPos, json.yPos, json.item, getParent(json.parent));
        this.stat = StatList.getOneShotStat(json.stat);
        if (stat == null) {
            throw new StatNotFoundException("Stat '" + json.stat + "' does not exist.");
        }
        this.name = json.name;
        this.description = json.desc;
        this.count = json.count;

        EnumChatFormatting color = EnumChatFormatting.getValueByName(json.color);
        if (color != null) {
            this.color = color;
        } else {
            AchievementGet.logger.warn("Unknown color: " + json.color);
        }
        if (json.special) {
            this.setSpecial();
        }
        if (json.parent == null) {
            this.initIndependentStat();
        }
        this.registerStat();
    }

    private static Achievement getParent(String parent) throws ParentNotLoadedException {
        if (parent == null)
            return null;
        if (!AchievementGet.instance.achievements.containsKey(parent))
            throw new ParentNotLoadedException();
        return AchievementGet.instance.achievements.get(parent);
    }

    public StatBase getStat() {
        return stat;
    }

    public int getCount() {
        return count;
    }

    public void unregisterStat() {
        AchievementList.achievementList.remove(this);
        StatList.allStats.remove(this);
        AchievementGet.instance.oneShotStats.remove(this.statId);
    }

    @Override
    public IChatComponent getStatName() {
        IChatComponent text = this.name.createCopy();
        ChatStyle style = text.getChatStyle();
        style.setColor(color);

        // So the achievement doesn't have to be registered on the client.
        IChatComponent desc = new ChatComponentText("");
        desc.appendSibling(text.createCopy()).appendText("\n");
        IChatComponent type = new ChatComponentText("Achievement");
        type.getChatStyle().setItalic(true);
        desc.appendSibling(type).appendText("\n");
        desc.appendSibling(new ChatComponentText(this.getDescription()));
        HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, desc);
        style.setChatHoverEvent(hover);

        return text;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

}
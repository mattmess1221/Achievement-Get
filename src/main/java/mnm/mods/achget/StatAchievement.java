package mnm.mods.achget;

import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.stats.Achievement;
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

    public StatAchievement(JsonAchievement json) {
        super(json.id, "", json.xPos, json.yPos, Items.apple, null);
        this.stat = StatList.getOneShotStat(json.stat);
        this.name = json.name;
        this.description = json.desc;
        this.count = json.count;
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
        IChatComponent text = this.name.createCopy();
        ChatStyle style = text.getChatStyle();
        style.setColor(EnumChatFormatting.GRAY);

        // So the achievement doesn't have to be registered on the client.
        IChatComponent desc = new ChatComponentText("");
        desc.appendSibling(text.createCopy()).appendText("\n");
        IChatComponent type = new ChatComponentText("Achievement");
        type.getChatStyle().setItalic(true);
        desc.appendSibling(type).appendText("\n");
        desc.appendSibling(new ChatComponentText(this.getDescription()));
        HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, desc);
        style.setChatHoverEvent(hover);
        // style.setChatHoverEvent(new
        // HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new
        // ChatComponentText(this.statId)));
        return text;
    }

    @Override
    public String getDescription() {
        return this.description;
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
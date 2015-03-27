package mnm.mods.achget;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
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

    private EnumChatFormatting color = EnumChatFormatting.GRAY;

    public StatAchievement(JsonAchievement json) {
        super(json.id, "", json.xPos, json.yPos, getItem(json.item), null);
        this.stat = StatList.getOneShotStat(json.stat);
        this.name = json.name;
        this.description = json.desc;
        this.count = json.count;

        EnumChatFormatting color = EnumChatFormatting.getValueByName(json.color);
        if (color != null) {
            this.color = color;
        } else {
            AchievementGet.logger.warn("Unknown color: " + (json.color == null ? "null" : json.color));
        }
        this.registerStat();
    }

    private static ItemStack getItem(String item) {
        String modid = "minecraft";
        String name = item;
        if (item.contains(":")) {
            modid = item.substring(0, item.indexOf(':'));
            name = item.substring(item.indexOf(':') + 1);
        }
        return GameRegistry.findItemStack(modid, name, 1);
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
        private String item = "minecraft:apple";
        private String color;
        private int count;
        private int xPos;
        private int yPos;

    }
}
package mnm.mods.achget;

import java.lang.reflect.Type;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import cpw.mods.fml.common.registry.GameRegistry;

public class JsonAchievement {

    public String id;
    public IChatComponent name;
    public String desc;
    public String parent;
    public String stat;
    public ItemStack item = new ItemStack(Items.apple);
    public String color = "gray";
    public boolean special;
    public int count;
    public int xPos;
    public int yPos;

    private static Item getItem(String item) {
        String modid = "minecraft";
        String name = item;
        if (item.contains(":")) {
            modid = item.substring(0, item.indexOf(':'));
            name = item.substring(item.indexOf(':') + 1);
        }
        return GameRegistry.findItem(modid, name);
    }

    public static class ItemSerializer implements JsonDeserializer<ItemStack> {

        @Override
        public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                String item = json.getAsString();
                return new ItemStack(getItem(item));
            } else if (json.isJsonObject()) {
                ItemShtack fake = context.deserialize(json, ItemShtack.class);
                Item item = getItem(fake.id);
                ItemStack stack = new ItemStack(item, 1, fake.meta);
                return stack;
            }
            throw new JsonParseException("Cannot be parsed into an ItemStack.");
        }

        private static class ItemShtack {
            private String id = "minecraft:apple";
            private int meta;
        }
    }

}
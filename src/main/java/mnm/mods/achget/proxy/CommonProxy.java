package mnm.mods.achget.proxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import mnm.mods.achget.AchievementGet;
import mnm.mods.achget.JsonAchievement;
import mnm.mods.achget.ParentNotLoadedException;
import mnm.mods.achget.StatAchievement;
import mnm.mods.achget.StatNotFoundException;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.AchievementPage;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommonProxy {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(IChatComponent.class, new IChatComponent.Serializer())
            .registerTypeAdapter(ItemStack.class, new JsonAchievement.ItemSerializer()).create();

    public void init(File f) {
        // The default
        String json = "[]";
        InputStream in = null;
        try {
            if (f.exists()) {
                json = FileUtils.readFileToString(f, Charsets.UTF_8);
            } else {
                // load and save defaults
                in = ClassLoader.getSystemResourceAsStream("achievements.json");
                if (in != null) {
                    json = IOUtils.toString(in);
                } else {
                    AchievementGet.logger.warn("Default config did not exist");
                }
                FileUtils.write(f, json, Charsets.UTF_8);
            }
        } catch (IOException e) {
            AchievementGet.logger.warn("Unable to load achievements.", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        register(json);
    }

    public void unregisterAchievements() {
    }

    public void register(String json) {
        AchievementGet.instance.jsonAch = json;
        List<JsonAchievement> achieves = Lists.newArrayList(GSON.fromJson(json, JsonAchievement[].class));
        register(achieves);
    }

    public void register(List<JsonAchievement> achieves) {
        this.unregisterAchievements();

        List<String> failed = Lists.newArrayList();
        AchievementGet.logger.info("Registering " + achieves.size() + " achievements.");
        // load achievements
        for (int i = 0; i < achieves.size(); i++) {
            JsonAchievement ja = achieves.get(i);
            try {
                StatAchievement sa = new StatAchievement(ja);
                AchievementGet.instance.achievements.put(sa.statId, sa);
            } catch (ParentNotLoadedException e) {
                if (!failed.contains(ja.parent)) {
                    achieves.add(ja); // do later
                } else {
                    failed.add(ja.id);
                }
            } catch (StatNotFoundException e) {
                // stat not valid
                AchievementGet.logger.warn(e.getMessage());
                failed.add(ja.id);
            }
        }
        if (failed.size() > 0) {
            AchievementGet.logger.warn(failed.size() + " achievements were not registered.");
        }
        // register page
        final String PAGE_NAME = "Achievement Get";
        Achievement[] array = AchievementGet.instance.achievements.values().toArray(new Achievement[0]);
        if (AchievementPage.getAchievementPage(PAGE_NAME) != null) {
            // modify current page
            AchievementPage page = AchievementPage.getAchievementPage(PAGE_NAME);
            page.getAchievements().clear();
            page.getAchievements().addAll(Lists.newArrayList(array));
        } else {
            // add new page
            AchievementPage page = new AchievementPage(PAGE_NAME, array);
            AchievementPage.registerAchievementPage(page);
        }
    }
}

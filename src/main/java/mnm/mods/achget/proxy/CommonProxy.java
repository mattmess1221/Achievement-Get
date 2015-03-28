package mnm.mods.achget.proxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import mnm.mods.achget.AchievementGet;
import mnm.mods.achget.StatAchievement;
import mnm.mods.achget.StatAchievement.JsonAchievement;
import mnm.mods.achget.StatAchievement.ParentNotLoadedException;
import net.minecraft.stats.Achievement;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.AchievementPage;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;

public class CommonProxy {

    public void init(File f) {
        // The default TODO externalize
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
        List<JsonAchievement> achieves = Lists.newArrayList(new GsonBuilder()
                .registerTypeAdapter(IChatComponent.class, new IChatComponent.Serializer()).create()
                .fromJson(json, JsonAchievement[].class));
        register(achieves);
    }

    public void register(List<JsonAchievement> achieves) {
        this.unregisterAchievements();

        AchievementGet.logger.info("Registering " + achieves.size() + " achievements.");
        // load achievements
        for (int i = 0; i < achieves.size(); i++) {
            JsonAchievement ja = achieves.get(i);
            try {
                StatAchievement sa = new StatAchievement(ja);
                AchievementGet.instance.achievements.put(sa.statId, sa);
            } catch (ParentNotLoadedException e) {
                achieves.add(ja); // do later
            }
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

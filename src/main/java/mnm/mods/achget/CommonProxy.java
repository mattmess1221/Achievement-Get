package mnm.mods.achget;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import mnm.mods.achget.StatAchievement.JsonAchievement;
import net.minecraft.stats.Achievement;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.AchievementPage;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;

public class CommonProxy {

    public void init(File f) {
        Logger logger = AchievementGet.logger;
        // The default TODO externalize
        String json = "[]";
        try {
            if (f.exists()) {
                json = FileUtils.readFileToString(f, Charsets.UTF_8);
            } else {
                // load and save defaults
                json = IOUtils.toString(ClassLoader.getSystemResourceAsStream("achget.json"));
                FileUtils.write(f, json);
            }
        } catch (IOException e) {
            logger.warn("Unable to load achievements.", e);
        }
        List<JsonAchievement> achieves = Lists.newArrayList(new GsonBuilder()
                .registerTypeAdapter(IChatComponent.class, new IChatComponent.Serializer()).create()
                .fromJson(json, JsonAchievement[].class));
        AchievementGet instance = AchievementGet.instance;

        logger.info("Registering " + achieves.size() + " achievements.");
        // load achievements
        ListIterator<JsonAchievement> iter = achieves.listIterator();
        for (int i = 0; i < achieves.size(); i++) {
            JsonAchievement ja = achieves.get(i);
            try {
                StatAchievement sa = new StatAchievement(ja);
                instance.achievements.put(sa.statId, sa);
            } catch (ParentNotLoadedException e) {
                achieves.add(ja); // do later
            }
        }
        // register page
        Achievement[] array = AchievementGet.instance.achievements.values().toArray(new Achievement[0]);
        AchievementPage page = new AchievementPage("Achievement Get", array);
        AchievementPage.registerAchievementPage(page);
    }
}

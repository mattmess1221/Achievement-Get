package mnm.mods.achget;

import java.io.File;
import java.io.IOException;

import mnm.mods.achget.StatAchievement.JsonAchievement;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.AchievementPage;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;

public class CommonProxy {

    public void init(File f) {
        Logger logger = AchievementGet.logger;
        // The default TODO externalize
        String json = "[{\n" // [{
                + "\t\"id\": \"walk1000\",\n" // "id": "walk100",
                + "\t\"name\": \"Kilo\",\n" // "name": "Kilo",
                + "\t\"desc\": \"Walk 1km\",\n" // "desc": "Walk 100m",
                // "stat": "stat.walkOneCm",
                + "\t\"stat\": \"stat.walkOneCm\",\n" //
                + "\t\"count\": 1000,\n" // "count": 100",
                + "\t\"xPos\": 0,\n" // "xPos": 0,
                + "\t\"yPos\": 0\n" // "yPos": 0
                + "}]"; // }]
        try {
            if (f.exists())
                json = FileUtils.readFileToString(f, Charsets.UTF_8);
            else
                FileUtils.write(f, json);
        } catch (IOException e) {
            logger.warn("Unable to load achievements.", e);
        }
        JsonAchievement[] achieves = new GsonBuilder()
                .registerTypeAdapter(IChatComponent.class, new IChatComponent.Serializer()).create()
                .fromJson(json, JsonAchievement[].class);
        AchievementGet instance = AchievementGet.instance;
        instance.achievements = new StatAchievement[achieves.length];

        logger.info("Registering " + achieves.length + " achievements.");
        // load achievements
        for (int i = 0; i < achieves.length; i++) {
            instance.achievements[i] = new StatAchievement(achieves[i]);
        }
        // register page
        AchievementPage page = new AchievementPage("Achievement Get", AchievementGet.instance.achievements);
        AchievementPage.registerAchievementPage(page);
    }
}

package mnm.mods.achget;

import java.io.File;
import java.io.IOException;

import mnm.mods.achget.StatAchievement.JsonAchievement;
import net.minecraft.util.IChatComponent;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;

public class CommonProxy {

    public void init(File f) {
        Logger logger = AchievementGet.logger;
        // The default
        String json = "[{\n" // [{
                + "\t\"id\": \"walk1000\",\n" // "id": "walk100",
                + "\t\"name\": \"Kilo\",\n" // "name": "Kilo",
                + "\t\"desc\": \"Walk 1km\",\n" // "desc": "Walk 100m",
                + "\t\"stat\": \"stat.walkOneCm\",\n" // "stat": "stat.walkOneCm",
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

        if (achieves.length == 0) {
            logger.info("No achievements registered");
            return;
        }
        // load achievements
        for (int i = 0; i < achieves.length; i++) {
            instance.achievements[i] = new StatAchievement(achieves[i]);
        }
    }
}

package mnm.mods.achget;

import java.io.File;
import java.io.IOException;

import mnm.mods.achget.StatAchievement.JsonAchievement;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.AchievementPage;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

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

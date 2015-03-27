package mnm.mods.achget;

import java.io.File;

import net.minecraftforge.common.AchievementPage;

public class ClientProxy extends CommonProxy {
    @Override
    public void init(File f) {
        super.init(f);

        // register page
        AchievementPage page = new AchievementPage("Achievement Get", AchievementGet.instance.achievements);
        AchievementPage.registerAchievementPage(page);
    }
}

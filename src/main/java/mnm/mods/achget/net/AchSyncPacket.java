package mnm.mods.achget.net;

import java.io.IOException;

import mnm.mods.achget.AchievementGet;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public class AchSyncPacket extends Packet {

    private String json;

    public AchSyncPacket() {
    }

    public AchSyncPacket(String json) {
        this.json = json;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.json = data.readStringFromBuffer(Integer.MAX_VALUE);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(json);
    }

    @Override
    public void processPacket(INetHandler handler) {
        AchievementGet.instance.registerAchivements(json);
    }
}

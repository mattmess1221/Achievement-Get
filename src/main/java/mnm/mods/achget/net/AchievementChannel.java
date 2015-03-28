package mnm.mods.achget.net;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.EnumMap;
import java.util.List;

import mnm.mods.achget.AchievementGet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;

@Sharable
public class AchievementChannel extends MessageToMessageCodec<FMLProxyPacket, Packet> {

    private EnumMap<Side, FMLEmbeddedChannel> channels;

    @Override
    protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
        // TODO Auto-generated method stub
        PacketBuffer payload = new PacketBuffer(msg.payload());
        Packet packet = new AchSyncPacket();
        packet.readPacketData(payload);
        packet.processPacket(null);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) throws Exception {
        // TODO Auto-generated method stub
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        msg.writePacketData(buffer);
        FMLProxyPacket proxyPacket = new FMLProxyPacket(buffer.copy(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL)
                .get());
        out.add(proxyPacket);
    }

    public void init() {
        this.channels = NetworkRegistry.INSTANCE.newChannel(AchievementGet.MODID, this);
    }

    public void sendTo(EntityPlayerMP player, Packet packet) {
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
        this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        this.channels.get(Side.SERVER).writeAndFlush(packet);
    }
}

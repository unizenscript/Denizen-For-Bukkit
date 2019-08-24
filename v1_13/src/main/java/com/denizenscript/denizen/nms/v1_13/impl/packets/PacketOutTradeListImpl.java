package com.denizenscript.denizen.nms.v1_13.impl.packets;

import com.denizenscript.denizen.nms.interfaces.packets.PacketOutTradeList;
import com.denizenscript.denizen.nms.util.TradeOffer;
import io.netty.buffer.Unpooled;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.server.v1_13_R2.PacketDataSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutCustomPayload;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PacketOutTradeListImpl implements PacketOutTradeList {

    private PacketPlayOutCustomPayload internal;
    private int container;
    private List<TradeOffer> tradeOffers;

    public PacketOutTradeListImpl(PacketPlayOutCustomPayload internal, PacketDataSerializer serializer) {
        this.internal = internal;
        try {
            container = serializer.readInt();
            tradeOffers = new ArrayList<>();
            byte tradeCount = serializer.readByte();
            for (byte i = 0; i < tradeCount; i++) {
                ItemStack firstCost = CraftItemStack.asBukkitCopy(serializer.k());
                ItemStack product = CraftItemStack.asBukkitCopy(serializer.k());
                boolean hasSecondCost = serializer.readBoolean();
                ItemStack secondCost = hasSecondCost ? CraftItemStack.asBukkitCopy(serializer.k()) : null;
                boolean usedMaxTimes = serializer.readBoolean();
                int currentUses = serializer.readInt();
                int maxUses = serializer.readInt();
                tradeOffers.add(new TradeOffer(product, firstCost, secondCost, usedMaxTimes, currentUses, maxUses));
            }
        }
        catch (Exception e) {
            Debug.echoError(e);
        }
    }

    @Override
    public List<TradeOffer> getTradeOffers() {
        return tradeOffers;
    }

    @Override
    public void setTradeOffers(List<TradeOffer> tradeOffers) {
        try {
            PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
            serializer.a(PacketPlayOutCustomPayload.a); // MC|TrList -> minecraft:trader_list
            serializer.writeInt(container);
            serializer.writeByte((byte) (tradeOffers.size() & 255));
            for (TradeOffer tradeOffer : tradeOffers) {
                serializer.a(CraftItemStack.asNMSCopy(tradeOffer.getFirstCost()));
                serializer.a(CraftItemStack.asNMSCopy(tradeOffer.getProduct()));
                boolean hasSecondCost = tradeOffer.hasSecondCost();
                serializer.writeBoolean(hasSecondCost);
                if (hasSecondCost) {
                    serializer.a(CraftItemStack.asNMSCopy(tradeOffer.getSecondCost()));
                }
                serializer.writeBoolean(tradeOffer.isUsedMaxTimes());
                serializer.writeInt(tradeOffer.getCurrentUses());
                serializer.writeInt(tradeOffer.getMaxUses());
            }
            internal.a(serializer);
        }
        catch (Exception e) {
            Debug.echoError(e);
        }
    }
}

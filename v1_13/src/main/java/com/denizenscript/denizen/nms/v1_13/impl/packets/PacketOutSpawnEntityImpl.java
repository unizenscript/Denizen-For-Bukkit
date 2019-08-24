package com.denizenscript.denizen.nms.v1_13.impl.packets;

import com.denizenscript.denizen.nms.interfaces.packets.PacketOutSpawnEntity;
import com.denizenscript.denizen.nms.util.ReflectionHelper;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.EntityTracker;
import net.minecraft.server.v1_13_R2.EntityTrackerEntry;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntityExperienceOrb;
import net.minecraft.server.v1_13_R2.WorldServer;

import java.util.UUID;

public class PacketOutSpawnEntityImpl implements PacketOutSpawnEntity {

    private Packet internal;
    private int entityId;
    private UUID entityUuid;

    public PacketOutSpawnEntityImpl(EntityPlayer player, Packet internal) {
        this.internal = internal;
        Integer integer = ReflectionHelper.getFieldValue(internal.getClass(), "a", internal);
        entityId = integer != null ? integer : -1;
        if (!(internal instanceof PacketPlayOutSpawnEntityExperienceOrb)) {
            entityUuid = ReflectionHelper.getFieldValue(internal.getClass(), "b", internal);
        }
        else {
            EntityTracker tracker = ((WorldServer) player.world).tracker;
            EntityTrackerEntry entry = tracker.trackedEntities.get(entityId);
            entityUuid = entry != null ? entry.b().getUniqueID() : null;
        }
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public UUID getEntityUuid() {
        return entityUuid;
    }
}

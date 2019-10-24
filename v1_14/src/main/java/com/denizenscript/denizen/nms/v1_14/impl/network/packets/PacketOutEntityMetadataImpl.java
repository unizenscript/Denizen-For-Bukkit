package com.denizenscript.denizen.nms.v1_14.impl.network.packets;

import com.denizenscript.denizen.nms.interfaces.packets.PacketOutEntityMetadata;
import com.denizenscript.denizen.nms.util.ReflectionHelper;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityMetadata;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class PacketOutEntityMetadataImpl implements PacketOutEntityMetadata {

    private PacketPlayOutEntityMetadata internal;
    private int entityId;
    private List<DataWatcher.Item<?>> metadata;

    public PacketOutEntityMetadataImpl(PacketPlayOutEntityMetadata internal) {
        this.internal = internal;
        try {
            entityId = ENTITY_ID.getInt(internal);
            metadata = (List<DataWatcher.Item<?>>) METADATA.get(internal);
        }
        catch (Exception e) {
            Debug.echoError(e);
        }
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public boolean checkForGlow() {
        for (DataWatcher.Item<?> data : metadata) {
            if (data.a().a() == 0) {
                // TODO: Instead of cancelling, casually strip out the 0x40 "Glowing" metadata rather than cancelling entirely?
                return true;
            }
        }
        return false;
    }

    private static final Field ENTITY_ID, METADATA;

    static {
        Map<String, Field> fields = ReflectionHelper.getFields(PacketPlayOutEntityMetadata.class);
        ENTITY_ID = fields.get("a");
        METADATA = fields.get("b");
    }
}

package com.denizenscript.denizen.nms.v1_14.impl.entities;

import com.denizenscript.denizen.nms.interfaces.FakeArrow;
import net.minecraft.server.v1_14_R1.EntityArrow;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftArrow;

public class CraftFakeArrowImpl extends CraftArrow implements FakeArrow {

    public CraftFakeArrowImpl(CraftServer craftServer, EntityArrow entityArrow) {
        super(craftServer, entityArrow);
    }

    @Override
    public void remove() {
        if (getPassenger() != null) {
            return;
        }
        super.remove();
    }

    @Override
    public String getEntityTypeName() {
        return "FAKE_ARROW";
    }
}

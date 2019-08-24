package com.denizenscript.denizen.nms.v1_14.helpers;

import com.denizenscript.denizen.nms.abstracts.AnimationHelper;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPolarBear;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSkeleton;
import org.bukkit.entity.EntityType;

public class AnimationHelperImpl extends AnimationHelper {

    public AnimationHelperImpl() {
        register("SKELETON_START_SWING_ARM", entity -> {
            if (entity.getType() == EntityType.SKELETON) {
                ((CraftSkeleton) entity).getHandle().q(true);
            }
        });
        register("SKELETON_STOP_SWING_ARM", entity -> {
            if (entity.getType() == EntityType.SKELETON) {
                ((CraftSkeleton) entity).getHandle().q(false);
            }
        });
        register("POLAR_BEAR_START_STANDING", entity -> {
            if (entity.getType() == EntityType.POLAR_BEAR) {
                ((CraftPolarBear) entity).getHandle().r(true);
            }
        });
        register("POLAR_BEAR_STOP_STANDING", entity -> {
            if (entity.getType() == EntityType.POLAR_BEAR) {
                ((CraftPolarBear) entity).getHandle().r(false);
            }
        });
    }
}

package dev.unizen.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.entity.Raider;

public class EntityPatrolTarget implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && ((EntityTag) entity).getBukkitEntity() instanceof Raider;
    }

    public static EntityPatrolTarget getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityPatrolTarget((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "patrol_target"
    };

    public static final String[] handledMechs = new String[] {
            "patrol_target"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityPatrolTarget(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return new LocationTag(((Raider) entity.getBukkitEntity()).getPatrolTarget().getLocation()).identify();
    }

    @Override
    public String getPropertyId() {
        return "patrol_target";
    }

    ///////////
    // ObjectTag Attributes
    ////////

    @Override
    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <EntityTag.patrol_target>
        // @returns LocationTag
        // @mechanism EntityTag.patrol_target
        // @group properties
        // @description
        // If the entity is a raider, returns the location the raider is targeting to patrol.
        // -->
        if (attribute.startsWith("patrol_target")) {
            return new LocationTag(((Raider) entity.getBukkitEntity()).getPatrolTarget().getLocation()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name patrol_target
        // @input LocationTag
        // @description
        // If the entity is a raider, sets the location the raider will target to patrol.
        // Provide no input to set no target.
        // @tags
        // <EntityTag.patrol_target>
        // -->
        if (mechanism.matches("patrol_target")) {
            Raider raider = (Raider) entity.getBukkitEntity();
            if (!mechanism.hasValue()) {
                raider.setPatrolTarget(null);
            }
            else {
                if (mechanism.requireObject(LocationTag.class)) {
                    raider.setPatrolTarget(mechanism.valueAsType(LocationTag.class).getBlock());
                }
            }
        }
    }
}

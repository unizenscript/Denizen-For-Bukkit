package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vex;

public class EntityCharging implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && ((EntityTag) entity).getBukkitEntityType() == EntityType.VEX;
    }

    public static EntityCharging getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityCharging((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "is_charging"
    };

    public static final String[] handledMechs = new String[] {
            "is_charging"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityCharging(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return String.valueOf(((Vex) entity.getBukkitEntity()).isCharging());
    }

    @Override
    public String getPropertyId() {
        return "is_charging";
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
        // @attribute <EntityTag.is_charging>
        // @returns ElementTag(Boolean)
        // @mechanism EntityTag.is_charging
        // @group properties
        // @description
        // If the entity is a vex, returns whether it is charging.
        // If set to true, the Vex will have a red angry texture.
        // -->
        if (attribute.startsWith("is_charging")) {
            return new ElementTag(((Vex) entity.getBukkitEntity()).isCharging()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name is_charging
        // @input ElementTag(Boolean)
        // @description
        // If the entity is a vex, sets the charging state of the entity.
        // @tags
        // <EntityTag.is_charging>
        // -->
        if (mechanism.matches("is_charging") && mechanism.requireBoolean()) {
            ((Vex) entity.getBukkitEntity()).setCharging(mechanism.getValue().asBoolean());
        }
    }
}

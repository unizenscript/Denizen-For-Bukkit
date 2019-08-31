package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;

public class EntityCritical implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && NMSHandler.getArrowHelper().isArrow(((EntityTag) entity).getBukkitEntity());
    }

    public static EntityCritical getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        else {
            return new EntityCritical((EntityTag) entity);
        }
    }

    public static final String[] handledTags = new String[] {
            "critical"
    };

    public static final String[] handledMechs = new String[] {
            "critical"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityCritical(EntityTag entity) {
        this.entity = entity;
    }

    EntityTag entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return NMSHandler.getArrowHelper().isCritical(entity.getBukkitEntity()) ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "critical";
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
        // @attribute <EntityTag.critical>
        // @returns ElementTag(Boolean)
        // @mechanism EntityTag.critical
        // @group properties
        // @description
        // If the entity is an arrow or trident, returns whether the arrow/trident is a critical arrow/trident.
        // -->
        if (attribute.startsWith("critical")) {
            return new ElementTag(NMSHandler.getArrowHelper().isCritical(entity.getBukkitEntity()))
                    .getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name critical
        // @input Element(Boolean)
        // @description
        // Changes whether an arrow/trident is a critical arrow/trident.
        // @tags
        // <EntityTag.critical>
        // -->

        if (mechanism.matches("critical") && mechanism.requireBoolean()) {
            NMSHandler.getArrowHelper().setCritical(entity.getBukkitEntity(), mechanism.getValue().asBoolean());
        }
    }
}

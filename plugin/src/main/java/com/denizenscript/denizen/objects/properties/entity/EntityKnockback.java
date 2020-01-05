package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;

public class EntityKnockback implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && NMSHandler.getArrowHelper().isArrow(((EntityTag) entity).getBukkitEntity());
    }

    public static EntityKnockback getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        else {
            return new EntityKnockback((EntityTag) entity);
        }
    }

    public static final String[] handledTags = new String[] {
            "knockback"
    };

    public static final String[] handledMechs = new String[] {
            "knockback"
    };

    private EntityKnockback(EntityTag entity) {
        this.entity = entity;
    }

    EntityTag entity;

    @Override
    public String getPropertyString() {
        return String.valueOf(NMSHandler.getArrowHelper().getKnockbackStrength(entity.getBukkitEntity()));
    }

    @Override
    public String getPropertyId() {
        return "knockback";
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {

        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <EntityTag.knockback>
        // @returns ElementTag(Number)
        // @mechanism EntityTag.knockback
        // @group properties
        // @description
        // If the entity is an arrow or trident, returns the knockback strength of the arrow/trident.
        // -->
        if (attribute.startsWith("knockback")) {
            return new ElementTag(NMSHandler.getArrowHelper().getKnockbackStrength(entity.getBukkitEntity()))
                    .getObjectAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name knockback
        // @input ElementTag(Number)
        // @description
        // Changes an arrow's/trident's knockback strength.
        // @tags
        // <EntityTag.knockback>
        // -->
        if (mechanism.matches("knockback") && mechanism.requireInteger()) {
            NMSHandler.getArrowHelper().setKnockbackStrength(entity.getBukkitEntity(), mechanism.getValue().asInt());
        }
    }
}

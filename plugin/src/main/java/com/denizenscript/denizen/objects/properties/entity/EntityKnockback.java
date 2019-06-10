package net.aufdemrand.denizen.objects.properties.entity;

import net.aufdemrand.denizen.nms.NMSHandler;
import net.aufdemrand.denizen.objects.EntityTag;
import net.aufdemrand.denizencore.objects.ElementTag;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.ObjectTag;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;

public class EntityKnockback implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && NMSHandler.getInstance().getArrowHelper().isArrow(((EntityTag) entity).getBukkitEntity());
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


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityKnockback(EntityTag entity) {
        this.entity = entity;
    }

    EntityTag entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return String.valueOf(NMSHandler.getInstance().getArrowHelper().getKnockbackStrength(entity.getBukkitEntity()));
    }

    @Override
    public String getPropertyId() {
        return "knockback";
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
        // @attribute <EntityTag.knockback>
        // @returns ElementTag(Number)
        // @mechanism EntityTag.knockback
        // @group properties
        // @description
        // If the entity is an arrow or trident, returns the knockback strength of the arrow/trident.
        // -->
        if (attribute.startsWith("knockback")) {
            return new ElementTag(NMSHandler.getInstance().getArrowHelper().getKnockbackStrength(entity.getBukkitEntity()))
                    .getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name knockback
        // @input Element(Number)
        // @description
        // Changes an arrow's/trident's knockback strength.
        // @tags
        // <EntityTag.knockback>
        // -->

        if (mechanism.matches("knockback") && mechanism.requireInteger()) {
            NMSHandler.getInstance().getArrowHelper().setKnockbackStrength(entity.getBukkitEntity(), mechanism.getValue().asInt());
        }
    }
}

package net.aufdemrand.denizen.objects.properties.entity;

import net.aufdemrand.denizen.nms.NMSHandler;
import net.aufdemrand.denizen.objects.EntityTag;
import net.aufdemrand.denizencore.objects.ElementTag;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dObjectTag;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;

public class EntityCritical implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && NMSHandler.getInstance().getArrowHelper().isArrow(((EntityTag) entity).getBukkitEntity());
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
        return String.valueOf(NMSHandler.getInstance().getArrowHelper().isCritical(entity.getBukkitEntity()));
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
            return new ElementTag(NMSHandler.getInstance().getArrowHelper().isCritical(entity.getBukkitEntity()))
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
            NMSHandler.getInstance().getArrowHelper().setCritical(entity.getBukkitEntity(), mechanism.getValue().asBoolean());
        }
    }
}

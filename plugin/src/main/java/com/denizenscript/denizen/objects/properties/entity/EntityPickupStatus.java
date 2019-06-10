package net.aufdemrand.denizen.objects.properties.entity;

import net.aufdemrand.denizen.nms.NMSHandler;
import net.aufdemrand.denizen.objects.EntityTag;
import net.aufdemrand.denizencore.objects.ElementTag;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.ObjectTag;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;

public class EntityPickupStatus implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && NMSHandler.getInstance().getArrowHelper().isArrow(((EntityTag) entity).getBukkitEntity());
    }

    public static EntityPickupStatus getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityPickupStatus((EntityTag) entity);
    }

    public static final String[] handledTags = {
            "pickup_status"
    };

    public static final String[] handledMechs = {
            "pickup_status"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityPickupStatus(EntityTag entity) {
        this.entity = entity;
    }

    EntityTag entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return NMSHandler.getArrowHelper().getPickupStatus(entity.getBukkitEntity());
    }

    @Override
    public String getPropertyId() {
        return "pickup_status";
    }

    ///////////
    // ObjectTag Attributes
    ////////

    @Override
    public String getAttribute(Attribute attribute) {

        if (attribute == null) {
            return "null";
        }

        // <--[tag]
        // @attribute <EntityTag.pickup_status>
        // @returns ElementTag
        // @mechanism EntityTag.pickup_status
        // @group properties
        // @description
        // If the entity is an arrow or trident, returns the pickup status of the arrow/trident.
        // -->
        if (attribute.startsWith("pickup_status")) {
            return new ElementTag(NMSHandler.getArrowHelper().getPickupStatus(entity.getBukkitEntity()))
                    .getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name pickup_status
        // @input Element
        // @description
        // Changes the pickup status of an arrow/trident.
        // Available pickup statuses can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/AbstractArrow.PickupStatus.html>
        // @tags
        // <EntityTag.pickup_status>
        // -->

        if (mechanism.matches("pickup_status")) {
            NMSHandler.getArrowHelper().setPickupStatus(entity.getBukkitEntity(), mechanism.getValue().asString());
        }
    }
}

package dev.unizen.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;

public class EntityFoxCrouching implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && ((EntityTag) entity).getBukkitEntityType() == EntityType.FOX;
    }

    public static EntityFoxCrouching getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityFoxCrouching((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "crouching"
    };

    public static final String[] handledMechs = new String[] {
            "crouching"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityFoxCrouching(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return String.valueOf(((Fox) entity.getBukkitEntity()).isCrouching());
    }

    @Override
    public String getPropertyId() {
        return "crouching";
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
        // @attribute <EntityTag.crouching>
        // @returns ElementTag(Boolean)
        // @mechanism EntityTag.crouching
        // @group properties
        // @description
        // If the entity is a fox, returns whether the entity is crouching.
        // -->
        if (attribute.startsWith("crouching")) {
            return new ElementTag(((Fox) entity.getBukkitEntity()).isCrouching()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name crouching
        // @input ElementTag(Boolean)
        // @description
        // If the entity is a fox, sets whether the fox is crouching.
        // @tags
        // <EntityTag.crouching>
        // -->
        if (mechanism.matches("crouching") && mechanism.requireBoolean()) {
            ((Fox) entity.getBukkitEntity()).setCrouching(mechanism.getValue().asBoolean());
        }
    }
}

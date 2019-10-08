package dev.unizen.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;

public class EntityFoxType implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && ((EntityTag) entity).getBukkitEntityType() == EntityType.FOX;
    }

    public static EntityFoxType getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityFoxType((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "fox_type"
    };

    public static final String[] handledMechs = new String[] {
            "fox_type"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityFoxType(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return ((Fox) entity.getBukkitEntity()).getFoxType().name();
    }

    @Override
    public String getPropertyId() {
        return "fox_type";
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
        // @attribute <EntityTag.fox_type>
        // @returns ElementTag
        // @mechanism EntityTag.fox_type
        // @group properties
        // @description
        // If the entity is a fox, return its type.
        // Can be either RED or SNOW.
        // -->
        if (attribute.startsWith("fox_type")) {
            return new ElementTag(((Fox) entity.getBukkitEntity()).getFoxType().name()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name fox_type
        // @input ElementTag
        // @description
        // Sets the fox entity's fox type.
        // Can be either RED or SNOW.
        // @tags
        // <EntityTag.fox_type>
        // -->
        if (mechanism.matches("fox_type") && mechanism.requireEnum(false, Fox.Type.values())) {
            ((Fox) entity.getBukkitEntity()).setFoxType(Fox.Type.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}

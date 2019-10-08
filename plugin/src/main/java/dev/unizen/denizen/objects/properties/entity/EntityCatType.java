package dev.unizen.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;

public class EntityCatType implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && ((EntityTag) entity).getBukkitEntityType() == EntityType.CAT;
    }

    public static EntityCatType getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityCatType((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "cat_type"
    };

    public static final String[] handledMechs = new String[] {
            "cat_type"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityCatType(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return ((Cat) entity.getBukkitEntity()).getCatType().name();
    }

    @Override
    public String getPropertyId() {
        return "cat_type";
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
        // @attribute <EntityTag.cat_type>
        // @returns ElementTag
        // @mechanism EntityTag.cat_type
        // @group properties
        // @description
        // Returns the cat type of this entity.
        // -->
        if (attribute.startsWith("cat_type")) {
            return new ElementTag(((Cat) entity.getBukkitEntity()).getCatType().name()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name cat_type
        // @input ElementTag
        // @description
        // Sets the cat type of this entity.
        // Valid cat types are: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Cat.Type.html>
        // @tags
        // <EntityTag.cat_type>
        // -->
        if (mechanism.matches("cat_type") && mechanism.requireEnum(false, Cat.Type.values())) {
            ((Cat) entity.getBukkitEntity()).setCatType(Cat.Type.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}

package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;

import java.util.ArrayList;
import java.util.List;

public class MaterialMultipleFacing implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof MultipleFacing;
    }

    public static MaterialMultipleFacing getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialMultipleFacing((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "valid_faces", "has_face", "faces"
    };

    public static final String[] handledMechs = new String[] {
            "faces"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialMultipleFacing(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private MultipleFacing getMultipleFacing() {
        return (MultipleFacing) material.getModernData().data;
    }

    private List<String> getFaces() {
        List<String> faces = new ArrayList<>();
        for (BlockFace face : getMultipleFacing().getFaces()) {
            faces.add(face.name());
        }
        return faces;
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return !getFaces().isEmpty() ? new ListTag(getFaces()).identify() : null;
    }

    @Override
    public String getPropertyId() {
        return "faces";
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
        // @attribute <MaterialTag.valid_faces>
        // @returns ListTag
        // @group properties
        // @description
        // If the material can have faces, returns which faces the material's texture can be displayed on.
        // A list of all faces can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockFace.html>
        // -->
        if (attribute.startsWith("valid_faces")) {
            ListTag allowedFaces = new ListTag();
            for (BlockFace face : getMultipleFacing().getAllowedFaces()) {
                allowedFaces.add(face.name());
            }
            return allowedFaces.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.has_face[<face>]>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.faces
        // @group properties
        // @description
        // If the material can have faces, returns whether the material's texture are displayed on this face.
        // A list of all faces can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockFace.html>
        // -->
        if (attribute.startsWith("has_face") && attribute.hasContext(1)) {
            String input = attribute.getContext(1);
            BlockFace face;
            try {
                face = BlockFace.valueOf(input.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                Debug.echoError("Invalid face!");
                return null;
            }
        }

        // <--[tag]
        // @attribute <MaterialTag.faces>
        // @returns ListTag
        // @mechanism MaterialTag.faces
        // @group properties
        // @description
        // If the material can have faces, returns a list of faces that the material's texture are displayed on.
        // A list of all faces can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockFace.html>
        // -->
        if (attribute.startsWith("faces")) {
            return new ListTag(getFaces()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name faces
        // @input ListTag
        // @description
        // Sets the faces that the material's textures are displayed on.
        // A list of all faces can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockFace.html>
        // @tags
        // <MaterialTag.faces>
        // <MaterialTag.valid_faces>
        // <MaterialTag.has_face[<face>]>
        // -->
        if (mechanism.matches("faces")) {
            List<BlockFace> validFaces = new ArrayList<>();
            for (String input : mechanism.valueAsType(ListTag.class)) {
                BlockFace face;
                try {
                    face = BlockFace.valueOf(input.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    continue;
                }
                if (!getMultipleFacing().getAllowedFaces().contains(face)) {
                    continue;
                }
                validFaces.add(face);
            }
            if (validFaces.isEmpty()) {
                Debug.echoError("No valid face was specified!");
                return;
            }
            for (BlockFace face : getMultipleFacing().getFaces()) {
                getMultipleFacing().setFace(face, false);
            }
            for (BlockFace face : validFaces) {
                getMultipleFacing().setFace(face, true);
            }
        }
    }
}

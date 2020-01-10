package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
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

    private List<String> getValidFaces() {
        List<String> faces = new ArrayList<>();
        for (BlockFace face : getMultipleFacing().getAllowedFaces()) {
            faces.add(face.name());
        }
        return faces;
    }

    private boolean hasFace(BlockFace face) {
        return getMultipleFacing().hasFace(face);
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

    public static void registerTags() {

        // <--[tag]
        // @attribute <MaterialTag.valid_faces>
        // @returns ListTag
        // @group properties
        // @description
        // If the material can have faces, returns which faces the material's texture can be displayed on.
        // A list of all faces can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockFace.html>
        // -->
        PropertyParser.<MaterialMultipleFacing>registerTag("valid_faces", (attribute, material) -> new ListTag(material.getValidFaces()));

        // <--[tag]
        // @attribute <MaterialTag.faces>
        // @returns ListTag
        // @mechanism MaterialTag.faces
        // @group properties
        // @description
        // If the material can have faces, returns a list of faces that the material's texture are displayed on.
        // A list of all faces can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockFace.html>
        // -->
        PropertyParser.<MaterialMultipleFacing>registerTag("faces", (attribute, material) -> new ListTag(material.getFaces()));

        // <--[tag]
        // @attribute <MaterialTag.has_face[<face>]>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.faces
        // @group properties
        // @description
        // If the material can have faces, returns whether the material's texture are displayed on this face.
        // A list of all faces can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockFace.html>
        // -->
        PropertyParser.<MaterialMultipleFacing>registerTag("has_face", (attribute, material) -> {
            if (!attribute.hasContext(1)) {
                Debug.echoError("Context is required for <MaterialTag.has_face[<face>]>!");
                return null;
            }
            BlockFace face;
            try {
                face = BlockFace.valueOf(attribute.getContext(1).toUpperCase());
            }
            catch (Exception e) {
                Debug.echoError("Invalid face specified!");
                return null;
            }
            return new ElementTag(material.hasFace(face));
        });
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

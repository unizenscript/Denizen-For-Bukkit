package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.RedstoneWire;

import java.util.*;

public class MaterialRedstoneConnection implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof RedstoneWire;
    }

    public static MaterialRedstoneConnection getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialRedstoneConnection((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "redstone_connections", "redstone_connection_to"
    };

    public static final String[] handledMechs = new String[] {
            "redstone_connections"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialRedstoneConnection(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private RedstoneWire getRedstoneWire() {
        return (RedstoneWire) material.getModernData().data;
    }

    private Set<BlockFace> getAllowedFaces() {
        return getRedstoneWire().getAllowedFaces();
    }

    private RedstoneWire.Connection getConnectionOnFace(BlockFace face) {
        return getRedstoneWire().getFace(face);
    }

    private Set<String> getConnections() {
        Set<String> output = new HashSet<>();
        for (BlockFace face : getAllowedFaces()) {
            if (getRedstoneWire().getFace(face) != RedstoneWire.Connection.NONE) {
                output.add(face.name() + "/" + getConnectionOnFace(face).name());
            }
        }
        return output;
    }

    private void debugValidDirection() {
        Debug.echoError("Invalid direction! Must be \"NORTH\", \"EAST\", \"SOUTH\", or \"WEST\"!");
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return !getConnections().isEmpty() ? new ListTag(getConnections()).identify() : null;
    }

    @Override
    public String getPropertyId() {
        return "redstone_connections";
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
        // @attribute <MaterialTag.redstone_connections>
        // @returns ListTag(ElementTag/ElementTag)
        // @mechanism MaterialTag.redstone_connections
        // @group properties
        // @description
        // Returns a list of all connections that the redstone wire material has.
        // -->
        if (attribute.startsWith("redstone_connections")) {
            return new ListTag(getConnections()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.redstone_connection_to[<direction>]>
        // @returns ElementTag
        // @mechanism MaterialTag.redstone_connections
        // @group properties
        // @description
        // Returns the type of connection that the redstone wire material has in the specified direction.
        // The direction can be NORTH, EAST, SOUTH, or WEST. The returned connection can be either SIDE or UP.
        // NOTE: A connection of type SIDE also covers the case of the redstone wire going down the side of the block.
        // -->
        if (attribute.startsWith("redstone_connection_to") && attribute.hasContext(1)) {
            String direction = attribute.getContext(1).toUpperCase();
            BlockFace face;
            try {
                face = BlockFace.valueOf(direction);
            }
            catch (IllegalArgumentException e) {
                debugValidDirection();
                return null;
            }
            if (face != BlockFace.NORTH && face != BlockFace.EAST && face != BlockFace.SOUTH && face != BlockFace.WEST) {
                debugValidDirection();
                return null;
            }
            return new ElementTag(getConnectionOnFace(face).name()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name redstone_connections
        // @input ListTag(ElementTag/ElementTag)
        // @description
        // Sets the redstone wire material's connections.
        // In the format DIRECTION/CONNECTION|...
        // The direction can be NORTH, EAST, SOUTH, or WEST.
        // The connection can be NONE, SIDE, or UP.
        // NOTE: Setting a connection to NONE does nothing, as this disconnects the wire from that face.
        // NOTE: A connection of type SIDE also covers the case of the redstone wire going down the side of the block.
        // @tags
        // <MaterialTag.redstone_connections>
        // <MaterialTag.redstone_connection_to[<direction>]>
        // -->
        if (mechanism.matches("redstone_connections") && mechanism.requireObject(ListTag.class)) {
            for (BlockFace face : getAllowedFaces()) {
                getRedstoneWire().setFace(face, RedstoneWire.Connection.NONE);
            }

            for (String data : mechanism.valueAsType(ListTag.class)) {
                List<String> faceData = CoreUtilities.split(data, '/');

                BlockFace face;
                RedstoneWire.Connection connection;
                if (faceData.size() != 2) {
                    continue;
                }
                try {
                    face = BlockFace.valueOf(faceData.get(0).toUpperCase());
                    connection = RedstoneWire.Connection.valueOf(faceData.get(1).toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    continue;
                }

                getRedstoneWire().setFace(face, connection);
            }
        }
    }
}

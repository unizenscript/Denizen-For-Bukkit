package com.denizenscript.denizen.objects.properties.item;

import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.util.jnbt.*;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.utilities.nbt.CustomNBT;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.core.EscapeTagBase;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.Deprecations;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ItemNBT implements Property {

    public static boolean describes(ObjectTag item) {
        return item instanceof ItemTag;
    }

    public static ItemNBT getFrom(ObjectTag item) {
        if (!describes(item)) {
            return null;
        }
        else {
            return new ItemNBT((ItemTag) item);
        }
    }

    public static final String[] handledTags = new String[] {
            "raw_nbt", "has_raw_nbt", "raw_nbt_keys", "has_nbt", "nbt_keys", "nbt"
    };

    public static final String[] handledMechs = new String[] {
            "remove_nbt", "nbt"
    };

    private ItemNBT(ItemTag item) {
        this.item = item;
    }

    ItemTag item;

    // Unizen start

    private Map<String, Tag> getRawNBTMap() {
        return NMSHandler.getItemHelper().getNbtData(item.getItemStack()).getValue();
    }

    private String rawNbtKeyToString(Tag tag) {
        if (tag instanceof CompoundTag) {
            return unwrapCompoundTag((CompoundTag) tag);
        }
        if (tag instanceof StringTag) {
            return "\"" + ((StringTag) tag).getValue().replace("\"", "\\\"") + "\"";
        }
        if (tag instanceof ByteTag) {
            return tag.getValue() + "b";
        }
        if (tag instanceof ShortTag) {
            return tag.getValue() + "s";
        }
        if (tag instanceof DoubleTag) {
            return tag.getValue() + "d";
        }
        if (tag instanceof FloatTag) {
            return tag.getValue() + "f";
        }
        ListTag listResult = rawNbtTagToList(tag);
        if (listResult != null) {
            StringBuilder build = new StringBuilder("[");
            for (int i = 0; i < listResult.size(); i++) {
                build.append(listResult.get(i)).append(",");
            }
            return build.substring(0, build.length() - 1) + "]";
        }
        return String.valueOf(tag.getValue());
    }

    private String unwrapCompoundTag(CompoundTag tag) {
        Map<String, Tag> tagMap = tag.getValue();
        if (tagMap.isEmpty()) {
            return "{}";
        }
        StringBuilder output = new StringBuilder("{");
        for (String key : tagMap.keySet()) {
            output.append(key).append(":");
            Tag result = tagMap.get(key);
            output.append(rawNbtKeyToString(result));
            output.append(",");
        }
        return output.substring(0, output.length() - 1) + "}";
    }

    private ListTag rawNbtTagToList(Tag tag) {
        if (tag instanceof JNBTListTag) {
            return rawNbtTagToList((JNBTListTag) tag);
        }
        else if (tag instanceof IntArrayTag) {
            return rawNbtTagToList((IntArrayTag) tag);
        }
        else if (tag instanceof ByteArrayTag) {
            return rawNbtTagToList((ByteArrayTag) tag);
        }
        return null;
    }

    private ListTag rawNbtTagToList(JNBTListTag listNBT) {
        ListTag list = new ListTag();
        for (Tag inList : listNBT.getValue()) {
            String result = inList.getValue().toString();
            if (inList instanceof CompoundTag) {
                result = unwrapCompoundTag((CompoundTag) inList);
            }
            list.add(result);
        }
        return list;
    }

    private ListTag rawNbtTagToList(IntArrayTag intArray) {
        ListTag list = new ListTag();
        for (int inList : intArray.getValue()) {
            list.add(String.valueOf(inList));
        }
        return list;
    }

    private ListTag rawNbtTagToList(ByteArrayTag byteArray) {
        ListTag list = new ListTag();
        for (int inList : byteArray.getValue()) {
            list.add(inList + "b");
        }
        return list;
    }

    private Tag getRawNbtTagForKey(String key) {
        if (key == null) {
            return null;
        }

        List<String> parts = CoreUtilities.split(key, '.');
        Tag getForKey = getRawNBTMap().get(parts.get(0));
        for (int i = 1; i < parts.size(); i++) {
            if (getForKey instanceof CompoundTag) {
                getForKey = ((CompoundTag) getForKey).getValue().get(parts.get(i));
            }
            else {
                return null;
            }
        }
        return getForKey;
    }


    private ListTag listRawNbtKeys(String startKey) {
        ListTag list = new ListTag();
        Map<String, Tag> startMap = getRawNBTMap();
        if (startKey != null) {
            if (startMap.get(startKey) instanceof CompoundTag) {
                startMap = ((CompoundTag) startMap.get(startKey)).getValue();
            }
        }
        String keyPrefix = startKey == null ? "" : startKey + ".";
        for (String key : startMap.keySet()) {
            list.add(keyPrefix + key);
            if (getRawNBTMap().get(key) instanceof CompoundTag) {
                for (String nestedKey : listRawNbtKeys(key)) {
                    list.add(keyPrefix + nestedKey);
                }
            }
        }
        return list;
    }

    // Unizen end

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {

        if (attribute == null) {
            return null;
        }

        // Unizen start

        // <--[tag]
        // @attribute <ItemTag.has_raw_nbt[<key>]>
        // @returns ElementTag(Boolean)
        // @group properties
        // @description
        // Returns whether the item has the specified raw NBT key.
        // -->
        if (attribute.startsWith("has_raw_nbt")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("Must specify an NBT key for <ItemTag.has_raw_nbt[<key>]>!");
                return null;
            }
            return new ElementTag(getRawNbtTagForKey(attribute.getContext(1)) != null)
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ItemTag.raw_nbt_keys[(<key>)]>
        // @returns ListTag
        // @group properties
        // @description
        // Returns a list of all raw NBT keys on the item.
        // -->
        if (attribute.startsWith("raw_nbt_keys")) {
            return listRawNbtKeys(attribute.getContext(1)).getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ItemTag.raw_nbt[<key>]>
        // @returns ElementTag/ListTag
        // @group properties
        // @description
        // Returns the value of the raw NBT key specified.
        // If the NBT value is a list or array, a ListTag is returned.
        // -->
        if (attribute.startsWith("raw_nbt")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("Must specify an NBT key for <ItemTag.raw_nbt[<key>]>!");
                return null;
            }
            String key = attribute.getContext(1);
            Tag tag = getRawNbtTagForKey(key);
            ListTag listTag = rawNbtTagToList(tag);
            if (tag == null) {
                Debug.echoError("Could not find the value for the raw NBT key \"" + key + "\"!");
                return null;
            }
            if (listTag != null) {
                return listTag.getObjectAttribute(attribute.fulfill(1));
            }
            return new ElementTag(rawNbtKeyToString(tag)).getObjectAttribute(attribute.fulfill(1));
        }

        // Unizen end

        // <--[tag]
        // @attribute <ItemTag.has_nbt[<key>]>
        // @returns ElementTag(Boolean)
        // @mechanism ItemTag.nbt
        // @group properties
        // @deprecated Use has_flag[...] instead.
        // @description
        // Deprecated: use <@link tag FlaggableObject.has_flag> instead.
        // -->
        if (attribute.startsWith("has_nbt")) {
            //Deprecations.itemNbt.warn(attribute.context);
            return new ElementTag(CustomNBT.hasCustomNBT(item.getItemStack(), attribute.getContext(1), CustomNBT.KEY_DENIZEN))
                    .getObjectAttribute(attribute.fulfill(1));
        }

        if (attribute.startsWith("nbt_keys")) {
            //Deprecations.itemNbt.warn(attribute.context);
            return new ListTag(CustomNBT.listNBT(item.getItemStack(), CustomNBT.KEY_DENIZEN))
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ItemTag.nbt[<key>]>
        // @returns ElementTag
        // @mechanism ItemTag.nbt
        // @group properties
        // @deprecated Use flag[...] instead.
        // @description
        // Deprecated: use <@link tag FlaggableObject.flag> instead.
        // -->
        if (attribute.matches("nbt")) {
            //Deprecations.itemNbt.warn(attribute.context);
            if (!attribute.hasContext(1)) {
                ListTag list = getNBTDataList();
                if (list == null) {
                    return null;
                }
                return list.getObjectAttribute(attribute.fulfill(1));
            }
            String res = CustomNBT.getCustomNBT(item.getItemStack(), attribute.getContext(1), CustomNBT.KEY_DENIZEN);
            if (res == null) {
                return null;
            }
            return new ElementTag(res)
                    .getObjectAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public ListTag getNBTDataList() {
        ItemStack itemStack = item.getItemStack();
        List<String> nbtKeys = CustomNBT.listNBT(itemStack, CustomNBT.KEY_DENIZEN);
        if (nbtKeys != null && !nbtKeys.isEmpty()) {
            ListTag list = new ListTag();
            for (String key : nbtKeys) {
                list.add(EscapeTagBase.escape(key) + "/" + EscapeTagBase.escape(CustomNBT.getCustomNBT(itemStack, key, CustomNBT.KEY_DENIZEN)));
            }
            return list;
        }
        return null;
    }

    @Override
    public String getPropertyString() {
        ListTag list = getNBTDataList();
        if (list == null) {
            return null;
        }
        return list.identify();
    }

    @Override
    public String getPropertyId() {
        return "nbt";
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object ItemTag
        // @name remove_nbt
        // @input ListTag
        // @deprecated Use 'flag' instead.
        // @description
        // Deprecated: use <@link mechanism ItemTag.flag> instead.
        // @tags
        // <ItemTag.nbt>
        // <ItemTag.has_nbt>
        // -->
        if (mechanism.matches("remove_nbt")) {
            //Deprecations.itemNbt.warn(mechanism.context);
            if (item.getMaterial().getMaterial() == Material.AIR) {
                mechanism.echoError("Cannot apply NBT to AIR!");
                return;
            }
            ItemStack itemStack = item.getItemStack();
            List<String> list;
            if (mechanism.hasValue()) {
                list = mechanism.valueAsType(ListTag.class);
            }
            else {
                list = CustomNBT.listNBT(itemStack, CustomNBT.KEY_DENIZEN);
            }
            for (String string : list) {
                itemStack = CustomNBT.removeCustomNBT(itemStack, string, CustomNBT.KEY_DENIZEN);
            }
            item.setItemStack(itemStack);
        }

        // <--[mechanism]
        // @object ItemTag
        // @name nbt
        // @input ListTag
        // @deprecated Use 'flag' instead.
        // @description
        // Deprecated: use <@link mechanism ItemTag.flag> instead.
        // @tags
        // <ItemTag.nbt>
        // <ItemTag.has_nbt>
        // -->
        if (mechanism.matches("nbt")) {
            //Deprecations.itemNbt.warn(mechanism.context);
            if (item.getMaterial().getMaterial() == Material.AIR) {
                mechanism.echoError("Cannot apply NBT to AIR!");
                return;
            }
            ListTag list = mechanism.valueAsType(ListTag.class);
            ItemStack itemStack = item.getItemStack();
            for (String string : list) {
                String[] split = string.split("/", 2);
                itemStack = CustomNBT.addCustomNBT(itemStack, EscapeTagBase.unEscape(split[0]), EscapeTagBase.unEscape(split[1]), CustomNBT.KEY_DENIZEN);
            }
            item.setItemStack(itemStack);
        }
    }
}

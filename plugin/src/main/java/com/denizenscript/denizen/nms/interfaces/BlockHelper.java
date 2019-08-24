package com.denizenscript.denizen.nms.interfaces;

import com.denizenscript.denizen.nms.abstracts.ModernBlockData;
import com.denizenscript.denizen.nms.util.PlayerProfile;
import com.denizenscript.denizen.nms.util.jnbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.material.MaterialData;

import java.util.List;

public interface BlockHelper {

    int idFor(Material mat);

    MaterialData getFlowerpotContents(Block block);

    void setFlowerpotContents(Block block, MaterialData data);

    PlayerProfile getPlayerProfile(Skull skull);

    void setPlayerProfile(Skull skull, PlayerProfile playerProfile);

    CompoundTag getNbtData(Block block);

    void setNbtData(Block block, CompoundTag compoundTag);

    BlockData getBlockData(Material material, byte data);

    BlockData getBlockData(Block block);

    default BlockData getBlockData(ModernBlockData data) {
        return null;
    }

    BlockData getBlockData(String compressedString);

    boolean hasBlock(Material material);

    boolean setBlockResistance(Material material, float resistance);

    float getBlockResistance(Material material);

    boolean isSafeBlock(Material material);

    default BlockState generateBlockState(Material mat) {
        return null;
    }

    List<Location> getBlocksList(PortalCreateEvent event);

    default String getPushReaction(Material mat) {
        throw new UnsupportedOperationException();
    }

    default void setPushReaction(Material mat, String reaction) {
        throw new UnsupportedOperationException();
    }

    default float getBlockStength(Material mat) {
        throw new UnsupportedOperationException();
    }

    default void setBlockStrength(Material mat, float strength) {
        throw new UnsupportedOperationException();
    }
}

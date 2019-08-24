package com.denizenscript.denizen.nms.v1_12.impl;

import com.denizenscript.denizen.nms.v1_12.helpers.PacketHelperImpl;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.abstracts.ProfileEditor;
import com.denizenscript.denizen.nms.util.PlayerProfile;
import com.denizenscript.denizen.nms.util.ReflectionHelper;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutRespawn;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class ProfileEditorImpl extends ProfileEditor {

    @Override
    protected void updatePlayer(Player player, final boolean isSkinChanging) {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        final UUID uuid = player.getUniqueId();
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityPlayer.getId());
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!p.getUniqueId().equals(uuid)) {
                PacketHelperImpl.sendPacket(p, destroyPacket);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
                PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(entityPlayer);
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    PacketHelperImpl.sendPacket(player, playerInfo);
                    if (!player.getUniqueId().equals(uuid)) {
                        PacketHelperImpl.sendPacket(player, spawnPacket);
                    }
                    else {
                        if (isSkinChanging) {
                            boolean isFlying = player.isFlying();
                            PacketHelperImpl.sendPacket(player, new PacketPlayOutRespawn(
                                    player.getWorld().getEnvironment().getId(),
                                    entityPlayer.getWorld().getDifficulty(),
                                    entityPlayer.getWorld().worldData.getType(),
                                    entityPlayer.playerInteractManager.getGameMode()));
                            player.teleport(player.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            player.setFlying(isFlying);
                        }
                        player.updateInventory();
                    }
                }
            }
        }.runTaskLater(NMSHandler.getJavaPlugin(), 5);
    }

    public static void updatePlayerProfiles(PacketPlayOutPlayerInfo packet) {
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = ReflectionHelper.getFieldValue(PacketPlayOutPlayerInfo.class, "a", packet);
        if (action != PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER) {
            return;
        }
        List<?> dataList = ReflectionHelper.getFieldValue(PacketPlayOutPlayerInfo.class, "b", packet);
        if (dataList != null) {
            try {
                for (Object data : dataList) {
                    GameProfile gameProfile = (GameProfile) playerInfoData_gameProfile.get(data);
                    if (fakeProfiles.containsKey(gameProfile.getId())) {
                        playerInfoData_gameProfile.set(data, getGameProfile(fakeProfiles.get(gameProfile.getId())));
                    }
                }
            }
            catch (Exception e) {
                Debug.echoError(e);
            }
        }
    }

    private static GameProfile getGameProfile(PlayerProfile playerProfile) {
        GameProfile gameProfile = new GameProfile(playerProfile.getUniqueId(), playerProfile.getName());
        gameProfile.getProperties().put("textures",
                new Property("textures", playerProfile.getTexture(), playerProfile.getTextureSignature()));
        return gameProfile;
    }

    private static final Field playerInfoData_gameProfile;

    static {
        Field pidGameProfile = null;
        try {
            for (Class clzz : PacketPlayOutPlayerInfo.class.getDeclaredClasses()) {
                if (CoreUtilities.toLowerCase(clzz.getName()).contains("infodata")) {
                    pidGameProfile = clzz.getDeclaredField("d"); // PlayerInfoData.
                    pidGameProfile.setAccessible(true);
                    break;
                }
            }
        }
        catch (Exception e) {
            Debug.echoError(e);
        }
        playerInfoData_gameProfile = pidGameProfile;
    }
}

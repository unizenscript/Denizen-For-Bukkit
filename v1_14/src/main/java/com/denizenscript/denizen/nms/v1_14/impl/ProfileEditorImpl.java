package com.denizenscript.denizen.nms.v1_14.impl;

import com.denizenscript.denizen.nms.abstracts.ProfileEditor;
import com.denizenscript.denizen.nms.v1_14.helpers.PacketHelperImpl;
import com.denizenscript.denizen.nms.v1_14.impl.network.handlers.DenizenNetworkManagerImpl;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.util.PlayerProfile;
import com.denizenscript.denizen.nms.util.ReflectionHelper;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
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
                            ((CraftServer) Bukkit.getServer()).getHandle().moveToWorld(
                                    entityPlayer, entityPlayer.dimension, true, player.getLocation(), false);
                        }
                        player.updateInventory();
                    }
                }
            }
        }.runTaskLater(NMSHandler.getJavaPlugin(), 5);
    }

    public static boolean handleMirrorProfiles(PacketPlayOutPlayerInfo packet, DenizenNetworkManagerImpl manager) {
        if (ProfileEditor.mirrorUUIDs.isEmpty()) {
            return true;
        }
        PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = ReflectionHelper.getFieldValue(PacketPlayOutPlayerInfo.class, "a", packet);
        if (action != PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER) {
            return true;
        }
        List dataList = ReflectionHelper.getFieldValue(PacketPlayOutPlayerInfo.class, "b", packet);
        if (dataList == null) {
            return true;
        }
        try {
            boolean any = false;
            for (Object data : dataList) {
                GameProfile gameProfile = (GameProfile) playerInfoData_gameProfile.get(data);
                if (ProfileEditor.mirrorUUIDs.contains(gameProfile.getId())) {
                    any = true;
                }
            }
            if (!any) {
                return true;
            }
            GameProfile ownProfile = manager.player.getProfile();
            for (Object data : dataList) {
                GameProfile gameProfile = (GameProfile) playerInfoData_gameProfile.get(data);
                if (!ProfileEditor.mirrorUUIDs.contains(gameProfile.getId())) {
                    PacketPlayOutPlayerInfo newPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
                    List newPacketDataList = ReflectionHelper.getFieldValue(PacketPlayOutPlayerInfo.class, "b", newPacket);
                    newPacketDataList.add(data);
                    manager.oldManager.sendPacket(newPacket);
                }
                else {
                    PacketPlayOutPlayerInfo newPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
                    List newPacketDataList = ReflectionHelper.getFieldValue(PacketPlayOutPlayerInfo.class, "b", newPacket);
                    GameProfile patchedProfile = new GameProfile(gameProfile.getId(), gameProfile.getName());
                    patchedProfile.getProperties().putAll(ownProfile.getProperties());
                    Object newData = playerInfoData_construct.newInstance(newPacket, patchedProfile,
                            playerInfoData_latency.getInt(data), playerInfoData_gamemode.get(data), playerInfoData_displayName.get(data));
                    newPacketDataList.add(newData);
                    manager.oldManager.sendPacket(newPacket);
                }
            }
            return false;
        }
        catch (Exception e) {
            Debug.echoError(e);
            return true;
        }
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

    public static final Class playerInfoData;

    public static final Field playerInfoData_latency,
            playerInfoData_gamemode,
            playerInfoData_gameProfile,
            playerInfoData_displayName;

    public static final Constructor playerInfoData_construct;

    static {
        Class pid = null;
        Field pidLatency = null, pidGamemode = null, pidGameProfile = null, pidDisplayName = null;
        Constructor pidConstruct = null;
        try {
            for (Class clzz : PacketPlayOutPlayerInfo.class.getDeclaredClasses()) {
                if (CoreUtilities.toLowerCase(clzz.getName()).contains("infodata")) { // PlayerInfoData.
                    pid = clzz;
                    pidLatency = clzz.getDeclaredField("b");
                    pidLatency.setAccessible(true);
                    pidGamemode = clzz.getDeclaredField("c");
                    pidGamemode.setAccessible(true);
                    pidGameProfile = clzz.getDeclaredField("d");
                    pidGameProfile.setAccessible(true);
                    pidDisplayName = clzz.getDeclaredField("e");
                    pidDisplayName.setAccessible(true);
                    pidConstruct = pid.getDeclaredConstructors()[0];
                    pidConstruct.setAccessible(true);
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        playerInfoData = pid;
        playerInfoData_latency = pidLatency;
        playerInfoData_gamemode = pidGamemode;
        playerInfoData_gameProfile = pidGameProfile;
        playerInfoData_displayName = pidDisplayName;
        playerInfoData_construct = pidConstruct;
    }
}

package nadiendev.cmdop.event;

import nadiendev.cmdop.commands.NickCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.EnumSet;

public class NicknameEventHandler {
    
    private static int tickCounter = 0;
    
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            NickCommand.applyNickname(player);
            updateTabListName(player);
        }
    }
    
    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        
        if (tickCounter >= 100) {
            tickCounter = 0;
            
            event.getServer().getPlayerList().getPlayers().forEach(player -> {
                updateTabListName(player);
            });
        }
    }
    
    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            NickCommand.applyNickname(player);
            updateTabListName(player);
        }
    }
    
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            NickCommand.applyNickname(player);
            updateTabListName(player);
        }
    }
    
    private static void updateTabListName(ServerPlayer player) {
        try {
            Component displayName = NickCommand.getDisplayNameComponent(player);
            
            var packet = new ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME),
                java.util.Collections.singletonList(player)
            );
            
            player.server.getPlayerList().getPlayers().forEach(p -> {
                p.connection.send(packet);
            });
            
        } catch (Exception e) {
            System.err.println("Error updating tab list: " + e.getMessage());
        }
    }
}
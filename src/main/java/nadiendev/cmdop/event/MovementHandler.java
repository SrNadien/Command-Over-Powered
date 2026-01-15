package nadiendev.cmdop.event;

import nadiendev.cmdop.data.PlayerDataManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementHandler {
    private static final Map<UUID, Double> lastPositions = new HashMap<>();
    
    @SubscribeEvent
    public void onLivingUpdate(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
            
            if (data.isAfk()) {
                UUID uuid = player.getUUID();
                double currentPos = player.getX() + player.getY() + player.getZ();
                
                if (lastPositions.containsKey(uuid)) {
                    double lastPos = lastPositions.get(uuid);
                    if (Math.abs(currentPos - lastPos) > 0.1) {
                        data.setAfk(false);
                        player.getServer().getPlayerList().broadcastSystemMessage(
                            Component.literal("§7" + player.getName().getString() + " ya no está AFK."), false);
                    }
                }
                
                lastPositions.put(uuid, currentPos);
            }
        }
    }
}
package nadiendev.cmdop.event;

import nadiendev.cmdop.commands.AfkCommand;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class AfkActivityHandler {
    
    private static int tickCounter = 0;
    
    
    @SubscribeEvent
    public static void onPlayerInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AfkCommand.updateActivity(player);
        }
    }
    
   
    @SubscribeEvent
    public static void onPlayerInteractItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AfkCommand.updateActivity(player);
        }
    }
    
   
    @SubscribeEvent
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AfkCommand.updateActivity(player);
        }
    }
    
    
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AfkCommand.onPlayerLogout(player.getUUID());
            MovementHandler.onPlayerLogout(player.getUUID());
        }
    }
    
    
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        
        
        if (tickCounter >= 100) {
            tickCounter = 0;
            
            
            event.getServer().getPlayerList().getPlayers().forEach(player -> {
                AfkCommand.checkAutoAfk(player);
            });
        }
    }
}
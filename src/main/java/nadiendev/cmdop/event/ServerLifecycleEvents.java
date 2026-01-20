package nadiendev.cmdop.event;

import nadiendev.cmdop.data.PlayerDataManager;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public class ServerLifecycleEvents {
    
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        System.out.println("[CMDOP] Server starting, initializing PlayerDataManager...");
        PlayerDataManager.initialize(event.getServer().getWorldPath(LevelResource.ROOT));
    }
    
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        System.out.println("[CMDOP] Server stopping, saving all player data...");
        PlayerDataManager.saveAll();
    }
    
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            System.out.println("[CMDOP] Player " + player.getName().getString() + " logged out, saving data...");
            PlayerDataManager.savePlayerData(player.getUUID());
        }
    }
    
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            System.out.println("[CMDOP] Player " + player.getName().getString() + " logged in, loading data...");
            PlayerDataManager.getData(player.getUUID());
        }
    }
}
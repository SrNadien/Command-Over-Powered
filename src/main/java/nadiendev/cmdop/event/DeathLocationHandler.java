package nadiendev.cmdop.event;

import nadiendev.cmdop.data.PlayerDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class DeathLocationHandler {
    
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
            
            BlockPos deathPos = player.blockPosition();
            String dimension = player.level().dimension().location().toString();
            
            data.setLastDeath(deathPos, dimension);
        }
    }
}
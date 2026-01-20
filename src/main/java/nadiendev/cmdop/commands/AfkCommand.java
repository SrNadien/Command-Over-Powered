package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AfkCommand {
    
    
    private static final Map<UUID, Long> LAST_ACTIVITY = new ConcurrentHashMap<>();
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("afk")
            .executes(AfkCommand::toggleAfk));
    }
    
    private static int toggleAfk(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_AFK.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /afk está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        boolean afk = !data.isAfk();
        data.setAfk(afk);
        
        
        if (!afk) {
            updateActivity(player);
        }
        
        if (afk) {
            ctx.getSource().getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("§7" + player.getName().getString() + " está AFK."), false);
        } else {
            ctx.getSource().getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("§7" + player.getName().getString() + " ya no está AFK."), false);
        }
        
        return 1;
    }
    
    
    public static void updateActivity(ServerPlayer player) {
        LAST_ACTIVITY.put(player.getUUID(), System.currentTimeMillis());
        
      
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        if (data.isAfk()) {
            data.setAfk(false);
            player.server.getPlayerList().broadcastSystemMessage(
                Component.literal("§7" + player.getName().getString() + " ya no está AFK."), false);
        }
    }
    
    
    public static void checkAutoAfk(ServerPlayer player) {
        if (!cmdopConfig.ENABLE_AFK.get() || !cmdopConfig.AUTO_AFK_ENABLED.get()) {
            return;
        }
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
      
        if (data.isAfk()) {
            return;
        }
        
        Long lastActivity = LAST_ACTIVITY.get(player.getUUID());
        if (lastActivity == null) {
           
            LAST_ACTIVITY.put(player.getUUID(), System.currentTimeMillis());
            return;
        }
        
        long timeSinceActivity = System.currentTimeMillis() - lastActivity;
        long afkThreshold = cmdopConfig.AUTO_AFK_TIME.get() * 1000L; // Convertir segundos a ms
        
        if (timeSinceActivity >= afkThreshold) {
            data.setAfk(true);
            player.server.getPlayerList().broadcastSystemMessage(
                Component.literal("§7" + player.getName().getString() + " está AFK."), false);
        }
    }
    
    
    public static void onPlayerLogout(UUID playerId) {
        LAST_ACTIVITY.remove(playerId);
    }
}
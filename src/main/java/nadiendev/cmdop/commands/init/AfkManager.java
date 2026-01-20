package nadiendev.cmdop.commands.init;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestor simplificado de estado AFK
 */
public class AfkManager {
    
    private static class SingletonHelper {
        private static final AfkManager INSTANCE = new AfkManager();
    }
    
    public static AfkManager getInstance() {
        return SingletonHelper.INSTANCE;
    }
    
    private final Map<UUID, PlayerAfkData> playerData = new ConcurrentHashMap<>();
    
    private AfkManager() {}
    
    /**
     * Contenedor de datos AFK del jugador
     */
    public static class PlayerAfkData {
        private boolean isAfk = false;
        private long lastActivity = System.currentTimeMillis();
        private String afkReason = null;
        private long afkStartTime = 0;
        
        public boolean isAfk() { return isAfk; }
        public void setAfk(boolean afk) { this.isAfk = afk; }
        public long getLastActivity() { return lastActivity; }
        public void setLastActivity(long time) { this.lastActivity = time; }
        public String getAfkReason() { return afkReason; }
        public void setAfkReason(String reason) { this.afkReason = reason; }
        public long getAfkStartTime() { return afkStartTime; }
        public void setAfkStartTime(long time) { this.afkStartTime = time; }
    }
    
    /**
     * Actualizar timestamp de actividad del jugador
     */
    public void updateActivity(UUID playerUuid) {
        PlayerAfkData data = playerData.computeIfAbsent(playerUuid, k -> new PlayerAfkData());
        data.setLastActivity(System.currentTimeMillis());
        
        if (data.isAfk()) {
            setAfkStatus(playerUuid, false, null);
        }
    }
    
    /**
     * Verificar si el jugador está AFK
     */
    public boolean isAfk(UUID playerUuid) {
        PlayerAfkData data = playerData.get(playerUuid);
        return data != null && data.isAfk();
    }
    
    /**
     * Verificar si el jugador está AFK (método de conveniencia)
     */
    public boolean isAfk(ServerPlayer player) {
        return isAfk(player.getUUID());
    }
    
    /**
     * Alternar estado AFK del jugador
     */
    public void toggleAfk(ServerPlayer player, String reason) {
        UUID uuid = player.getUUID();
        PlayerAfkData data = playerData.computeIfAbsent(uuid, k -> new PlayerAfkData());
        setAfkStatus(uuid, !data.isAfk(), reason);
    }
    
    /**
     * Establecer estado AFK del jugador
     */
    public void setAfkStatus(UUID playerUuid, boolean afk, String reason) {
        PlayerAfkData data = playerData.computeIfAbsent(playerUuid, k -> new PlayerAfkData());
        boolean wasAfk = data.isAfk();
        
        data.setAfk(afk);
        data.setAfkReason(reason);
        
        if (afk && !wasAfk) {
            data.setAfkStartTime(System.currentTimeMillis());
            onPlayerGoAfk(playerUuid, reason);
        } else if (!afk && wasAfk) {
            data.setAfkStartTime(0);
            data.setLastActivity(System.currentTimeMillis());
            onPlayerReturnFromAfk(playerUuid);
        }
    }
    
    /**
     * Obtener razón AFK del jugador
     */
    public String getAfkReason(UUID playerUuid) {
        PlayerAfkData data = playerData.get(playerUuid);
        return data != null ? data.getAfkReason() : null;
    }
    
    /**
     * Obtener duración AFK (en milisegundos)
     */
    public long getAfkDuration(UUID playerUuid) {
        PlayerAfkData data = playerData.get(playerUuid);
        if (data == null || !data.isAfk()) return 0;
        return System.currentTimeMillis() - data.getAfkStartTime();
    }
    
    /**
     * Llamado cuando el jugador se pone AFK
     */
    private void onPlayerGoAfk(UUID playerUuid, String reason) {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;
            
            ServerPlayer player = server.getPlayerList().getPlayer(playerUuid);
            if (player == null) return;
            
            String message = "§7" + player.getName().getString() + " está AFK.";
            if (reason != null && !reason.trim().isEmpty()) {
                message = "§7" + player.getName().getString() + " está AFK (" + reason + ").";
            }
            
            server.getPlayerList().broadcastSystemMessage(Component.literal(message), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Llamado cuando el jugador regresa de AFK
     */
    private void onPlayerReturnFromAfk(UUID playerUuid) {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;
            
            ServerPlayer player = server.getPlayerList().getPlayer(playerUuid);
            if (player == null) return;
            
            String message = "§7" + player.getName().getString() + " ya no está AFK.";
            server.getPlayerList().broadcastSystemMessage(Component.literal(message), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Remover datos del jugador al desconectarse
     */
    public void onPlayerLogout(UUID playerUuid) {
        playerData.remove(playerUuid);
    }
}
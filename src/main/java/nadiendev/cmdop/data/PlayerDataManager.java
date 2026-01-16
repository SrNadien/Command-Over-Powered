package nadiendev.cmdop.data;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private static final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();
    
    public static PlayerData getData(UUID uuid) {
        return playerData.computeIfAbsent(uuid, k -> new PlayerData());
    }
    
    public static void saveData() {
        // Aquí podrías implementar guardado persistente si lo necesitas
        // Por ahora los datos se mantienen en memoria durante la sesión del servidor
    }
    
    public static void clearData(UUID uuid) {
        playerData.remove(uuid);
    }
    
    public static class PlayerData {
        private boolean flying = false;
        private boolean vanished = false;
        private boolean god = false;
        private boolean afk = false;
        private String nickname = null;
        private Map<String, HomeData> homes = new HashMap<>();
        private long lastHomeUse = 0;
        private BlockPos lastDeathLocation = null;
        private String lastDeathDimension = null;
        private UUID tpaRequest = null;
        private long tpaRequestTime = 0;
        
        public boolean isFlying() { return flying; }
        public void setFlying(boolean flying) { this.flying = flying; }
        
        public boolean isVanished() { return vanished; }
        public void setVanished(boolean vanished) { this.vanished = vanished; }
        
        public boolean isGod() { return god; }
        public void setGod(boolean god) { this.god = god; }
        
        public boolean isAfk() { return afk; }
        public void setAfk(boolean afk) { this.afk = afk; }
        
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        
        public Map<String, HomeData> getHomes() { return homes; }
        
        public void addHome(String name, BlockPos pos, String dimension) {
            homes.put(name, new HomeData(pos, dimension));
        }
        
        public void removeHome(String name) {
            homes.remove(name);
        }
        
        public long getLastHomeUse() { return lastHomeUse; }
        public void setLastHomeUse(long time) { this.lastHomeUse = time; }
        
        public BlockPos getLastDeathLocation() { return lastDeathLocation; }
        public String getLastDeathDimension() { return lastDeathDimension; }
        public void setLastDeath(BlockPos pos, String dimension) {
            this.lastDeathLocation = pos;
            this.lastDeathDimension = dimension;
        }
        
        public UUID getTpaRequest() { return tpaRequest; }
        public long getTpaRequestTime() { return tpaRequestTime; }
        public void setTpaRequest(UUID uuid, long time) {
            this.tpaRequest = uuid;
            this.tpaRequestTime = time;
        }
        public void clearTpaRequest() {
            this.tpaRequest = null;
            this.tpaRequestTime = 0;
        }
    }
    
    public static class HomeData {
        private final BlockPos position;
        private final String dimension;
        
        public HomeData(BlockPos position, String dimension) {
            this.position = position;
            this.dimension = dimension;
        }
        
        public BlockPos getPosition() { return position; }
        public String getDimension() { return dimension; }
    }
}
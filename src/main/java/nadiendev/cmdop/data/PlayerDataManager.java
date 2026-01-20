package nadiendev.cmdop.data;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private static final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();
    private static Path DATA_FOLDER = null;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Inicializa el sistema de datos con la ruta del mundo actual
     */
    public static void initialize(Path worldPath) {
        try {
            // Usar la carpeta data del mundo actual
            DATA_FOLDER = worldPath.resolve("data").resolve("cmdop");
            Files.createDirectories(DATA_FOLDER);
            System.out.println("[CMDOP] Player data folder initialized at: " + DATA_FOLDER.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[CMDOP] Failed to create player data folder: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene los datos de un jugador (carga desde archivo si es necesario)
     */
    public static PlayerData getData(UUID uuid) {
        return playerData.computeIfAbsent(uuid, k -> {
            PlayerData data = new PlayerData();
            loadPlayerData(uuid, data);
            return data;
        });
    }
    
    /**
     * Guarda los datos de un jugador específico
     */
    public static void savePlayerData(UUID uuid) {
        PlayerData data = playerData.get(uuid);
        if (data == null || DATA_FOLDER == null) return;
        
        try {
            Path playerFile = DATA_FOLDER.resolve(uuid.toString() + ".json");
            
            JsonObject json = new JsonObject();
            json.addProperty("flying", data.flying);
            json.addProperty("vanished", data.vanished);
            json.addProperty("god", data.god);
            json.addProperty("afk", data.afk);
            json.addProperty("lastHomeUse", data.lastHomeUse);
            
            // Guardar homes
            JsonArray homesArray = new JsonArray();
            for (Map.Entry<String, HomeData> entry : data.homes.entrySet()) {
                JsonObject homeObj = new JsonObject();
                homeObj.addProperty("name", entry.getKey());
                
                BlockPos pos = entry.getValue().getPosition();
                homeObj.addProperty("x", pos.getX());
                homeObj.addProperty("y", pos.getY());
                homeObj.addProperty("z", pos.getZ());
                homeObj.addProperty("dimension", entry.getValue().getDimension());
                
                homesArray.add(homeObj);
            }
            json.add("homes", homesArray);
            
            // Guardar última ubicación de muerte
            if (data.lastDeathLocation != null) {
                JsonObject deathObj = new JsonObject();
                deathObj.addProperty("x", data.lastDeathLocation.getX());
                deathObj.addProperty("y", data.lastDeathLocation.getY());
                deathObj.addProperty("z", data.lastDeathLocation.getZ());
                deathObj.addProperty("dimension", data.lastDeathDimension);
                json.add("lastDeath", deathObj);
            }
            
            // Guardar ubicación anterior
            if (data.previousLocation != null) {
                JsonObject prevObj = new JsonObject();
                prevObj.addProperty("x", data.previousLocation.getX());
                prevObj.addProperty("y", data.previousLocation.getY());
                prevObj.addProperty("z", data.previousLocation.getZ());
                prevObj.addProperty("dimension", data.previousDimension);
                json.add("previousLocation", prevObj);
            }
            
            Files.writeString(playerFile, GSON.toJson(json));
            
        } catch (Exception e) {
            System.err.println("[CMDOP] Failed to save player data for " + uuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los datos de un jugador desde archivo
     */
    private static void loadPlayerData(UUID uuid, PlayerData data) {
        if (DATA_FOLDER == null) return;
        
        try {
            Path playerFile = DATA_FOLDER.resolve(uuid.toString() + ".json");
            
            if (!Files.exists(playerFile)) {
                return; // No hay datos guardados, usar valores por defecto
            }
            
            String jsonStr = Files.readString(playerFile);
            JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();
            
            // Cargar datos básicos
            if (json.has("flying")) data.flying = json.get("flying").getAsBoolean();
            if (json.has("vanished")) data.vanished = json.get("vanished").getAsBoolean();
            if (json.has("god")) data.god = json.get("god").getAsBoolean();
            if (json.has("afk")) data.afk = json.get("afk").getAsBoolean();
            if (json.has("lastHomeUse")) data.lastHomeUse = json.get("lastHomeUse").getAsLong();
            
            // Cargar homes
            if (json.has("homes")) {
                JsonArray homesArray = json.getAsJsonArray("homes");
                for (JsonElement element : homesArray) {
                    JsonObject homeObj = element.getAsJsonObject();
                    
                    String name = homeObj.get("name").getAsString();
                    int x = homeObj.get("x").getAsInt();
                    int y = homeObj.get("y").getAsInt();
                    int z = homeObj.get("z").getAsInt();
                    String dimension = homeObj.get("dimension").getAsString();
                    
                    BlockPos pos = new BlockPos(x, y, z);
                    data.homes.put(name, new HomeData(pos, dimension));
                }
            }
            
            // Cargar última ubicación de muerte
            if (json.has("lastDeath")) {
                JsonObject deathObj = json.getAsJsonObject("lastDeath");
                int x = deathObj.get("x").getAsInt();
                int y = deathObj.get("y").getAsInt();
                int z = deathObj.get("z").getAsInt();
                String dimension = deathObj.get("dimension").getAsString();
                
                data.lastDeathLocation = new BlockPos(x, y, z);
                data.lastDeathDimension = dimension;
            }
            
            // Cargar ubicación anterior
            if (json.has("previousLocation")) {
                JsonObject prevObj = json.getAsJsonObject("previousLocation");
                int x = prevObj.get("x").getAsInt();
                int y = prevObj.get("y").getAsInt();
                int z = prevObj.get("z").getAsInt();
                String dimension = prevObj.get("dimension").getAsString();
                
                data.previousLocation = new BlockPos(x, y, z);
                data.previousDimension = dimension;
            }
            
        } catch (Exception e) {
            System.err.println("[CMDOP] Failed to load player data for " + uuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda todos los datos de jugadores
     */
    public static void saveAll() {
        System.out.println("[CMDOP] Saving all player data...");
        int count = 0;
        for (UUID uuid : playerData.keySet()) {
            savePlayerData(uuid);
            count++;
        }
        System.out.println("[CMDOP] Saved data for " + count + " players");
    }
    
    /**
     * Limpia los datos de un jugador de la memoria (pero no del archivo)
     */
    public static void clearData(UUID uuid) {
        savePlayerData(uuid); // Guardar antes de limpiar
        playerData.remove(uuid);
    }
    
    /**
     * Obtiene todos los datos de jugadores (útil para comandos admin)
     */
    public static Map<UUID, PlayerData> getAllData() {
        return new HashMap<>(playerData);
    }
    
    public static class PlayerData {
        private boolean flying = false;
        private boolean vanished = false;
        private boolean god = false;
        private boolean afk = false;
        private Map<String, HomeData> homes = new HashMap<>();
        private long lastHomeUse = 0;
        private BlockPos lastDeathLocation = null;
        private String lastDeathDimension = null;
        private BlockPos previousLocation = null;
        private String previousDimension = null;
        private UUID tpaRequest = null;
        private long tpaRequestTime = 0;
        private boolean tpaHereRequest = false;
        
        public boolean isFlying() { return flying; }
        public void setFlying(boolean flying) { this.flying = flying; }
        
        public boolean isVanished() { return vanished; }
        public void setVanished(boolean vanished) { this.vanished = vanished; }
        
        public boolean isGod() { return god; }
        public void setGod(boolean god) { this.god = god; }
        
        public boolean isAfk() { return afk; }
        public void setAfk(boolean afk) { this.afk = afk; }
        
        public Map<String, HomeData> getHomes() { return homes; }
        
        public void addHome(String name, BlockPos pos, String dimension) {
            homes.put(name, new HomeData(pos, dimension));
        }
        
        public void removeHome(String name) {
            homes.remove(name);
        }
        
        public boolean hasHome(String name) {
            return homes.containsKey(name);
        }
        
        public int getHomeCount() {
            return homes.size();
        }
        
        public long getLastHomeUse() { return lastHomeUse; }
        public void setLastHomeUse(long time) { this.lastHomeUse = time; }
        
        public BlockPos getLastDeathLocation() { return lastDeathLocation; }
        public String getLastDeathDimension() { return lastDeathDimension; }
        public void setLastDeath(BlockPos pos, String dimension) {
            this.lastDeathLocation = pos;
            this.lastDeathDimension = dimension;
        }
        
        public BlockPos getPreviousLocation() { return previousLocation; }
        public String getPreviousDimension() { return previousDimension; }
        public void setPreviousLocation(BlockPos pos, String dimension) {
            this.previousLocation = pos;
            this.previousDimension = dimension;
        }
        
        public UUID getTpaRequest() { return tpaRequest; }
        public long getTpaRequestTime() { return tpaRequestTime; }
        public boolean isTpaHereRequest() { return tpaHereRequest; }
        
        public void setTpaRequest(UUID uuid, long time) {
            this.tpaRequest = uuid;
            this.tpaRequestTime = time;
            this.tpaHereRequest = false;
        }
        
        public void setTpaRequest(UUID uuid, long time, boolean isHere) {
            this.tpaRequest = uuid;
            this.tpaRequestTime = time;
            this.tpaHereRequest = isHere;
        }
        
        public void clearTpaRequest() {
            this.tpaRequest = null;
            this.tpaRequestTime = 0;
            this.tpaHereRequest = false;
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
        
        @Override
        public String toString() {
            return "HomeData{pos=" + position + ", dim=" + dimension + "}";
        }
    }
}
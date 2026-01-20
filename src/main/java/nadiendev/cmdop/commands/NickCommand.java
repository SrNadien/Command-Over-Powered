package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.commands.init.MessageUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class NickCommand {
    private static final Map<UUID, String> NICKNAMES = new ConcurrentHashMap<>();
    private static final Path NICK_DATA_FILE = Paths.get("config", "cmdop", "nickname_data.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    // Patrón más permisivo: permite letras, números, espacios, guiones bajos y códigos de color
    private static final Pattern VALID_NICK_PATTERN = Pattern.compile("^[a-zA-Z0-9_&§# ]+$");
    // Patrón para remover códigos de color: &x (legacy) y &#RRGGBB (hex)
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("&[0-9a-fk-or]|&#[0-9a-fA-F]{6}");
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!cmdopConfig.ENABLE_NICKNAME.get()) return;
        
        loadNicknameData();
        
        // Comando /nickname <nickname> - Para uno mismo
        // Comando /nickname <player> <nickname> - Para otro jugador (admin)
        dispatcher.register(Commands.literal("nickname")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("nickname", StringArgumentType.greedyString())
                .executes(ctx -> setNickname(ctx.getSource(), StringArgumentType.getString(ctx, "nickname"), null)))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("nickname", StringArgumentType.greedyString())
                    .executes(ctx -> setNickname(ctx.getSource(), StringArgumentType.getString(ctx, "nickname"), 
                        EntityArgument.getPlayer(ctx, "player"))))));
        
        // Alias /nick
        dispatcher.register(Commands.literal("nick")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("nickname", StringArgumentType.greedyString())
                .executes(ctx -> setNickname(ctx.getSource(), StringArgumentType.getString(ctx, "nickname"), null)))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("nickname", StringArgumentType.greedyString())
                    .executes(ctx -> setNickname(ctx.getSource(), StringArgumentType.getString(ctx, "nickname"), 
                        EntityArgument.getPlayer(ctx, "player"))))));
    }
    
    private static int setNickname(CommandSourceStack source, String nickname, ServerPlayer target) {
        if (!cmdopConfig.ENABLE_NICKNAME.get()) {
            source.sendFailure(MessageUtil.error("El comando /nickname está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = target != null ? target : source.getPlayer();
        if (player == null) return 0;
        
        // Comando para resetear el nickname
        if (nickname.equalsIgnoreCase("off") || nickname.equalsIgnoreCase("reset")) {
            return resetNickname(source, player, target != null);
        }
        
        // Validar formato del nickname
        if (!isValidNickname(nickname)) {
            player.sendSystemMessage(MessageUtil.error("Formato de nickname inválido. Usa solo letras, números, espacios y códigos de color (&)."));
            return 0;
        }
        
        // Remover códigos de color para validar longitud real
        String withoutColors = removeColorCodes(nickname);
        
        // Validar longitud mínima
        if (withoutColors.length() < 3) {
            player.sendSystemMessage(MessageUtil.error("El nickname es muy corto (mínimo 3 caracteres sin códigos de color)."));
            return 0;
        }
        
        // Validar longitud máxima
        if (withoutColors.length() > 16) {
            player.sendSystemMessage(MessageUtil.error("El nickname es muy largo (máximo 16 caracteres sin códigos de color)."));
            return 0;
        }
        
        // Verificar si el nickname ya está en uso
        if (isNicknameTaken(nickname, player.getUUID())) {
            player.sendSystemMessage(MessageUtil.error("Este nickname ya está en uso por otro jugador."));
            return 0;
        }
        
        // Guardar el nickname
        NICKNAMES.put(player.getUUID(), nickname);
        saveNicknameData();
        
        // Convertir códigos de color & a §
        String coloredNick = nickname.replace("&", "§");
        player.sendSystemMessage(MessageUtil.success("Nickname establecido a: " + coloredNick));
        
        // Si un admin cambió el nick de otro jugador
        if (target != null) {
            source.sendSuccess(() -> MessageUtil.success("Nickname de " + player.getName().getString() + " cambiado a: " + coloredNick), true);
        }
        
        // Aplicar el nickname inmediatamente
        applyNickname(player);
        
        return 1;
    }
    
    private static int resetNickname(CommandSourceStack source, ServerPlayer player, boolean isOther) {
        if (!NICKNAMES.containsKey(player.getUUID())) {
            player.sendSystemMessage(MessageUtil.info("No tienes un nickname configurado."));
            return 0;
        }
        
        NICKNAMES.remove(player.getUUID());
        saveNicknameData();
        
        player.sendSystemMessage(MessageUtil.success("Nickname reiniciado a tu nombre original."));
        
        if (isOther) {
            source.sendSuccess(() -> MessageUtil.success("Nickname de " + player.getName().getString() + " reiniciado."), true);
        }
        
        // Remover el custom name
        player.setCustomName(null);
        player.setCustomNameVisible(false);
        
        return 1;
    }
    
    /**
     * Valida si el nickname tiene un formato permitido
     */
    private static boolean isValidNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return false;
        }
        return VALID_NICK_PATTERN.matcher(nickname).matches();
    }
    
    /**
     * Remueve todos los códigos de color del nickname
     */
    private static String removeColorCodes(String nickname) {
        if (nickname == null) return "";
        return COLOR_CODE_PATTERN.matcher(nickname).replaceAll("");
    }
    
    /**
     * Verifica si un nickname ya está en uso por otro jugador
     */
    private static boolean isNicknameTaken(String nickname, UUID excludePlayer) {
        String cleanNickname = removeColorCodes(nickname).toLowerCase().trim();
        
        return NICKNAMES.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(excludePlayer))
            .anyMatch(entry -> removeColorCodes(entry.getValue()).toLowerCase().trim().equals(cleanNickname));
    }
    
    /**
     * Obtiene el nickname de un jugador
     */
    public static String getNickname(UUID playerId) {
        return NICKNAMES.get(playerId);
    }
    
    /**
     * Obtiene el nombre para mostrar como Component
     */
    public static Component getDisplayNameComponent(ServerPlayer player) {
        String nickname = NICKNAMES.get(player.getUUID());
        if (nickname != null) {
            return Component.literal(nickname.replace("&", "§"));
        }
        return player.getName();
    }
    
    /**
     * Obtiene el nombre para mostrar como String
     */
    public static String getDisplayName(ServerPlayer player) {
        String nickname = NICKNAMES.get(player.getUUID());
        if (nickname != null) {
            return nickname.replace("&", "§");
        }
        return player.getName().getString();
    }
    
    /**
     * Aplica el nickname visualmente sobre la cabeza del jugador
     */
    public static void applyNickname(ServerPlayer player) {
        String nickname = NICKNAMES.get(player.getUUID());
        if (nickname != null) {
            String coloredNick = nickname.replace("&", "§");
            player.setCustomName(Component.literal(coloredNick));
            player.setCustomNameVisible(true);
        } else {
            player.setCustomName(null);
            player.setCustomNameVisible(false);
        }
    }
    
    /**
     * Carga los nicknames desde el archivo JSON
     */
    private static void loadNicknameData() {
        try {
            if (!Files.exists(NICK_DATA_FILE)) {
                Files.createDirectories(NICK_DATA_FILE.getParent());
                return;
            }
            
            String json = Files.readString(NICK_DATA_FILE);
            JsonObject data = JsonParser.parseString(json).getAsJsonObject();
            
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                try {
                    UUID playerId = UUID.fromString(entry.getKey());
                    String nickname = entry.getValue().getAsString();
                    NICKNAMES.put(playerId, nickname);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid UUID in nickname data: " + entry.getKey());
                }
            }
            
            System.out.println("Loaded " + NICKNAMES.size() + " nicknames from file");
            
        } catch (Exception e) {
            System.err.println("Failed to load nickname data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda los nicknames en el archivo JSON
     */
    private static void saveNicknameData() {
        try {
            JsonObject data = new JsonObject();
            
            for (Map.Entry<UUID, String> entry : NICKNAMES.entrySet()) {
                data.addProperty(entry.getKey().toString(), entry.getValue());
            }
            
            Files.createDirectories(NICK_DATA_FILE.getParent());
            Files.writeString(NICK_DATA_FILE, GSON.toJson(data));
            
        } catch (Exception e) {
            System.err.println("Failed to save nickname data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
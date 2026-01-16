package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SkullCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("skull")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", StringArgumentType.word())
                .executes(SkullCommand::giveSkull)));
    }
    
    private static int giveSkull(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_SKULL.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /skull está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        String targetName = StringArgumentType.getString(ctx, "player");
        
        // Buscar en jugadores conectados
        for (ServerPlayer serverPlayer : player.server.getPlayerList().getPlayers()) {
            if (serverPlayer.getGameProfile().getName().equalsIgnoreCase(targetName)) {
                giveSkullWithProfile(player, serverPlayer.getGameProfile(), targetName);
                return 1;
            }
        }
        
        // Buscar en la caché de usuario
        Optional<GameProfile> cachedProfile = player.server.getProfileCache().get(targetName);
        if (cachedProfile.isPresent()) {
            GameProfile profile = cachedProfile.get();
            // Verificar si tiene propiedades (skin)
            if (!profile.getProperties().isEmpty()) {
                giveSkullWithProfile(player, profile, targetName);
                return 1;
            }
        }
        
        // Si no se encontró o no tiene skin, buscar en Mojang
        player.sendSystemMessage(Component.literal("§eBuscando perfil de " + targetName + "..."));
        
        CompletableFuture.runAsync(() -> {
            try {
                // Primero obtener el UUID del nombre
                Optional<GameProfile> profileOpt = player.server.getProfileCache().get(targetName);
                GameProfile profile;
                
                if (profileOpt.isPresent() && profileOpt.get().getId() != null) {
                    profile = profileOpt.get();
                } else {
                    // Si no está en caché, crear uno básico
                    profile = new GameProfile(null, targetName);
                }
                
                // Obtener el MinecraftSessionService para resolver el perfil completo
                MinecraftSessionService sessionService = player.server.getSessionService();
                
                if (profile.getId() != null) {
                    GameProfile fullProfile = sessionService.fetchProfile(profile.getId(), true).profile();
                    if (fullProfile != null && !fullProfile.getProperties().isEmpty()) {
                        player.server.execute(() -> {
                            giveSkullWithProfile(player, fullProfile, targetName);
                            player.sendSystemMessage(Component.literal("§aEncontrado perfil premium de " + targetName + "."));
                        });
                        return;
                    }
                }
                
                // Si no se pudo obtener, dar cabeza sin textura
                player.server.execute(() -> {
                    giveSkullWithProfile(player, new GameProfile(null, targetName), targetName);
                    player.sendSystemMessage(Component.literal("§cNo se pudo obtener la textura. El jugador puede no ser premium o no existir."));
                });
                
            } catch (Exception e) {
                player.server.execute(() -> {
                    giveSkullWithProfile(player, new GameProfile(null, targetName), targetName);
                    player.sendSystemMessage(Component.literal("§cError al buscar el perfil. Dando cabeza sin textura."));
                });
            }
        });
        
        return 1;
    }
    
    private static void giveSkullWithProfile(ServerPlayer player, GameProfile profile, String targetName) {
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        skull.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        
        if (!player.getInventory().add(skull)) {
            player.drop(skull, false);
        }
        
        player.sendSystemMessage(Component.literal("§aRecibiste la cabeza de " + targetName + "."));
    }
}
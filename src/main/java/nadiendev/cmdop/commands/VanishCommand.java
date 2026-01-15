package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class VanishCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("vanish")
            .requires(source -> source.hasPermission(2))
            .executes(VanishCommand::toggle));
    }
    
    private static int toggle(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_VANISH.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /vanish está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        boolean vanished = !data.isVanished();
        data.setVanished(vanished);
        
        player.setInvisible(vanished);
        
        if (vanished) {
            player.getServer().getPlayerList().getPlayers().forEach(p -> {
                if (p != player && !p.hasPermissions(2)) {
                    p.connection.send(new net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket(
                        java.util.List.of(player.getUUID())));
                }
            });
            player.sendSystemMessage(Component.literal("§aAhora eres invisible."));
        } else {
            player.getServer().getPlayerList().getPlayers().forEach(p -> {
                if (p != player) {
                    p.connection.send(new net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket(
                        net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, player));
                }
            });
            player.sendSystemMessage(Component.literal("§cYa no eres invisible."));
        }
        
        return 1;
    }
}
package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TpaCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpa")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(TpaCommand::requestTeleport)));
        
        dispatcher.register(Commands.literal("tpaccept")
            .executes(TpaCommand::acceptTeleport));
        
        dispatcher.register(Commands.literal("tpdeny")
            .executes(TpaCommand::denyTeleport));
    }
    
    private static int requestTeleport(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_TPA.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /tpa está deshabilitado."));
            return 0;
        }
        
        ServerPlayer sender = ctx.getSource().getPlayer();
        if (sender == null) return 0;
        
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            
            if (target == sender) {
                sender.sendSystemMessage(Component.literal("§cNo puedes enviarte una solicitud a ti mismo."));
                return 0;
            }
            
            PlayerDataManager.PlayerData data = PlayerDataManager.getData(target.getUUID());
            data.setTpaRequest(sender.getUUID(), System.currentTimeMillis());
            
            sender.sendSystemMessage(Component.literal("§aSolicitud de teletransporte enviada a " + 
                target.getName().getString() + "."));
            target.sendSystemMessage(Component.literal("§e" + sender.getName().getString() + 
                " te ha enviado una solicitud de teletransporte."));
            target.sendSystemMessage(Component.literal("§eUsa /tpaccept para aceptar o /tpdeny para rechazar."));
            
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cJugador no encontrado."));
            return 0;
        }
    }
    
    private static int acceptTeleport(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        if (data.getTpaRequest() == null) {
            player.sendSystemMessage(Component.literal("§cNo tienes solicitudes pendientes."));
            return 0;
        }
        
        long timeSince = System.currentTimeMillis() - data.getTpaRequestTime();
        if (timeSince > 60000) {
            data.clearTpaRequest();
            player.sendSystemMessage(Component.literal("§cLa solicitud ha expirado."));
            return 0;
        }
        
        ServerPlayer sender = player.getServer().getPlayerList().getPlayer(data.getTpaRequest());
        if (sender == null) {
            data.clearTpaRequest();
            player.sendSystemMessage(Component.literal("§cEl jugador no está conectado."));
            return 0;
        }
        
        sender.teleportTo(player.serverLevel(), player.getX(), player.getY(), player.getZ(), 
            sender.getYRot(), sender.getXRot());
        
        sender.sendSystemMessage(Component.literal("§aTeletransportado a " + player.getName().getString() + "."));
        player.sendSystemMessage(Component.literal("§a" + sender.getName().getString() + " fue teletransportado a ti."));
        
        data.clearTpaRequest();
        return 1;
    }
    
    private static int denyTeleport(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        if (data.getTpaRequest() == null) {
            player.sendSystemMessage(Component.literal("§cNo tienes solicitudes pendientes."));
            return 0;
        }
        
        ServerPlayer sender = player.getServer().getPlayerList().getPlayer(data.getTpaRequest());
        if (sender != null) {
            sender.sendSystemMessage(Component.literal("§c" + player.getName().getString() + 
                " rechazó tu solicitud de teletransporte."));
        }
        
        player.sendSystemMessage(Component.literal("§cSolicitud rechazada."));
        data.clearTpaRequest();
        
        return 1;
    }
}
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
        // /tpa <player> - Solicitar teletransportarse a otro jugador
        dispatcher.register(Commands.literal("tpa")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(TpaCommand::sendTpaRequest)));
        
        // /tpahere <player> - Solicitar que otro jugador se teletransporte a ti
        dispatcher.register(Commands.literal("tpahere")
            .then(Commands.argument("player", EntityArgument.player())
                .executes(TpaCommand::sendTpaHereRequest)));
        
        // /tpaccept - Aceptar solicitud de teletransporte
        dispatcher.register(Commands.literal("tpaccept")
            .executes(TpaCommand::acceptTpa));
        
        // /tpdeny - Rechazar solicitud de teletransporte
        dispatcher.register(Commands.literal("tpdeny")
            .executes(TpaCommand::denyTpa));
    }
    
    private static int sendTpaRequest(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_TPA.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /tpa está deshabilitado."));
            return 0;
        }
        
        ServerPlayer sender = ctx.getSource().getPlayer();
        if (sender == null) return 0;
        
        ServerPlayer target;
        try {
            target = EntityArgument.getPlayer(ctx, "player");
        } catch (Exception e) {
            sender.sendSystemMessage(Component.literal("§cJugador no encontrado."));
            return 0;
        }
        
        if (sender == target) {
            sender.sendSystemMessage(Component.literal("§cNo puedes enviarte una solicitud a ti mismo."));
            return 0;
        }
        
        PlayerDataManager.PlayerData targetData = PlayerDataManager.getData(target.getUUID());
        targetData.setTpaRequest(sender.getUUID(), System.currentTimeMillis(), false);
        
        sender.sendSystemMessage(Component.literal("§aSolicitud de teletransporte enviada a " + target.getName().getString() + "."));
        target.sendSystemMessage(Component.literal("§e" + sender.getName().getString() + " quiere teletransportarse a ti. Usa /tpaccept o /tpdeny."));
        
        return 1;
    }
    
    private static int sendTpaHereRequest(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_TPA.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /tpahere está deshabilitado."));
            return 0;
        }
        
        ServerPlayer sender = ctx.getSource().getPlayer();
        if (sender == null) return 0;
        
        ServerPlayer target;
        try {
            target = EntityArgument.getPlayer(ctx, "player");
        } catch (Exception e) {
            sender.sendSystemMessage(Component.literal("§cJugador no encontrado."));
            return 0;
        }
        
        if (sender == target) {
            sender.sendSystemMessage(Component.literal("§cNo puedes enviarte una solicitud a ti mismo."));
            return 0;
        }
        
        PlayerDataManager.PlayerData targetData = PlayerDataManager.getData(target.getUUID());
        targetData.setTpaRequest(sender.getUUID(), System.currentTimeMillis(), true);
        
        sender.sendSystemMessage(Component.literal("§aSolicitud enviada a " + target.getName().getString() + " para que se teletransporte a ti."));
        target.sendSystemMessage(Component.literal("§e" + sender.getName().getString() + " quiere que te teletransportes a él. Usa /tpaccept o /tpdeny."));
        
        return 1;
    }
    
    private static int acceptTpa(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        if (data.getTpaRequest() == null) {
            player.sendSystemMessage(Component.literal("§cNo tienes solicitudes de teletransporte pendientes."));
            return 0;
        }
        
        // Verificar que la solicitud no haya expirado (60 segundos)
        long currentTime = System.currentTimeMillis();
        if (currentTime - data.getTpaRequestTime() > 60000) {
            player.sendSystemMessage(Component.literal("§cLa solicitud de teletransporte ha expirado."));
            data.clearTpaRequest();
            return 0;
        }
        
        ServerPlayer requester = player.getServer().getPlayerList().getPlayer(data.getTpaRequest());
        if (requester == null) {
            player.sendSystemMessage(Component.literal("§cEl jugador no está conectado."));
            data.clearTpaRequest();
            return 0;
        }
        
        boolean isTpaHere = data.isTpaHereRequest();
        
        if (isTpaHere) {
            // El jugador se teletransporta al que envió la solicitud
            BackCommand.savePreviousLocation(player);
            player.teleportTo(requester.serverLevel(), 
                requester.getX(), requester.getY(), requester.getZ(),
                player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.literal("§aTeletransportado a " + requester.getName().getString() + "."));
            requester.sendSystemMessage(Component.literal("§a" + player.getName().getString() + " se ha teletransportado a ti."));
        } else {
            // El que envió la solicitud se teletransporta al jugador
            BackCommand.savePreviousLocation(requester);
            requester.teleportTo(player.serverLevel(), 
                player.getX(), player.getY(), player.getZ(),
                requester.getYRot(), requester.getXRot());
            requester.sendSystemMessage(Component.literal("§aTeletransportado a " + player.getName().getString() + "."));
            player.sendSystemMessage(Component.literal("§a" + requester.getName().getString() + " se ha teletransportado a ti."));
        }
        
        data.clearTpaRequest();
        return 1;
    }
    
    private static int denyTpa(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        if (data.getTpaRequest() == null) {
            player.sendSystemMessage(Component.literal("§cNo tienes solicitudes de teletransporte pendientes."));
            return 0;
        }
        
        ServerPlayer requester = player.getServer().getPlayerList().getPlayer(data.getTpaRequest());
        if (requester != null) {
            requester.sendSystemMessage(Component.literal("§c" + player.getName().getString() + " ha rechazado tu solicitud."));
        }
        
        player.sendSystemMessage(Component.literal("§cSolicitud rechazada."));
        data.clearTpaRequest();
        
        return 1;
    }
}
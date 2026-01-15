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

public class GodCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("god")
            .requires(source -> source.hasPermission(2))
            .executes(GodCommand::toggleSelf)
            .then(Commands.argument("player", EntityArgument.player())
                .executes(GodCommand::toggleOther)));
    }
    
    private static int toggleSelf(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_GOD.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /god está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        toggleGod(player);
        return 1;
    }
    
    private static int toggleOther(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_GOD.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /god está deshabilitado."));
            return 0;
        }
        
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            toggleGod(target);
            ctx.getSource().sendSuccess(() -> Component.literal("§aModo dios de " + 
                target.getName().getString() + " cambiado."), true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cJugador no encontrado."));
            return 0;
        }
    }
    
    private static void toggleGod(ServerPlayer player) {
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        boolean god = !data.isGod();
        data.setGod(god);
        
        player.getAbilities().invulnerable = god;
        player.onUpdateAbilities();
        
        player.sendSystemMessage(Component.literal(god ? "§aModo dios activado." : "§cModo dios desactivado."));
    }
}
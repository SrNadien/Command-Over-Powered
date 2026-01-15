package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class AfkCommand {
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
        
        if (afk) {
            ctx.getSource().getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("§7" + player.getName().getString() + " está AFK."), false);
        } else {
            ctx.getSource().getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("§7" + player.getName().getString() + " ya no está AFK."), false);
        }
        
        return 1;
    }
}
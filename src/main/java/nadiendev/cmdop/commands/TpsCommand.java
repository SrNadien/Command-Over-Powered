package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public class TpsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tps")
            .requires(source -> source.hasPermission(2))
            .executes(TpsCommand::showTps));
    }
    
    private static int showTps(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_TPS.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /tps está deshabilitado."));
            return 0;
        }
        
        MinecraftServer server = ctx.getSource().getServer();
        
        double mspt = server.getAverageTickTimeNanos() / 1_000_000.0;
        double tps = Math.min(1000.0 / mspt, 20.0);
        
        String color;
        if (tps >= 18) {
            color = "§a";
        } else if (tps >= 15) {
            color = "§e";
        } else {
            color = "§c";
        }
        
        ctx.getSource().sendSuccess(() -> Component.literal("§6TPS: " + color + 
            String.format("%.2f", tps) + " §7(MSPT: " + String.format("%.2f", mspt) + "ms)"), false);
        
        return 1;
    }
}
package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class MoreCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("more")
            .requires(source -> source.hasPermission(2))
            .executes(ctx -> fillStack(ctx, 64))
            .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                .executes(ctx -> fillStack(ctx, IntegerArgumentType.getInteger(ctx, "amount")))));
    }
    
    private static int fillStack(CommandContext<CommandSourceStack> ctx, int amount) {
        if (!cmdopConfig.ENABLE_MORE.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /more está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        ItemStack held = player.getMainHandItem();
        
        if (held.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cDebes sostener un ítem."));
            return 0;
        }
        
        int maxStack = Math.min(held.getMaxStackSize(), amount);
        held.setCount(maxStack);
        
        player.sendSystemMessage(Component.literal("§aÍtem rellenado a " + maxStack + "."));
        
        return 1;
    }
}
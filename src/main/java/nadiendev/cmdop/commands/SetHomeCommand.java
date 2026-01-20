package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SetHomeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sethome")
            .executes(ctx -> setHome(ctx, "home"))
            .then(Commands.argument("name", StringArgumentType.word())
                .executes(ctx -> setHome(ctx, StringArgumentType.getString(ctx, "name")))));
    }
    
    private static int setHome(CommandContext<CommandSourceStack> ctx, String name) {
        if (!cmdopConfig.ENABLE_HOME.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /sethome está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        if (data.getHomes().size() >= cmdopConfig.MAX_HOMES.get() && !data.getHomes().containsKey(name)) {
            player.sendSystemMessage(Component.literal("§cHas alcanzado el máximo de hogares (" + 
                cmdopConfig.MAX_HOMES.get() + ")."));
            return 0;
        }
        
        BlockPos pos = player.blockPosition();
        String dimension = player.level().dimension().location().toString();
        
        data.addHome(name, pos, dimension);
        
        // GUARDAR AUTOMÁTICAMENTE
        PlayerDataManager.savePlayerData(player.getUUID());
        
        player.sendSystemMessage(Component.literal("§aHogar '" + name + "' establecido."));
        
        return 1;
    }
}
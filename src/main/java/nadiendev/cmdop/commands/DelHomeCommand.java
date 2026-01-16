package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DelHomeCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_HOMES = (ctx, builder) -> {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player != null) {
            PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
            return SharedSuggestionProvider.suggest(data.getHomes().keySet(), builder);
        }
        return builder.buildFuture();
    };
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("delhome")
            .then(Commands.argument("name", StringArgumentType.word())
                .suggests(SUGGEST_HOMES)
                .executes(DelHomeCommand::deleteHome)));
        
        dispatcher.register(Commands.literal("removehome")
            .then(Commands.argument("name", StringArgumentType.word())
                .suggests(SUGGEST_HOMES)
                .executes(DelHomeCommand::deleteHome)));
    }
    
    private static int deleteHome(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_HOME.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /delhome está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        String name = StringArgumentType.getString(ctx, "name");
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        if (!data.getHomes().containsKey(name)) {
            player.sendSystemMessage(Component.literal("§cNo existe un hogar con el nombre '" + name + "'."));
            return 0;
        }
        
        data.removeHome(name);
        player.sendSystemMessage(Component.literal("§aHogar '" + name + "' eliminado."));
        
        return 1;
    }
}
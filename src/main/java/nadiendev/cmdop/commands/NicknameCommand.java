package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.data.PlayerDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class NicknameCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nickname")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("nickname", StringArgumentType.greedyString())
                .executes(ctx -> setNickname(ctx, StringArgumentType.getString(ctx, "nickname"), null)))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("nickname", StringArgumentType.greedyString())
                    .executes(ctx -> setNickname(ctx, StringArgumentType.getString(ctx, "nickname"), 
                        EntityArgument.getPlayer(ctx, "player"))))));
        
        dispatcher.register(Commands.literal("nick")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("nickname", StringArgumentType.greedyString())
                .executes(ctx -> setNickname(ctx, StringArgumentType.getString(ctx, "nickname"), null)))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("nickname", StringArgumentType.greedyString())
                    .executes(ctx -> setNickname(ctx, StringArgumentType.getString(ctx, "nickname"), 
                        EntityArgument.getPlayer(ctx, "player"))))));
    }
    
    private static int setNickname(CommandContext<CommandSourceStack> ctx, String nickname, ServerPlayer target) {
        if (!cmdopConfig.ENABLE_NICKNAME.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /nickname está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = target != null ? target : ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        PlayerDataManager.PlayerData data = PlayerDataManager.getData(player.getUUID());
        
        if (nickname.equalsIgnoreCase("off") || nickname.equalsIgnoreCase("reset")) {
            data.setNickname(null);
            player.setCustomName(null);
            player.sendSystemMessage(Component.literal("§aNickname reiniciado."));
        } else {
            String coloredNick = nickname.replace("&", "§");
            data.setNickname(coloredNick);
            player.setCustomName(Component.literal(coloredNick));
            player.sendSystemMessage(Component.literal("§aNickname establecido a: " + coloredNick));
        }
        
        if (target != null) {
            ctx.getSource().sendSuccess(() -> Component.literal("§aNickname de " + 
                player.getName().getString() + " cambiado."), true);
        }
        
        return 1;
    }
}
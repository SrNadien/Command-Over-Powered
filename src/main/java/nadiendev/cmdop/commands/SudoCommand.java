package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SudoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sudo")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("command", StringArgumentType.greedyString())
                    .executes(SudoCommand::executeSudo))));
    }
    
    private static int executeSudo(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_SUDO.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /sudo está deshabilitado."));
            return 0;
        }
        
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            ServerPlayer sender = ctx.getSource().getPlayer();
            String command = StringArgumentType.getString(ctx, "command");
            
           
            if (sender != null && target.getUUID().equals(sender.getUUID())) {
                ctx.getSource().sendFailure(Component.literal("§cNo puedes usar sudo en ti mismo."));
                return 0;
            }
            
           
            if (command.toLowerCase().startsWith("c:")) {
                final String chatMessage = command.substring(2).trim();
                
                target.getServer().execute(() -> {
                    
                    target.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal("<" + target.getName().getString() + "> " + chatMessage), 
                        false
                    );
                });
                
                ctx.getSource().sendSuccess(() -> 
                    Component.literal("§aForzado a " + target.getName().getString() + " a decir: " + chatMessage), 
                    true);
                return 1;
            }
            
            
            if (command.startsWith("/")) {
                command = command.substring(1);
            }
            
            final String finalCommand = command;
            
           
            target.getServer().execute(() -> {
                try {
                    target.getServer().getCommands().performPrefixedCommand(
                        target.createCommandSourceStack(), 
                        finalCommand
                    );
                } catch (Exception e) {
                    if (sender != null) {
                        sender.sendSystemMessage(Component.literal("§cError al ejecutar el comando."));
                    }
                }
            });
            
            ctx.getSource().sendSuccess(() -> 
                Component.literal("§aForzado a " + target.getName().getString() + " a ejecutar: /" + finalCommand), 
                true);
            
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cNo se pudo ejecutar el comando."));
            return 0;
        }
    }
}
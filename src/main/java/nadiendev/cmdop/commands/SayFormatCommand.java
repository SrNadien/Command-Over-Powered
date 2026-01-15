package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class SayFormatCommand {
    private static String customSayFormat = null;
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sayformat")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("format", StringArgumentType.greedyString())
                .executes(SayFormatCommand::setFormat)));
    }
    
    private static int setFormat(CommandContext<CommandSourceStack> ctx) {
        String format = StringArgumentType.getString(ctx, "format");
        
        if (format.equalsIgnoreCase("reset") || format.equalsIgnoreCase("default")) {
            customSayFormat = null;
            ctx.getSource().sendSuccess(() -> Component.literal("§aFormato de /say restablecido al predeterminado."), true);
        } else {
            customSayFormat = format.replace("&", "§");
            ctx.getSource().sendSuccess(() -> Component.literal("§aFormato de /say cambiado a: " + customSayFormat), true);
        }
        
        return 1;
    }
    
    public static String getSayFormat() {
        if (customSayFormat != null) {
            return customSayFormat;
        }
        return cmdopConfig.SAY_FORMAT.get().replace("&", "§");
    }
}
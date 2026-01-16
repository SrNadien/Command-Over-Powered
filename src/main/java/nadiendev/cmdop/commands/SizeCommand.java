package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SizeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("size")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("scale", DoubleArgumentType.doubleArg(0.0625))
                    .executes(SizeCommand::changeSize))));
    }
    
    private static int changeSize(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_SIZE.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /size está deshabilitado."));
            return 0;
        }
        
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            double scale = DoubleArgumentType.getDouble(ctx, "scale");
            
            int configMax = cmdopConfig.SIZE_MAX_SCALE.get();
            
            
            if (scale > configMax) {
                ctx.getSource().sendFailure(Component.literal("§cEl tamaño máximo permitido es " + configMax + "."));
                return 0;
            }
            
            Holder<Attribute> scaleAttr = Attributes.SCALE;
            AttributeInstance instance = target.getAttribute(scaleAttr);
            
            if (instance != null) {
                instance.setBaseValue(scale);
                
                ctx.getSource().sendSuccess(() -> Component.literal("§aTamaño de " + 
                    target.getName().getString() + " cambiado a " + scale + "."), true);
                target.sendSystemMessage(Component.literal("§aTu tamaño ha sido cambiado a " + scale + "."));
                
                return 1;
            }
            
            ctx.getSource().sendFailure(Component.literal("§cNo se pudo cambiar el tamaño."));
            return 0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§cJugador no encontrado."));
            return 0;
        }
    }
}
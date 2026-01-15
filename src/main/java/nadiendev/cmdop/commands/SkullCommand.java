package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import com.mojang.authlib.GameProfile;

public class SkullCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("skull")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", StringArgumentType.word())
                .executes(SkullCommand::giveSkull)));
    }
    
    private static int giveSkull(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_SKULL.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /skull está deshabilitado."));
            return 0;
        }
        
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        
        String targetName = StringArgumentType.getString(ctx, "player");
        
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        GameProfile profile = new GameProfile(null, targetName);
        skull.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        
        if (!player.getInventory().add(skull)) {
            player.drop(skull, false);
        }
        
        player.sendSystemMessage(Component.literal("§aRecibiste la cabeza de " + targetName + "."));
        
        return 1;
    }
}
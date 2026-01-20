package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class HelpCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        //  /cmdop help
        dispatcher.register(Commands.literal("cmdop")
            .then(Commands.literal("help")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayer();
                    if (player != null) {
                        showHelp(player);
                    } else {
                        showHelpConsole(ctx.getSource());
                    }
                    return 1;
                }))
        );
        
        // También mantener /cmdophelp por compatibilidad
        dispatcher.register(Commands.literal("cmdophelp")
            .executes(ctx -> {
                ServerPlayer player = ctx.getSource().getPlayer();
                if (player != null) {
                    showHelp(player);
                } else {
                    showHelpConsole(ctx.getSource());
                }
                return 1;
            })
        );
    }
    
    private static void showHelp(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§6§l=== CMDOP - Lista de Comandos ==="));
        player.sendSystemMessage(Component.literal(""));
        
        if (cmdopConfig.ENABLE_FLY.get()) {
            player.sendSystemMessage(Component.literal("§e/fly §7- Activa o desactiva el modo vuelo"));
        }
        
        if (cmdopConfig.ENABLE_VANISH.get()) {
            player.sendSystemMessage(Component.literal("§e/vanish §7- Te hace invisible para otros jugadores"));
        }
        
        if (cmdopConfig.ENABLE_GOD.get()) {
            player.sendSystemMessage(Component.literal("§e/god §7- Activa o desactiva el modo dios"));
        }
        
        if (cmdopConfig.ENABLE_GAMEMODE.get()) {
            player.sendSystemMessage(Component.literal("§e/gmc §7- Cambia a modo creativo"));
            player.sendSystemMessage(Component.literal("§e/gms §7- Cambia a modo supervivencia"));
            player.sendSystemMessage(Component.literal("§e/gma §7- Cambia a modo aventura"));
            player.sendSystemMessage(Component.literal("§e/gmsp §7- Cambia a modo espectador"));
        }
        
        if (cmdopConfig.ENABLE_NICKNAME.get()) {
            player.sendSystemMessage(Component.literal("§e/nick <nombre> §7- Establece tu nickname"));
            player.sendSystemMessage(Component.literal("§e/nick reset §7- Reinicia tu nickname"));
        }
        
        if (cmdopConfig.ENABLE_HOME.get()) {
            player.sendSystemMessage(Component.literal("§e/sethome <nombre> §7- Establece un hogar"));
            player.sendSystemMessage(Component.literal("§e/home <nombre> §7- Teletransporta a un hogar"));
            player.sendSystemMessage(Component.literal("§e/delhome <nombre> §7- Elimina un hogar"));
            player.sendSystemMessage(Component.literal("§e/homes §7- Lista todos tus hogares"));
        }
        
        if (cmdopConfig.ENABLE_TPA.get()) {
            player.sendSystemMessage(Component.literal("§e/tpa <jugador> §7- Solicita teletransportarte a un jugador"));
            player.sendSystemMessage(Component.literal("§e/tpahere <jugador> §7- Solicita que un jugador se teletransporte a ti"));
            player.sendSystemMessage(Component.literal("§e/tpaccept §7- Acepta una solicitud de teletransporte"));
            player.sendSystemMessage(Component.literal("§e/tpyes §7- Acepta una solicitud de teletransporte"));
            player.sendSystemMessage(Component.literal("§e/tpdeny §7- Rechaza una solicitud de teletransporte"));
        }
        
        if (cmdopConfig.ENABLE_HEAL.get()) {
            player.sendSystemMessage(Component.literal("§e/heal §7- Restaura tu vida al máximo"));
        }
        
        if (cmdopConfig.ENABLE_FEED.get()) {
            player.sendSystemMessage(Component.literal("§e/feed §7- Restaura tu hambre al máximo"));
        }
        
        if (cmdopConfig.ENABLE_REPAIR.get()) {
            player.sendSystemMessage(Component.literal("§e/repair §7- Repara el item en tu mano"));
            player.sendSystemMessage(Component.literal("§e/fix §7- Repara el item en tu mano"));
        }
        
        if (cmdopConfig.ENABLE_ENDERCHEST.get()) {
            player.sendSystemMessage(Component.literal("§e/enderchest §7- Abre tu enderchest"));
            player.sendSystemMessage(Component.literal("§e/ec §7- Abre tu enderchest"));
        }
        
        if (cmdopConfig.ENABLE_INVSEE.get()) {
            player.sendSystemMessage(Component.literal("§e/invsee <jugador> §7- Ve el inventario de un jugador"));
        }
        
        if (cmdopConfig.ENABLE_WORKBENCH.get()) {
            player.sendSystemMessage(Component.literal("§e/workbench §7- Abre una mesa de crafteo"));
            player.sendSystemMessage(Component.literal("§e/wb §7- Abre una mesa de crafteo"));
        }
        
        if (cmdopConfig.ENABLE_SKULL.get()) {
            player.sendSystemMessage(Component.literal("§e/skull <jugador> §7- Obtiene la cabeza de un jugador"));
        }
        
        if (cmdopConfig.ENABLE_MORE.get()) {
            player.sendSystemMessage(Component.literal("§e/more §7- Maximiza el stack del item en tu mano"));
        }
        
        if (cmdopConfig.ENABLE_SIZE.get()) {
            player.sendSystemMessage(Component.literal("§e/size <tamaño> §7- Cambia tu tamaño"));
        }
        
        if (cmdopConfig.ENABLE_SPEED.get()) {
            player.sendSystemMessage(Component.literal("§e/speed <velocidad> §7- Cambia tu velocidad"));
        }
        
        if (cmdopConfig.ENABLE_TPS.get()) {
            player.sendSystemMessage(Component.literal("§e/tps §7- Muestra el TPS del servidor"));
        }
        
        if (cmdopConfig.ENABLE_TIME_WEATHER.get()) {
            player.sendSystemMessage(Component.literal("§e/day §7- Cambia el tiempo a día"));
            player.sendSystemMessage(Component.literal("§e/night §7- Cambia el tiempo a noche"));
            player.sendSystemMessage(Component.literal("§e/sun §7- Cambia el clima a soleado"));
            player.sendSystemMessage(Component.literal("§e/rain §7- Cambia el clima a lluvia"));
        }
        
        if (cmdopConfig.ENABLE_AFK.get()) {
            player.sendSystemMessage(Component.literal("§e/afk §7- Marca tu estado como ausente"));
        }
        
        if (cmdopConfig.ENABLE_SUDO.get()) {
            player.sendSystemMessage(Component.literal("§e/sudo <jugador> <comando> §7- Ejecuta un comando como otro jugador"));
        }
        
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§6§l================================"));
    }
    
    private static void showHelpConsole(CommandSourceStack source) {
        source.sendSystemMessage(Component.literal("=== CMDOP - Lista de Comandos ==="));
        source.sendSystemMessage(Component.literal(""));
        
        if (cmdopConfig.ENABLE_FLY.get()) {
            source.sendSystemMessage(Component.literal("/fly - Activa o desactiva el modo vuelo"));
        }
        
        if (cmdopConfig.ENABLE_VANISH.get()) {
            source.sendSystemMessage(Component.literal("/vanish - Te hace invisible para otros jugadores"));
        }
        
        if (cmdopConfig.ENABLE_GOD.get()) {
            source.sendSystemMessage(Component.literal("/god - Activa o desactiva el modo dios"));
        }
        
        if (cmdopConfig.ENABLE_GAMEMODE.get()) {
            source.sendSystemMessage(Component.literal("/gmc - Cambia a modo creativo"));
            source.sendSystemMessage(Component.literal("/gms - Cambia a modo supervivencia"));
            source.sendSystemMessage(Component.literal("/gma - Cambia a modo aventura"));
            source.sendSystemMessage(Component.literal("/gmsp - Cambia a modo espectador"));
        }
        
        if (cmdopConfig.ENABLE_NICKNAME.get()) {
            source.sendSystemMessage(Component.literal("/nick <nombre> - Establece tu nickname"));
            source.sendSystemMessage(Component.literal("/nick reset - Reinicia tu nickname"));
        }
        
        if (cmdopConfig.ENABLE_HOME.get()) {
            source.sendSystemMessage(Component.literal("/sethome <nombre> - Establece un hogar"));
            source.sendSystemMessage(Component.literal("/home <nombre> - Teletransporta a un hogar"));
            source.sendSystemMessage(Component.literal("/delhome <nombre> - Elimina un hogar"));
            source.sendSystemMessage(Component.literal("/homes - Lista todos tus hogares"));
        }
        
        if (cmdopConfig.ENABLE_TPA.get()) {
            source.sendSystemMessage(Component.literal("/tpa <jugador> - Solicita teletransportarte a un jugador"));
            source.sendSystemMessage(Component.literal("/tpahere <jugador> - Solicita que un jugador se teletransporte a ti"));
            source.sendSystemMessage(Component.literal("/tpaccept - Acepta una solicitud de teletransporte"));
            source.sendSystemMessage(Component.literal("/tpyes - Acepta una solicitud de teletransporte"));
            source.sendSystemMessage(Component.literal("/tpdeny - Rechaza una solicitud de teletransporte"));
        }
        
        if (cmdopConfig.ENABLE_HEAL.get()) {
            source.sendSystemMessage(Component.literal("/heal - Restaura tu vida al máximo"));
        }
        
        if (cmdopConfig.ENABLE_FEED.get()) {
            source.sendSystemMessage(Component.literal("/feed - Restaura tu hambre al máximo"));
        }
        
        if (cmdopConfig.ENABLE_REPAIR.get()) {
            source.sendSystemMessage(Component.literal("/repair - Repara el item en tu mano"));
            source.sendSystemMessage(Component.literal("/fix - Repara el item en tu mano"));
        }
        
        if (cmdopConfig.ENABLE_ENDERCHEST.get()) {
            source.sendSystemMessage(Component.literal("/enderchest - Abre tu enderchest"));
            source.sendSystemMessage(Component.literal("/ec - Abre tu enderchest"));
        }
        
        if (cmdopConfig.ENABLE_INVSEE.get()) {
            source.sendSystemMessage(Component.literal("/invsee <jugador> - Ve el inventario de un jugador"));
        }
        
        if (cmdopConfig.ENABLE_WORKBENCH.get()) {
            source.sendSystemMessage(Component.literal("/workbench - Abre una mesa de crafteo"));
            source.sendSystemMessage(Component.literal("/wb - Abre una mesa de crafteo"));
        }
        
        if (cmdopConfig.ENABLE_SKULL.get()) {
            source.sendSystemMessage(Component.literal("/skull <jugador> - Obtiene la cabeza de un jugador"));
        }
        
        if (cmdopConfig.ENABLE_MORE.get()) {
            source.sendSystemMessage(Component.literal("/more - Maximiza el stack del item en tu mano"));
        }
        
        if (cmdopConfig.ENABLE_SIZE.get()) {
            source.sendSystemMessage(Component.literal("/size <tamaño> - Cambia tu tamaño"));
        }
        
        if (cmdopConfig.ENABLE_SPEED.get()) {
            source.sendSystemMessage(Component.literal("/speed <velocidad> - Cambia tu velocidad"));
        }
        
        if (cmdopConfig.ENABLE_TPS.get()) {
            source.sendSystemMessage(Component.literal("/tps - Muestra el TPS del servidor"));
        }
        
        if (cmdopConfig.ENABLE_TIME_WEATHER.get()) {
            source.sendSystemMessage(Component.literal("/day - Cambia el tiempo a día"));
            source.sendSystemMessage(Component.literal("/night - Cambia el tiempo a noche"));
            source.sendSystemMessage(Component.literal("/sun - Cambia el clima a soleado"));
            source.sendSystemMessage(Component.literal("/rain - Cambia el clima a lluvia"));
        }
        
        if (cmdopConfig.ENABLE_AFK.get()) {
            source.sendSystemMessage(Component.literal("/afk - Marca tu estado como ausente"));
        }
        
        if (cmdopConfig.ENABLE_SUDO.get()) {
            source.sendSystemMessage(Component.literal("/sudo <jugador> <comando> - Ejecuta un comando como otro jugador"));
        }
        
        source.sendSystemMessage(Component.literal(""));
        source.sendSystemMessage(Component.literal("================================"));
    }
}
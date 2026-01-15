package nadiendev.cmdop.commands;

import nadiendev.cmdop.config.cmdopConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class TimeWeatherCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("day")
            .requires(source -> source.hasPermission(2))
            .executes(TimeWeatherCommand::setDay));
        
        dispatcher.register(Commands.literal("night")
            .requires(source -> source.hasPermission(2))
            .executes(TimeWeatherCommand::setNight));
        
        dispatcher.register(Commands.literal("sun")
            .requires(source -> source.hasPermission(2))
            .executes(TimeWeatherCommand::clearWeather));
    }
    
    private static int setDay(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_TIME_WEATHER.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /day está deshabilitado."));
            return 0;
        }
        
        for (ServerLevel level : ctx.getSource().getServer().getAllLevels()) {
            level.setDayTime(1000);
        }
        
        ctx.getSource().sendSuccess(() -> Component.literal("§aHorario cambiado a día."), true);
        return 1;
    }
    
    private static int setNight(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_TIME_WEATHER.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /night está deshabilitado."));
            return 0;
        }
        
        for (ServerLevel level : ctx.getSource().getServer().getAllLevels()) {
            level.setDayTime(13000);
        }
        
        ctx.getSource().sendSuccess(() -> Component.literal("§aHorario cambiado a noche."), true);
        return 1;
    }
    
    private static int clearWeather(CommandContext<CommandSourceStack> ctx) {
        if (!cmdopConfig.ENABLE_TIME_WEATHER.get()) {
            ctx.getSource().sendFailure(Component.literal("§cEl comando /sun está deshabilitado."));
            return 0;
        }
        
        for (ServerLevel level : ctx.getSource().getServer().getAllLevels()) {
            level.setWeatherParameters(6000, 0, false, false);
        }
        
        ctx.getSource().sendSuccess(() -> Component.literal("§aClima despejado."), true);
        return 1;
    }
}
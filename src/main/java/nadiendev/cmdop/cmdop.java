package nadiendev.cmdop;

import nadiendev.cmdop.commands.*;
import nadiendev.cmdop.config.cmdopConfig;
import nadiendev.cmdop.event.ChatFormatHandler;
import nadiendev.cmdop.event.DeathLocationHandler;
import nadiendev.cmdop.event.MovementHandler;
import nadiendev.cmdop.event.NicknameEventHandler;
import nadiendev.cmdop.event.ServerLifecycleEvents;
import nadiendev.cmdop.data.PlayerDataManager;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(cmdop.MODID)
public class cmdop {
    public static final String MODID = "cmdop";
    
    public cmdop(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, cmdopConfig.SPEC);
        
        // Registrar listeners de comandos
        NeoForge.EVENT_BUS.addListener(this::onCommandsRegister);
        
        // Registrar listener para cuando inicie el servidor
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        
        // Registrar event handlers
        NeoForge.EVENT_BUS.register(new ChatFormatHandler());
        NeoForge.EVENT_BUS.register(new DeathLocationHandler());
        NeoForge.EVENT_BUS.register(new MovementHandler());
        NeoForge.EVENT_BUS.register(new NicknameEventHandler());
        NeoForge.EVENT_BUS.register(new ServerLifecycleEvents());
    }
    
    private void onServerStarting(ServerStartingEvent event) {
        // Inicializar PlayerDataManager con la ruta del mundo
        PlayerDataManager.initialize(event.getServer().getWorldPath(LevelResource.ROOT));
    }
    
    private void onCommandsRegister(RegisterCommandsEvent event) {
        FlyCommand.register(event.getDispatcher());
        VanishCommand.register(event.getDispatcher());
        SetHomeCommand.register(event.getDispatcher());
        DelHomeCommand.register(event.getDispatcher());
        HomeCommand.register(event.getDispatcher());
        GamemodeCommand.register(event.getDispatcher());
        TpaCommand.register(event.getDispatcher());
        GodCommand.register(event.getDispatcher());
        NickCommand.register(event.getDispatcher());
        EnderChestCommand.register(event.getDispatcher());
        InvseeCommand.register(event.getDispatcher());
        SkullCommand.register(event.getDispatcher());
        MoreCommand.register(event.getDispatcher());
        TpsCommand.register(event.getDispatcher());
        TimeWeatherCommand.register(event.getDispatcher());
        AfkCommand.register(event.getDispatcher());
        BackCommand.register(event.getDispatcher());
        ThorCommand.register(event.getDispatcher());
        WorkbenchCommand.register(event.getDispatcher());
        SizeCommand.register(event.getDispatcher());
        SpeedCommand.register(event.getDispatcher());
        SayFormatCommand.register(event.getDispatcher());
        HealCommand.register(event.getDispatcher());
        FeedCommand.register(event.getDispatcher());
        SudoCommand.register(event.getDispatcher());
        HomesCommand.register(event.getDispatcher());
        RepairCommand.register(event.getDispatcher());
        HelpCommand.register(event.getDispatcher());
    }
}
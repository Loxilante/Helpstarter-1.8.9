package com.loxil.helpstarter;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = HelpStarter.MODID, name = "HelpStarter", version = HelpStarter.VERSION)
public class HelpStarter
{
    public static final String MODID = "helpstarter";
    public static final String VERSION = "0.2.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // Register client command
        ClientCommandHandler.instance.registerCommand(new HsCommand());
    }
}

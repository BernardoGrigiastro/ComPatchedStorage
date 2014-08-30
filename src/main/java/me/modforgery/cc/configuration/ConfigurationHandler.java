package me.modforgery.cc.configuration;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import me.modforgery.cc.CompactChests;
import me.modforgery.cc.init.ChestReferences;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Toby on 29/08/2014.
 */
public class ConfigurationHandler
{
    public static Configuration configuration;

    public static void init(File configFile)
    {
        if(configuration == null)
        {
            configuration = new Configuration(configFile);
            loadConfiguration();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent configChangedEvent)
    {
        if(configChangedEvent.modID.equalsIgnoreCase(ChestReferences.ID))
        {
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {
        CompactChests.difficulty = configuration.getInt("difficulty", "Crafting", 0, 0, 5, "Sets the level of difficulty");

        if(configuration.hasChanged())
        {
            configuration.save();
        }
    }
}
package com.c1ouds.betteriron;

import java.io.File;
import java.util.Set;

import com.c1ouds.betteriron.utility.ItemMetaKey;
import net.minecraftforge.common.config.Configuration;

public class Config {

    static public Set<ItemMetaKey> ironItems;
    static public float coalOreHardness = 3.0F;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        //greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?");
        coalOreHardness = configuration.getFloat("coalOreHardness", Configuration.CATEGORY_GENERAL,
            3.0F, 0.1F, 20.0F,"Default value is vanilla");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}

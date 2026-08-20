package com.c1ouds.betteriron.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class EarlyConfig {
    private static File cfgFile;

    /**
     * Универсальный метод для поиска любого float-значения в конфиге до загрузки Forge.
     * @param keyName Имя переменной в файле (например, "coalOreHardness")
     * @param defaultValue Значение по умолчанию, если файл не найден или строчка повреждена
     */
    public static float getFloat(String keyName, float defaultValue) {
        initialize(); //lazy init to read config

        if (!cfgFile.exists()) {
            System.out.println("[BetterIron: EarlyConfig] Config file not found");
            return defaultValue;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(cfgFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(keyName + "=")) {
                    float parsed = Float.parseFloat(line.substring(line.indexOf("=") + 1).trim());
                    System.out.println("[BetterIron: EarlyConfig] parsed coalOreHardness as " + parsed);
                    return parsed;
                }
            }
        } catch (Exception e) {
            System.out.println("[BetterIron: EarlyConfig] Couldn't read config file " + e.getMessage());
        }

        return defaultValue;
    }

    static void initialize() {
        if(cfgFile == null) {
            cfgFile = new File("config/betteriron.cfg");
        }
    }
}

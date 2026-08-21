package com.c1ouds.betteriron.utility;

import scala.Int;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class EarlyConfig {
    private static File cfgFile;
    private static boolean cfcFileCheck = false;

    /**
     * Универсальный метод для поиска любого float-значения в конфиге до загрузки Forge.
     * @param keyName Имя переменной в файле (например, "coalOreHardness")
     * @param defaultValue Значение по умолчанию, если файл не найден или строчка повреждена
     */
    public static float getFloat(String keyName, float defaultValue) {
        initialize(); //lazy init to read config

        if (!cfgFile.exists()) {
            if(!cfcFileCheck) {
                System.out.println("[BetterIron: EarlyConfig] Config file not found");
                cfcFileCheck = true;
            }
            return defaultValue;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(cfgFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(keyName + "=")) {
                    float parsed = Float.parseFloat(line.substring(line.indexOf("=") + 1).trim());
                    System.out.println("[BetterIron: EarlyConfig] parsed "+keyName+" as " + parsed);
                    return parsed;
                }
            }
        } catch (Exception e) {
            System.out.println("[BetterIron: EarlyConfig] Couldn't read "+keyName+" from config " + e.getMessage());
        }

        return defaultValue;
    }
    public static int getInt(String keyName, int defaultValue) {
        initialize(); //lazy init to read config

        if (!cfgFile.exists()) {
            System.out.println("[BetterIron: EarlyConfig] Config file not found");
            return defaultValue;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(cfgFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(keyName + "=")) {
                    int parsed = Integer.parseInt(line.substring(line.indexOf("=") + 1).trim());
                    System.out.println("[BetterIron: EarlyConfig] parsed "+keyName+" as " + parsed);
                    return parsed;
                }
            }
        } catch (Exception e) {
            System.out.println("[BetterIron: EarlyConfig] Couldn't read "+keyName+" from config " + e.getMessage());
        }

        return defaultValue;
    }

    static void initialize() {
        if(cfgFile == null) {
            cfgFile = new File("config/betteriron.cfg");
        }
    }
}

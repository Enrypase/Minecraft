package passcraft.poweritems;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class ConfigFile {

    private File file;
    private FileConfiguration customFile;

    public ConfigFile(){
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("PowerItems").getDataFolder(), "PowerItemsConfig.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            }
            catch(IOException e){

            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public void save(){
        try {
            customFile.save(file);
        }
        catch(IOException e){

        }
    }

    public FileConfiguration get(){
        return customFile;
    }

    public void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public String toString(){
        String object = "Normal File: " + file.getName() + " in " + file.getAbsolutePath() + "\n" + "YML File: " + customFile.getName() + " in " + customFile.getCurrentPath();
        return object;
    }

}

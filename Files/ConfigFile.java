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
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("PowerItems").getDataFolder(), "PowerItemsConfigurazione.yml");

        if(!file.exists()){
            try {
                file.createNewFile();
            }
            catch(IOException e){

            }
        }

        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration get(){
        return customFile;
    }

    public void save(){
        try {
            customFile.save(file);
        }
        catch(IOException e){

        }
    }

    public void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }

}

package passcraft.runes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public final class Runes extends JavaPlugin implements Listener {

    //---->> CATCH E OTTIMIZZAZIONI
    //Variables section
    String logPath;
    boolean otherLogs;
    ConfigFile configFile;

    int deleteLogsEvery = 1024;

    ArrayList<String> confirmShutdown;
    ArrayList<Rune> runes;
    ArrayList<PlayerRune> playerRunes;

    //Start & Stop methods section
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,this);

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        configFile = new ConfigFile();
        configFile.get().options().copyDefaults(true);
        configFile.save();

        confirmShutdown = new ArrayList<String>();

        System.out.println("[Runes] Enrypase -  Plugin's Path: " + Bukkit.getServer().getPluginManager().getPlugin("Runes").getDataFolder().getPath());
        logPath = Bukkit.getServer().getPluginManager().getPlugin("Runes").getDataFolder().getPath();

        runes = new ArrayList<Rune>();
        playerRunes = new ArrayList<PlayerRune>();

        readYML();
        System.out.println("[Runes] Enrypase - YML read successfull");

        System.out.println("[Runes] Enrypase - Remember to check the logs!");
    }

    @Override
    public void onDisable() {
        System.out.println("[Runes] Enrypase: Shutting down...");
        reloadLists();
    }

    //Commands section
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player && sender.isOp()){
            writeLog(logPath, ((Player) sender).getDisplayName() + " used " + command);

            if(command.getName().equals("reload")){
                System.out.println("[Runes] Enrypase - Reloading plugin");
                configFile.reload();
                reloadLists();
                readYML();
                sender.sendMessage(ChatColor.GREEN + "Plugin Resetted Succesfully");

                return true;
            }
            else if(command.getName().equals("getRunes")){
                for (int i = 0; i < runes.size(); i++) {
                    sender.sendMessage(Integer.toString(i) + ") "  + runes.get(i).toString() + "\n");
                }
                sender.sendMessage(ChatColor.GREEN + "Command Execution Successfully");
                return true;
            }
            else if(command.getName().equals("getRune")){
                if(args.length == 1){
                    try{
                        int position = Integer.parseInt(args[0]);
                        if(position > 0 && position < runes.size()){
                            sender.sendMessage(ChatColor.BOLD + Integer.toString(position) + ChatColor.RESET +  ") "  + runes.get(position).toString() + "\n");
                            sender.sendMessage(ChatColor.GREEN + "Command Execution Successfully");
                            return true;
                        }
                    }
                    catch(ArithmeticException exception){
                        sender.sendMessage(ChatColor.RED + "Command Execution Unsuccessfull - Wrong Param");
                        return false;
                    }
                }
                sender.sendMessage(ChatColor.RED + "Command Execution Unsuccessfull - Wrong Param");
                return false;
            }
            else if(command.getName().equals("shutdown")){
                if(confirmShutdown.contains(((Player) sender).getPlayerListName())){
                    confirmShutdown = new ArrayList<String>();
                    try {
                        getServer().getPluginManager().disablePlugin(this);
                    }
                    catch (Exception exception){
                        System.out.println("[Runes] Enrypase - Plugin stopped");
                    }
                    sender.sendMessage(ChatColor.GREEN + "Command Execution Successfully");
                    return true;
                }
                else{
                    confirmShutdown.add(((Player) sender).getPlayerListName());
                    sender.sendMessage(ChatColor.GREEN + "The plugin will require a general reload of the server");
                    sender.sendMessage(ChatColor.GREEN + "Execute the command again to shutdown");
                    return true;
                }
            }
        }
        else if(!sender.isOp())
            writeLog(logPath, ((Player) sender).getDisplayName() + " tried to use " + command);

        sender.sendMessage(ChatColor.RED + "Command Execution Unsuccessfull");
        return false;
    }

    //Starting methods section
    public void readYML(){
        AtomicInteger posItem = new AtomicInteger();
        runes.add(new Rune());
        ArrayList<String> potionEffects = new ArrayList<String>();
        writeLog(logPath, "\n\nStarting to read the YML file...");

        configFile.get().getConfigurationSection(configFile.get().getCurrentPath()).getKeys(true).forEach(itemName -> {
            String[] path = itemName.split("\\.");

            if(path.length == 2){
                if(path[1].equals("deleteLogsEvery(KB)")) {
                    //Dichiaro, controllo il parse, controllo la correttezza
                    int value;
                    try{
                        value = Integer.parseInt(configFile.get().getString(itemName));
                    }
                    catch(ArithmeticException exception){
                        value = 16;
                        writeLog(logPath, itemName + " Wrong value, assigning the default one: 16Kb");
                    }
                    //16Kb minimo di log
                    if(value > 16){
                        deleteLogsEvery = value;
                        writeLog(logPath, path[0] + " Value assigned: " + deleteLogsEvery);
                    }
                    else{
                        deleteLogsEvery = 16;
                        writeLog(logPath, path[0] + " !DEFAULT Value assigned!: " + deleteLogsEvery);
                    }
                }
                else if(path[1].equals("otherLogs")){
                    otherLogs = Boolean.parseBoolean(configFile.get().getString(itemName));
                    writeLog(logPath, path[0] + " Value assigned: " + otherLogs);
                }
                else{
                    writeLog(logPath, "Global params reading finished");
                }
            }
            else if(path.length == 3){
                if(path[2].equals("consume")){
                    int value;
                    try{
                        value = Integer.parseInt(configFile.get().getString(itemName));
                    }
                    catch(ArithmeticException exception){
                        value = 0;
                        writeLog(logPath, path[2] + " Wrong value, assigning the default one: 0");
                    }
                    if(value >= 0){
                        runes.get(posItem.intValue()).setConsume(value);
                        writeLog(logPath, path[2] + " Value assigned: " + deleteLogsEvery);
                    }
                    else{
                        runes.get(posItem.intValue()).setConsume(value);
                        writeLog(logPath, path[2] + " !DEFAULT Value assigned!: " + deleteLogsEvery);
                    }
                }
                else if(path[2].equals("name")){
                    runes.get(posItem.intValue()).setName(configFile.get().getString(itemName));
                    writeLog(logPath, path[2] + " Value assigned: " + configFile.get().getString(itemName));
                }
                else if(path[2].equals("lore")){
                    runes.get(posItem.intValue()).setLore(configFile.get().getString(itemName));
                    writeLog(logPath, path[2] + " Value assigned: " + configFile.get().getString(itemName));
                }
                else if(path[2].equals("end")){
                    writeLog(logPath, "Item " + runes.get(posItem.intValue()).toString()+ " added in position " + posItem);
                    potionEffects.add("/");
                    posItem.getAndIncrement();
                    runes.add(posItem.intValue(), new Rune());
                }
            }
            else if(path.length == 5){
                if(path[4].equals("type")){
                    potionEffects.add(configFile.get().getString(itemName));
                }
                else if (path[4].equals("duration")){
                    int duration = Integer.parseInt(configFile.get().getString(itemName));
                    duration = duration * 20;
                    potionEffects.add(Integer.toString(duration));
                }
                else if(path[4].equals("amplifier")){
                    potionEffects.add(Integer.toString(Integer.parseInt(configFile.get().getString(itemName)) - 1));
                    potionEffects.add(";");
                }
            }
        });

        int itemPosition = 0;
        for (int i = 0; i < potionEffects.size(); i++){
            if(potionEffects.get(i).equals("/")) {
                itemPosition++;
            }
            if(potionEffects.get(i).equals(";")){
                try {
                    PotionEffect p = new PotionEffect(PotionEffectType.getByName(potionEffects.get(i - 3)), Integer.parseInt(potionEffects.get(i - 2)), Integer.parseInt(potionEffects.get(i - 1)));
                    ArrayList<PotionEffect> pp = runes.get(itemPosition).getEffects();
                    pp.add(p);
                    runes.get(itemPosition).setEffects(pp);
                }
                catch(NullPointerException exception){
                    System.out.println("[PowerItems] Enrypase - Error in potion configurations in the following element:");
                    System.out.println("[PowerItems] " + PotionEffectType.getByName(potionEffects.get(i - 5)) + ", " + Integer.parseInt(potionEffects.get(i - 4)) + ", " + Integer.parseInt(potionEffects.get(i - 3)) + ", " + Integer.parseInt(potionEffects.get(i - 2)) + ", " + Integer.parseInt(potionEffects.get(i - 1)));
                    writeLog(logPath, "Error in potion configurations in the following element:" + PotionEffectType.getByName(potionEffects.get(i - 5)) + ", " + Integer.parseInt(potionEffects.get(i - 4)) + ", " + Integer.parseInt(potionEffects.get(i - 3)) + ", " + Integer.parseInt(potionEffects.get(i - 2)) + ", " + Integer.parseInt(potionEffects.get(i - 1)));
                }
            }
        }

        String potionArray = "";
        for (int i = 0; i < potionEffects.size(); i++){
            if(i != 0)
                potionArray += potionEffects.get(i) + "|";
            else
                potionArray +=  "|" + potionEffects.get(i) +  "|";
        }

        writeLog(logPath, "6---------------------------------------------------------------------9");

        writeLog(logPath, "POTION EFFECTS: ");
        writeLog(logPath, potionArray);

        writeLog(logPath, "6---------------------------------------------------------------------9");

        runes.remove(runes.size() - 1);
        writeLog(logPath, "ITEMS: ");
        for (int i = 0; i < runes.size(); i++) {
            writeLog(logPath, runes.get(i).toString());
        }
        writeLog(logPath, "6---------------------------------------------------------------------9");

        writeLog(logPath, "");
        writeLog(logPath, "");
        writeLog(logPath, "!Player items usage logs setted on: " + otherLogs + " !");
        writeLog(logPath, "");
        writeLog(logPath, "If you find some issues report them on: https://github.com/Enrypase/Minecraft");
        writeLog(logPath, "");
        writeLog(logPath, "Check runes commands. May be useful!");
        writeLog(logPath, "");
        writeLog(logPath, "Enjoy! - By Enrypase");
        writeLog(logPath, "");
        writeLog(logPath, "");
    }

    public void writeLog(String path, String logMessage){
        File log = new File(path + "/Runes_log.txt");
        File tempLog = new File(path + "/Runes_tempLog.txt");

        try {
            if(!log.exists()) {
                log.createNewFile();
            }
            else{
                if(log.length() > (deleteLogsEvery * 1024)) {
                    log.delete();
                }
            }
            if(!tempLog.exists()) {
                tempLog.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempLog));
            BufferedReader br = new BufferedReader(new FileReader(log));
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            String line;
            while((line = br.readLine()) != null){
                bw.write(line + "\n");
            }
            bw.write(formatter.format(date) + "\t" + logMessage + "\n");
            br.close();
            bw.close();

            log.delete();
            tempLog.renameTo(log.getAbsoluteFile());
        }
        catch (IOException ioException){
            System.out.println(ioException);
            System.out.println("[Runes] Enrypase - Error: Couldn't write the log file");
            System.out.println("[Runes] Enrypase - If the error persists check the PowerItems directory in /plugins/PowerItems");
            System.out.println("[Runes] Enrypase - 1) If the directory does not exist create it");
            System.out.println("[Runes] Enrypase - 2) If the directory exists but the error persists try to delete the log file");
        }
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent e){
        checkInventory((Player) e.getPlayer());
    }

    @EventHandler
    public void playerLogin(PlayerLoginEvent e){
        checkInventory(e.getPlayer());
    }

    @EventHandler
    public void playerDrop(PlayerDropItemEvent e){
        checkInventory(e.getPlayer());
    }

    //Note: In 1.8 version is the only way to reach the purpose!
    @EventHandler
    public void playerPickup(PlayerPickupItemEvent e){
        checkInventory(e.getPlayer());
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e){
        checkInventory(e.getEntity());
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e){
        for (int i = 0; i < playerRunes.size(); i++) {
            Player player = playerRunes.get(i).getPlayer();
            for(int j = 0; j < playerRunes.get(i).getRune().getEffects().size(); j++){
                addEffect(playerRunes.get(i).getRune(), player);
            }
            if(playerRunes.get(i).getRune().getConsume() > 0){
                if(System.currentTimeMillis() / 1000 > playerRunes.get(i).getTime() + playerRunes.get(i).getRune().getConsume()){
                    ItemStack[] inventory = playerRunes.get(i).getPlayer().getInventory().getContents();

                    for (int j = 0; j < inventory.length; j++) {
                        if(getRunePos(inventory[j]) == i){
                            inventory[j] = null;
                            player.updateInventory();
                            playerRunes.remove(i);
                        }
                    }
                }
                else if(System.currentTimeMillis() / 1000 > playerRunes.get(i).getTime() + playerRunes.get(i).getRune().getConsume() - 60 && !playerRunes.get(i).getWarning()){
                    playerRunes.get(i).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "Rune will last for &5 60 seconds"));
                    playerRunes.get(i).setWarning(true);
                    //Global variable?
                }
            }
        }
    }

    //Chechs the inventory to insert/remove a player rune
    public void checkInventory(Player player){
        ItemStack[] inventory = player.getInventory().getContents();
        ArrayList<Rune> actualRunes = new ArrayList<Rune>();

        for (int i = 0; i < inventory.length; i++) {
            if(getRunePos(inventory[i]) != -1){
                actualRunes.add(runes.get(getRunePos(inventory[i])));
            }
        }
        for (int i = 0; i < playerRunes.size(); i++) {
            if(playerRunes.get(i).getPlayer().equals(player)){
                boolean confirmed = false;
                for (int j = 0; j < actualRunes.size(); j++) {
                    if(playerRunes.get(i).getRune().equals(actualRunes.get(j))){
                        confirmed = true;
                    }
                }

                if(!confirmed){
                    playerRunes.remove(i);
                }
            }
        }
        for (int i = 0; i < actualRunes.size(); i++) {
            boolean confirmed = false;
            for (int j = 0; j < playerRunes.size(); j++) {
                if(actualRunes.get(i).equals(playerRunes.get(j).getRune())){
                    confirmed = true;
                }
            }

            if(!confirmed){
                playerRunes.add(new PlayerRune(player, actualRunes.get(i), System.currentTimeMillis()/1000, false));
                addEffect(actualRunes.get(i), player);
            }
        }

    }

    public int getRunePos(ItemStack item){
        try{
            for(int i = 0; i < runes.size(); i++){
                if(item.getItemMeta().getDisplayName().contains(runes.get(i).getName()) && item.getItemMeta().getLore().get(0).contains((runes.get(i).getLore()))){
                    return i;
                }
            }
        }
        catch(NullPointerException exception){
            return -1;
        }
        return -1;
    }

    public void reloadLists(){
        runes = new ArrayList<Rune>();
        playerRunes = new ArrayList<PlayerRune>();
        confirmShutdown = new ArrayList<String>();
    }

    public void addEffect(Rune r, Player player){
        for (int i = 0; i < r.getEffects().size(); i++) {
            player.addPotionEffect(playerRunes.get(i).getRune().getEffects().get(i));
        }
    }

}
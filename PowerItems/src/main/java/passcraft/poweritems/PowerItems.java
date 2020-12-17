package passcraft.poweritems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class PowerItems extends JavaPlugin implements Listener {

    //Variables section
    String logPath;
    ConfigFile configFile;

    ArrayList<Item> items;
    ArrayList<ItemIstance> itemIstances;
    int itemsCooldown, secondsCooldown, deleteLogsEvery;
    String cooldownMessage, offCooldownMessage;

    //S&S methods section
    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        configFile = new ConfigFile();
        configFile.get().options().copyDefaults(true);
        configFile.save();

        items = new ArrayList<Item>();
        itemIstances = new ArrayList<ItemIstance>();

        System.out.println("PATH: " + Bukkit.getServer().getPluginManager().getPlugin("PowerItems").getDataFolder().getPath());
        logPath = Bukkit.getServer().getPluginManager().getPlugin("PowerItems").getDataFolder().getPath();

        readYML();
        System.out.println("[PowerItems] Enrypase - YML read successfull");



        writeLog(logPath, "ITEMS: ");
        for (int i = 0; i < items.size(); i++) {
            writeLog(logPath, items.get(i).toString());
        }
        System.out.println("[PowerItems] Enrypase - Remember to check the logs!");

    }

    @Override
    public void onDisable() {
        System.out.println("[PowerItems] Enrypase: Shutting down...");
        onEnable();
    }

    //Commands section
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(command.getName().equals("reload") && sender.isOp()){
            if(sender instanceof Player && sender.isOp()){
                System.out.println("Enrypase 01 - Reloading plugin");
                Player player = (Player) sender;
                onEnable();
                player.sendMessage(ChatColor.GREEN + "Plugin Resetted Succesfully");
                return true;
            }
        }
        return false;
    }

    //Starting methods section
    public void readYML(){
        Item item = new Item();
        ArrayList<String> potionEffects = new ArrayList<String>();
        writeLog(logPath, "\n\nStarting to read the YML file...");
        configFile.get().getConfigurationSection("powerItems").getKeys(true).forEach(itemName -> {
            String[] path = itemName.split("\\.");
            if(path.length == 1){
                if(itemName.equals("deleteLogsEvery(KB)")) {
                    deleteLogsEvery = Integer.parseInt(configFile.get().getString("powerItems." + itemName));
                    potionEffects.add("/");
                    writeLog(logPath, path[0] + " Value assigned: " + deleteLogsEvery);
                }
                else if(itemName.equals("itemsBeforeCooldown")) {
                    itemsCooldown = Integer.parseInt(configFile.get().getString("powerItems." + itemName));
                    writeLog(logPath, path[0] + " Value assigned: " + itemsCooldown);
                }
                else if(itemName.equals("cooldown(s)")){
                    secondsCooldown = Integer.parseInt(configFile.get().getString("powerItems." + itemName));
                    writeLog(logPath, path[0] + " Value assigned: " + secondsCooldown);
                }
                else if(itemName.equals("cooldownMessage")){
                    cooldownMessage = configFile.get().getString("powerItems." + itemName);
                    writeLog(logPath, path[0] + " Value assigned: " + cooldownMessage);
                }
                else if(itemName.equals("offCooldownMessage")){
                    offCooldownMessage = configFile.get().getString("powerItems." + itemName);
                    writeLog(logPath, path[0] + " Value assigned: " + offCooldownMessage);
                }
                else{
                    writeLog(logPath, "Global params reading finished");
                }
            }
            //CONTROLLARE I BOOLEANI
            else if(path.length == 2){
                if(path[1].equals("leftClick")){
                    if(Integer.parseInt(configFile.get().getString("powerItems." + itemName)) == 1){
                        item.setLeftClick(true);
                    }
                    else{
                        item.setLeftClick(false);
                    }
                    writeLog(logPath, path[1] + " Value assigned: " + configFile.get().getString("powerItems." + itemName));
                }
                else if(path[1].equals("consume")){
                    if(Integer.parseInt(configFile.get().getString("powerItems." + itemName)) == 1){
                        item.setLeftClick(true);
                    }
                    else{
                        item.setLeftClick(false);
                    }
                    writeLog(logPath, path[1] + " Value assigned: " + configFile.get().getString("powerItems." + itemName));
                }
                else if(path[1].equals("name")){
                    item.setName(configFile.get().getString("powerItems." + itemName));
                    writeLog(logPath, path[1] + " Value assigned: " + configFile.get().getString("powerItems." + itemName));
                }
                else if(path[1].equals("lore")){
                    item.setLore(configFile.get().getString("powerItems." + itemName));
                    writeLog(logPath, path[1] + " Value assigned: " + configFile.get().getString("powerItems." + itemName));
                }
                else if(path[1].equals("message")){
                    item.setMessage(configFile.get().getString("powerItems." + itemName));
                    writeLog(logPath, path[1] + " Value assigned: " + configFile.get().getString("powerItems." + itemName));
                    items.add(item);
                }
            }
            else if(path.length == 4){
                if(path[3].equals("type")){
                    potionEffects.add(configFile.get().getString("powerItems." + itemName));
                }
                else if (path[3].equals("duration")){
                    potionEffects.add(configFile.get().getString("powerItems." + itemName));
                }
                else if(path[3].equals("amplifier")){
                    potionEffects.add(configFile.get().getString("powerItems." + itemName));
                    potionEffects.add(";");
                }
            }
        });

        int itemPosition = -1;
        for (int i = 0; i < potionEffects.size(); i++){
            if(potionEffects.get(i).equals("/")) {
                itemPosition++;
            }
            if(potionEffects.get(i).equals(";")){
                PotionEffect p = new PotionEffect(PotionEffectType.getByName(potionEffects.get(i-3)), Integer.parseInt(potionEffects.get(i-2)), Integer.parseInt(potionEffects.get(i-1)));
                ArrayList<PotionEffect> pp = items.get(itemPosition).getEffects();
                pp.add(p);
                items.get(itemPosition).setEffects(pp);
            }
        }

        String potionArray = "";
        for (int i = 0; i < potionEffects.size(); i++){
            if(i != 0)
                potionArray += potionEffects.get(i) + "|";
            else
                potionArray +=  "|" + potionEffects.get(i) +  "|";
        }
        writeLog(logPath, potionArray);
    }

    public void writeLog(String path, String logMessage){
        BufferedWriter bw = null;
        BufferedReader br = null;
        File log = new File(path + "/Enrypase 01 - log.txt");
        File tempLog = new File(path + "/Enrypase 01 - tempLog.txt");

        try {
            if(!log.exists()) {
                log.createNewFile();
            }
            //if(log.length() > (deleteLogsEvery * 1024))
                //log.delete();
            if(!tempLog.exists()) {
                tempLog.createNewFile();
            }

            bw = new BufferedWriter(new FileWriter(tempLog));
            br = new BufferedReader(new FileReader(log));

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
            tempLog.renameTo(log);
        }
        catch (IOException ioException){
            System.out.println("[PowerItems] Enrypase - Error: Couldn't write log file");
            System.out.println(ioException.getStackTrace());
            System.out.println("----------");
            System.out.println(ioException);
        }
    }

    //Action listeners section
    @EventHandler
    public void rightClick(PlayerInteractEvent e){
        System.out.println("ITEM NAME: " + e.getItem().getType().name());
        if(!e.getAction().name().contains("LEFT_CLICK_AIR")  && !e.getAction().name().contains("LEFT_CLICK_BLOCK")){
            if(!e.getItem().getType().name().equals("AIR")){
                if(e.getItem().getItemMeta().hasLore() && specifiedItem(e.getItem()) && !items.get(getItemPosition(e.getItem())).getLeftClick()){
                    //If the istance exists
                    if(findIstance(e.getPlayer())){
                        int istancePosition = getIstancePosition(e.getPlayer());
                        //If you can use more items...
                        if(itemIstances.get(istancePosition).getIstanceNumber() < itemsCooldown){
                            int position = getItemPosition(e.getItem());
                            if(position != -1){
                                ItemIstance itemIstance = new ItemIstance(items.get(position), e.getPlayer(), System.currentTimeMillis() / 1000, 1);
                                itemIstances.add(itemIstance);

                                diminuisci(e);

                                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(e.getItem())).getMessage()));

                                setEffects(items.get(getItemPosition(e.getItem())).getEffects(), e);
                                writeLog(logPath, e.getPlayer().getPlayerListName() + " used the following item: " + items.get(getItemPosition(e.getItem())) + " (THERE CAN BE MORE ISTANCES)");
                            }
                        }
                        //Else check the time...
                        else if(System.currentTimeMillis() > itemIstances.get(istancePosition).getTime() + secondsCooldown){
                            ItemIstance itemIstance = itemIstances.get(istancePosition);
                            itemIstance.setIstanceNumber(1);
                            itemIstance.setTime(System.currentTimeMillis() / 1000);
                            itemIstances.remove(istancePosition);
                            itemIstances.add(istancePosition, itemIstance);

                            diminuisci(e);

                            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(e.getItem())).getMessage()));

                            setEffects(items.get(getItemPosition(e.getItem())).getEffects(), e);
                            writeLog(logPath, e.getPlayer().getPlayerListName() + " used the following item: " + items.get(getItemPosition(e.getItem())) + " (NO MORE IN COOLDOWN)");
                        }
                        //If the cooldown still in progress...
                        else if(System.currentTimeMillis() < itemIstances.get(istancePosition).getTime() + secondsCooldown){
                            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', cooldownMessage));
                            writeLog(logPath, e.getPlayer().getPlayerListName() + " tried to use an item but it still beeing in cooldown");
                        }
                    }
                    //ELse create it
                    else{
                        int position = getItemPosition(e.getItem());
                        if(position != -1){
                            ItemIstance itemIstance = new ItemIstance(items.get(position), e.getPlayer(), System.currentTimeMillis() / 1000, 1);
                            itemIstances.add(itemIstance);

                            diminuisci(e);

                            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(e.getItem())).getMessage()));

                            setEffects(items.get(getItemPosition(e.getItem())).getEffects(), e);
                            writeLog(logPath, e.getPlayer().getPlayerListName() + " used the following item: " + items.get(getItemPosition(e.getItem())) + " (ISTANCE NOT EXISTS)");
                        }
                    }
                }
            }
        }
    }

    /*
    @EventHandler
    public void leftClick(EntityDamageByEntityEvent e){
        if(e.getDamager().getType().name().equals("PLAYER") && e.getEntity().getType().name().equals("PLAYER")){
            System.out.println("ITEM NAME: " + e.getItem().getType().name());
            if (!e.getItem().getType().name().equals("AIR")) {
                if (e.getItem().getItemMeta().hasLore() && specifiedItem(e.getItem()) && items.get(getItemPosition(e.getItem())).getLeftClick()) {

                }
            }
        }
    }*/

    //Useful methods
    public boolean specifiedItem(ItemStack item){
        for (int i = 0; i < items.size(); i++) {
            if(item.getItemMeta().getLore().equals(items.get(i).getLore()) && item.getItemMeta().getDisplayName().equals(items.get(i).getName())){
                return true;
            }
        }
        return false;
    }

    public boolean findIstance(Player player){
        for (int i = 0; i < itemIstances.size(); i++) {
            if(itemIstances.get(i).getPlayer().equals(player)){
                return true;
            }
        }
        return false;
    }

    public int getItemPosition(ItemStack item){
        for (int i = 0; i < items.size(); i++) {
            if(item.getItemMeta().getLore().equals(items.get(i).getLore()) && item.getItemMeta().getDisplayName().equals(items.get(i).getName())){
                return i;
            }
        }
        return -1;
    }

    public void diminuisci(PlayerInteractEvent e){
        ItemStack item = e.getItem();
        ItemStack[] playerInventory = e.getPlayer().getInventory().getContents();
        int itemPosition = e.getPlayer().getInventory().getHeldItemSlot();

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount()  - 1);
        }
        else if (item.getAmount() == 1) {
            playerInventory[itemPosition] = null;
            e.getPlayer().getInventory().setItem(itemPosition, playerInventory[itemPosition]);
        }
    }

    public int getIstancePosition(Player player){
        for (int i = 0; i < itemIstances.size(); i++) {
            if(itemIstances.get(i).getPlayer().equals(player)){
                return i;
            }
        }
        return -1;
    }

    public void setEffects(ArrayList<PotionEffect> potionEffects, PlayerInteractEvent e){
        potionEffects =  items.get(getItemPosition(e.getItem())).getEffects();
        for (int i = 0; i < potionEffects.size(); i++) {
            e.getPlayer().addPotionEffect(potionEffects.get(i));
        }
    }

}
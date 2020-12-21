package passcraft.poweritems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public final class PowerItems extends JavaPlugin implements Listener {

    //Variables section
    String logPath;
    ConfigFile configFile;

    ArrayList<Item> items;
    ArrayList<ItemIstance> itemIstances;
    ArrayList<PlayerEffect> queueEffects;
    int itemsCooldown, secondsCooldown, deleteLogsEvery;
    String cooldownMessage, offCooldownMessage;

    //S&S methods section
    @Override
    public void onEnable() {
        //OCCHIO ALLA PRIMA RIGA :O
        //getServer().getPluginManager().enablePlugin(this);
        getServer().getPluginManager().registerEvents(this,this);

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        configFile = new ConfigFile();
        configFile.get().options().copyDefaults(true);
        configFile.save();

        items = new ArrayList<Item>();
        itemIstances = new ArrayList<ItemIstance>();
        queueEffects = new ArrayList<PlayerEffect>();

        System.out.println("PATH: " + Bukkit.getServer().getPluginManager().getPlugin("PowerItems").getDataFolder().getPath());
        logPath = Bukkit.getServer().getPluginManager().getPlugin("PowerItems").getDataFolder().getPath();

        readYML();
        items.remove(items.size() - 1);
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
        //Bug nel riavvio, conta come doppie le azioni
        onEnable();
    }

    //Commands section
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(command.getName().equals("reload") && sender.isOp()){
            if(sender instanceof Player && sender.isOp()){
                System.out.println("[PowerItems] Enrypase - Reloading plugin");
                Player player = (Player) sender;
                onEnable();
                player.sendMessage(ChatColor.GREEN + "Plugin Resetted Succesfully");
                return true;
            }
        }
        else if(command.getName().equals("getItems") && sender.isOp()){
            Player player = (Player) sender;
            for (int i = 0; i < items.size(); i++) {
                player.sendMessage(ChatColor.GREEN + items.get(i).toString());
            }
        }
        else if(command.getName().equals("reset") && sender.isOp()){
            Player player = (Player) sender;
            for (int i = 0; i < itemIstances.size(); i++) {
                if(itemIstances.get(i).getPlayer().getPlayerListName().equals(((Player) sender).getPlayerListName())){
                    itemIstances.remove(i);
                    player.sendMessage(ChatColor.GREEN + "Command Execution Successfully");
                    return true;
                }
            }
        }
        else if(command.getName().equals("shutdown") && sender.isOp()){
            getServer().getPluginManager().disablePlugin(this);
            sender.sendMessage(ChatColor.GREEN + "Command Execution Successfully");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Command Execution Unsuccessfull");
        return false;
    }

    //Starting methods section
    public void readYML(){
        AtomicInteger posItem = new AtomicInteger();
        items.add(new Item());
        ArrayList<String> potionEffects = new ArrayList<String>();
        writeLog(logPath, "\n\nStarting to read the YML file...");
        configFile.get().getConfigurationSection("powerItems").getKeys(true).forEach(itemName -> {
            String[] path = itemName.split("\\.");
            if(path.length == 1){
                if(itemName.equals("deleteLogsEvery(KB)")) {
                    deleteLogsEvery = Integer.parseInt(configFile.get().getString("powerItems." + itemName));
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
                    items.get(posItem.intValue()).setLeftClick(Boolean.parseBoolean(configFile.get().getString("powerItems." + itemName)));
                    writeLog(logPath, path[1] + " Value assigned: " + configFile.get().getString("powerItems." + itemName));
                }
                else if(path[1].equals("consume")){
                    items.get(posItem.intValue()).setConsume(Boolean.parseBoolean(configFile.get().getString("powerItems." + itemName)));
                    writeLog(logPath, path[1] + " Value assigned: " + configFile.get().getString("powerItems." + itemName));
                }
                else if(path[1].equals("name")){
                    items.get(posItem.intValue()).setName(configFile.get().getString("powerItems." + itemName));
                    writeLog(logPath, path[1] + " Value assigned: " + configFile.get().getString("powerItems." + itemName));
                }
                else if(path[1].equals("lore")){
                    items.get(posItem.intValue()).setLore(configFile.get().getString("powerItems." + itemName));
                    writeLog(logPath, path[1] + " Value assigned: " + configFile.get().getString("powerItems." + itemName));
                }
                else if(path[1].equals("message")){
                    items.get(posItem.intValue()).setMessage(configFile.get().getString("powerItems." + itemName));
                    writeLog(logPath, path[1] + " Value assigned: " + configFile.get().getString("powerItems." + itemName));
                }
                else if(path[1].equals("end")){
                    writeLog(logPath, "Item " + items.get(posItem.intValue()).toString()+ " added in position " + posItem);
                    potionEffects.add("/");
                    posItem.getAndIncrement();
                    items.add(posItem.intValue(), new Item());
                }
            }
            else if(path.length == 4){
                if(path[3].equals("type")){
                    potionEffects.add(configFile.get().getString("powerItems." + itemName));
                }
                else if (path[3].equals("duration")){
                    int duration = Integer.parseInt(configFile.get().getString("powerItems." + itemName));
                    duration = duration * 20;
                    potionEffects.add(Integer.toString(duration));
                }
                else if(path[3].equals("amplifier")){
                    potionEffects.add(configFile.get().getString("powerItems." + itemName));
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
        try {
            if (!e.getAction().name().contains("LEFT_CLICK_AIR") && !e.getAction().name().contains("LEFT_CLICK_BLOCK")) {
                ItemStack[] playerInventory = e.getPlayer().getInventory().getContents();
                int itemPosition = e.getPlayer().getInventory().getHeldItemSlot();
                ItemStack item = playerInventory[itemPosition];

                if(!item.getType().name().equals("AIR")){
                    int itemPos = getItemPosition(e.getItem());
                    if ( e.getItem().getItemMeta().hasLore() && specifiedItem(e.getItem()) && itemPos != -1 && !items.get(itemPos).getLeftClick()) {
                        //If the istance exists
                        if (findIstance(e.getPlayer())) {
                            int istancePosition = getIstancePosition(e.getPlayer());
                            //If you can use more items...
                            int position = getItemPosition(e.getItem());
                            if (itemIstances.get(istancePosition).getIstanceNumber() < itemsCooldown) {

                                if (position != -1) {
                                    ItemIstance itemIstance = new ItemIstance(items.get(position), e.getPlayer(), System.currentTimeMillis() / 1000, (itemIstances.get(istancePosition).getIstanceNumber() + 1));
                                    itemIstances.remove(istancePosition);
                                    itemIstances.add(itemIstance);

                                    diminuisci(e.getPlayer());

                                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(e.getItem())).getMessage()));

                                    saveEffects(e.getPlayer(), items.get(position));

                                    setEffects(e.getItem(), e.getPlayer());
                                    writeLog(logPath, e.getPlayer().getPlayerListName() + " used the following item: " + items.get(getItemPosition(e.getItem())) + " (THERE CAN BE MORE ISTANCES)");
                                }
                            }
                            //Else check the time...
                            else if (System.currentTimeMillis() / 1000 > itemIstances.get(istancePosition).getTime() + secondsCooldown) {
                                ItemIstance itemIstance = itemIstances.get(istancePosition);
                                itemIstance.setIstanceNumber(1);
                                itemIstance.setTime(System.currentTimeMillis() / 1000);
                                itemIstances.remove(istancePosition);
                                itemIstances.add(istancePosition, itemIstance);

                                diminuisci(e.getPlayer());

                                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(e.getItem())).getMessage()));

                                saveEffects(e.getPlayer(), items.get(position));

                                setEffects(e.getItem(), e.getPlayer());
                                writeLog(logPath, e.getPlayer().getPlayerListName() + " used the following item: " + items.get(getItemPosition(e.getItem())) + " (NO MORE IN COOLDOWN)");
                            }
                            //If the cooldown still in progress...
                            else if (System.currentTimeMillis() / 1000 < itemIstances.get(istancePosition).getTime() + secondsCooldown) {
                                String message = cooldownMessage.replace("{cooldown}", Long.toString(itemIstances.get(istancePosition).getTime() + secondsCooldown - System.currentTimeMillis() / 1000));
                                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                writeLog(logPath, e.getPlayer().getPlayerListName() + " tried to use an item but it still beeing on cooldown");
                            }
                        }
                        //ELse create it
                        else {
                            int position = getItemPosition(e.getItem());
                            if (position != -1) {
                                ItemIstance itemIstance = new ItemIstance(items.get(position), e.getPlayer(), System.currentTimeMillis() / 1000, 1);
                                itemIstances.add(itemIstance);

                                diminuisci(e.getPlayer());

                                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(e.getItem())).getMessage()));

                                saveEffects(e.getPlayer(), items.get(position));

                                setEffects(e.getItem(), e.getPlayer());
                                writeLog(logPath, e.getPlayer().getPlayerListName() + " used the following item: " + items.get(getItemPosition(e.getItem())) + " (ISTANCE NOT EXISTS)");
                            }
                        }
                    }
                }
            }
        }
        catch(Exception exception){
            //Cerca di limitare il catch
            System.out.println(exception);
            System.out.println(exception.getStackTrace());
        }
    }

    //RENDERE GENERALI I METODI SIA PER destro CHE PER sinistro
    //Fatto questo manca solo da mettere in una coda gli effetti... Poi basta :D
    @EventHandler
    public void leftClick(EntityDamageByEntityEvent e){
        if(e.getDamager().getType().name().equals("PLAYER") && e.getEntity().getType().name().equals("PLAYER")){
            try {
                Player attack = (Player) e.getDamager();
                Player defense = (Player) e.getEntity();
                //Evito l'uso di ItemInHand siccome è deprecato, tanto vale usare questo...
                ItemStack[] playerInventory = attack.getInventory().getContents();
                int itemPosition = attack.getInventory().getHeldItemSlot();
                ItemStack item = playerInventory[itemPosition];

                if (!item.getType().name().equals("AIR") && item.getItemMeta().hasLore() && specifiedItem(item) && items.get(getItemPosition(item)).getLeftClick()) {
                    if (findIstance(attack)) {
                        int istancePosition = getIstancePosition(attack);
                        //If you can use more items...
                        if (itemIstances.get(istancePosition).getIstanceNumber() < itemsCooldown) {
                            int position = getItemPosition(item);
                            if (position != -1) {
                                ItemIstance itemIstance = new ItemIstance(items.get(position), attack, System.currentTimeMillis() / 1000, (itemIstances.get(istancePosition).getIstanceNumber() + 1));
                                itemIstances.remove(istancePosition);
                                itemIstances.add(itemIstance);

                                diminuisci(attack);

                                attack.sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(item)).getMessage()));

                                setEffects(item, defense);
                                writeLog(logPath, attack.getPlayerListName() + " used the following item: " + items.get(getItemPosition(item)) + " on " + defense.getPlayerListName());
                            }
                        }
                        //Else check the time...
                        else if (System.currentTimeMillis() / 1000 > itemIstances.get(istancePosition).getTime() + secondsCooldown) {
                            ItemIstance itemIstance = itemIstances.get(istancePosition);
                            itemIstance.setIstanceNumber(1);
                            itemIstance.setTime(System.currentTimeMillis() / 1000);
                            itemIstances.remove(istancePosition);
                            itemIstances.add(istancePosition, itemIstance);

                            diminuisci(attack);

                            attack.sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(item)).getMessage()));

                            setEffects(item, defense);
                            writeLog(logPath, attack.getPlayerListName() + " used the following item: " + items.get(getItemPosition(item)) + " on " + defense.getPlayerListName());
                        }
                        //If the cooldown still in progress...
                        else if (System.currentTimeMillis() / 1000 < itemIstances.get(istancePosition).getTime() + secondsCooldown) {
                            String message = cooldownMessage.replace("{cooldown}", Long.toString(itemIstances.get(istancePosition).getTime() + secondsCooldown - System.currentTimeMillis() / 1000));
                            attack.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            writeLog(logPath, attack.getPlayerListName() + " tried to use an attack item but it still beeing on cooldown");
                        }
                    }
                    //ELse create it
                    else {
                        int position = getItemPosition(item);
                        if (position != -1) {
                            ItemIstance itemIstance = new ItemIstance(items.get(position), attack, System.currentTimeMillis() / 1000, 1);
                            itemIstances.add(itemIstance);

                            diminuisci(attack);

                            attack.sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(item)).getMessage()));

                            setEffects(item, defense);
                            writeLog(logPath, attack.getPlayerListName() + " used the following item: " + items.get(getItemPosition(item)) + " on " + defense.getPlayerListName());
                        }
                    }
                }
            }
            catch (Exception exception){
                //Cerca di limitare il catch
            }
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e){
        checkQueue();
        checkCooldown();
    }

    //Useful methods
    public boolean specifiedItem(ItemStack item){
        for (int i = 0; i < items.size(); i++) {
            if(item.getItemMeta().getLore().get(0).contains(items.get(i).getLore()) && item.getItemMeta().getDisplayName().contains(items.get(i).getName())){
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
            if(item.getItemMeta().getLore().get(0).contains(items.get(i).getLore()) && item.getItemMeta().getDisplayName().contains(items.get(i).getName())){
                return i;
            }
        }
        return -1;
    }

    public void diminuisci(Player player){
        ItemStack[] playerInventory = player.getInventory().getContents();
        int itemPosition = player.getInventory().getHeldItemSlot();
        ItemStack item = playerInventory[itemPosition];

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount()  - 1);
        }
        else if (item.getAmount() == 1) {
            playerInventory[itemPosition] = null;
            player.getInventory().setItem(itemPosition, playerInventory[itemPosition]);
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

    //Sistemare le cose siccome non aggiunge l'effetto ma salva quello già presente
    public void setEffects(ItemStack item, Player playerEffects){
        ArrayList<PotionEffect> potionEffects =  items.get(getItemPosition(item)).getEffects();

        for (int i = 0; i < potionEffects.size(); i++) {
            playerEffects.addPotionEffect(potionEffects.get(i));
        }
    }

    public void saveEffects(Player player, Item item){
        if(player.getActivePotionEffects().size() > 0) {
            PotionEffect[] potionEffects = new PotionEffect[1];
            potionEffects = player.getActivePotionEffects().toArray(potionEffects);
            for (int i = 0; i < potionEffects.length; i++) {
                for (int j = 0; j < item.getEffects().size(); j++) {
                    if (potionEffects[i].getType().getName().equals(item.getEffects().get(j).getType().getName())) {
                        queueEffects.add(new PlayerEffect(player, System.currentTimeMillis() / 1000, potionEffects[i]));
                    }
                }
            }
        }
    }

    public void checkCooldown(){
        for(int i = 0; i < itemIstances.size(); i++){
            if(System.currentTimeMillis() / 1000 > itemIstances.get(i).getTime() + secondsCooldown){
                if(itemIstances.get(i).getIstanceNumber() > 1){
                    itemIstances.get(i).setIstanceNumber(itemIstances.get(i).getIstanceNumber() - 1);
                }
                else if(itemIstances.get(i).getIstanceNumber() == 1){
                    itemIstances.remove(i);
                }
            }
        }
    }

    public void checkQueue(){
        for (int i = 0; i < queueEffects.size(); i++) {
            if(System.currentTimeMillis() / 1000 > queueEffects.get(i).getTime() + queueEffects.get(i).getEffect().getDuration() / 20){
                queueEffects.get(i).getPlayer().addPotionEffect(queueEffects.get(i).getEffect());
                queueEffects.remove(i);
            }
        }
    }

}
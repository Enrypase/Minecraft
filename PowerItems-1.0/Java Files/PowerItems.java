package passcraft.poweritems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

    //---->> CATCH E OTTIMIZZAZIONI
    //Variables section
    String logPath;
    boolean otherLogs;
    ConfigFile configFile;

    ArrayList<Item> items;
    ArrayList<ItemIstance> itemIstances;
    ArrayList<PlayerEffect> queueEffects;

    ArrayList<PlayerCounter> time;
    ArrayList<PotionDelay> potionDelays;

    int itemsCooldown, secondsCooldown, deleteLogsEvery = 1024;
    String cooldownMessage, offCooldownMessage;

    ArrayList<String> confirmShutdown;

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

        items = new ArrayList<Item>();
        itemIstances = new ArrayList<ItemIstance>();
        queueEffects = new ArrayList<PlayerEffect>();

        time = new ArrayList<PlayerCounter>();
        potionDelays = new ArrayList<PotionDelay>();

        System.out.println("[PowerItems] Plugin's Path: " + Bukkit.getServer().getPluginManager().getPlugin("PowerItems").getDataFolder().getPath());
        logPath = Bukkit.getServer().getPluginManager().getPlugin("PowerItems").getDataFolder().getPath();

        readYML();
        System.out.println("[PowerItems] Enrypase - YML read successfull");
        System.out.println("[PowerItems] Enrypase - Remember to check the logs!");
    }

    @Override
    public void onDisable() {
        System.out.println("[PowerItems] Enrypase: Shutting down...");
        reloadLists();
    }

    //Commands section
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player && sender.isOp()){
            writeLog(logPath, ((Player) sender).getDisplayName() + " used " + command);

            if(command.getName().equals("reload")){
                System.out.println("[PowerItems] Enrypase - Reloading plugin");
                configFile.reload();
                reloadLists();
                readYML();
                sender.sendMessage(ChatColor.GREEN + "Plugin Resetted Succesfully");

                return true;
            }
            else if(command.getName().equals("getItems")){
                for (int i = 0; i < items.size(); i++) {
                    sender.sendMessage(Integer.toString(i) + ") "  + items.get(i).toString() + "\n");
                }
                sender.sendMessage(ChatColor.GREEN + "Command Execution Successfully");
                return true;
            }
            else if(command.getName().equals("getItem")){
                if(args.length == 1){
                    try{
                        int position = Integer.parseInt(args[0]);
                        if(position > 0 && position < items.size()){
                            sender.sendMessage(ChatColor.BOLD + Integer.toString(position) + ChatColor.RESET +  ") "  + items.get(position).toString() + "\n");
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
            else if(command.getName().equals("reset")){
                confirmShutdown = new ArrayList<String>();
                for (int i = 0; i < itemIstances.size(); i++) {
                    if(itemIstances.get(i).getPlayer().getPlayerListName().equals(((Player) sender).getPlayerListName())){
                        itemIstances.remove(i);
                        sender.sendMessage(ChatColor.GREEN + "Command Execution Successfully");
                        return true;
                    }
                }
            }
            else if(command.getName().equals("shutdown")){
                if(confirmShutdown.contains(((Player) sender).getPlayerListName())){
                    confirmShutdown = new ArrayList<String>();
                    try {
                        getServer().getPluginManager().disablePlugin(this);
                    }
                    catch (Exception exception){
                        System.out.println("[PowerItems] Enrypase - Plugin stopped");
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
    //Attenzione c'è da testare il controllo sull'input
    public void readYML(){
        AtomicInteger posItem = new AtomicInteger();
        items.add(new Item());
        ArrayList<String> potionEffects = new ArrayList<String>();
        writeLog(logPath, "\n\nStarting to read the YML file...");
        configFile.get().getConfigurationSection("powerItems").getKeys(true).forEach(itemName -> {
            String[] path = itemName.split("\\.");
            if(path.length == 1){
                if(itemName.equals("deleteLogsEvery(KB)")) {
                    //Dichiaro, controllo il parse, controllo la correttezza
                    int value;
                    try{
                        value = Integer.parseInt(configFile.get().getString("powerItems." + itemName));
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
                //Rinominare il playerLogs
                //Input va bene siccome o è false o true...
                else if(itemName.equals("otherLogs")){
                    otherLogs = Boolean.parseBoolean(configFile.get().getString("powerItems." + itemName));
                    writeLog(logPath, path[0] + " Value assigned: " + otherLogs);
                }
                else if(itemName.equals("itemsBeforeCooldown")) {
                    int value;
                    try{
                        value = Integer.parseInt(configFile.get().getString("powerItems." + itemName));
                    }
                    catch (ArithmeticException exception){
                        value = 0;
                        writeLog(logPath, itemName + " Wrong value, assigning the default one: 0");
                    }
                    //Può essere anche 0
                    if(value >= 0){
                        itemsCooldown = value;
                        writeLog(logPath, path[0] + " Value assigned: " + itemsCooldown);
                    }
                    else{
                        itemsCooldown = 0;
                        writeLog(logPath, path[0] + " !DEFAULT Value assigned!: " + itemsCooldown);
                    }

                }
                else if(itemName.equals("cooldown(s)")){
                    int value;
                    try{
                        value = Integer.parseInt(configFile.get().getString("powerItems." + itemName));
                    }
                    catch (ArithmeticException exception){
                        value = 0;
                        writeLog(logPath, itemName + " Wrong value, assigning the default one: 0");
                    }
                    //Può essere anche 0
                    if(value >= 0){
                        secondsCooldown = value;
                        writeLog(logPath, path[0] + " Value assigned: " + secondsCooldown);
                    }
                    else{
                        secondsCooldown = 0;
                        writeLog(logPath, path[0] + " !DEFAULT Value assigned!: " + secondsCooldown);
                    }
                }
                else if(itemName.equals("cooldownMessage")){
                    cooldownMessage = configFile.get().getString("powerItems." + itemName);
                    writeLog(logPath, path[0] + " Value assigned: " + cooldownMessage);
                }
                //Non implementato nel YML, basta aggiungere che quando si esegue l'azione di rimozione si manda al giocatore il determinato messaggio
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
                    potionEffects.add(Integer.toString(Integer.parseInt(configFile.get().getString("powerItems." + itemName)) - 1));
                }
                else if(path[3].equals("delay")){
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
                try {
                    PotionEffect p = new PotionEffect(PotionEffectType.getByName(potionEffects.get(i - 4)), Integer.parseInt(potionEffects.get(i - 3)), Integer.parseInt(potionEffects.get(i - 2)));
                    ArrayList<PotionEffect> pp = items.get(itemPosition).getEffects();
                    pp.add(p);
                    items.get(itemPosition).setEffects(pp);

                    if(Integer.parseInt(potionEffects.get(i - 1)) > 0){
                        PotionDelay pd = new PotionDelay(items.get(itemPosition), p, System.currentTimeMillis() / 1000, Integer.parseInt(potionEffects.get(i - 1)));
                        potionDelays.add(pd);
                    }
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

        items.remove(items.size() - 1);
        writeLog(logPath, "ITEMS: ");
        for (int i = 0; i < items.size(); i++) {
            writeLog(logPath, items.get(i).toString());
        }
        writeLog(logPath, "6---------------------------------------------------------------------9");

        writeLog(logPath, "");
        writeLog(logPath, "");
        writeLog(logPath, "!Player items usage logs setted on: " + otherLogs + " !");
        writeLog(logPath, "");
        writeLog(logPath, "If you find some issues report them on: https://github.com/Enrypase/Minecraft");
        writeLog(logPath, "");
        writeLog(logPath, "Check powerItems commands. May be useful!");
        writeLog(logPath, "");
        writeLog(logPath, "Enjoy! - By Enrypase");
        writeLog(logPath, "");
        writeLog(logPath, "");
    }

    //ATtenzione, aggiunti sout per il controllo della creazione/eliminazione dei log
    public void writeLog(String path, String logMessage){
        File log = new File(path + "/PowerItems_log.txt");
        File tempLog = new File(path + "/PowerItems_tempLog.txt");

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
            System.out.println("[PowerItems] Enrypase - Error: Couldn't write the log file");
            System.out.println("[PowerItems] Enrypase - If the error persists check the PowerItems directory in /plugins/PowerItems");
            System.out.println("[PowerItems] Enrypase - 1) If the directory does not exist create it");
            System.out.println("[PowerItems] Enrypase - 2) If the directory exists but the error persists try to delete the log file");
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

                int itemPos = getItemPosition(item);
                if(!item.getType().name().equals("AIR") && itemPos != -1 && !items.get(itemPos).getLeftClick()){
                    //If the istance exists
                    Player player = e.getPlayer();
                    int istancePosition = getIstancePosition(player);
                    if (istancePosition != -1) {
                        //If you can use more items...
                        if (itemIstances.get(istancePosition).getIstanceNumber() < itemsCooldown) {

                            ItemIstance itemIstance = new ItemIstance(items.get(itemPos), e.getPlayer(), System.currentTimeMillis() / 1000, (itemIstances.get(istancePosition).getIstanceNumber() + 1));
                            itemIstances.remove(istancePosition);
                            itemIstances.add(itemIstance);

                            diminuisci(player);

                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(e.getItem())).getMessage()));

                            setEffects(e.getItem(), player);
                            if(otherLogs)
                                writeLog(logPath, player.getPlayerListName() + " used the following item: " + items.get(getItemPosition(e.getItem())) + " (THERE CAN BE MORE ISTANCES)");
                        }
                        //Else check the time...
                        else if (System.currentTimeMillis() / 1000 > itemIstances.get(istancePosition).getTime() + secondsCooldown) {
                            ItemIstance itemIstance = itemIstances.get(istancePosition);
                            itemIstance.setIstanceNumber(1);
                            itemIstance.setTime(System.currentTimeMillis() / 1000);
                            itemIstances.remove(istancePosition);
                            itemIstances.add(istancePosition, itemIstance);

                            diminuisci(e.getPlayer());

                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(e.getItem())).getMessage()));

                            setEffects(e.getItem(), player);
                            if(otherLogs)
                                writeLog(logPath, player.getPlayerListName() + " used the following item: " + items.get(getItemPosition(e.getItem())) + " (NO MORE IN COOLDOWN)");
                        }
                        //If the cooldown still in progress...
                        else if (System.currentTimeMillis() / 1000 < itemIstances.get(istancePosition).getTime() + secondsCooldown) {
                            String message = cooldownMessage.replace("{cooldown}", Long.toString(itemIstances.get(istancePosition).getTime() + secondsCooldown - System.currentTimeMillis() / 1000));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            if(otherLogs)
                                writeLog(logPath, player.getPlayerListName() + " tried to use an item but it still beeing on cooldown");
                        }
                    }
                    //ELse create it
                    else {
                        ItemIstance itemIstance = new ItemIstance(items.get(itemPos), e.getPlayer(), System.currentTimeMillis() / 1000, 1);
                        itemIstances.add(itemIstance);

                        diminuisci(player);

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(e.getItem())).getMessage()));

                        setEffects(e.getItem(), player);
                        if(otherLogs)
                            writeLog(logPath, player.getPlayerListName() + " used the following item: " + items.get(getItemPosition(e.getItem())) + " (ISTANCE NOT EXISTS)");
                    }
                }
            }
        }
        catch(Exception exception){
            //Cerca di limitare il catch
            //Poi verrà lasciato vuoto
            //System.out.println(exception);
            //System.out.println(exception.getStackTrace());
        }
    }

    @EventHandler
    public void leftClick(EntityDamageByEntityEvent e){
        try {
            if(e.getDamager().getType().name().equals("PLAYER") && e.getEntity().getType().name().equals("PLAYER")){
                Player attack = (Player) e.getDamager();
                Player defense = (Player) e.getEntity();
                ItemStack[] playerInventory = attack.getInventory().getContents();
                int itemPosition = attack.getInventory().getHeldItemSlot();
                ItemStack item = playerInventory[itemPosition];

                int itemPos = getItemPosition(item);
                if (!item.getType().name().equals("AIR") && itemPos != -1 && items.get(getItemPosition(item)).getLeftClick()) {
                    int istancePosition = getIstancePosition(attack);
                    if (istancePosition != -1) {
                        //If you can use more items...
                        if (itemIstances.get(istancePosition).getIstanceNumber() < itemsCooldown) {
                            if (itemPos != -1) {
                                ItemIstance itemIstance = new ItemIstance(items.get(itemPos), attack, System.currentTimeMillis() / 1000, (itemIstances.get(istancePosition).getIstanceNumber() + 1));
                                itemIstances.remove(istancePosition);
                                itemIstances.add(itemIstance);

                                diminuisci(attack);

                                attack.sendMessage(ChatColor.translateAlternateColorCodes('&', items.get(getItemPosition(item)).getMessage()));

                                setEffects(item, defense);
                                if(otherLogs)
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
                            if(otherLogs)
                                writeLog(logPath, attack.getPlayerListName() + " used the following item: " + items.get(getItemPosition(item)) + " on " + defense.getPlayerListName());
                        }
                        //If the cooldown still in progress...
                        else if (System.currentTimeMillis() / 1000 < itemIstances.get(istancePosition).getTime() + secondsCooldown) {
                            String message = cooldownMessage.replace("{cooldown}", Long.toString(itemIstances.get(istancePosition).getTime() + secondsCooldown - System.currentTimeMillis() / 1000));
                            attack.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            if(otherLogs)
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
                            if(otherLogs)
                                writeLog(logPath, attack.getPlayerListName() + " used the following item: " + items.get(getItemPosition(item)) + " on " + defense.getPlayerListName());
                        }
                    }
                }
            }

        }
        catch (Exception exception){
            //Cerca di limitare il catch
           // System.out.println(exception);
           // System.out.println(exception.getStackTrace());
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e){
        Player rip = e.getEntity();
        for (int i = 0; i < itemIstances.size(); i++) {
            if(itemIstances.get(i).getPlayer().getPlayerListName().equals(rip.getPlayerListName())){
                itemIstances.remove(i);
            }
        }
        for (int i = 0; i < queueEffects.size(); i++) {
            if(queueEffects.get(i).getPlayer().getPlayerListName().equals(rip.getPlayerListName())){
                queueEffects.remove(i);
            }
        }
        for (int i = 0; i < time.size(); i++) {
            if(time.get(i).getPlayer().getPlayerListName().equals(rip.getPlayerListName())){
                time.remove(i);
            }
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e){
        checkQueue();
        checkCooldown();
        checkPotionDelays();
        //System.out.println("ON GROUND: " + e.getPlayer().isOnGround());
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

        if(items.get(getItemPosition(item)).getConsume()){
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount()  - 1);
            }
            else if (item.getAmount() == 1) {
                playerInventory[itemPosition] = null;
                player.getInventory().setItem(itemPosition, playerInventory[itemPosition]);
            }
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
    //ATTENZIONE, HO INIZIATO L'ADATTAMENTO ALLA FUNZIONE DEI DELAYS
    public void setEffects(ItemStack item, Player playerEffects){
        ArrayList<PotionEffect> potionEffects =  items.get(getItemPosition(item)).getEffects();
        PotionEffect[] playerPotionEffects = new PotionEffect[1];
        playerPotionEffects = playerEffects.getActivePotionEffects().toArray(playerPotionEffects);

        for (int i = 0; i < potionEffects.size(); i++) {
            //Possibile ottimizzazione, sia il potion effect è già presente in item, basterebbe passargli un int per identificarlo
            int posPotionDelay = controlDelays(item, potionEffects.get(i));
            if(posPotionDelay == -1){
                if(playerPotionEffects.length > 0 && !isPotionNull(playerPotionEffects[0])) {
                    for (int j = 0; j < playerPotionEffects.length; j++) {
                        if(hasTheEffect(potionEffects.get(i), playerPotionEffects)){
                            if(potionEffects.get(i).getType().getName().equals(playerPotionEffects[j].getType().getName())) {
                                if (potionEffects.get(i).getAmplifier() > playerPotionEffects[j].getAmplifier()) {
                                    queueEffects.add(new PlayerEffect(playerEffects, System.currentTimeMillis() / 1000, playerPotionEffects[j], playerPotionEffects[j].getDuration() + waitingInQueue(playerEffects)));
                                    playerEffects.removePotionEffect(playerPotionEffects[j].getType());
                                    playerEffects.addPotionEffect(potionEffects.get(i));
                                }
                                else {
                                    queueEffects.add(new PlayerEffect(playerEffects, System.currentTimeMillis() / 1000, potionEffects.get(i), playerPotionEffects[j].getDuration() + waitingInQueue(playerEffects)));
                                }
                            }
                        }
                        else{
                            playerEffects.addPotionEffect(potionEffects.get(i));
                        }
                    }
                }
                else{
                    playerEffects.addPotionEffect(potionEffects.get(i));
                }
            }
            else{
                time.add(new PlayerCounter(playerEffects, potionDelays.get(posPotionDelay).getPotionEffect(), System.currentTimeMillis() / 1000, potionDelays.get(posPotionDelay).getDelay()));
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
            if(System.currentTimeMillis() / 1000 > queueEffects.get(i).getTime() + queueEffects.get(i).getDelay() / 20){
                queueEffects.get(i).getPlayer().addPotionEffect(queueEffects.get(i).getEffect());
                queueEffects.remove(i);
            }
        }
    }

    public void reloadLists(){
        items = new ArrayList<Item>();
        itemIstances = new ArrayList<ItemIstance>();
        queueEffects = new ArrayList<PlayerEffect>();
        time = new ArrayList<PlayerCounter>();
        potionDelays = new ArrayList<PotionDelay>();
        confirmShutdown = new ArrayList<String>();
    }

    public boolean isPotionNull(PotionEffect potionEffect){
        try{
            if(potionEffect.equals(null)){
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception exception){
            return true;
        }
    }

    public boolean hasTheEffect(PotionEffect potionEffect, PotionEffect[] playerPotionEffects){
        for (int i = 0; i < playerPotionEffects.length; i++) {
            if(playerPotionEffects[i].getType().getName().equals(potionEffect.getType().getName())){
                return true;
            }
        }
        return false;
    }

    //Attenzione, nuovo metodo da testare
    public int waitingInQueue(Player player){
        int waitingTime = 0;
        for(int i = 0; i < queueEffects.size(); i++){
            if(queueEffects.get(i).getPlayer().getPlayerListName().equals(player.getPlayerListName())){
                waitingTime += queueEffects.get(i).getDelay();
            }
        }
        return waitingTime;
    }

    public int controlDelays(ItemStack item, PotionEffect effect){
            for (int i = 0; i < potionDelays.size(); i++) {
                if (item.getItemMeta().getDisplayName().contains(potionDelays.get(i).getItem().getName()) && item.getItemMeta().getLore().get(0).contains(potionDelays.get(i).getItem().getLore()) && potionDelays.get(i).getPotionEffect().getType().getName().equals(effect.getType().getName())) {
                    return i;
                }
            }
            return -1;
    }

    public void checkPotionDelays(){
        for (int i = 0; i < time.size(); i++) {
            if(System.currentTimeMillis() / 1000 > time.get(i).getTime() + time.get(i).getTick()){
                time.get(i).getPlayer().addPotionEffect(time.get(i).getEffect());
                time.remove(i);
            }
        }
    }
}
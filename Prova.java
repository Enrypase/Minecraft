package prova.prova;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.io.*;


public final class Prova extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        System.out.println("Enrypase - Plugin Prova startato");
        getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        System.out.println("Enrypase: Uscendo");
    }

    @EventHandler
    public void rightClick(PlayerInteractEvent e) throws InterruptedException {
        //Eliminiamo il left click per eseguire l'azione
        if(!e.getAction().name().contains("LEFT_CLICK_AIR")  && !e.getAction().name().contains("LEFT_CLICK_BLOCK")){

            //Solamente se è un oggetto speciale (con lore)
            Player giocatore = e.getPlayer();
            ItemStack[] oggettiInventario = giocatore.getInventory().getContents();
            ItemStack oggetto = e.getItem();
            int posInventario = ricerca(oggettiInventario, e);
            if (posInventario > -1 && oggetto.getItemMeta().hasLore()) {

                ItemMeta metadataOggetto = oggetto.getItemMeta();
                String[] info = new String[2];
                //Occhio che potrebbe essere null, split su "=" che così ti da n di robe
                info[0] = metadataOggetto.getDisplayName();         //NOME
                info[1] = metadataOggetto.getLore().toString();     //LORE

                if (info[0].contains("Lion's Strength") && info[1].contains("Click for 7 seconds of strength III.")) {
                    diminuisci("Lion's Strength", giocatore, e, oggettiInventario, oggetto, posInventario);
                }
                else if(info[0].contains("Leopard's Speed") && info[1].contains("Click for 20 seconds of speed III.")){
                    diminuisci("Leopard's Speed", giocatore, e, oggettiInventario, oggetto, posInventario);
                }
                else if(info[0].contains("Rhino's Armor") && info[1].contains("Click for 10 seconds of tanking.")){
                    diminuisci("Rhino's Armor", giocatore, e, oggettiInventario, oggetto, posInventario);
                }
                else if(info[0].contains("Rabbit's Jump") && info[1].contains("Click for jumping into space.")){
                    diminuisci("Rabbit's Jump", giocatore, e, oggettiInventario, oggetto, posInventario);
                }
            }

        }

    }

    @EventHandler
    public void eliminaFile(PlayerQuitEvent e){
        //ELIMINA FILE DEL PLAYER
    }

    @EventHandler
    public void danno(EntityDamageByEntityEvent e){
        if(e.getDamager().getType().name().equals("PLAYER") && e.getEntity().getType().name().equals("PLAYER")){
            Player attaccante = (Player) e.getDamager();
            Player difensore = (Player) e.getEntity();

            //UNICA SOLUZIONE PER TROVARE QUALE ITEM HA USATO, SICCOME IL RESTO SI PERDE DURANTE IL CAST DA ENTITY A PLAYER
            ItemStack[] inventario = attaccante.getInventory().getContents();
            int posizioneAttuale = attaccante.getInventory().getHeldItemSlot();
            try{
                if (inventario[posizioneAttuale].getItemMeta().hasLore()) {
                    if (inventario[posizioneAttuale].getItemMeta().getDisplayName().contains("Zombie's Flesh") || inventario[posizioneAttuale].getItemMeta().getLore().contains("Hit your opponent to set his hunger to 0.")) {
                        diminuisci("Zombie's Flesh", attaccante, null, inventario, inventario[posizioneAttuale], posizioneAttuale);
                    }
                }
            }
            catch (Exception e1){

            }
        }
    }

    public int ricerca(ItemStack[] is, PlayerInteractEvent e){
        for(int i = 0; i < is.length; i++){
            if (is[i] != null && is[i].isSimilar(e.getItem())) {
                return i;
            }
        }
        return -1;
    }

    public boolean controllaFiles(int t, Player p){

        File path = new File("./plugins/Prova");
        String contents[] = path.list();
        for(int i=0; i < contents.length; i++) {
            if(contents[i].contains(p.getPlayerListName())){
                //PATTERN FILE: nomePlayer_tempoVecchio_delay
                String[] informazioni = contents[i].split("_");
                if(informazioni.length == 3){
                    long vecchioTempo = 0;
                    int delay = 0;
                    try {
                        vecchioTempo = Long.parseLong(informazioni[1]);
                        vecchioTempo = vecchioTempo/1000;
                        delay = Integer.parseInt(informazioni[2]);
                    }
                    catch(Exception e){

                    }

                    if(System.currentTimeMillis()/1000 > vecchioTempo + delay) {
                        String percorsoVecchio = "./plugins/Prova/" + informazioni[0] + "_" + informazioni[1] + "_" + informazioni[2];
                        File f = new File(percorsoVecchio);
                        f.delete();
                        String percorsoNuovo = "./plugins/Prova/" + p.getPlayerListName() + "_" + System.currentTimeMillis() + "_" + t;
                        f = new File(percorsoNuovo);
                        return true;
                    }
                    else {
                        p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "Power Items" + ChatColor.YELLOW + "]" + ChatColor.GRAY + " Are on cooldown for: " + ChatColor.DARK_RED + ((vecchioTempo + delay) - System.currentTimeMillis()/1000) + ChatColor.GRAY + "s");
                        return false;
                    }
                }
            }
        }
        File f = new File("./plugins/Prova/" + p.getPlayerListName() + "_" + System.currentTimeMillis() + "_" + t);
        try {
            String percorso = "./plugins/Prova/" + p.getPlayerListName() + "_" + System.currentTimeMillis() + "_" + t;
            BufferedWriter bw = new BufferedWriter(new FileWriter(percorso));
            bw.close();
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public void diminuisci(String nomeOggetto, Player giocatore, PlayerInteractEvent e, ItemStack[] oggettiInventario, ItemStack oggetto, int posInventario){
        //IL PARAMETRO DI CONTROLLAFILES INDICA IL COOLDOWN
        if(controllaFiles(45, giocatore)) {
            if (oggetto.getAmount() > 1) {
                oggetto.setAmount(oggetto.getAmount() - 1);
            } else if (oggetto.getAmount() == 1) {
                try {
                    oggettiInventario[posInventario] = null;
                    giocatore.getInventory().setItem(posInventario, oggettiInventario[posInventario]);
                } catch (IndexOutOfBoundsException error) {
                    System.out.println("Errore - IOoBE - Prova dell'Enrypase");
                }
            }
            if(nomeOggetto.contains("Lion's Strength")){
                PotionEffect forza = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 7*20, 2);
                giocatore.addPotionEffect(forza);
            }
            else if(nomeOggetto.contains("Leopard's Speed")){
                PotionEffect velocità = new PotionEffect(PotionEffectType.SPEED, 20*20, 2);
                giocatore.addPotionEffect(velocità);
            }
            else if(nomeOggetto.contains("Rhino's Armor")){
                PotionEffect assorbimento = new PotionEffect(PotionEffectType.ABSORPTION, 10*20, 2);
                PotionEffect rigenerazione = new PotionEffect(PotionEffectType.REGENERATION, 10*20, 4);
                PotionEffect resistenza = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10*20, 2);
                giocatore.addPotionEffect(assorbimento);
                giocatore.addPotionEffect(rigenerazione);
                giocatore.addPotionEffect(resistenza);
            }
            else if(nomeOggetto.contains("Rabbit's Jump")){
                PotionEffect salto = new PotionEffect(PotionEffectType.JUMP, 2*20, 40);
                PotionEffect resistenza = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 8*20, 9);
                giocatore.addPotionEffect(salto);
                giocatore.addPotionEffect(resistenza);
            }
            else if(nomeOggetto.contains("Zombie's Flesh")){
                giocatore.setFoodLevel(0);
            }
        }

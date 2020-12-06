package passcript.enhancedblocks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.io.*;


public final class EnhancedBlocks extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        //Messaggio di inizio del programma
        System.out.println("Enrypase - Plugin Prova startato");
        getServer().getPluginManager().registerEvents(this,this);
        //Messaggio antecedente all'eliminazione dei vari file di log
        System.out.println("Enrypase - Procedo a eliminare i files...");
        File f = new File("./plugins/Prova/");
        String[] filePresenti = filePresenti(f);
        for(int i = 0; i < filePresenti.length; i++){
            if(!filePresenti[i].equals("DO NOT DELETE")){
                f = new File("./plugins/Prova/" + filePresenti[i]);
                f.delete();
                System.out.println("Enrypase - File " + filePresenti[i] + " eliminato");
            }
        }
    }

    @Override
    public void onDisable() {
        //All'uscita non fa altro che scrivere che sta uscendo
        System.out.println("Enrypase - Uscendo");
    }

    //ELENCO OGGETTI CHE DANNO BENEFICIO AL GIOCATORE
    @EventHandler
    public void rightClick(PlayerInteractEvent e) throws InterruptedException {
        //Eliminiamo le azioni riguardanti click non desiderati
        if(!e.getAction().name().contains("LEFT_CLICK_AIR")  && !e.getAction().name().contains("LEFT_CLICK_BLOCK")){
            //Gli oggetti che ci interessano sono quelli con la lore, di conseguenza se un oggetto non è dotato di questa non lo prendiamo in considerazione
            //Procedo, inoltre, a salvare quelle che sono le prime variabili
            Player giocatore = e.getPlayer();
            ItemStack[] oggettiInventario = giocatore.getInventory().getContents();
            ItemStack oggetto = e.getItem();
            int posInventario = ricerca(oggettiInventario, e);
            if (posInventario > -1 && oggetto.getItemMeta().hasLore()) {
                //Salvataggio di ulteriori informazioni
                ItemMeta metadataOggetto = oggetto.getItemMeta();
                String[] info = new String[2];
                info[0] = metadataOggetto.getDisplayName();         //NOME
                info[1] = metadataOggetto.getLore().toString();     //LORE
                //Divisione dei vari oggetti ricevuti con il richiamo al metodo per diminuire e gestire i file
                if (info[0].contains("Lion's Strength") && info[1].contains("Click for 7 seconds of strength III.")) {
                    diminuisci("Lion's Strength", giocatore, giocatore, oggettiInventario, oggetto, posInventario);
                }
                else if(info[0].contains("Leopard's Speed") && info[1].contains("Click for 20 seconds of speed III.")){
                    diminuisci("Leopard's Speed", giocatore, giocatore,  oggettiInventario, oggetto, posInventario);
                }
                else if(info[0].contains("Rhino's Armor") && info[1].contains("Click for 10 seconds of tanking.")){
                    diminuisci("Rhino's Armor", giocatore, giocatore, oggettiInventario, oggetto, posInventario);
                }
                else if(info[0].contains("Rabbit's Jump") && info[1].contains("Click for jumping into space.")){
                    diminuisci("Rabbit's Jump", giocatore, giocatore,  oggettiInventario, oggetto, posInventario);
                }
            }
        }
    }

    //ELENCO OGGETTI CHE FANNO DANNO AD ALTRI GIOCATORI
    @EventHandler
    public void danno(EntityDamageByEntityEvent e){
        //Se entrambe le entità coinvolte sono giocatori (non applicabile quindi ai mob)
        if(e.getDamager().getType().name().equals("PLAYER") && e.getEntity().getType().name().equals("PLAYER")){
            //L'evento selezionato ci permette di suddividere i due player in chi fa il danno e chi lo riceve
            Player attaccante = (Player) e.getDamager();
            Player difensore = (Player) e.getEntity();
            //La soluzione individuata da me per trovare l'arma del giocatore è salvare tutto l'inventario di esso e la posizione dell'oggetto selezionato
            ItemStack[] inventario = attaccante.getInventory().getContents();
            int posizioneAttuale = attaccante.getInventory().getHeldItemSlot();
            //Uso il try-catch perchè non mi veniva in mentre altro metodo per eliminare l'aria
            try{
                //Se l'oggetto selezionato ha una lore; perchè quelli senza non ci interessano
                if (inventario[posizioneAttuale].getItemMeta().hasLore()) {
                    //Suddivisione dei vari oggetti
                    if (inventario[posizioneAttuale].getItemMeta().getDisplayName().contains("Zombie's Flesh") || inventario[posizioneAttuale].getItemMeta().getLore().contains("Hit your opponent to set his hunger to 0.")) {
                        diminuisci("Zombie's Flesh", difensore, attaccante, inventario, inventario[posizioneAttuale], posizioneAttuale);
                    }
                    else if(inventario[posizioneAttuale].getItemMeta().getDisplayName().contains("Squid's Ink") || inventario[posizioneAttuale].getItemMeta().getLore().contains("Blind your enemies in one hit!")){
                        diminuisci("Squid's Ink", difensore, attaccante, inventario, inventario[posizioneAttuale], posizioneAttuale);
                    }
                    else if(inventario[posizioneAttuale].getItemMeta().getDisplayName().contains("Snake's Venom") || inventario[posizioneAttuale].getItemMeta().getLore().contains("Hit your enemies to weaken them.")){
                        diminuisci("Snake's Venom", difensore, attaccante, inventario, inventario[posizioneAttuale], posizioneAttuale);
                    }
                }
            }
            catch (Exception e1){
                //Niente
            }
        }
    }

    //Metodo per cercare la posizione di un oggetto nell'inventario
    //-- Utilizzato solamente dal metodo rightClick(...)
    public int ricerca(ItemStack[] is, PlayerInteractEvent e){
        for(int i = 0; i < is.length; i++){
            if (is[i] != null && is[i].isSimilar(e.getItem())) {
                return i;
            }
        }
        return -1;
    }

    //Metodo per diminuire gli oggetti cliccati e per richiamare il metodo di gestione dei files
    //Sono presenti due giocatori siccome, come nel caso della seconda procedura, è possibile che le instanze Player() coinvolte siano due
    public void diminuisci(String nomeOggetto, Player giocatoreEffetto, Player giocatoreMessaggio, ItemStack[] oggettiInventario, ItemStack oggetto, int posInventario){
        //controllaFiles(...) è la funzione fondamentale per la gestione dei files contenenti le informazioni dei giocatori con i relativi tempi
        //Parametro "t" indica il tempo di Cooldown - Attualmente 45 secondi
        if(controllaFiles(45, giocatoreMessaggio)) {
            //Se gli oggetti sono più di uno diminuisci
            if (oggetto.getAmount() > 1) {
                oggetto.setAmount(oggetto.getAmount() - 1);
            }
            //Se, invece, l'oggetto è uno eliminalo, siccome non è possibile diminuirlo
            else if (oggetto.getAmount() == 1) {
                oggettiInventario[posInventario] = null;
                giocatoreEffetto.getInventory().setItem(posInventario, oggettiInventario[posInventario]);
            }
            //Fine della parte riguardante la diminuzione del numero di oggetti
            //Inizio della parte di suddivisione degli oggetti con relativi effetti
            if(nomeOggetto.contains("Lion's Strength")){
                PotionEffect forza = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 7*20, 2);
                giocatoreEffetto.addPotionEffect(forza);
            }
            else if(nomeOggetto.contains("Leopard's Speed")){
                PotionEffect velocità = new PotionEffect(PotionEffectType.SPEED, 20*20, 2);
                giocatoreEffetto.addPotionEffect(velocità);
            }
            else if(nomeOggetto.contains("Rhino's Armor")){
                PotionEffect assorbimento = new PotionEffect(PotionEffectType.ABSORPTION, 10*20, 2);
                PotionEffect rigenerazione = new PotionEffect(PotionEffectType.REGENERATION, 10*20, 4);
                PotionEffect resistenza = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10*20, 2);
                giocatoreEffetto.addPotionEffect(assorbimento);
                giocatoreEffetto.addPotionEffect(rigenerazione);
                giocatoreEffetto.addPotionEffect(resistenza);
            }
            else if(nomeOggetto.contains("Rabbit's Jump")){
                PotionEffect salto = new PotionEffect(PotionEffectType.JUMP, 2*20, 40);
                PotionEffect resistenza = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 8*20, 9);
                giocatoreEffetto.addPotionEffect(salto);
                giocatoreEffetto.addPotionEffect(resistenza);
            }
            else if(nomeOggetto.contains("Zombie's Flesh")){
                giocatoreEffetto.setFoodLevel(0);
            }
            else if(nomeOggetto.contains("Squid's Ink")){
                PotionEffect cecità = new PotionEffect(PotionEffectType.BLINDNESS, 7*20, 2);
                PotionEffect lentezza = new PotionEffect(PotionEffectType.BLINDNESS, 7*20, 1);
                giocatoreEffetto.addPotionEffect(cecità);
                giocatoreEffetto.addPotionEffect(lentezza);
            }
            else if(nomeOggetto.contains("Snake's Venom")){
                PotionEffect veleno = new PotionEffect(PotionEffectType.POISON, 15*20, 0);
                PotionEffect lentezza = new PotionEffect(PotionEffectType.SLOW, 15*20, 0);
                PotionEffect nausea = new PotionEffect(PotionEffectType.CONFUSION, 8*20, 1);
                giocatoreEffetto.addPotionEffect(veleno);
                giocatoreEffetto.addPotionEffect(lentezza);
                giocatoreEffetto.addPotionEffect(nausea);
            }
        }
    }

    //Metodo fondamentale per la gestione dei files
    //C'è un solo giocatore, siccome quello che riceverà il messaggio sarà lo stesso che ha usato l'oggetto
    public boolean controllaFiles(int t, Player pMex){
        //Controllo dei files presenti nella directory interessata
        File path = new File("./plugins/Prova");
        String[] contents = filePresenti(path);
        //Per tutti i files presenti
        for(int i = 0; i < contents.length; i++) {
            //Se contengono il nome del giocatore, ovvero ci interessano
            //Il pattern dei file è nomeGiocatore_tempoEffettuatoComando_delay
            if(contents[i].contains(pMex.getPlayerListName())){
                //Suddividi le informazioni
                String[] informazioni = contents[i].split("_");
                //Se il file segue il pattern soprastante e, di conseguenza, contiene due "_"
                if(informazioni.length == 3){
                    long vecchioTempo = 0;
                    int delay = 0;
                    try {
                        //Se è possibile convertire i valori
                        vecchioTempo = Long.parseLong(informazioni[1]);
                        vecchioTempo = vecchioTempo/1000;
                        delay = Integer.parseInt(informazioni[2]);
                    }
                    catch(Exception e){
                        //Altrimenti utilizza quelli di default (0)
                    }
                    //Se il tempo è scaduto
                    if(System.currentTimeMillis()/1000 > vecchioTempo + delay) {
                        //Si prevede all'eliminazione del file, la creazione di uno nuovo e si da l'OK per l'utilizzo dell'oggetto
                        eliminaFile(informazioni);
                        String percorsoNuovo = "./plugins/Prova/" + pMex.getPlayerListName() + "_" + System.currentTimeMillis() + "_" + t;
                        File f = new File(percorsoNuovo);
                        return true;
                    }
                    else {
                        //Altrimenti il cooldown non è ancora scaduto e, di conseguenza, si restituisce un messaggio al giocatore interessato
                        pMex.sendMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "Power Items" + ChatColor.YELLOW + "]" + ChatColor.GRAY + " Are on cooldown for: " + ChatColor.DARK_RED + ((vecchioTempo + delay) - System.currentTimeMillis()/1000) + ChatColor.GRAY + "s");
                        return false;
                    }
                }
            }
        }
        //Se il tile non viene trovato significa che non esiste e, allora, se ne crea uno nuovo e si da l'OK al giocatore
        File f = new File("./plugins/Prova/" + pMex.getPlayerListName() + "_" + System.currentTimeMillis() + "_" + t);
        try {
            String percorso = "./plugins/Prova/" + pMex.getPlayerListName() + "_" + System.currentTimeMillis() + "_" + t;
            BufferedWriter bw = new BufferedWriter(new FileWriter(percorso));
            bw.close();
        }
        catch(Exception e){
            //In caso di qualsiasi errore non permettere all'utente l'utilizzo dell'oggetto
            return false;
        }
        return true;
    }

    //Metodo comodo che ci permette di eliminare un file date delle informazioni
    public void eliminaFile(String informazioni[]){
        String percorsoVecchio = "./plugins/Prova/" + informazioni[0] + "_" + informazioni[1] + "_" + informazioni[2];
        File f = new File(percorsoVecchio);
        f.delete();
    }

    //Metodo comodo che restituisce i file presenti in una directory da noi data
    public String[] filePresenti(File path){
        String contents[] = path.list();
        return contents;
    }
}

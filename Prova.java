package prova.prova;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
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

                if (info[0].contains("Lion's Strength") && info[1].contains("Click for 7 seconds of strenght III.")) {
                    diminuisci(giocatore, e, oggettiInventario, oggetto, posInventario);
                }
                else if(info[0].contains("Leopard's Speed") && info[1].contains("Click for 20 seconds of speed III.")){
                    diminuisci(giocatore, e, oggettiInventario, oggetto, posInventario);
                }
                else if(info[0].contains("Rhino's Armor") && info[1].contains("Click for 10 seconds of tanking.")){
                    diminuisci(giocatore, e, oggettiInventario, oggetto, posInventario);
                }
                else if(info[0].contains("Rabbit's Jump") && info[1].contains("Click jumping into space.")){
                    diminuisci(giocatore, e, oggettiInventario, oggetto, posInventario);
                }
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
        System.out.println("LUNGHEZZA " + contents.length);
        for(int i=0; i < contents.length; i++) {
            System.out.println(i + "  NOME  " + contents[i]);
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
                        System.out.println("Percorso vecchio trovato: " + percorsoVecchio);
                        File f = new File(percorsoVecchio);
                        f.delete();
                        System.out.println("Percorso vecchio eliminato con successo");
                        String percorsoNuovo = "./plugins/Prova/" + p.getPlayerListName() + "_" + System.currentTimeMillis() + "_" + t;
                        f = new File(percorsoNuovo);
                        System.out.println("Percorso nuovo trovato " + percorsoNuovo);
                        return true;
                    }
                    else {
                        System.out.println("Tempo rimanente " +  ((vecchioTempo + delay) - System.currentTimeMillis()/1000));
                        p.sendMessage("Power items are still in cooldown for: "  +  ((vecchioTempo + delay) - System.currentTimeMillis()/1000) + " seconds");
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
            System.out.println("Scrivo file " + percorso);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public void diminuisci(Player giocatore, PlayerInteractEvent e, ItemStack[] oggettiInventario, ItemStack oggetto, int posInventario){
        if(controllaFiles(30, giocatore)) {
            if (oggetto.getAmount() > 1) {
                oggetto.setAmount(oggetto.getAmount() - 1);
            } else if (oggetto.getAmount() == 1) {
                try {
                    oggettiInventario[posInventario] = null;
                    e.getPlayer().getInventory().setItem(posInventario, oggettiInventario[posInventario]);
                } catch (IndexOutOfBoundsException error) {
                    System.out.println("Errore - IOoBE - Prova dell'Enrypase");
                }
            }
        }
    }
}

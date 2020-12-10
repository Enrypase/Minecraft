package passcript.rune;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.io.File;

public class Rune extends JavaPlugin implements Listener {

    private String directoryPlugin = "./plugins/Rune/";

    @Override
    public void onEnable() {
        //Messaggio di inizio del programma
        System.out.println("Enrypase - Plugin Rune inizializzato");
        getServer().getPluginManager().registerEvents(this,this);
        File f = new File(directoryPlugin);
        String[] filePresenti = filePresenti(f);
        for(int i = 0; i < filePresenti.length; i++){
            if(!filePresenti[i].equals("DO NOT DELETE")){
                f = new File(directoryPlugin + filePresenti[i]);
                f.delete();
                System.out.println("Enrypase - File " + filePresenti[i] + " eliminato");
            }
        }
    }

    @Override
    public void onDisable() {
        //All'uscita non fa altro che scrivere che sta uscendo
        System.out.println("Enrypase - Uscendo da Rune");
    }

    @EventHandler
    public void verificaPresenza(InventoryCloseEvent e){
        System.out.println("E0");
        Player giocatore = (Player) e.getPlayer();
        controllaInventario(giocatore);
    }
    @EventHandler
    public void verificaPresenza(InventoryOpenEvent e){
        System.out.println("E1");
        Player giocatore = (Player) e.getPlayer();
        controllaInventario(giocatore);
    }
    @EventHandler
    public void verificaPMorte(PlayerDeathEvent e){
        System.out.println("E2");
        Player giocatore = e.getEntity();
        controllaInventario(giocatore);
    }
    @EventHandler
    public void verificaDrop(PlayerDropItemEvent e){
        System.out.println("E3");
        Player giocatore = e.getPlayer();
        controllaInventario(giocatore);
    }

    public void controllaInventario(Player giocatore){
        ItemStack[] inventario = giocatore.getInventory().getContents();
        //CONTROLLA LE RUNE DI UN PLAYER
        File f = new File(directoryPlugin);
        String[] filePresenti = filePresenti(f);
        boolean[] confermati = new boolean[filePresenti.length];
        //MAN MANO CHE TROVA UNA RUNA LO METTE TRUE
        for(int i = 0; i < inventario.length; i++){
            try {
                System.out.println(inventario[i].getItemMeta().getDisplayName() + "    " + inventario[i].getItemMeta().getLore() + "    " + inventario[i].getType());
                if(inventario[i].getItemMeta().hasLore()) {
                    if(inventario[i].getItemMeta().getDisplayName().contains("ciao") && inventario[i].getItemMeta().getLore().contains("mao")){
                        System.out.println("Oggetto trovato");
                        PotionEffect nausea = new PotionEffect(PotionEffectType.CONFUSION, 8*20, 10);
                        giocatore.addPotionEffect(nausea);
                    }
                }
            }
            catch (Exception e){

            }
        }
        //ELIMINA I FILES NON CONFERMATI E TOGLI I RELATIVI EFFETTI
        for(int i = 0; i < filePresenti.length; i++){
            if(!confermati[i]){

            }
        }
        giocatore.removePotionEffect(null);
    }

    //Metodo comodo che ci permette di eliminare un file date delle informazioni
    public void eliminaFile(String informazioni[]){
        String percorsoVecchio = directoryPlugin + informazioni[0] + "_" + informazioni[1] + "_" + informazioni[2] + "_" + informazioni[3];
        File f = new File(percorsoVecchio);
        f.delete();
    }

    //Metodo comodo che restituisce i file presenti in una directory da noi data
    public String[] filePresenti(File path){
        String contents[] = path.list();
        return contents;
    }

    //EFFETTO CHE DATO UN NOME E UNA LORE RITORNA IL RELATIVO EFFETTO
    public PotionEffect effetto(String nome, String lore){
        if(nome.contains("") && lore.contains("")){
            return null;
        }
        return null;
    }

}
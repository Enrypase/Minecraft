package passcraft.poweritems;

import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class PlayerEffect {
    private Player player;
    private long time;
    private PotionEffect effect;

    public PlayerEffect(Player player, long time, PotionEffect effect){
        this.player = player;
        this.time = time;
        this.effect = effect;
    }

    public Player getPlayer(){
        return player;
    }
    public long getTime(){
        return time;
    }
    public PotionEffect getEffect(){
        return effect;
    }
    public void setPlayer(Player player){
        this.player = player;
    }
    public void setTime(long time){
        this.time = time;
    }
    public void setEffect(PotionEffect effect){
        this.effect = effect;
    }

    public String toString(){
        return "Ciao bello!";
    }
}

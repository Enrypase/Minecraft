package passcraft.poweritems;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerCounter {

    private Player player;
    private PotionEffect effect;
    private long time;
    private int tick;

    public PlayerCounter(Player player, PotionEffect effect, long time, int tick){
        this.player = player;
        this.effect = effect;
        this.time = time;
        this.tick = tick;
    }

    public void setPlayer(Player player){
        this.player = player;
    }
    public void setEffect(PotionEffect effect){
        this.effect = effect;
    }
    public void setTime(long time){
        this.time = time;
    }
    public void setTick(int tick){
        this.tick = tick;
    }

    public Player getPlayer(){
        return player;
    }
    public PotionEffect getEffect(){
        return effect;
    }
    public long getTime(){
        return  time;
    }
    public int getTick(){
        return tick;
    }


    public String toString(){
        String object = "PLAYER COUNTER - Player: " + player.getPlayerListName() + ", Effect:" + effect + ", Time: " + time + ", Tick: " + tick ;
        return object;
    }

}

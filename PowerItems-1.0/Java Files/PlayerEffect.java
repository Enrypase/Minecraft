package passcraft.poweritems;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerEffect {
    private Player player;
    private long time;
    private PotionEffect effect;
    private int delay;

    public PlayerEffect(Player player, long time, PotionEffect effect, int delay){
        this.player = player;
        this.time = time;
        this.effect = effect;
        this.delay = delay;
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
    public int getDelay(){
        return delay;
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
    public void setDelay(int delay){
        this.delay = delay;
    }

    public String toString(){
        String object = "PLAYER EFFECT - Player: " + player.getPlayerListName() + ", time: " + time + ", delay: " + delay + ", effect: " + effect.getType().getName() + " " + effect.getDuration() + " " + effect.getAmplifier();
        return  object;
    }
}

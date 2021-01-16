package passcraft.runes;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class PlayerRune {

    private Player player;
    private Rune rune;
    private long time;
    private boolean warning;

    public PlayerRune(Player player, Rune rune, long time, boolean warning){
        this.player = player;
        this.rune = rune;
        this.time = time;
        this.warning = warning;
    }

    public void setTime(long time){
        this.time = time;
    }
    public void setWarning(boolean warning) { this.warning = warning; }
    public Player getPlayer(){
        return player;
    }
    public Rune getRune(){
        return rune;
    }
    public long getTime(){
        return time;
    }
    public boolean getWarning(){ return  warning; }

    public String toString(){
        String object = "Player: " + player.getPlayerListName()+", Rune: " + rune.getName() + ", warning: " + warning;
        return object;
    }

}

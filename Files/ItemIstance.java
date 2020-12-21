package passcraft.poweritems;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class ItemIstance extends Item{

    private Item item;
    private Player player;
    private long time;
    private int istanceNumber;

    public ItemIstance(Item item, Player player, long time, int istanceNumber){
        this.item = item;
        this.player = player;
        this.time = time;
        this.istanceNumber = istanceNumber;
    }

    public void setItem(Item item){
        this.item = item;
    }
    public void setPlayer(Player player){
        this.player = player;
    }
    public void setTime(long time){
        this.time = time;
    }
    public void setIstanceNumber(int istanceNumber){
        this.istanceNumber = istanceNumber;
    }

    public Item getItem(){
        return item;
    }
    public Player getPlayer(){
        return player;
    }
    public long getTime(){
        return time;
    }
    public int getIstanceNumber(){
        return istanceNumber;
    }

}

package passcraft.poweritems;

import org.bukkit.potion.PotionEffect;

public class PotionDelay {

    private Item item;
    private PotionEffect potionEffect;
    private long time;
    private int delay;

    public PotionDelay(Item item, PotionEffect potionEffect, long time, int delay){
        this.item = item;
        this.potionEffect = potionEffect;
        this.time = time;
        this.delay = delay;
    }

    public void setItem(Item item){
        this.item = item;
    }
    public void setPotionEffect(PotionEffect potionEffect){
        this.potionEffect = potionEffect;
    }
    public void setTime(long time){
        this.time = time;
    }
    public void setDelay(int delay){
        this.delay = delay;
    }

    public Item getItem(){
        return item;
    }
    public PotionEffect getPotionEffect(){
        return potionEffect;
    }
    public long getTime(){
        return time;
    }
    public int getDelay(){
        return delay;
    }

    public String toString(){
        String object = "POTION DELAY - Item:" + item.toString() + ", Potion Effect: " + potionEffect.getType() + ", Time: " + time + ", Delay: " + delay;
        return object;
    }

}

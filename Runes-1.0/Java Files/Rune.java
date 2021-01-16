package passcraft.runes;

import org.bukkit.potion.PotionEffect;
import java.util.ArrayList;

public class Rune {

    private String name;
    private String lore;
    private int consume;
    private ArrayList<PotionEffect> effects;
    boolean activated;

    public Rune(){
        effects = new ArrayList<PotionEffect>();
        activated = false;
    }

    public Rune(String name, String lore, int consume, ArrayList<PotionEffect> effects, boolean activated){
        this.name = name;
        this.lore = lore;
        this.consume = consume;
        this.effects = effects;
        this.activated = activated;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setLore(String lore){
        this.lore = lore;
    }
    public void setConsume(int consume){
        this.consume = consume;
    }
    public void setEffects(ArrayList<PotionEffect> effects){
        this.effects = effects;
    }
    public void setActivated(boolean activated) { this.activated = activated; }
    public String getName(){
        return name;
    }
    public String getLore(){
        return lore;
    }
    public int getConsume(){
        return consume;
    }
    public ArrayList<PotionEffect> getEffects(){
        return effects;
    }
    public boolean getActivated(){ return activated; }

    public String toString(){
        String object = "RUNE - Name: " + name + ", Lore: " + lore + ", Consume: " + consume + ", Activated: " + activated + ", Effects: ";
        for(PotionEffect p : effects){
            object += "Effect: " + p.getType().getName() + "|" + p.getDuration() + "|" + p.getAmplifier();
        }
        return object;
    }

}

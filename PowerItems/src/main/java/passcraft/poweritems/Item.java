package passcraft.poweritems;

import org.bukkit.potion.PotionEffect;
import java.util.ArrayList;

public class Item {

    private String name, lore, message;
    boolean leftClick, consume;
    ArrayList<PotionEffect> effects;

    public Item(){
        /*name = "noName";
        lore = "noLore";
        message = "noMessage";
        leftClick = false;
        consume = false;*/
        effects = new ArrayList<PotionEffect>();
    }

    public Item(String name, String lore, String message, boolean leftClick, boolean consume, ArrayList<PotionEffect> effects){
        this.name = name;
        this.lore = lore;
        this.message = message;
        this.leftClick = leftClick;
        this.consume = consume;
        this.effects = effects;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setLore(String lore){
        this.lore = lore;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public void setLeftClick(boolean leftClick){
        this.leftClick = leftClick;
    }
    public void setConsume(boolean consume){
        this.consume = consume;
    }
    public void setEffects(ArrayList<PotionEffect> effects){
        this.effects = effects;
    }

    public String getName(){
        return name;
    }
    public String getLore(){
        return lore;
    }
    public String getMessage(){
        return message;
    }
    public boolean getLeftClick(){
        return leftClick;
    }
    public boolean getConsume(){
        return consume;
    }
    public ArrayList<PotionEffect> getEffects(){
        return effects;
    }

    public String toString(){
        //Sistemare
        String desc = "Left Click: " + leftClick + ", Cosume; " + consume + ", Name: " + name + ", Lore: " + lore + ", Message: " + message + ", Effects: ";
        for (int i = 0; i < effects.size(); i++) {
            desc += effects.get(i).getType().getName() + "|" + effects.get(i).getDuration() + "|" + effects.get(i).getAmplifier() + ", ";
        }
        return  desc;
    }

}

# Minecraft Plugins

## (1) PowerItems

### :clipboard: Description:
**PowerItems** is a minecraft plugin that aims to give special **effects** to a **player** that uses a particoular **item**.

### :question: How to use:
Pretty easy: configure **PowerItemsConfig.yml** in **/plugins/PowerItems/**. This file will be created after the first execution of the plugin. <br>
Check a YML configuration example in **config.yml** in the following directory: **/plugins/PowerItems/**

### :gear: Implemeted Features:
**Power Items** implements the **YML Reading** feature so you don't have to modify the code but just the file. <br>
It implements **logs** features too. The log file will be created in the following directory of the server: **/plugins/PowerItems/**. <br>
**Right Click** objects to assign the effects to the player that uses the item. <br>
**Left Click** on a player to assign the item effects to the player you hit. <br>
**Effects queue** for the right click items' effects. <br>
**Delay** of an effect, just specify it in the specific tag in the YML!. <br>

### :pencil2: Commands:
**/poweritems:reload** reloads the plugin <br>
**/poweritems:getItems** returns in the chat the configured items <br>
**/poweritems:getItem [number]** returns in the chat the configured item selected <br>
**/poweritems:reset** removes the cooldown of a player <br>
**/poweritems:shutdown** shutsdown the plugin, a reload is needed to make the plugin work again <br>

### :exclamation: Versions:
**1.0 data**: First version of the plugin.

### :+1: Still supported?
Yes - The plugin is based on **Minecraft 1.8** even if it is programmed in **Spigot 1.12**.

<!-- -------------------------------------------------------------------------------------------------------------------------------- -->

## (2) Runes

### :clipboard: Description:
**Runes** is a minecraft plugin that aims to give special **effects** to a **player** that has a particoular **item** in the **inventory**.

### :question: How to use:
Pretty easy: configure **Runes.yml** in **/plugins/Runes/**. This file will be created after the first execution of the plugin. <br>
Check a YML configuration example in **config.yml** in the following directory: **/plugins/Runes/**

### :gear: Implemeted Features:
**Runes** implements the **YML Reading** feature so you don't have to modify the code but just the file. <br>
It implements **logs** features too. The log file will be created in the following directory of the server: **/plugins/Runes/**. <br>
**Check Inventory** features are implemented too. <br>
**Consume** of a rune after the specified amount of seconds. Just specify it in the YML!. <br>
**Alert** when the rune has only 60 seconds left. <br>
**Removing rune** from the inventory after the specified amount of time. <br>

### :pencil2: Commands:
**/runes:reload** reloads the plugin <br>
**/runes:getRunes** returns in the chat the configured items <br>
**/runes:getRune [number]** returns in the chat the configured rune selected <br>
**/runes:shutdown** shutsdown the plugin, a reload is needed to make the plugin work again <br>

### :exclamation: Versions:
**1.0 data**: First version of the plugin.

### :+1: Still supported?
Yes - The plugin is based on **Minecraft 1.8** even if it is programmed in **Spigot 1.12**.

<!-- -------------------------------------------------------------------------------------------------------------------------------- -->

### Notes:
There's a file with all the names of potion effects you must use in the YMLs. <br>
The plugins won't accept the names you see in minecraft! Example "Strength III". <br>

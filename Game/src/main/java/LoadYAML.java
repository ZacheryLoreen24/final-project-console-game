import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import org.yaml.snakeyaml.Yaml;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class LoadYAML {

    String fname;
    HashMap<String, Object> data;
    HashMap<String, Room> rooms = new HashMap<>();
    HashMap<String, Item> items = new HashMap<>();

    // load room data from yaml file
    // could do this more cleverly with packaged class definitions
    // Something like this:
    //InputStream stream = new FileInputStream(fname);
    //Room room = (new Yaml(new Constructor(Room.class))).load(stream);
    public HashMap<String,Room> loadRooms() {
        data = load("rooms.yaml");
        for (String name : data.keySet()) {
            List<Item> contents = new ArrayList<>();
            Map<String, Object> inRoom = (HashMap) data.get(name);
            List<String> contemps = (ArrayList) inRoom.get("contents");
            for (String it : contemps) contents.add(items.get(it));
            Map<String, String> doors = (HashMap) inRoom.get("doors");
            rooms.put(name, new Room(name, contents, doors));
        }
        return rooms;
    }

    public HashMap<String,Item> loadItems() {
        data = load("items.yaml");
        for (String name : data.keySet()) {
            Map<String, Object> properties = (HashMap) data.get(name);
            String desc = (String) properties.get("description");
            Map<String, Object> use = (HashMap) properties.get("use");
            String usetext = (String) use.get("text");
            String useaction = (String) use.get("action");
            List<String> types = (ArrayList) properties.get("type");
            items.put(name, new Item(name, types, desc, usetext, useaction));
            Item item;

            // Check item types and instantiate accordingly
             if (types.contains("Animal")) {
                int health = (int) properties.get("health");
                int minDamage = (int) properties.get("min-damage"); 
                int maxDamage = (int) properties.get("max-damage"); 
                item = new Animal(name, types, desc, usetext, useaction, health, minDamage, maxDamage);
             } else if (types.contains("Enemy")) { 
                int health = (int) properties.get("health");
                int minDamage = (int) properties.get("min-damage");
                int maxDamage = (int) properties.get("max-damage");
                    item = new Enemy(name, types, desc, usetext, useaction, health, minDamage, maxDamage);
                
        } else if (types.contains("Weapons")) {
                int minDamage = (int) properties.get("min-damage");
                int maxDamage = (int) properties.get("max-damage");
                item = new Weapons(name, types, desc, usetext, useaction, minDamage, maxDamage);
            
            } else if (types.contains("Stationary")) {
                item = new Stationary(name, types, desc, usetext, useaction);
            } else if (types.contains("Utility")) {
                Number minDamageNum = (Number) properties.get("min-damage");
                Number maxDamageNum = (Number) properties.get("max-damage");
                int min = minDamageNum.intValue();
                int max = maxDamageNum.intValue();
                item = new Utility(name, types, desc, usetext, useaction, min, max);
            } else if (types.contains("Key")) {
                // Key is just an Item by logic, no extra properties needed
                item = new Item(name, types, desc, usetext, useaction);
            } else if (types.contains("Healing")) {
                int heal = (int) properties.get("heal");
                item = new Healing(name, types, desc, usetext, useaction, heal);
            } else if (types.contains("Plant")) {
                int health = (int) properties.get("health");
                item = new Plant(name, types, desc, usetext, useaction, health);
            } else {
                // Default Item if none of the specialized types match
                item = new Item(name, types, desc, usetext, useaction);
            }
    
            items.put(name, item);
        }
        return items;
    }

    public HashMap<String, Object> load(String fname) {
            Yaml yaml = new Yaml();
            File file = new File("./config/"+fname);
            try {
                FileInputStream inputStream = new FileInputStream(file);
                data = yaml.load(inputStream);
            } catch (FileNotFoundException e) {System.out.println("Couldn't find file");}
        return data;
    }

    public LoadYAML() {
        items = loadItems();
        rooms = loadRooms();
    }
}
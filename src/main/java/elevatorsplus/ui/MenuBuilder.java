package elevatorsplus.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import elevatorsplus.database.Elevator;
import lombok.RequiredArgsConstructor;
import ru.soknight.lib.configuration.Messages;

@RequiredArgsConstructor
public class MenuBuilder {

	private final Messages messages;
	private final MenuPattern pattern;
	
	public Inventory build(Elevator elevator) {
		String name = elevator.getName();
		int levels = elevator.getLevelsCount();
		int current = elevator.getCurrentLevel();
		
		String title = messages.format(pattern.getTitle(), "%name%", name, "%levels%", levels, "%current%", current);
		
		int size = pattern.getSize();
		if(pattern.isAutosize()) {
			size = levels / 9;
			if(levels % 9 > 0) size++;
			size *= 9;
		}
		
		Inventory inventory = Bukkit.createInventory(null, size, title);
		
		// Adding pre-current entries
		inventory = fillPreCurrent(inventory, elevator);
		
		// Adding current entry
		ItemStack curitem = pattern.getCurrent();
		curitem = formatMeta(curitem, elevator, current);
		inventory.setItem(current - 1, curitem);
		
		// Adding post-current entries
		inventory = fillPostCurrent(inventory, elevator);
		
		return inventory;
	}
	
	private Inventory fillPreCurrent(Inventory inventory, Elevator elevator) {
		int current = elevator.getCurrentLevel();
		
		if(current == 1) return inventory;
		
		ItemStack other = pattern.getOther();
		for(int i = 0; i < current; i++) {
			ItemStack temp = other.clone();
			temp = formatMeta(temp, elevator, i + 1);
			inventory.setItem(i, temp);
		}
		
		return inventory;
	}
	
	private Inventory fillPostCurrent(Inventory inventory, Elevator elevator) {
		int current = elevator.getCurrentLevel();
		int levels = elevator.getLevelsCount();
		
		if(current == levels) return inventory;
		
		ItemStack other = pattern.getOther();
		for(int i = current; i < levels; i++) {
			ItemStack temp = other.clone();
			temp = formatMeta(temp, elevator, i + 1);
			inventory.setItem(i, temp);
		}
		
		return inventory;
	}
	
	private ItemStack formatMeta(ItemStack item, Elevator elevator, int level) {
		ItemMeta meta = item.getItemMeta();
		
		String name = elevator.getName();
		int levels = elevator.getLevelsCount();
		int current = elevator.getCurrentLevel();
		
		if(meta.hasDisplayName()) {
			String dn = meta.getDisplayName();
			dn = messages.format(dn, "%name%", name, "%level%", level, "%levels%", levels, "%current%", current);
			meta.setDisplayName(dn);
		}
		
		if(meta.hasLore()) {
			List<String> raw = meta.getLore();
			List<String> lore = new ArrayList<>();
			
			raw.forEach(s -> {
				if(s.contains("%"))
					s = messages.format(s, "%name%", name, "%level%", level, "%levels%", levels, "%current%", current);
				lore.add(s);
			});
			meta.setLore(lore);
		}
		
		item.setItemMeta(meta);
		return item;
	}
	
}

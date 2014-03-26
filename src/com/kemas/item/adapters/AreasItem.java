package com.kemas.item.adapters;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public class AreasItem {
	HashMap<String, Object> Area;

	public AreasItem(Object Area) {
		HashMap<String, Object> AreaHashMap = (HashMap<String, Object>) Area;
		this.Area = AreaHashMap;
	}

	public AreasItem(HashMap<String, Object> Area) {
		this.Area = Area;
	}

	public Long getID() {
		Long ID = Long.parseLong(Area.get("id").toString());
		return ID;
	}

	public String getName() {
		String Name = Area.get("name").toString();
		return Name;
	}

}

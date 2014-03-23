package com.kemas.datasources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.hupernikao;

public class DataSourcePoint {
	private Configuration config;
	private OpenERP oerp_connection;
	private List<Long> data = null;
	private int SIZE = 0;

	public DataSourcePoint(Context CTX, String PointsType) {
		config = new Configuration(CTX);
		oerp_connection = hupernikao.BuildOpenERPConnection(config);
		List<Object> args = new ArrayList<Object>();
		args.add(new Object[] { "collaborator_id", "=", Integer.parseInt(config.getCollaboratorID()) });
		if (PointsType != "all") {
			args.add(new Object[] { "type", "=", PointsType });
		}
		Long[] Point_ids = oerp_connection.search("kemas.history.points", args);
		SIZE = Point_ids.length;
		data = new ArrayList<Long>(SIZE);
		for (Long id : Point_ids) {
			data.add(id);
		}
	}

	public int getSize() {
		return SIZE;
	}

	public List<HashMap<String, Object>> getData(int offset, int limit) {
		List<HashMap<String, Object>> result = null;
		List<Long> newList = new ArrayList<Long>(limit);

		int end = offset + limit;
		if (end > data.size()) {
			end = data.size();
		}
		newList.addAll(data.subList(offset, end));
		
		result = oerp_connection.getPoints(newList);
		for (HashMap<String, Object> Record : result) {
			HashMap<String, Object> DatePoint = hupernikao.Convert_UTCtoGMT_Str(Record.get("date").toString());
			Record.remove("date");
			Record.put("date", DatePoint.get("date"));
			Record.put("hour", DatePoint.get("hour"));
			Record.put("day", DatePoint.get("day"));
		}
		return result;
	}
}

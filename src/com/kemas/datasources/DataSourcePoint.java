package com.kemas.datasources;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.hupernikao;

public class DataSourcePoint {
	private Configuration config;
	private OpenERP oerp_connection;
	private long number_items = 100;
	private long offset = 0;
	private long limit;

	private long CollaboratorID;
	private String PointsType;

	public DataSourcePoint(Context CTX, String PointsType, long limit) {
		config = new Configuration(CTX);
		this.limit = limit;
		this.CollaboratorID = Long.parseLong(config.getCollaboratorID());
		this.PointsType = PointsType;

		oerp_connection = hupernikao.BuildOpenERPConnection(config);
		this.number_items = oerp_connection.getCountPoints(CollaboratorID, PointsType);
	}

	public long getSize() {
		return number_items;
	}

	public List<HashMap<String, Object>> getData() {
		List<HashMap<String, Object>> result;
		result = oerp_connection.getPoints(CollaboratorID, PointsType, offset, limit);

		for (HashMap<String, Object> Record : result) {
			HashMap<String, Object> DatePoints = hupernikao.Convert_UTCtoGMT_Str(Record.get("date").toString());
			Record.remove("date");
			Record.put("date", DatePoints.get("date"));
			Record.put("hour", DatePoints.get("hour"));
			Record.put("day", DatePoints.get("day_name"));
		}

		offset += limit;
		return result;
	}
}

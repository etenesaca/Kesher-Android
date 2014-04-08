package com.kemas.datasources;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.hupernikao;

public class DataSourceAttendance {
	private Configuration config;
	private OpenERP oerp_connection;
	private long number_items = 100;
	private long offset = 0;
	private long limit;

	private long CollaboratorID;
	private String AttendancesType;

	public DataSourceAttendance(Context CTX, String AttendancesType, long limit) {
		config = new Configuration(CTX);
		this.limit = limit;
		this.CollaboratorID = Long.parseLong(config.getCollaboratorID());
		this.AttendancesType = AttendancesType;

		oerp_connection = hupernikao.BuildOpenERPConnection(config);
		this.number_items = oerp_connection.getCountAttendances(CollaboratorID, AttendancesType);
	}

	public long getSize() {
		return number_items;
	}

	public List<HashMap<String, Object>> getData() {
		List<HashMap<String, Object>> result;
		result = oerp_connection.getAttendances(CollaboratorID, AttendancesType, offset, limit);

		for (HashMap<String, Object> Record : result) {
			HashMap<String, Object> DateAttendance = hupernikao.Convert_UTCtoGMT_Str(Record.get("date").toString());
			Record.remove("date");
			Record.put("date", DateAttendance.get("date"));
			Record.put("hour", DateAttendance.get("hour"));
			Record.put("day", DateAttendance.get("day_name"));
		}

		offset += limit;
		return result;
	}
}

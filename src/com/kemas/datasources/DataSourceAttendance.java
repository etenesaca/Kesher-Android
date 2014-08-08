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
			// Checkin
			Record.put("checkin", hupernikao.Convert_UTCtoGMT_Str(Record.get("checkin").toString()));
			// Checkout
			if (!Record.get("checkout").toString().equals("false")) {
				Record.put("checkout", hupernikao.Convert_UTCtoGMT_Str(Record.get("checkout").toString()));
			}
			Record.put("checkout", Record.get("checkout"));
		}

		offset += limit;
		return result;
	}
}

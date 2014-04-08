package com.kemas.datasources;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.hupernikao;

public class DataSourceEvent {
	private Configuration config;
	private OpenERP oerp_connection;
	private long number_items = 100;
	private long offset = 0;
	private long limit;

	private long CollaboratorID;
	private String EventsState;

	public DataSourceEvent(Context CTX, String EventsState, long limit) {
		config = new Configuration(CTX);
		this.limit = limit;
		this.CollaboratorID = Long.parseLong(config.getCollaboratorID());
		this.EventsState = EventsState;

		oerp_connection = hupernikao.BuildOpenERPConnection(config);
		this.number_items = oerp_connection.getCountEvents(CollaboratorID, EventsState);
	}

	public long getSize() {
		return number_items;
	}

	public List<HashMap<String, Object>> getData() {
		List<HashMap<String, Object>> result;
		result = oerp_connection.getEvents(CollaboratorID, EventsState, offset, limit);

		for (HashMap<String, Object> Record : result) {

			// Procesa horas
			HashMap<String, Object> DateStart = hupernikao.Convert_UTCtoGMT_Str(Record.get("date_start").toString(), false);
			HashMap<String, Object> DateStop = hupernikao.Convert_UTCtoGMT_Str(Record.get("date_stop").toString(), false);

			Record.remove("date_start");
			Record.remove("date_stop");

			HashMap<String, Object> Date = new HashMap<String, Object>();
			Date.put("day", DateStart.get("day"));
			Date.put("year", DateStart.get("year"));
			Date.put("month_name", DateStart.get("month_name").toString().substring(0, 3));
			Date.put("day_name", DateStart.get("day_name"));
			Record.put("date", Date);

			String Hours = DateStart.get("hour") + " - " + DateStop.get("hour");
			Record.put("hours", Hours);

			// Procesar estado
			if (Record.get("state").toString().equals("creating"))
				Record.put("state", "Creando");
			else if (Record.get("state").toString().equals("draft"))
				Record.put("state", "Borrador");
			else if (Record.get("state").toString().equals("on_going"))
				Record.put("state", "En Curso");
			else if (Record.get("state").toString().equals("closed"))
				Record.put("state", "Finalizado");
			else if (Record.get("state").toString().equals("canceled"))
				Record.put("state", "Cancelado");
		}

		offset += limit;
		return result;
	}
}

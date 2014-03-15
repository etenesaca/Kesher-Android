/*
 * Copyright (C) 2012 Daniel Medina <http://danielme.com>
 * 
 * This file is part of "Android Paginated ListView Demo".
 * 
 * "Android Paginated ListView Demo" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * "Android Paginated ListView Demo" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 3
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0.html/>
 */

package com.kemas.datasources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.kemas.Configuration;
import com.kemas.OpenERP;
import com.kemas.hupernikao;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class DataSourceAttendance {
	private Configuration config;
	private OpenERP oerp_connection;
	// Singleton pattern
	private static DataSourceAttendance datasource = null;
	private List<Long> data = null;
	private static int SIZE = 0;

	public DataSourceAttendance(Context CTX) {
		// Lineas para habilitar el acceso a la red
		// y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		config = new Configuration(CTX);
		oerp_connection = hupernikao.BuildOpenERPConnection(config);
		Long[] attendance_ids = oerp_connection.search("kemas.attendance", new Object[] {});
		SIZE = attendance_ids.length;
		data = new ArrayList<Long>(SIZE);
		for (Long id : attendance_ids) {
			data.add(id);
		}
	}

	public int getSize() {
		return SIZE;
	}

	/**
	 * Returns the elements in a <b>NEW</b> list.
	 */
	public List<HashMap<String, Object>> getData(int offset, int limit) {
		List<HashMap<String, Object>> result = null;
		String[] fields_to_read = new String[] { "code" };
		List<Long> newList = new ArrayList<Long>(limit);

		int end = offset + limit;
		if (end > data.size()) {
			end = data.size();
		}
		newList.addAll(data.subList(offset, end));
		result = oerp_connection.read("kemas.attendance", newList, fields_to_read);
		return result;
	}

}
package com.kemas;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

/**
 * Esta Clase implementa Metodos Propios para el Funcionamiento de Kemas con
 * OpenERP y esta basada en la Libreria OpenERPConnection
 * */
public class OpenERP extends OpenERPConnection {
	public final String ModuleName = "kemas";

	/** Verifica que le Modulo kemas este Instalado **/
	public boolean Module_Installed() {
		boolean result = false;
		try {
			XMLRPCClient client = new XMLRPCClient(mUrl);
			Object resp = client.call("execute", mDatabase, getUserId(), mPassword, "kemas.func", "module_installed", ModuleName);
			result = Boolean.parseBoolean(resp + "");
		} catch (XMLRPCException e) {
			e.printStackTrace();
		}
		return result;
	}

	/** Obtiene los datos de un Colaborador **/
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getCollaborator(int CollaboratorID) {
		HashMap<String, Object> result = null;
		try {
			XMLRPCClient client = new XMLRPCClient(mUrl);
			Object collaborator = (Object) client.call("execute", mDatabase, getUserId(), mPassword, "kemas.collaborator", "get_collaborator", CollaboratorID);
			try {
				result = (HashMap<String, Object>) collaborator;
			} catch (Exception e) {
			}
		} catch (XMLRPCException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Obtiene los datos que se necesitan para mostrar el Menu lateral de la
	 * aplicación
	 **/
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getNavigationmenuInfo(long CollaboratorID) {
		HashMap<String, Object> result = null;
		try {
			XMLRPCClient client = new XMLRPCClient(mUrl);
			Object NavigationmenuInfo = (Object) client.call("execute", mDatabase, getUserId(), mPassword, "kemas.collaborator", "get_info_for_navigation", CollaboratorID);
			try {
				result = (HashMap<String, Object>) NavigationmenuInfo;
			} catch (Exception e) {
			}
		} catch (XMLRPCException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Obtene el número de los cambios de puntaje
	 **/
	public long getCountPoints(long CollaboratorID, String PointsType) {
		long Count = 0;
		try {
			XMLRPCClient client = new XMLRPCClient(mUrl);

			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("collaborator_id", CollaboratorID);
			if (PointsType != "all")
				args.put("type", PointsType);
			Object CountObject = (Object) client.call("execute", mDatabase, getUserId(), mPassword, "kemas.history.points", "get_count_points_to_mobilapp", args);
			Count = Long.parseLong(CountObject.toString());
		} catch (XMLRPCException e) {
			e.printStackTrace();
		}
		return Count;
	}

	/**
	 * Obtene una lista de registros de cambio de puntaje
	 **/
	public List<HashMap<String, Object>> getPoints(long CollaboratorID, String PointsType, long offset, long limit) {
		List<HashMap<String, Object>> Records = null;
		try {
			XMLRPCClient client = new XMLRPCClient(mUrl);

			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("collaborator_id", CollaboratorID);
			if (PointsType != "all")
				args.put("type", PointsType);

			Object[] Points = (Object[]) client.call("execute", mDatabase, getUserId(), mPassword, "kemas.history.points", "get_points_to_mobilapp", args, offset, limit);
			Records = new ArrayList<HashMap<String, Object>>(Points.length);
			for (Object Record : Points) {
				Object[] PointArray = (Object[]) Record;
				HashMap<String, Object> Point = new HashMap<String, Object>();
				Point.put("id", PointArray[0]);
				Point.put("points", PointArray[1]);
				Point.put("type", PointArray[2]);
				Point.put("date", PointArray[3]);
				Records.add((HashMap<String, Object>) Point);
			}
		} catch (XMLRPCException e) {
			e.printStackTrace();
		}
		return Records;
	}

	/**
	 * Obtene el número de los registros de asistencias
	 **/
	public long getCountAttendances(long CollaboratorID, String AttendancesType) {
		long Count = 0;
		try {
			XMLRPCClient client = new XMLRPCClient(mUrl);

			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("collaborator_id", CollaboratorID);
			if (AttendancesType != "all")
				args.put("type", AttendancesType);
			Object CountObject = (Object) client.call("execute", mDatabase, getUserId(), mPassword, "kemas.attendance", "get_count_attendances_to_mobilapp", args);
			Count = Long.parseLong(CountObject.toString());
		} catch (XMLRPCException e) {
			e.printStackTrace();
		}
		return Count;
	}

	/**
	 * Obtene una lista de registros de asistencias
	 **/
	public List<HashMap<String, Object>> getAttendances(long CollaboratorID, String AttendancesType, long offset, long limit) {
		List<HashMap<String, Object>> Records = null;
		try {
			XMLRPCClient client = new XMLRPCClient(mUrl);

			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("collaborator_id", CollaboratorID);
			if (AttendancesType != "all")
				args.put("type", AttendancesType);

			Object[] Attendances = (Object[]) client.call("execute", mDatabase, getUserId(), mPassword, "kemas.attendance", "get_attendances_to_mobilapp", args, offset, limit);
			Records = new ArrayList<HashMap<String, Object>>(Attendances.length);
			for (Object Record : Attendances) {
				Object[] AttendanceArray = (Object[]) Record;
				HashMap<String, Object> Attendance = new HashMap<String, Object>();
				Attendance.put("id", AttendanceArray[0]);
				Attendance.put("service", AttendanceArray[1]);
				Attendance.put("type", AttendanceArray[2]);
				Attendance.put("date", AttendanceArray[3]);
				Records.add((HashMap<String, Object>) Attendance);
			}
		} catch (XMLRPCException e) {
			e.printStackTrace();
		}
		return Records;
	}

	/** Constructor con el uid en Integer **/
	public OpenERP(String server, Integer port, String db, String user, String pass, Integer uid) throws MalformedURLException {
		super(server, port, db, user, pass, uid);
	}

	/** Constructor con el uid en String **/
	public OpenERP(String server, String port, String db, String user, String pass, String uid) throws MalformedURLException {
		super(server, port, db, user, pass, uid);
	}

	/** Redefición del método Connect **/
	public static OpenERP connect(String server, Integer port, String db, String user, String pass) {
		return login(server, port, db, user, pass);
	}

	/** Redefinición del Metodo login **/
	protected static OpenERP login(String server, Integer port, String db, String user, String pass) {
		OpenERP result = null;
		OpenERPConnection res_login = OpenERPConnection.login(server, port, db, user, pass);
		if (res_login != null) {
			try {
				result = new OpenERP(server, port, db, user, pass, res_login.getUserId());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/*
	 * String[] fields_to_read = {}; fields_to_read = new String[] { "user_id"
	 * }; HashMap<String, Object> Collaborator =
	 * oerp_connection.read("kemas.collaborator", collaborator_id,
	 * fields_to_read);
	 * 
	 * // Leer los datos del perfil del Usuario Object[] User_tpl = (Object[])
	 * Collaborator.get("user_id"); fields_to_read = new String[] {
	 * "image_medium", "partner_id" }; HashMap<String, Object> User =
	 * oerp_connection.read("res.users", Long.parseLong(User_tpl[0] + ""),
	 * fields_to_read); User.put("name", User_tpl[1] + "");
	 * 
	 * Long config_id = oerp_connection.search("kemas.config", new Object[] {},
	 * 1)[0]; fields_to_read = new String[] { "mobile_background",
	 * "mobile_background_text_color" }; HashMap<String, Object> System_Config =
	 * oerp_connection.read("kemas.config", config_id, fields_to_read);
	 */
}
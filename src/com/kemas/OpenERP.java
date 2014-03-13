package com.kemas;

import java.net.MalformedURLException;
import java.util.HashMap;

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
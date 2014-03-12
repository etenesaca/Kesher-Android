package com.kemas;

import android.content.Context;
import android.content.SharedPreferences;

public class Configuration {
	// Definimos el nombre del archivo de configuracion
	private final String SHARED_PREFS_FILE = "AOPrefs";

	// Datos que se van a manejar
	private final String KEY_SERVER = "server";
	private final String KEY_PORT = "port";
	private final String KEY_DATABASE = "database";
	private final String KEY_LOGIN = "login";
	private final String KEY_PASSWORD = "pass";
	private final String KEY_PHOTO = "photo";
	private final String KEY_USERID = "userid";
	private final String KEY_COLLABORATORID = "collaboratorid";
	private final String KEY_CI = "ci";

	private final String KEY_TZ = "tz";
	private final String KEY_LANG = "lang";
	private final String KEY_COMPANY = "company";
	private final String KEY_EMAIL = "email";
	
	private final String KEY_BACKGROUND = "background";
	private final String KEY_TEXT_COLOR = "text_color";

	// Datos del Perfil
	private final String KEY_NAME = "name";
	private final String KEY_TEAM = "team";

	private Context mContext;

	public Configuration(Context context) {
		mContext = context;
	}

	// Obtenemos el archivo donde se guardan las preferencias para poder
	// modificarlas o leerlas
	private SharedPreferences getSettings() {
		return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}
	
	/*
	 * SERVER
	 */
	public String getServer() {
		return getSettings().getString(KEY_SERVER, null);
	}

	public void setServer(String server) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_SERVER, server);
		editor.commit();
	}

	/*
	 * PORT
	 */
	public String getPort() {
		return getSettings().getString(KEY_PORT, null);
	}

	public void setPort(String port) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PORT, port);
		editor.commit();
	}

	/*
	 * USERNAME
	 */
	public void setLogin(String login) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_LOGIN, login);
		editor.commit();
	}

	public String getLogin() {
		return getSettings().getString(KEY_LOGIN, null);
	}

	/*
	 * DATABASE
	 */
	public void setDataBase(String database) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_DATABASE, database);
		editor.commit();
	}

	public String getDataBase() {
		return getSettings().getString(KEY_DATABASE, null);
	}

	/*
	 * PASSWORD
	 */
	public String getPassword() {
		return getSettings().getString(KEY_PASSWORD, null);
	}

	public void setPassword(String pass) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PASSWORD, pass);
		editor.commit();
	}

	/*
	 * NOMBRE DEL EMPLEADO
	 */
	public String getName() {
		return getSettings().getString(KEY_NAME, null);
	}

	public void setName(String name) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_NAME, name);
		editor.commit();
	}
	
	/*
	 * NOMBRE DEL TEAM
	 */
	public String getTeam() {
		return getSettings().getString(KEY_TEAM, null);
	}

	public void setTeam(String team) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_TEAM, team);
		editor.commit();
	}

	/*
	 * PHOTO DEL EMPLEADO
	 */
	public String getPhoto() {
		return getSettings().getString(KEY_PHOTO, null);
	}

	public void setPhoto(String Photo) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PHOTO, Photo);
		editor.commit();
	}

	/*
	 * USERID
	 */
	public String getUserID() {
		return getSettings().getString(KEY_USERID, null);
	}

	public void setUserID(String UserID) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_USERID, UserID);
		editor.commit();
	}

	/*
	 * COLLABORATORID
	 */
	public String getCollaboratorID() {
		return getSettings().getString(KEY_COLLABORATORID, null);
	}

	public void setCollaboratorID(String CollaboratorID) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_COLLABORATORID, CollaboratorID);
		editor.commit();
	}

	/*
	 * CI
	 */
	public String getCI() {
		return getSettings().getString(KEY_CI, null);
	}

	public void setCI(String CI) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_CI, CI);
		editor.commit();
	}

	/*
	 * TZ
	 */
	public String getTz() {
		return getSettings().getString(KEY_TZ, null);
	}

	public void setTz(String Tz) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_TZ, Tz);
		editor.commit();
	}
	
	/*
	 * LANG
	 */
	public String getLang() {
		return getSettings().getString(KEY_LANG, null);
	}

	public void setLang(String Lang) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_LANG, Lang);
		editor.commit();
	}
	
	/*
	 * COMPANY
	 */
	public String getCompany() {
		return getSettings().getString(KEY_COMPANY, null);
	}

	public void setCompany(String Company) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_COMPANY, Company);
		editor.commit();
	}
	
	/*
	 * EMAIL
	 */
	public String getEmail() {
		return getSettings().getString(KEY_EMAIL, null);
	}

	public void setEmail(String Email) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_EMAIL, Email);
		editor.commit();
	}
	
	/*
	 * TEXT COLOR
	 */
	public String getTextColor() {
		return getSettings().getString(KEY_TEXT_COLOR, null);
	}

	public void setTextColor(String TextColor) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_TEXT_COLOR, TextColor);
		editor.commit();
	}
	
	/*
	 * BACKGROUND
	 */
	public String getBackground() {
		return getSettings().getString(KEY_BACKGROUND, null);
	}

	public void setBackground(String Background) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_BACKGROUND, Background);
		editor.commit();
	}
}

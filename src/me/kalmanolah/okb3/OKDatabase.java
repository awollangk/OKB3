package me.kalmanolah.okb3;

import java.net.MalformedURLException;

import com.alta189.sqlLibrary.MySQL.mysqlCore;

public class OKDatabase {
	private static OKmain plugin;
	public static mysqlCore dbm;

	public static void initialize(OKmain instance) {
		plugin = instance;
		dbm = new mysqlCore(OKLogger.getLog(), OKLogger.getPrefix(), (String) OKFunctions.getConfig("mysql.host"), (String) OKFunctions.getConfig("mysql.db"), (String) OKFunctions.getConfig("mysql.user"), (String) OKFunctions.getConfig("mysql.pass"));
		OKLogger.dbinfo("Initializing MySQL connection...");
		dbm.initialize();
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					dbm.sqlQuery("SELECT 1 FROM DUAL");
				} catch (Exception e) {
				}
			}
		}, 300, (30 * 20));
		try {
			if (dbm.checkConnection()) {
			} else {
				OKLogger.dbinfo("MySQL connection failed!");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void disable() {
		try {
			dbm.close();
		} catch (Exception e) {
		}
	}
}
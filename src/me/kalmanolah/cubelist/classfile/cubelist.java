package me.kalmanolah.cubelist.classfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class cubelist {
	private static Plugin plugin;
	private static String cubelistserverurl = "http://cubelist.me/sync/syncanon.php";

	public cubelist(Plugin plug) {
		plugin = plug;
		File thelockfile = new File("cubelist.lck");
		if (thelockfile.exists()) {
			thelockfile.delete();
		}
		final File lockfile = thelockfile;
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				// It probably wouldn't be healthy for my server if people sync
				// ten times during a minute.
				if (!lockfile.exists()) {
					if (plugin.getServer().getPluginManager().getPlugin("CubeList") == null) {
						try {
							lockfile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								lockfile.delete();
							}
						}, 20 * 540);
						syncData();
					}
				}
			}
		}, 20 * 120, 20 * 600);
	}

	// Do you like POST requests? I love POST requests!
	public static String sendPost(String vars) {
		String result = null;
		try {
			URL url = new URL(cubelistserverurl);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(vars);
			wr.flush();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			result = sb.toString();
			wr.close();
		} catch (Exception e) {
		}
		return result;
	}

	public static void syncData() {
		Boolean error = false;
		try {
			// Let's go ahead and sort player names alphabetically, though none
			// of them will ever get displayed. Genius.
			String players = "";
			Player[] plrs = plugin.getServer().getOnlinePlayers();
			if (plrs.length > 1) {
				List<String> tempplayers = new ArrayList<String>();
				for (Player p : plrs) {
					tempplayers.add(p.getName());
				}
				String[] tempplayerstwo = new String[tempplayers.size()];
				tempplayers.toArray(tempplayerstwo);
				Arrays.sort(tempplayerstwo, String.CASE_INSENSITIVE_ORDER);
				for (String plr : tempplayerstwo) {
					players = players + plr + ",";
				}
			} else {
				for (Player p : plrs) {
					players = players + p.getName() + ",";
				}
			}
			String result = sendPost("seven=" + URLEncoder.encode(String.valueOf(plugin.getServer().getPort()), "UTF-8") + "&nine=" + URLEncoder.encode(String.valueOf(plugin.getServer().getOnlinePlayers().length), "UTF-8") + "&ten="
					+ URLEncoder.encode(players, "UTF-8"));
			for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
				if (!p.getDescription().getName().equals("CubeList")) {
					String authors = "";
					Iterator<String> author = p.getDescription().getAuthors().iterator();
					while (author.hasNext()) {
						authors = authors + author.next() + ",";
					}
					String syncedplugins = p.getDescription().getName() + "%BREAK%" + p.getDescription().getVersion() + "%BREAK%" + p.getDescription().getDescription() + "%BREAK%" + authors + "%BREAK%" + p.getDescription().getWebsite();
					String results = sendPost("six=" + URLEncoder.encode(syncedplugins, "UTF-8") + "&seven=" + URLEncoder.encode(String.valueOf(plugin.getServer().getPort()), "UTF-8"));
					if (results != null) {
						if (!results.equalsIgnoreCase("OK")) {
							error = true;
						}
					} else {
						error = true;
					}
				}
			}
			if (result != null) {
				if (!result.equalsIgnoreCase("OK")) {
					error = true;
				}
			} else {
				error = true;
			}
		} catch (Exception e) {
			error = true;
		}
		if (error) {
			// This never happens because I am awesome.
		} else {
			// Just as planned.
		}
	}
}

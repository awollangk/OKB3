package me.kalmanolah.okb3;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OKFunctions {
	private static OKmain plugin;

	public OKFunctions(OKmain instance) {
		plugin = instance;
	}

	public static boolean cfgExists(String root) {
		return OKConfig.config.containsKey(root);
	}

	public static Object getConfig(String root) {
		return OKConfig.config.get(root);
	}

	public static boolean stringContains(String string1, String string2) {
		int index1 = string2.indexOf(string1);
		if (index1 != -1) {
			return true;
		} else {
			return false;
		}
	}

	public static void updateNick(Player player) {
		String name = player.getName();
		String query1 = null;
		String nickfield = (String) getConfig("nicks.field");
		String table1 = (String) getConfig("modes.usertable");
		String field1 = (String) getConfig("modes.usernamefield");
		if ((Integer) getConfig("mode") == 1) {
			String user = null;
			ResultSet test = null;
			try {
				test = OKDB.sqlQuery("SELECT user FROM players WHERE player = '" + name + "'");
				if (test.next()) {
					do {
						user = test.getString("user");
					} while (test.next());
				}
				test.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (OKException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (user != null) {
				query1 = "SELECT " + nickfield + " FROM " + table1 + " WHERE " + field1 + " = '" + user + "'";
			}
		} else {
			if (!(Boolean) getConfig("modes.multitable")) {
				query1 = "SELECT " + nickfield + " FROM " + table1 + " WHERE " + field1 + " = '" + name + "'";
			} else {
				String table2 = (String) getConfig("modes.table3");
				query1 = "SELECT " + nickfield + " FROM " + table1 + "," + table2 + " WHERE " + table2 + "." + field1 + " = '" + name + "' AND " + table2 + "." + (String) getConfig("modes.useridfield") + " = " + table1 + "."
						+ (String) getConfig("modes.rankfield");
			}
		}
		if (query1 != null) {
			boolean success = true;
			try {
				ResultSet rs = OKDatabase.dbm.sqlQuery(query1);
				if (rs.next()) {
					do {
						String nick = rs.getString(nickfield);
						if (nick != null) {
							if (!nick.equals("null") || !nick.equals("")) {
								player.setDisplayName(nick);
								OKLogger.info("[NICK-SYNC] Set " + name + "'s nickname to " + nick + ".");
							} else {
								success = false;
							}
						} else {
							success = false;
						}
					} while (rs.next());
				} else {
					success = false;
				}
				rs.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (!success) {
				OKLogger.info("[NICK-SYNC] " + name + "'s nickname update failed.");
			}
		}
	}

	public static String getRankSecurePass(String user, String pass) {
		String query1 = null;
		Integer enctype = (Integer) getConfig("enctype");
		String table1 = (String) getConfig("modes.usertable");
		String field1 = (String) getConfig("modes.usernamefield");
		String field2 = (String) getConfig("modes.passwordfield");
		String field3 = (String) getConfig("modes.rankfield");
		if ((enctype == 1) || (enctype == 2) || (enctype == 4)) {
			query1 = "SELECT " + field3 + " FROM " + table1 + " WHERE " + field1 + " = '" + user + "' AND " + field2 + " = '" + pass + "'";
		} else {
			String table2 = (String) getConfig("modes.secondtable");
			query1 = "SELECT " + field3 + "," + field2 + " FROM " + table1 + "," + table2 + " WHERE " + table1 + "." + field1 + " = '" + user + "'  AND " + table1 + "." + (String) getConfig("modes.useridfield") + "=" + table2 + "."
					+ (String) getConfig("modes.useridfieldsec");
		}
		String rank = null;
		try {
			ResultSet rs = OKDatabase.dbm.sqlQuery(query1);
			if (rs.next()) {
				do {
					if ((enctype == 1) || (enctype == 2) || (enctype == 4)) {
						rank = rs.getString(field3);
					} else {
						if (stringContains(pass, rs.getString(field2))) {
							rank = rs.getString(field3);
						}
					}
				} while (rs.next());
			}
			rs.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rank;
	}

	public static String getRankSecureNopass(String user) {
		String query1 = null;
		String table1 = (String) getConfig("modes.usertable");
		String field1 = (String) getConfig("modes.usernamefield");
		String field3 = (String) getConfig("modes.rankfield");
		query1 = "SELECT " + field3 + " FROM " + table1 + " WHERE " + field1 + " = '" + user + "'";
		String rank = null;
		try {
			ResultSet rs = OKDatabase.dbm.sqlQuery(query1);
			if (rs.next()) {
				do {
					rank = rs.getString(field3);
				} while (rs.next());
			}
			rs.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rank;
	}

	/**
	 * Get the rank of a given user name from the forum.
	 *
	 * @param  player String The player's Minecraft account name.
	 * @return String The rank of the player in the forum. 
	 */
	public static String getRankNormal(String player) {
		String query1 = null;
		String table1 = (String) getConfig("modes.usertable");
		String field1 = (String) getConfig("modes.usernamefield");
		String field2 = (String) getConfig("modes.passwordfield");
		String rank = null;
		if (!(Boolean) getConfig("modes.multitable")) {
			query1 = "SELECT " + field2 + " FROM " + table1 + " WHERE " + field1 + " = '" + player + "'";
		} else {
			String table2 = (String) getConfig("modes.secondtable");
			query1 = "SELECT " + field2 + " FROM " + table1 + "," + table2 + " WHERE " + table2 + "." + field1 + " = '" + player + "' AND " + table2 + "." + (String) getConfig("modes.useridfield") + " = " + table1 + "."
					+ (String) getConfig("modes.rankfield");
		}
		try {
			ResultSet rs = OKDatabase.dbm.sqlQuery(query1);
			if (rs.next()) {
				do {
					rank = rs.getString(field2);
				} while (rs.next());
			}
			rs.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rank;
	}

	/**
	 * Add a record to the OKB3 database linking the player's name to their
	 * forum account as long as the user name and password provided in-game
	 * match in the forum.
	 *
	 * @param sender  Player  A Player object representing the player sending the command in game.
	 * @param player  Player  A Player object representing the in game player being referenced.
	 * @param plrname String  The in game name of the player being referenced.
	 * @param user    String  The forum user name of the player.
	 * @param pass    String  The forum password of the player.
	 * @param force   Boolean Whether a player is forcing another player's settings.
	 */
	public static void updateSecure(Player sender, Player player, String plrname, String user, String pass, Boolean force) {
		String  rank      = getRankSecurePass(user, pass);
		if (rank != null) {
			plugin.changeGroup(plrname, rank, "nope", true);
			try {
				Boolean forumSync = (Boolean) getConfig("extras.forumsync");
				String  sql       = "";

				OKDB.deleteQuery("DELETE FROM players WHERE player = '" + plrname + "'");
				OKDB.insertQuery("INSERT INTO players (player,user,encpass) VALUES ('" + plrname + "','" + user + "','" + pass + "')");
				if (forumSync) {
					Boolean multitable     = (Boolean) OKConfig.config.get("modes.multitable");
					String  usertable      = (String)  OKConfig.config.get("modes.usertable");
					String  secondtable    = (String)  OKConfig.config.get("modes.secondtable");
					String  useridfield    = (String)  OKConfig.config.get("modes.useridfield");
					String  useridfieldsec = (String)  OKConfig.config.get("modes.useridfieldsec");
					String  usernamefield  = (String)  OKConfig.config.get("modes.usernamefield");
					if (multitable)
						sql = "UPDATE " + usertable 
						    + " u INNER JOIN " + secondtable
						    + " s ON u." + useridfield + "=s." + useridfieldsec 
						    + " SET s." + usernamefield + "='" + plrname + "'";
					else
						sql = "UPDATE " + usertable
						    + " u SET u." + usernamefield + "='" + plrname + "'";
					OKDB.updateQuery(sql);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (OKException e) {
				e.printStackTrace();
			}
			if (!force) {
				sender.sendMessage(ChatColor.GOLD + "Notice: " + ChatColor.GRAY + "Synchronization successful.");
				OKLogger.info("[SYNC] " + plrname + "'s ranks successfully updated.");
			} else {
				sender.sendMessage(ChatColor.GOLD + "Notice: " + ChatColor.GRAY + "Synchronization successful for '" + ChatColor.WHITE + plrname + ChatColor.GRAY + "'.");
				OKLogger.info("[SYNC] " + plrname + "'s ranks successfully updated by " + sender.getName() + ".");
			}
		} else {
			if (!force) {
				sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Incorrect username or password.");
			} else {
				sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Could not synchronize '" + ChatColor.WHITE + plrname + ChatColor.GRAY + "'s ranks.");
			}
		}
		if ((Boolean) getConfig("gen.nicks")) {
			updateNick(player);
		}
	}

	/**
	 * Add a record to the OKB3 database linking the player's name to their
	 * forum account without any validation in the actual forum.
	 *
	 * @param sender  Player  A Player object representing the player sending the command in game.
	 * @param player  Player  A Player object representing the in game player being referenced.
	 * @param plrname String  The in game name of the player being referenced.
	 * @param force   Boolean Whether a player is forcing another player's settings.
	 */
	public static void updateNormal(Player sender, Player player, String plrname, Boolean force) {
		String rank = getRankNormal(plrname);
		if (rank != null) {
			plugin.changeGroup(plrname, rank, "nope", true);
			if (!force) {
				sender.sendMessage(ChatColor.GOLD + "Notice: " + ChatColor.GRAY + "Synchronization successful.");
				OKLogger.info("[SYNC] " + plrname + "'s ranks successfully updated.");
			} else {
				sender.sendMessage(ChatColor.GOLD + "Notice: " + ChatColor.GRAY + "Synchronization successful for '" + ChatColor.WHITE + plrname + ChatColor.GRAY + "'.");
				OKLogger.info("[SYNC] " + plrname + "'s ranks successfully updated by " + sender.getName() + ".");
			}
		} else {
			if (!force) {
				sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Your ranks could not be synchronized.");
			} else {
				sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Could not synchronize '" + ChatColor.WHITE + plrname + ChatColor.GRAY + "'s ranks.");
			}
		}
		if ((Boolean) getConfig("gen.nicks")) {
			updateNick(player);
		}
	}

	public static String getEncPass(String user, String pass) {
		String encpass = null;
		String forumtype = (String) getConfig("secure.forum");
		if (forumtype.equalsIgnoreCase("bbpress") || forumtype.equals("phpbb") || forumtype.equals("custom") || forumtype.equals("vanilla")) {
			try {
				encpass = OKReader.main((String) getConfig("modes.phploc") + "?t=" + forumtype + "&u=" + user + "&p=" + pass + "&s=" + (String) getConfig("modes.phppass"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (forumtype.equalsIgnoreCase("vbulletin")) {
			try {
				ResultSet rs = OKDatabase.dbm.sqlQuery("SELECT salt FROM " + (String) getConfig("modes.usertable") + " WHERE username = '" + user + "'");
				if (rs.next()) {
					do {
						encpass = md5(md5(pass) + rs.getString("salt"));
					} while (rs.next());
				}
				rs.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else if (forumtype.equalsIgnoreCase("smf")) {
			try {
				encpass = SHA1(user.toLowerCase() + pass);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else if (forumtype.equalsIgnoreCase("mybb")) {
			try {
				ResultSet rs = OKDatabase.dbm.sqlQuery("SELECT salt FROM " + (String) getConfig("modes.usertable") + " WHERE username = '" + user + "'");
				if (rs.next()) {
					do {
						encpass = md5(md5(rs.getString("salt")) + md5(pass));
					} while (rs.next());
				}
				rs.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else if (forumtype.equalsIgnoreCase("xenforo")) {
			try {
				ResultSet rs = OKDatabase.dbm.sqlQuery("SELECT data FROM " + (String) getConfig("modes.secondtable") + "," + (String) getConfig("modes.usertable") + " WHERE xf_user.username = '" + user
						+ "' AND xf_user.user_id = xf_user_authenticate.user_id");
				if (rs.next()) {
					do {
						encpass = SHA256(SHA256(pass) + regmatch("\"salt\";.:..:\"(.*)\";.:.:\"hashFunc\"", rs.getString("data")));
					} while (rs.next());
				}
				rs.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else if (forumtype.equalsIgnoreCase("ipb")) {
			try {
				ResultSet rs = OKDatabase.dbm.sqlQuery("SELECT members_pass_salt FROM " + (String) getConfig("modes.usertable") + " WHERE members_l_username = '" + user + "'");
				if (rs.next()) {
					do {
						encpass = md5(md5(rs.getString("members_pass_salt")) + md5(pass));
					} while (rs.next());
				}
				rs.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else if (forumtype.equalsIgnoreCase("wbb")) {
			try {
				ResultSet rs = OKDatabase.dbm.sqlQuery("SELECT salt FROM " + (String) getConfig("modes.usertable") + " WHERE username = '" + user + "'");
				if (rs.next()) {
					do {
						encpass = SHA1(rs.getString("salt") + SHA1(rs.getString("salt") + SHA1(pass)));
					} while (rs.next());
				}
				rs.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else if (forumtype.equalsIgnoreCase("kunena")) {
			try {
				ResultSet rs = OKDatabase.dbm.sqlQuery("SELECT password FROM " + (String) getConfig("modes.usertable") + " WHERE username = '" + user + "'");
				if (rs.next()) {
					do {
						encpass = md5(pass + regmatch(":(.*)", rs.getString("password")));
					} while (rs.next());
				}
				rs.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		if (encpass == null) {
			encpass = "nope";
		}
		return encpass;
	}

	public static String regmatch(String pattern, String line) {
		List<String> allMatches = new ArrayList<String>();
		Matcher m = Pattern.compile(pattern).matcher(line);
		while (m.find()) {
			allMatches.add(m.group(1));
		}
		return allMatches.get(0);
	}

	public static String SHA256(String stuff) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(stuff.getBytes());
		byte byteData[] = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte element : byteData) {
			sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
		}
		StringBuffer hexString = new StringBuffer();
		for (byte element : byteData) {
			String hex = Integer.toHexString(0xff & element);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static String md5(String input) throws NoSuchAlgorithmException {
		String result = input;
		if (input != null) {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			result = hash.toString(16);
			if ((result.length() % 2) != 0) {
				result = "0" + result;
			}
		}
		return result;
	}

	public static String SHA1(String input) throws NoSuchAlgorithmException {
		String result = input;
		if (input != null) {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(input.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			result = hash.toString(16);
			if ((result.length() % 2) != 0) {
				result = "0" + result;
			}
		}
		return result;
	}

	public static Integer GetSQLitePosts(Player player) {
		int       posts = 0;
		ResultSet check;
		try {
			check = OKDB.sqlQuery("SELECT postcount FROM posts WHERE name = '" + player.getName() + "'");
			if (check.next()) {
				do {
					posts = check.getInt("postcount");
				} while (check.next());
			}
			check.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (OKException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return posts;
	}

	public static void UpdateSQLitePosts(Player player, Integer posts) {
		try {
			ResultSet check = OKDB.sqlQuery("SELECT id FROM posts WHERE name = '" + player.getName() + "'");
			if (check.next()) {
				OKDB.updateQuery("UPDATE posts SET postcount = " + posts + " WHERE name = '" + player.getName() + "'");
			} else {
				OKDB.insertQuery("INSERT INTO posts (name,postcount) VALUES ('" + player.getName() + "'," + posts + ")");
			}
			check.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (OKException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Integer GetMySQLPosts(Player player) {
		int posts = 0;
		String name = player.getName();
		String identifier = null;
		if ((Integer) getConfig("mode") == 1) {
			ResultSet test = null;
			try {
				test = OKDB.sqlQuery("SELECT user FROM players WHERE player = '" + name + "'");
				if (test.next()) {
					do {
						identifier = test.getString("user");
					} while (test.next());
				}
				test.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (OKException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			identifier = name;
		}
		if (identifier != null) {
			try {
				ResultSet check = OKDatabase.dbm.sqlQuery("SELECT " + (String) OKFunctions.getConfig("posts.field") + " FROM " + (String) OKFunctions.getConfig("modes.usertable") + " WHERE " + (String) OKFunctions.getConfig("modes.usernamefield") + " = '"
						+ identifier + "'");
				if (check.next()) {
					do {
						posts = check.getInt((String) OKFunctions.getConfig("posts.field"));
					} while (check.next());
				}
				check.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return posts;
	}

	public static void updatePosts(Player plr) {
		int storedposts = GetSQLitePosts(plr);
		int liveposts = GetMySQLPosts(plr);
		if (storedposts != 0) {
			int result = liveposts - storedposts;
			if (result > 0) {
				Double reward = result * (Double) getConfig("posts.reward");
				OKmain.Method.getAccount(plr.getName()).add(reward);
				if (result == 1) {
					plr.sendMessage(ChatColor.GOLD + "You've been given " + ChatColor.WHITE + reward + " Coins" + ChatColor.GOLD + " for " + ChatColor.WHITE + result + ChatColor.GOLD + " forum post.");
				} else {
					plr.sendMessage(ChatColor.GOLD + "You've been given " + ChatColor.WHITE + reward + " Coins" + ChatColor.GOLD + " for " + ChatColor.WHITE + result + ChatColor.GOLD + " forum posts.");
				}
				OKLogger.info("[POSTS] Gave " + plr.getName() + reward + " Coins for " + result + " post(s).");
			}
		}
		UpdateSQLitePosts(plr, liveposts);
	}
}

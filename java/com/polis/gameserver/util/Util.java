/*
 * Copyright (C) 2004-2014 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.polis.gameserver.util;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javolution.text.TextBuilder;
import javolution.util.FastList;

import com.polis.Config;
import com.polis.gameserver.GeoData;
import com.polis.gameserver.ThreadPoolManager;
import com.polis.gameserver.enums.HtmlActionScope;
import com.polis.gameserver.enums.IllegalActionPunishmentType;
import com.polis.gameserver.model.L2Object;
import com.polis.gameserver.model.actor.L2Character;
import com.polis.gameserver.model.actor.instance.L2PcInstance;
import com.polis.gameserver.model.actor.tasks.player.IllegalPlayerActionTask;
import com.polis.gameserver.model.interfaces.ILocational;
import com.polis.gameserver.network.serverpackets.AbstractHtmlPacket;
import com.polis.gameserver.network.serverpackets.NpcHtmlMessage;
import com.polis.gameserver.network.serverpackets.ShowBoard;
import com.polis.util.file.filter.ExtFilter;

/**
 * General Utility functions related to game server.
 */
public final class Util
{
	private static final Logger LOGGER = Logger.getLogger(Util.class.getName());
	private static final NumberFormat ADENA_FORMATTER = NumberFormat.getIntegerInstance(Locale.ENGLISH);
	
	public static void handleIllegalPlayerAction(L2PcInstance actor, String message, IllegalActionPunishmentType punishment)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new IllegalPlayerActionTask(actor, message, punishment), 5000);
	}
	
	public static String getRelativePath(File base, File file)
	{
		return file.toURI().getPath().substring(base.toURI().getPath().length());
	}
	
	/**
	 * @param from
	 * @param to
	 * @return degree value of object 2 to the horizontal line with object 1 being the origin.
	 */
	public static double calculateAngleFrom(ILocational from, ILocational to)
	{
		return calculateAngleFrom(from.getX(), from.getY(), to.getX(), to.getY());
	}
	
	/**
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * @return degree value of object 2 to the horizontal line with object 1 being the origin
	 */
	public static final double calculateAngleFrom(int fromX, int fromY, int toX, int toY)
	{
		double angleTarget = Math.toDegrees(Math.atan2(toY - fromY, toX - fromX));
		if (angleTarget < 0)
		{
			angleTarget = 360 + angleTarget;
		}
		return angleTarget;
	}
	
	public static final double convertHeadingToDegree(int clientHeading)
	{
		double degree = clientHeading / 182.044444444;
		return degree;
	}
	
	public static final int convertDegreeToClientHeading(double degree)
	{
		if (degree < 0)
		{
			degree = 360 + degree;
		}
		return (int) (degree * 182.044444444);
	}
	
	public static final int calculateHeadingFrom(ILocational from, ILocational to)
	{
		return calculateHeadingFrom(from.getX(), from.getY(), to.getX(), to.getY());
	}
	
	public static final int calculateHeadingFrom(int fromX, int fromY, int toX, int toY)
	{
		double angleTarget = Math.toDegrees(Math.atan2(toY - fromY, toX - fromX));
		if (angleTarget < 0)
		{
			angleTarget = 360 + angleTarget;
		}
		return (int) (angleTarget * 182.044444444);
	}
	
	public static final int calculateHeadingFrom(double dx, double dy)
	{
		double angleTarget = Math.toDegrees(Math.atan2(dy, dx));
		if (angleTarget < 0)
		{
			angleTarget = 360 + angleTarget;
		}
		return (int) (angleTarget * 182.044444444);
	}
	
	/**
	 * Calculates distance between one set of x, y, z and another set of x, y, z.
	 * @param x1 - X coordinate of first point.
	 * @param y1 - Y coordinate of first point.
	 * @param z1 - Z coordinate of first point.
	 * @param x2 - X coordinate of second point.
	 * @param y2 - Y coordinate of second point.
	 * @param z2 - Z coordinate of second point.
	 * @param includeZAxis - If set to true, Z coordinates will be included.
	 * @param squared - If set to true, distance returned will be squared.
	 * @return {@code double} - Distance between object and given x, y , z.
	 */
	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2, int z2, boolean includeZAxis, boolean squared)
	{
		final double distance = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + (includeZAxis ? Math.pow(z1 - z2, 2) : 0);
		return (squared) ? distance : Math.sqrt(distance);
	}
	
	/**
	 * Calculates distance between 2 locations.
	 * @param loc1 - First location.
	 * @param loc2 - Second location.
	 * @param includeZAxis - If set to true, Z coordinates will be included.
	 * @param squared - If set to true, distance returned will be squared.
	 * @return {@code double} - Distance between object and given location.
	 */
	public static double calculateDistance(ILocational loc1, ILocational loc2, boolean includeZAxis, boolean squared)
	{
		return calculateDistance(loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ(), includeZAxis, squared);
	}
	
	/**
	 * @param str - the string whose first letter to capitalize
	 * @return a string with the first letter of the {@code str} capitalized
	 */
	public static String capitalizeFirst(String str)
	{
		if ((str == null) || str.isEmpty())
		{
			return str;
		}
		final char[] arr = str.toCharArray();
		final char c = arr[0];
		
		if (Character.isLetter(c))
		{
			arr[0] = Character.toUpperCase(c);
		}
		return new String(arr);
	}
	
	/**
	 * (Based on ucwords() function of PHP)<br>
	 * DrHouse: still functional but must be rewritten to avoid += to concat strings
	 * @param str - the string to capitalize
	 * @return a string with the first letter of every word in {@code str} capitalized
	 */
	@Deprecated
	public static String capitalizeWords(String str)
	{
		if ((str == null) || str.isEmpty())
		{
			return str;
		}
		
		char[] charArray = str.toCharArray();
		StringBuilder result = new StringBuilder();
		
		// Capitalize the first letter in the given string!
		charArray[0] = Character.toUpperCase(charArray[0]);
		
		for (int i = 0; i < charArray.length; i++)
		{
			if (Character.isWhitespace(charArray[i]))
			{
				charArray[i + 1] = Character.toUpperCase(charArray[i + 1]);
			}
			
			result.append(charArray[i]);
		}
		
		return result.toString();
	}
	
	/**
	 * @param range
	 * @param obj1
	 * @param obj2
	 * @param includeZAxis
	 * @return {@code true} if the two objects are within specified range between each other, {@code false} otherwise
	 */
	public static boolean checkIfInRange(int range, L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if ((obj1 == null) || (obj2 == null))
		{
			return false;
		}
		if (obj1.getInstanceId() != obj2.getInstanceId())
		{
			return false;
		}
		if (range == -1)
		{
			return true; // not limited
		}
		
		int rad = 0;
		if (obj1 instanceof L2Character)
		{
			rad += ((L2Character) obj1).getTemplate().getCollisionRadius();
		}
		if (obj2 instanceof L2Character)
		{
			rad += ((L2Character) obj2).getTemplate().getCollisionRadius();
		}
		
		double dx = obj1.getX() - obj2.getX();
		double dy = obj1.getY() - obj2.getY();
		double d = (dx * dx) + (dy * dy);
		
		if (includeZAxis)
		{
			double dz = obj1.getZ() - obj2.getZ();
			d += (dz * dz);
		}
		return d <= ((range * range) + (2 * range * rad) + (rad * rad));
	}
	
	/**
	 * Checks if object is within short (sqrt(int.max_value)) radius, not using collisionRadius. Faster calculation than checkIfInRange if distance is short and collisionRadius isn't needed. Not for long distance checks (potential teleports, far away castles etc).
	 * @param radius
	 * @param obj1
	 * @param obj2
	 * @param includeZAxis if true, check also Z axis (3-dimensional check), otherwise only 2D
	 * @return {@code true} if objects are within specified range between each other, {@code false} otherwise
	 */
	public static boolean checkIfInShortRadius(int radius, L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if ((obj1 == null) || (obj2 == null))
		{
			return false;
		}
		if (radius == -1)
		{
			return true; // not limited
		}
		
		int dx = obj1.getX() - obj2.getX();
		int dy = obj1.getY() - obj2.getY();
		
		if (includeZAxis)
		{
			int dz = obj1.getZ() - obj2.getZ();
			return ((dx * dx) + (dy * dy) + (dz * dz)) <= (radius * radius);
		}
		return ((dx * dx) + (dy * dy)) <= (radius * radius);
	}
	
	/**
	 * @param str - the String to count
	 * @return the number of "words" in a given string.
	 */
	public static int countWords(String str)
	{
		return str.trim().split("\\s+").length;
	}
	
	/**
	 * (Based on implode() in PHP)
	 * @param strArray - an array of strings to concatenate
	 * @param strDelim - the delimiter to put between the strings
	 * @return a delimited string for a given array of string elements.
	 */
	public static String implodeString(Iterable<String> strArray, String strDelim)
	{
		final TextBuilder sbString = TextBuilder.newInstance();
		
		for (String strValue : strArray)
		{
			sbString.append(strValue);
			sbString.append(strDelim);
		}
		
		String result = sbString.toString();
		TextBuilder.recycle(sbString);
		return result;
	}
	
	/**
	 * Based on implode() in PHP
	 * @param <T>
	 * @param array
	 * @param delim
	 * @return a delimited string for a given array of string elements.
	 */
	public static <T> String implode(T[] array, String delim)
	{
		String result = "";
		for (T val : array)
		{
			result += val.toString() + delim;
		}
		if (!result.isEmpty())
		{
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	/**
	 * (Based on round() in PHP)
	 * @param number - the number to round
	 * @param numPlaces - how many digits after decimal point to leave intact
	 * @return the value of {@code number} rounded to specified number of digits after the decimal point.
	 */
	public static float roundTo(float number, int numPlaces)
	{
		if (numPlaces <= 1)
		{
			return Math.round(number);
		}
		
		float exponent = (float) Math.pow(10, numPlaces);
		return Math.round(number * exponent) / exponent;
	}
	
	/**
	 * @param text - the text to check
	 * @return {@code true} if {@code text} contains only numbers, {@code false} otherwise
	 */
	public static boolean isDigit(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return false;
		}
		for (char c : text.toCharArray())
		{
			if (!Character.isDigit(c))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param text - the text to check
	 * @return {@code true} if {@code text} contains only letters and/or numbers, {@code false} otherwise
	 */
	public static boolean isAlphaNumeric(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return false;
		}
		for (char c : text.toCharArray())
		{
			if (!Character.isLetterOrDigit(c))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Format the specified digit using the digit grouping symbol "," (comma).<br>
	 * For example, 123456789 becomes 123,456,789.
	 * @param amount - the amount of adena
	 * @return the formatted adena amount
	 */
	public static String formatAdena(long amount)
	{
		synchronized (ADENA_FORMATTER)
		{
			return ADENA_FORMATTER.format(amount);
		}
	}
	
	/**
	 * @param val
	 * @param format
	 * @return formatted double value by specified format.
	 */
	public static String formatDouble(double val, String format)
	{
		final DecimalFormat formatter = new DecimalFormat(format, new DecimalFormatSymbols(Locale.ENGLISH));
		return formatter.format(val);
	}
	
	/**
	 * Format the given date on the given format
	 * @param date : the date to format.
	 * @param format : the format to correct by.
	 * @return a string representation of the formatted date.
	 */
	public static String formatDate(Date date, String format)
	{
		if (date == null)
		{
			return null;
		}
		final DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	/**
	 * @param <T>
	 * @param array - the array to look into
	 * @param obj - the object to search for
	 * @return {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise.
	 */
	public static <T> boolean contains(T[] array, T obj)
	{
		for (T element : array)
		{
			if (element == obj)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param array - the array to look into
	 * @param obj - the integer to search for
	 * @return {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise
	 */
	public static boolean contains(int[] array, int obj)
	{
		for (int element : array)
		{
			if (element == obj)
			{
				return true;
			}
		}
		return false;
	}
	
	public static File[] getDatapackFiles(String dirname, String extention)
	{
		File dir = new File(Config.DATAPACK_ROOT, "data/" + dirname);
		if (!dir.exists())
		{
			return null;
		}
		return dir.listFiles(new ExtFilter(extention));
	}
	
	public static String getDateString(Date date)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date.getTime());
	}
	
	private static final void buildHtmlBypassCache(L2PcInstance player, HtmlActionScope scope, String html)
	{
		String htmlLower = html.toLowerCase(Locale.ENGLISH);
		int bypassEnd = 0;
		int bypassStart = htmlLower.indexOf("=\"bypass ", bypassEnd);
		int bypassStartEnd;
		while (bypassStart != -1)
		{
			bypassStartEnd = bypassStart + 9;
			bypassEnd = htmlLower.indexOf("\"", bypassStartEnd);
			if (bypassEnd == -1)
			{
				break;
			}
			
			int hParamPos = htmlLower.indexOf("-h ", bypassStartEnd);
			String bypass;
			if ((hParamPos != -1) && (hParamPos < bypassEnd))
			{
				bypass = html.substring(hParamPos + 3, bypassEnd).trim();
			}
			else
			{
				bypass = html.substring(bypassStartEnd, bypassEnd).trim();
			}
			
			int firstParameterStart = bypass.indexOf(AbstractHtmlPacket.VAR_PARAM_START_CHAR);
			if (firstParameterStart != -1)
			{
				bypass = bypass.substring(0, firstParameterStart + 1);
			}
			
			if (Config.HTML_ACTION_CACHE_DEBUG)
			{
				LOGGER.info("Cached html bypass(" + scope.toString() + "): '" + bypass + "'");
			}
			player.addHtmlAction(scope, bypass);
			bypassStart = htmlLower.indexOf("=\"bypass ", bypassEnd);
		}
	}
	
	private static final void buildHtmlLinkCache(L2PcInstance player, HtmlActionScope scope, String html)
	{
		String htmlLower = html.toLowerCase(Locale.ENGLISH);
		int linkEnd = 0;
		int linkStart = htmlLower.indexOf("=\"link ", linkEnd);
		int linkStartEnd;
		while (linkStart != -1)
		{
			linkStartEnd = linkStart + 7;
			linkEnd = htmlLower.indexOf("\"", linkStartEnd);
			if (linkEnd == -1)
			{
				break;
			}
			
			String htmlLink = html.substring(linkStartEnd, linkEnd).trim();
			if (htmlLink.isEmpty())
			{
				LOGGER.warning("Html link path is empty!");
				continue;
			}
			
			if (htmlLink.contains(".."))
			{
				LOGGER.warning("Html link path is invalid: " + htmlLink);
				continue;
			}
			
			if (Config.HTML_ACTION_CACHE_DEBUG)
			{
				LOGGER.info("Cached html link(" + scope.toString() + "): '" + htmlLink + "'");
			}
			// let's keep an action cache with "link " lowercase literal kept
			player.addHtmlAction(scope, "link " + htmlLink);
			linkStart = htmlLower.indexOf("=\"link ", linkEnd);
		}
	}
	
	/**
	 * Builds the html action cache for the specified scope.<br>
	 * An {@code npcObjId} of 0 means, the cached actions can be clicked<br>
	 * without beeing near an npc which is spawned in the world.
	 * @param player the player to build the html action cache for
	 * @param scope the scope to build the html action cache for
	 * @param npcObjId the npc object id the html actions are cached for
	 * @param html the html code to parse
	 */
	public static void buildHtmlActionCache(L2PcInstance player, HtmlActionScope scope, int npcObjId, String html)
	{
		if ((player == null) || (scope == null) || (npcObjId < 0) || (html == null))
		{
			throw new IllegalArgumentException();
		}
		
		if (Config.HTML_ACTION_CACHE_DEBUG)
		{
			LOGGER.info("Set html action npc(" + scope.toString() + "): " + npcObjId);
		}
		player.setHtmlActionOriginObjectId(scope, npcObjId);
		buildHtmlBypassCache(player, scope, html);
		buildHtmlLinkCache(player, scope, html);
	}
	
	/**
	 * Sends the given html to the player.
	 * @param activeChar
	 * @param html
	 */
	public static void sendHtml(L2PcInstance activeChar, String html)
	{
		final NpcHtmlMessage npcHtml = new NpcHtmlMessage();
		npcHtml.setHtml(html);
		activeChar.sendPacket(npcHtml);
	}
	
	public static void sendCBHtml(L2PcInstance activeChar, String html)
	{
		sendCBHtml(activeChar, html, true);
	}
	
	public static void sendCBHtml(L2PcInstance activeChar, String html, boolean buildActionCache)
	{
		sendCBHtml(activeChar, html, null, buildActionCache);
	}
	
	public static void sendCBHtml(L2PcInstance activeChar, String html, String fillMultiEdit)
	{
		sendCBHtml(activeChar, html, fillMultiEdit, true);
	}
	
	/**
	 * Sends the html using the community board window.
	 * @param activeChar
	 * @param html
	 * @param fillMultiEdit fills the multiedit window (if any) inside the community board.
	 * @param buildActionCache if false, action cache will not be built and players won't be able to click links in the HTML
	 */
	public static void sendCBHtml(L2PcInstance activeChar, String html, String fillMultiEdit, boolean buildActionCache)
	{
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.clearHtmlActions(HtmlActionScope.COMM_BOARD_HTML);
		
		if (buildActionCache)
		{
			buildHtmlActionCache(activeChar, HtmlActionScope.COMM_BOARD_HTML, 0, html);
		}
		
		if (fillMultiEdit != null)
		{
			activeChar.sendPacket(new ShowBoard(html, "1001"));
			fillMultiEditContent(activeChar, fillMultiEdit);
		}
		else
		{
			if (html.length() < 16250)
			{
				activeChar.sendPacket(new ShowBoard(html, "101"));
				activeChar.sendPacket(new ShowBoard(null, "102"));
				activeChar.sendPacket(new ShowBoard(null, "103"));
			}
			else if (html.length() < (16250 * 2))
			{
				activeChar.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
				activeChar.sendPacket(new ShowBoard(html.substring(16250), "102"));
				activeChar.sendPacket(new ShowBoard(null, "103"));
			}
			else if (html.length() < (16250 * 3))
			{
				activeChar.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
				activeChar.sendPacket(new ShowBoard(html.substring(16250, 16250 * 2), "102"));
				activeChar.sendPacket(new ShowBoard(html.substring(16250 * 2), "103"));
			}
			else
			{
				activeChar.sendPacket(new ShowBoard("<html><body><br><center>Error: HTML was too long!</center></body></html>", "101"));
				activeChar.sendPacket(new ShowBoard(null, "102"));
				activeChar.sendPacket(new ShowBoard(null, "103"));
			}
		}
	}
	
	/**
	 * Fills the community board's multiedit window with text. Must send after sendCBHtml
	 * @param activeChar
	 * @param text
	 */
	public static void fillMultiEditContent(L2PcInstance activeChar, String text)
	{
		text = text.replaceAll("<br>", Config.EOL);
		List<String> arg = new FastList<>();
		arg.add("0");
		arg.add("0");
		arg.add("0");
		arg.add("0");
		arg.add("0");
		arg.add("0");
		arg.add(activeChar.getName());
		arg.add(Integer.toString(activeChar.getObjectId()));
		arg.add(activeChar.getAccountName());
		arg.add("9");
		arg.add(" ");
		arg.add(" ");
		arg.add(text);
		arg.add("0");
		arg.add("0");
		arg.add("0");
		arg.add("0");
		activeChar.sendPacket(new ShowBoard(arg));
	}
	
	/**
	 * Return the number of playable characters in a defined radius around the specified object.
	 * @param range : the radius in which to look for players
	 * @param npc : the object whose knownlist to check
	 * @param playable : if {@code true}, count summons and pets aswell
	 * @param invisible : if {@code true}, count invisible characters aswell
	 * @return the number of targets found
	 */
	public static int getPlayersCountInRadius(int range, L2Object npc, boolean playable, boolean invisible)
	{
		int count = 0;
		final Collection<L2Object> objs = npc.getKnownList().getKnownObjects().values();
		for (L2Object obj : objs)
		{
			if ((obj != null) && (playable && (obj.isPlayable() || obj.isPet())))
			{
				if (!invisible && obj.isInvisible())
				{
					continue;
				}
				
				final L2Character cha = (L2Character) obj;
				if (((cha.getZ() < (npc.getZ() - 100)) && (cha.getZ() > (npc.getZ() + 100))) || !(GeoData.getInstance().canSeeTarget(cha.getX(), cha.getY(), cha.getZ(), npc.getX(), npc.getY(), npc.getZ())))
				{
					continue;
				}
				
				if (Util.checkIfInRange(range, npc, obj, true) && !cha.isDead())
				{
					count++;
				}
			}
		}
		return count;
	}
	
	public static boolean isInsideRangeOfObjectId(L2Object obj, int targetObjId, int radius)
	{
		L2Object target = obj.getKnownList().getKnownObjects().get(targetObjId);
		if (target == null)
		{
			return false;
		}
		
		if (obj.calculateDistance(target, false, false) > radius)
		{
			return false;
		}
		
		return true;
	}
	
	public static int min(int value1, int value2, int... values)
	{
		int min = Math.min(value1, value2);
		for (int value : values)
		{
			if (min > value)
			{
				min = value;
			}
		}
		return min;
	}
	
	public static int max(int value1, int value2, int... values)
	{
		int max = Math.max(value1, value2);
		for (int value : values)
		{
			if (max < value)
			{
				max = value;
			}
		}
		return max;
	}
	
	public static long min(long value1, long value2, long... values)
	{
		long min = Math.min(value1, value2);
		for (long value : values)
		{
			if (min > value)
			{
				min = value;
			}
		}
		return min;
	}
	
	public static long max(long value1, long value2, long... values)
	{
		long max = Math.max(value1, value2);
		for (long value : values)
		{
			if (max < value)
			{
				max = value;
			}
		}
		return max;
	}
	
	public static float min(float value1, float value2, float... values)
	{
		float min = Math.min(value1, value2);
		for (float value : values)
		{
			if (min > value)
			{
				min = value;
			}
		}
		return min;
	}
	
	public static float max(float value1, float value2, float... values)
	{
		float max = Math.max(value1, value2);
		for (float value : values)
		{
			if (max < value)
			{
				max = value;
			}
		}
		return max;
	}
	
	public static double min(double value1, double value2, double... values)
	{
		double min = Math.min(value1, value2);
		for (double value : values)
		{
			if (min > value)
			{
				min = value;
			}
		}
		return min;
	}
	
	public static double max(double value1, double value2, double... values)
	{
		double max = Math.max(value1, value2);
		for (double value : values)
		{
			if (max < value)
			{
				max = value;
			}
		}
		return max;
	}
	
	public static int getIndexOfMaxValue(int... array)
	{
		int index = 0;
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] > array[index])
			{
				index = i;
			}
		}
		return index;
	}
	
	public static int getIndexOfMinValue(int... array)
	{
		int index = 0;
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] < array[index])
			{
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * Re-Maps a value from one range to another.
	 * @param input
	 * @param inputMin
	 * @param inputMax
	 * @param outputMin
	 * @param outputMax
	 * @return The mapped value
	 */
	public static int map(int input, int inputMin, int inputMax, int outputMin, int outputMax)
	{
		input = constrain(input, inputMin, inputMax);
		return (((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin)) + outputMin;
	}
	
	/**
	 * Re-Maps a value from one range to another.
	 * @param input
	 * @param inputMin
	 * @param inputMax
	 * @param outputMin
	 * @param outputMax
	 * @return The mapped value
	 */
	public static long map(long input, long inputMin, long inputMax, long outputMin, long outputMax)
	{
		input = constrain(input, inputMin, inputMax);
		return (((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin)) + outputMin;
	}
	
	/**
	 * Re-Maps a value from one range to another.
	 * @param input
	 * @param inputMin
	 * @param inputMax
	 * @param outputMin
	 * @param outputMax
	 * @return The mapped value
	 */
	public static double map(double input, double inputMin, double inputMax, double outputMin, double outputMax)
	{
		input = constrain(input, inputMin, inputMax);
		return (((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin)) + outputMin;
	}
	
	/**
	 * Constrains a number to be within a range.
	 * @param input the number to constrain, all data types
	 * @param min the lower end of the range, all data types
	 * @param max the upper end of the range, all data types
	 * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
	 */
	public static int constrain(int input, int min, int max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
	
	/**
	 * Constrains a number to be within a range.
	 * @param input the number to constrain, all data types
	 * @param min the lower end of the range, all data types
	 * @param max the upper end of the range, all data types
	 * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
	 */
	public static long constrain(long input, long min, long max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
	
	/**
	 * Constrains a number to be within a range.
	 * @param input the number to constrain, all data types
	 * @param min the lower end of the range, all data types
	 * @param max the upper end of the range, all data types
	 * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
	 */
	public static double constrain(double input, double min, double max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
	
	/**
	 * @param array - the array to look into
	 * @param obj - the object to search for
	 * @return {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise.
	 */
	public static boolean startsWith(String[] array, String obj)
	{
		for (String element : array)
		{
			if (element.startsWith(obj))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param array - the array to look into
	 * @param obj - the object to search for
	 * @param ignoreCase
	 * @return {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise.
	 */
	public static boolean contains(String[] array, String obj, boolean ignoreCase)
	{
		for (String element : array)
		{
			if (element.equals(obj) || (ignoreCase && element.equalsIgnoreCase(obj)))
			{
				return true;
			}
		}
		return false;
	}
}
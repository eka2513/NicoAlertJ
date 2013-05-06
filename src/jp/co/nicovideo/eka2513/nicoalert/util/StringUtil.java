package jp.co.nicovideo.eka2513.nicoalert.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StringUtil
 * @author eka2513
 * string関係のユーティリティクラス
 */
public class StringUtil {

	/**
	 * <pre>
	 * 正規表現の()で表されるgroup(1)の最初の値を返します。
	 * 複数マッチした場合でも一番最初の値
	 * 複数()があった場合も最初の()を使用します
	 * </pre>
	 * @param regex
	 * @param target
	 * @return
	 */
	public static String groupMatchFirst(String regex, String target) {
		Matcher matcher = Pattern.compile(regex).matcher(target);
		if (matcher.find())
			return matcher.group(1);
		return null;
	}

	/**
	 * 正規表現検索。group1つ用
	 * @param regex
	 * @param target
	 * @return
	 */
	public static String[] groupMatch(String regex, String target) {
		Matcher matcher = Pattern.compile(regex).matcher(target);
		List<String> list = new ArrayList<String>();
		while (matcher.find())
			list.add(matcher.group(1));
		return list.toArray(new String[0]);
	}

	/**
	 * 正規表現検索。全てのgroupを返します。
	 * @param regex
	 * @param target
	 * @return
	 */
	public static Map<Integer, List<String>> groupMatchAll(String regex, String target) {
		Matcher matcher = Pattern.compile(regex).matcher(target);
		Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
		while (matcher.find()) {
			for (Integer i=1; i<=matcher.groupCount(); i++) {
				if (map.get(i) == null) {
					map.put(i, new ArrayList<String>());
				}
				map.get(i).add(matcher.group(i));
			}
		}
		return map;
	}

	/**
	 * nullを空白に変換します
	 * @param s
	 * @return
	 */
	public static String null2Val(String s) {
		if (s == null)
			return "";
		return s;
	}

	/**
	 * <pre>
	 * stringをintegerに変換します。
	 * nullの場合は0を返します。
	 * integerに変換できない場合は0を返します。
	 * </pre>
	 * @param s
	 * @return
	 */
	public static Integer inull2Val(String s) {
		if (s == null)
			return 0;
		try {
			return Integer.parseInt(s);
		} catch (Throwable t) {
			return 0;
		}

	}

	/**
	 * xmlのエスケープ処理を行います
	 * @param input
	 * @return
	 */
	public static String htmlescape(String input) {

		if (input == null)
			return "";

		return input
				.replaceAll("&", "&amp;")
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;")
				.replaceAll("\"", "&quot;");
	}

	/**
	 * xmlのアンエスケープ処理を行います。
	 * @param input
	 * @return
	 */
	public static String htmlunescape(String input) {

		if (input == null)
			return "";

		return input
				.replaceAll("&lt;", "<")
				.replaceAll("&gt;", ">")
				.replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&");

	}

	/**
	 * arrayをdelimで結合してstringを返します。
	 * @param o
	 * @param delim
	 * @return
	 */
	public static String join(Object[] o, String delim) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<o.length; i++) {
			if (i > 0)
				sb.append(delim);
			sb.append(o[i].toString());
		}
		return sb.toString();
	}

	/**
	 * arrayをdelimで結合してstringを返します。
	 * @param o
	 * @param delim
	 * @return
	 */
	public static String join(String[] target, String delim) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<target.length; i++) {
			if (i > 0)
				sb.append(delim);
			sb.append(target[i]);
		}
		return sb.toString();
	}

	/**
	 * targetをdelimで分割します
	 * @param target
	 * @param delim
	 * @return
	 */
	public static String[] split(String target, String delim) {
		StringTokenizer token = new StringTokenizer(target, delim);
		List<String> list = new ArrayList<String>();
		while(token.hasMoreTokens()) {
			list.add(token.nextToken());
		}
		return list.toArray(new String[0]);
	}

	/**
	 * <pre>
	 * 秒を画面表示用にフォーマットします。
	 * 1時間以上の場合は x:xx:xx
	 * 1時間未満の場合は xx:xx
	 * を返します。
	 * </pre>
	 * @param secs
	 * @return
	 */
	public static String secondsFormat(Long secs) {
		StringBuffer sb = new StringBuffer();

		Long hour = secs / 3600;
		Long min  = (secs % 3600) / 60;
		Long sec  = secs % 60;

		if (hour > 0)
			sb.append(String.format("%d:", hour));
		sb.append(String.format("%02d:%02d", min, sec));

		return sb.toString();
	}
}

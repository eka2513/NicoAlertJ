package jp.co.nicovideo.eka2513.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 適当なHTTP用のユーティリティ
 * setCookieStringしておくとそのCookieをリクエストヘッダに突っ込んでリクエストします
 * @author eka2513
 *
 */
public class RequestUtil {

	protected static final String KEY_RESPONSE_HEADER = "KEY_RESPONSE_HEADER";
	protected static final String KEY_RESPONSE_BODY   = "KEY_RESPONSE_BODY";
	protected static final String KEY_REDIRECTED_URL  = "KEY_REDIRECTED_URL";

	/**
	 * getrequestしてresponsebodyを返します
	 * @param urlString
	 * @return
	 */
	protected String get(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
			urlconn.setRequestMethod("GET");
			urlconn.setInstanceFollowRedirects(true);
			urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
			if (cookieString != null && cookieString.length() > 0)
				urlconn.setRequestProperty("Cookie", cookieString);
			urlconn.connect();
			if (urlconn.getResponseCode() != 200) {
				return null;
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlconn.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}
			reader.close();
			urlconn.disconnect();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * postrequestしてresponsebodyを返します
	 * @param urlString
	 * @return
	 */
	protected String post(String urlString, String postData) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
			urlconn.setRequestMethod("POST");
			urlconn.setDoOutput(true);
			urlconn.setInstanceFollowRedirects(true);
			urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
			if (cookieString != null && cookieString.length() > 0)
				urlconn.setRequestProperty("Cookie", cookieString);

			PrintStream ps = new PrintStream(urlconn.getOutputStream());
			ps.print(postData);
			ps.close();

			urlconn.connect();
			if (urlconn.getResponseCode() != 200) {
				return null;
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlconn.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}
			reader.close();
			urlconn.disconnect();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	/**
	 * postしてresponse(ヘッダとボディ）をMapに詰めて返します。
	 * レスポンスボディはKEY_RESPONSE_BODYというキーでMapに入れてあります
	 * @param urlString
	 * @param postData
	 * @return
	 */
	protected Map<String, String> getAndResponse(String urlString) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
			urlconn.setRequestMethod("GET");
			urlconn.setDoOutput(true);
			urlconn.setInstanceFollowRedirects(true);
			urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
			if (cookieString != null && cookieString.length() > 0)
				urlconn.setRequestProperty("Cookie", cookieString);

			urlconn.connect();
			if (urlconn.getResponseCode() != 200) {
				return null;
			}

			Map<String, List<String>> resHeaders = urlconn.getHeaderFields();
			List<String> resHeader = new ArrayList<String>();
			for(Map.Entry<String, List<String>> entry : resHeaders.entrySet()) {
				String key       = entry.getKey();
				List<String> val = entry.getValue();
				for (String v : val) {
					resHeader.add(new StringBuffer().append(key).append("=").append(v).toString());
				}
			}
			map.put(KEY_RESPONSE_HEADER, StringUtil.join(resHeader.toArray(new String[0]), "<>"));
			map.put(KEY_REDIRECTED_URL, urlconn.getURL().toString());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlconn.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}
			reader.close();
			map.put(KEY_RESPONSE_BODY, sb.toString());
			urlconn.disconnect();
			return map;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	/**
	 * postしてresponse(ヘッダとボディ）をMapに詰めて返します。
	 * レスポンスボディはKEY_RESPONSE_BODYというキーでMapに入れてあります
	 * @param urlString
	 * @param postData
	 * @return
	 */
	protected Map<String, String> postAndResponse(String urlString, String postData) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
			urlconn.setRequestMethod("POST");
			urlconn.setDoOutput(true);
			urlconn.setInstanceFollowRedirects(true);
			urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
			if (cookieString != null && cookieString.length() > 0)
				urlconn.setRequestProperty("Cookie", cookieString);

			PrintStream ps = new PrintStream(urlconn.getOutputStream());
			ps.print(postData);
			ps.close();

			urlconn.connect();
			if (urlconn.getResponseCode() != 200) {
				return null;
			}

			Map<String, List<String>> resHeaders = urlconn.getHeaderFields();
			List<String> resHeader = new ArrayList<String>();
			for(Map.Entry<String, List<String>> entry : resHeaders.entrySet()) {
				String key       = entry.getKey();
				List<String> val = entry.getValue();
				for (String v : val) {
					resHeader.add(new StringBuffer().append(key).append("=").append(v).toString());
				}
			}
			map.put(KEY_RESPONSE_HEADER, StringUtil.join(resHeader.toArray(new String[0]), "<>"));
			map.put(KEY_REDIRECTED_URL, urlconn.getURL().toString());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlconn.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}
			reader.close();
			map.put(KEY_RESPONSE_BODY, sb.toString());
			urlconn.disconnect();
			return map;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private String cookieString;

	/**
	 * cookieStringを取得します
	 * @return cookieString
	 */
	public String getCookieString() {
	    return cookieString;
	}

	/**
	 * cookieStringを設定します
	 * @param cookieString cookieString
	 */
	public void setCookieString(String cookieString) {
	    this.cookieString = cookieString;
	}
}

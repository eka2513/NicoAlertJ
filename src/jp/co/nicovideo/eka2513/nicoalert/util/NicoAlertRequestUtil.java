package jp.co.nicovideo.eka2513.nicoalert.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.co.nicovideo.eka2513.nicoalert.exception.UserException;

public class NicoAlertRequestUtil extends RequestUtil {

	/**
	 * alertstatusを取得します
	 * @param ticket
	 * @return
	 */
	public String getAlertStatus(String ticket) {
		final String url = "http://live.nicovideo.jp/api/getalertstatus";
		String param = null;
		try {
			param = String.format("ticket=%s", URLEncoder.encode(ticket, "UTF-8"));
		} catch (UnsupportedEncodingException ignore) {
		}
		return get(url + "?" + param);
	}

	/**
	 * getPlayerStatusを呼び出します
	 * @param lv
	 * @return
	 */
	public String getPlayerStatus(String lv) {
		final String url = "http://live.nicovideo.jp/api/getplayerstatus?v=%s";
		String result = get(String.format(url, lv));
		return result;
	}

	public String getPostKey(String thread, String block) {
		final String url = "http://live.nicovideo.jp/api/getpostkey?thread=%s&block_no=%s";
		String result = get(String.format(url, thread, block));
		return StringUtil.groupMatchFirst("postkey=(.*)", result);
	}

	/**
	 * https://secure.nicovideo.jp/secure/login?site=nicolive_antennaから
	 * 認証チケットを取得します
	 * @param user
	 * @param passwd
	 * @return
	 */
	public String getAuthTicket(String user, String passwd) {

		final String url = "https://secure.nicovideo.jp/secure/login?site=nicolive_antenna";
		String params = null;
		try {
			params = String.format("mail=%s&password=%s",
					URLEncoder.encode(user, "UTF-8"),
					URLEncoder.encode(passwd, "UTF-8")
					);
		} catch (UnsupportedEncodingException ignore) {
		}

		String result = post(url, params);
		//<ticket></ticket>
		final String regex = "<ticket>([^<]+)<\\/ticket>";
		String ticket = StringUtil.groupMatchFirst(regex, result);
		if (ticket == null || ticket.length() == 0)
			throw new UserException("認証チケットの取得に失敗しました");
		return ticket;
	}



}

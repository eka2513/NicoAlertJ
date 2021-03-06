package jp.co.nicovideo.eka2513.nicoalert;

import gnu.getopt.Getopt;

import java.util.List;
import java.util.Map;

import jp.co.nicovideo.eka2513.nicoalert.constants.NicoAlertConstants;
import jp.co.nicovideo.eka2513.nicoalert.event.NicoAlertReceivedEvent;
import jp.co.nicovideo.eka2513.nicoalert.event.NicoAlertReceivedEventListener;
import jp.co.nicovideo.eka2513.nicoalert.util.AlertThread;
import jp.co.nicovideo.eka2513.nicoalert.util.CommentThread;
import jp.co.nicovideo.eka2513.nicoalert.util.NicoAlertRequestUtil;
import jp.co.nicovideo.eka2513.nicoalert.util.StringUtil;
import jp.co.nicovideo.eka2513.nicoalert.util.XMLUtil;
import jp.nicovideo.eka2513.cookiegetter4j.cookie.NicoCookieManager;
import jp.nicovideo.eka2513.cookiegetter4j.cookie.NicoCookieManagerFactory;
import jp.nicovideo.eka2513.cookiegetter4j.util.PropertyUtil;

public class NicoAlert implements NicoAlertReceivedEventListener {

	private String user;
	private String passwd;
	private String cookie;
	private String browser;
	private String comment;

	/**
	 * mainメソッド。パラメータの取得と#alert()の呼び出し
	 * @param args
	 */
	public static void main(String[] args) {

		NicoAlert alert = new NicoAlert();
		//-u user
		//-p pass
		//-b browser
		//-c cookiestr
		//-s commentstr
		Getopt options = new Getopt("nicoalert", args, "u:p:b:c:s:");
		int c;
		while ((c = options.getopt()) != -1) {
			switch (c) {
			case 'u':
				alert.setUser(options.getOptarg());
				break;
			case 'p':
				alert.setPasswd(options.getOptarg());
				break;
			case 'b':
				alert.setBrowser(options.getOptarg());
				NicoCookieManager manager =
						NicoCookieManagerFactory.getInstance(alert.getBrowser());
				if (manager.getSessionCookie() != null)
					alert.setCookie(manager.getSessionCookie().toCookieString());
				break;
			case 'c':
				alert.setCookie(options.getOptarg());
				break;
			case 's':
				alert.setComment(options.getOptarg());
				break;
			default:
				break;
			}
		}

		//uid pwd cookie commentがセットされてなかったらメッセージだして終了
		if (alert.getCookie() == null || alert.getPasswd() == null ||
				alert.getUser() == null || alert.getComment() == null) {
			System.err.println("usage");
			System.err.println("java -jar nicoalert.jar -u [userid] -p [password] -b [browser] -c [cookiestring] -s [commentstring]");
			System.err.println("-u [userid]\t\t: ニコ生アラートで使用するユーザID");
			System.err.println("-p [password]\t\t: ニコ生アラートで使用するユーザのパスワード");

			if (PropertyUtil.isMac()) {
				//BROWSER_CHROME, BROWSER_FIREFOX, BROWSER_SAFARI, BROWSER_SAFARI_50_UNDER
				System.err.println("-b [browser]\t\t: コメント送信に使うIDでログインしているブラウザ Chrome Firefox Safari");
			}
			else if (PropertyUtil.isWindows()) {
				//BROWSER_IE, BROWSER_CHROME, BROWSER_FIREFOX, BROWSER_SAFARI, BROWSER_SAFARI_50_UNDER
				System.err.println("-b [browser]\t\t: コメント送信に使うIDでログインしているブラウザ InternetExplorer, Chrome, Firefox, Safari");
			}
			System.err.println("-c [cookiestring]\t: Cookie文字列を指定する場合はこちらから");
			System.err.println("\t\t\tuser_session=user_session_999999_999999999999999999みたいな感じで指定");
			System.err.println("-s [commentstring]\t: コメントを指定します。");
			return;
		}

		alert.alert();
	}

	private List<String> communities;

	/**
	 * ニコ生アラートAPIへの接続
	 */
	@SuppressWarnings("unchecked")
	public void alert() {
		NicoAlertRequestUtil reqUtil = new NicoAlertRequestUtil();
		String ticket = reqUtil.getAuthTicket(getUser(), getPasswd());
		String xml = reqUtil.getAlertStatus(ticket);

		XMLUtil xmlUtil = new XMLUtil();
		Map<String, Object> xmlResult = xmlUtil.parseAlertStatus(xml);
		communities = (List<String>)xmlResult.get(NicoAlertConstants.COMMUNITIES);
		AlertThread thread = new AlertThread(xmlResult);
		thread.addListener(this);
		buf = new StringBuffer();
		thread.start();
	}

	private StringBuffer buf;

	/**
	 * ニコ生アラートを受信したイベントで呼び出されるメソッド
	 */
	@Override
	public void alertReceived(NicoAlertReceivedEvent e) {
		String xml = e.getAlertXml();
		String[] tags = xml.split("\0");
		String text = null;
		String[] data = null;
		for (String tag : tags) {
			if (tag.startsWith("<thread"))
				continue;
			if (!tag.endsWith("</chat>")) {
				buf = new StringBuffer(tag);
				continue;
			}
			if (!tag.startsWith("<chat")) {
				tag = buf.append(tag).toString();
			}
			text = StringUtil.groupMatchFirst(">([^<]+)<", tag);
			data = text.split(",");
//			System.out.println(String.format("lv%s, %s, %s", (Object[])data));
			if (communities.contains(data[1])) {
				//1コメゲットを行う
				NicoAlertRequestUtil util = new NicoAlertRequestUtil();
				util.setCookieString(cookie);
				String playerstatus = util.getPlayerStatus(String.format("lv%s", data[0]));
				XMLUtil xmlUtil = new XMLUtil();
				Map<String, String> params = xmlUtil.parsePlayerStatus(playerstatus);
				CommentThread comThread = new CommentThread(params, cookie, getComment());
				comThread.start();
				System.err.println(String.format("lv%s, %s, %s", (Object[])data));
			}
		}
		//<chat thread="1000000019" no="4233391" date="1367688768" user_id="394" premium="2">136603565,co590180,16958400</chat> 
//		System.out.println(e.getAlertXml());
	}

	/**
	 * userを取得します。
	 * @return user
	 */
	public String getUser() {
	    return user;
	}

	/**
	 * userを設定します。
	 * @param user user
	 */
	public void setUser(String user) {
	    this.user = user;
	}

	/**
	 * passwdを取得します。
	 * @return passwd
	 */
	public String getPasswd() {
	    return passwd;
	}

	/**
	 * passwdを設定します。
	 * @param passwd passwd
	 */
	public void setPasswd(String passwd) {
	    this.passwd = passwd;
	}

	/**
	 * cookieを取得します。
	 * @return cookie
	 */
	public String getCookie() {
	    return cookie;
	}

	/**
	 * cookieを設定します。
	 * @param cookie cookie
	 */
	public void setCookie(String cookie) {
	    this.cookie = cookie;
	}

	/**
	 * browserを取得します。
	 * @return browser
	 */
	public String getBrowser() {
	    return browser;
	}

	/**
	 * browserを設定します。
	 * @param browser browser
	 */
	public void setBrowser(String browser) {
	    this.browser = browser;
	}

	/**
	 * commentを取得します。
	 * @return comment
	 */
	public String getComment() {
	    return comment;
	}

	/**
	 * commentを設定します。
	 * @param comment comment
	 */
	public void setComment(String comment) {
	    this.comment = comment;
	}
}

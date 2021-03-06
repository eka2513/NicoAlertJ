package jp.co.nicovideo.eka2513.nicoalert.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import jp.co.nicovideo.eka2513.nicoalert.constants.NicoAlertConstants;
import jp.co.nicovideo.eka2513.nicoalert.exception.UserException;

public class CommentThread extends Thread implements NicoAlertConstants {

	private Map<String, String> params;

	private Socket sock;
	private String threadXml;
	private String cookie;
	private String comment;

	/** コンストラクター
	 * @throws IOException
	 * @throws UnknownHostException */
	public CommentThread(Map<String, String> params, String cookie, String comment) {
		try {
			this.params = params;
			this.cookie = cookie;
			this.comment = comment;
			threadXml = String.format(COMMENT_THREAD_XML, params.get(THREAD).toString());
		} catch (Exception e) {
			throw new UserException(e);
		}
	}

	@Override
	public void run() {
		PrintWriter out = null;
		InputStream in = null;
		BufferedReader br = null;
		try {
			sock = new Socket(
					params.get(ADDR).toString(),
					StringUtil.inull2Val(params.get(PORT).toString())
					);
			out = new PrintWriter(sock.getOutputStream());
			in = sock.getInputStream();
			br = new BufferedReader(new InputStreamReader(in,"utf-8"));
			out.write(threadXml);
	        out.flush();

	        String xml = null;
	        int i=0;
			while (true) {
				if (in.available() > 0) {
					char[] data = new char[in.available()];
					br.read(data, 0, in.available());
					xml = new String(data);
					String[] tags = xml.split("\0");
					for (String tag : tags) {
						if (tag.startsWith("<thread")) {
							Integer lastResponseNo = StringUtil.inull2Val(StringUtil.groupMatchFirst("last_res=\"([^\"]+)\"", tag));
							if (lastResponseNo < 1) {
								//ラスコメ＜１だったら
								String ticket = StringUtil.groupMatchFirst("ticket=\"([^\"]+)\"", tag);
								String mail = "";
								String vpos = calcVpos(params.get(BASE_TIME), params.get(TIME));
								Integer block = lastResponseNo / 100;
//								String comment = String.format("%dコメゲット", lastResponseNo+1);
								NicoAlertRequestUtil util = new NicoAlertRequestUtil();
								util.setCookieString(cookie);
								String postKey = util.getPostKey(params.get(THREAD), block.toString());
								String chatXml =
										String.format(
												"<chat thread=\"%s\" ticket=\"%s\" vpos=\"%s\" postkey=\"%s\" user_id=\"%s\" premium=\"%s\" mail=\"%s\">%s</chat>\0",
												params.get(THREAD),
												ticket,
												vpos,
												postKey,
												params.get(USER_ID),
												params.get(PREMIUM),
												mail,
												StringUtil.htmlescape(comment)
										);
								out.write(chatXml);
						        out.flush();
							}
							break;
						}
					}
				}
				i++;
				if (i > 100)
					break;	//永久ループ防止
				Thread.sleep(10);
			}
		} catch (IOException e) {
			System.err.println("parameters");
			for (Map.Entry<String, String> entry : params.entrySet()) {
				System.err.println(entry.getKey() + "=" + entry.getValue());
			}
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				if (br != null)
					br.close();
				if (in != null)
					in.close();
				if (sock != null)
					sock.close();
			} catch (IOException ignore) {
			}
		}
	}

	public String calcVpos(String startTime, String time) {
		return String.valueOf((Long.valueOf(time) - Long.valueOf(startTime))*100);
	}

}
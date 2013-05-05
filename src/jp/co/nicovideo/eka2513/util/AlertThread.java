package jp.co.nicovideo.eka2513.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import jp.co.nicovideo.eka2513.constants.NicoAlertConstants;
import jp.co.nicovideo.eka2513.event.NicoAlertReceivedEvent;
import jp.co.nicovideo.eka2513.event.NicoAlertReceivedEventListener;
import jp.co.nicovideo.eka2513.exception.UserException;

public class AlertThread extends Thread implements NicoAlertConstants {

	private Map<String, Object> params;

	private Socket sock;
	private String xml;
	private boolean stop;

	private NicoAlertReceivedEventListener listener;

	/** コンストラクター
	 * @throws IOException
	 * @throws UnknownHostException */
	public AlertThread(Map<String, Object> params) {
		try {
			this.params = params;
			stop = false;
		} catch (Exception e) {
			throw new UserException(e);
		}
	}

	public void exit() {
		stop = true;
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
			xml = String.format(
					"<thread thread=\"%s\" version=\"20061206\" res_from=\"-1\"/>\0",
					params.get(THREAD).toString()
					);
			out = new PrintWriter(sock.getOutputStream());
			in = sock.getInputStream();
			br = new BufferedReader(new InputStreamReader(in,"utf-8"));
			out.write(xml);
	        out.flush();

			while (true) {
				if (stop)
					break;
				if (in.available() > 0) {
					char[] data = new char[in.available()];
					br.read(data, 0, in.available());
					listener.alertReceived(new NicoAlertReceivedEvent(this, new String(data)));
				}
				Thread.sleep(10);
			}
		} catch (IOException e) {
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

	/**
	 * listenerを設定します。
	 * @param listener listener
	 */
	public void addListener(NicoAlertReceivedEventListener listener) {
	    this.listener = listener;
	}
	/**
	 * listenerを設定します。
	 * @param listener listener
	 */
	public void removeListener() {
	    this.listener = null;
	}
}
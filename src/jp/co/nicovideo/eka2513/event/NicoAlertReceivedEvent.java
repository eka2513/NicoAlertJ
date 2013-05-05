package jp.co.nicovideo.eka2513.event;
import java.util.EventObject;


public class NicoAlertReceivedEvent extends EventObject {

	private static final long serialVersionUID = 1347488693544423375L;

	private String alertXml;

	public NicoAlertReceivedEvent(Object source, String str) {
		super(source);
		alertXml = str;
	}

	/**
	 * alertXmlを取得します。
	 * @return alertXml
	 */
	public String getAlertXml() {
	    return alertXml;
	}

}

package jp.co.nicovideo.eka2513.nicoalert.event;
import java.util.EventObject;

/**
 * つくったけどいらんかった
 * @author eka2513
 *
 */
@Deprecated
public class NicoCommentReceivedEvent extends EventObject {

	private static final long serialVersionUID = 5917421742857203927L;

	private String commentXml;

	public NicoCommentReceivedEvent(Object source, String str) {
		super(source);
		commentXml = str;
	}

	/**
	 * commentXmlを取得します。
	 * @return commentXml
	 */
	public String getCommentXml() {
	    return commentXml;
	}

}

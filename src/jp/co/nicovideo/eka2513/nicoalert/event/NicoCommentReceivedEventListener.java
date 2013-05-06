package jp.co.nicovideo.eka2513.nicoalert.event;
import java.util.EventListener;


/**
 * つくったけどいらんかった
 * @author eka2513
 *
 */
@Deprecated
public interface NicoCommentReceivedEventListener extends EventListener {
	public void commentReceived(NicoCommentReceivedEvent e);
}

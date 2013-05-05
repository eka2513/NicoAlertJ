package jp.co.nicovideo.eka2513.event;
import java.util.EventListener;


public interface NicoCommentReceivedEventListener extends EventListener {
	public void commentReceived(NicoCommentReceivedEvent e);
}

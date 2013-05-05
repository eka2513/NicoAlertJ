package jp.co.nicovideo.eka2513.event;
import java.util.EventListener;


public interface NicoAlertReceivedEventListener extends EventListener {
	public void alertReceived(NicoAlertReceivedEvent e);
}

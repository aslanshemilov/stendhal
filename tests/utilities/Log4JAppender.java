package utilities;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class Log4JAppender extends AppenderSkeleton {
	
	public static final Log4JAppender INSTANCE = new Log4JAppender();
	
	private static List<String> messages = new ArrayList<String>();
	

	@Override
	protected void append(final LoggingEvent event) {
		messages.add(event.getRenderedMessage());
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}

	public static String[] getMessages() {
		return  messages.toArray(new String[messages.size()]);
	}

	public static void clear() {
		messages.clear();
	}

}

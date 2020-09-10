package c8y.devteams.agent;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Ivelin Yanev
 * @since 02.09.2020
 *
 */
public class ErrorLog {
	private boolean empty = true;

	private StringWriter sw = new StringWriter();

	private PrintWriter pw = new PrintWriter(sw, true);

	public void add(String s) {
		empty = false;
		pw.println(s);
	}

	public void add(Throwable throwable) {
		empty = false;
		throwable.printStackTrace(pw);
	}

	public boolean isEmpty() {
		return empty;
	}

	@Override
	public String toString() {
		return sw.getBuffer().toString();
	}

	public static String toString(Throwable throwable) {
		ErrorLog el = new ErrorLog();
		el.add(throwable);
		return el.toString();
	}
}

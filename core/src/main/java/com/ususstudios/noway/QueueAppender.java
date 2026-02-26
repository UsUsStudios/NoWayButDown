package com.ususstudios.noway;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** A custom implementation of the log4j appender so I can do custom stuff */
@Plugin(name = "Queue", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class QueueAppender extends AbstractAppender {
    /** Writes to the log file */
	private PrintWriter fileWriter;
	/** The working instance of itself */
    static private QueueAppender self;

    /**
     * Yeah I don't really know how this works
     * @param name ?
     * @param filter ?
     * @param layout ?
     * @param ignoreExceptions ?
    */
	protected QueueAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
		try {
			File file = new File("logs/latest.log");
			if (new File("logs/").mkdirs()) { LOGGER.info("Created logs directory"); }
			if (file.createNewFile()) { LOGGER.info("Created latest.log file"); }
			fileWriter = new PrintWriter(new FileWriter(file, true), true);
			String dateTime =   LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
			fileWriter.println(" --- New Session - " + dateTime + " --- ");
		} catch (IOException e) {
			Main.handleException(e);
		}
		self = this;
	}

    /**
     * No clue what this does either
     * @param name ?
     * @param layout ?
     * @param filter ?
     * @return ?
     */
	@PluginFactory
	public static QueueAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") Filter filter) {
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		return new QueueAppender(name, filter, layout, true);
	}

    /**
     * Prints an error to stderr and the log file
     * @param e The exception, including stack trace, that should be written
     */
	public static void printError(Exception e) {
		System.err.println("\u001B[31m" + e.getClass().getName() + ": " + e.getMessage());
		if (self.fileWriter != null) {
			self.fileWriter.print("An exception occurred: ");
			self.fileWriter.flush();
		}
		for (StackTraceElement element : e.getStackTrace()) {
			String message = "\t" + element.toString();
			System.err.println(message);
			if (self.fileWriter != null) {
				self.fileWriter.println(message.replace("\u001B[34m", "")
						.replace("\u001B[32m", "")
						.replace("\u001B[36m", "")
						.replace("\u001B[m", ""));
				self.fileWriter.flush();
			}
		}
	}

    /**
     * Writes a log to stdout and the log file
     * @param event The event to write
     */
	@Override
	public void append(LogEvent event) {
		String message = new String(getLayout().toByteArray(event));

		System.out.print(message);
		if (fileWriter != null) {
			fileWriter.print(message.replace("\u001B[34m", "")
							.replace("\u001B[32m", "")
							.replace("\u001B[36m", "")
							.replace("\u001B[m", ""));
			fileWriter.flush();
		}
	}
}

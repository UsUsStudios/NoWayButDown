package com.ususstudios.noway;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.*;

@Plugin(name = "Queue", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class QueueAppender extends AbstractAppender {
	private PrintWriter fileWriter;
	
	protected QueueAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
		try {
			File file = new File("logs/latest.log");
			if (new File("logs/").mkdirs()) { LOGGER.info("Created logs directory"); }
			if (file.createNewFile()) { LOGGER.info("Created latest.log file"); }
			fileWriter = new PrintWriter(new FileWriter(file, true), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
	
	@Override
	public void append(LogEvent event) {
		String message = new String(getLayout().toByteArray(event));
		
		System.out.print(message);
		if (fileWriter != null) {
			fileWriter.print(message);
			fileWriter.flush();
		}
	}
}

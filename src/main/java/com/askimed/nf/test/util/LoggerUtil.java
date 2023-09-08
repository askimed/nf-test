package com.askimed.nf.test.util;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class LoggerUtil {

	public static String DEFAULT_PATTERN = "%d{MMM-dd HH:mm:ss.SSS} [%t] %-5level %logger - %msg%n";

	public static void init(String root, String filename, Level level) {

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.reset();

		Logger logger = context.getLogger(root);

		PatternLayoutEncoder logEncoder = new PatternLayoutEncoder();
		logEncoder.setContext(context);
		logEncoder.setPattern(DEFAULT_PATTERN);
		logEncoder.start();

		FileAppender<ILoggingEvent> appender = new FileAppender<ILoggingEvent>();
		appender.setEncoder(logEncoder);
		appender.setContext(context);
		appender.setFile(filename);
		appender.setAppend(false);
		appender.start();

		// TODO: use RollingFileAppender to keep last X log files (like .nextflow.log)

		logger.addAppender(appender);
		logger.setAdditive(false);
		logger.setLevel(level);

	}

}

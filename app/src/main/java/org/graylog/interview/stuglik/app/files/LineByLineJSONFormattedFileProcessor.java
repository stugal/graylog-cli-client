/**
 * 
 */
package org.graylog.interview.stuglik.app.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graylog.interview.stuglik.api.config.GELFConst;
import org.graylog.interview.stuglik.app.config.ConfigConst;
import org.graylog.interview.stuglik.files.AbstractFileProcessor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public class LineByLineJSONFormattedFileProcessor extends AbstractFileProcessor {

	private static final Logger LOG = LogManager.getLogger(LineByLineJSONFormattedFileProcessor.class);

	/** JSON parser */
	private JSONParser parser;

	/** GELF Required fields */
	private String gelfVersion;
	private String gelfHost;
	private String gelfShortMessage;
	private String gelfFullMessage;
	private String gelfLevel;

	public LineByLineJSONFormattedFileProcessor() {
		super();
		this.parser = new JSONParser();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void processFile(File file) {		
		int messageCount = 0;
		LOG.info("Processing file '{}'...", file.getName());
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {

				if (!isRun()) {
					LOG.debug("File processing thread stopped...");
					break;
				}

				try {
					String gelfJson = buildGELFJson(line);	
					getGraylogDispatcher().submitMessage(gelfJson);
					messageCount++;
				} catch (ParseException e) {
					LOG.error("Parsing error.", e);
				}
			}
		} catch (IOException e) {
			LOG.fatal("Cannot open file '" + file.getName() + "'", e);
			getGraylogDispatcher().stopDispatcher(true);
			return;
		}
		
		LOG.info("Finished processing file '{}', submitted '{}' message(s)", file.getName(), messageCount);
		getGraylogDispatcher().notifyEndOfFileProcessing();
	}


	@SuppressWarnings("unchecked")
	private String buildGELFJson(String line) throws ParseException {
		// parse the line
		JSONObject json = (JSONObject) parser.parse(line);

		// create gelf json
		JSONObject gelfJson = new JSONObject();

		// add required GELF fields
		gelfJson.put(GELFConst.VERSION_FIELD, this.gelfVersion);
		gelfJson.put(GELFConst.HOST_FIELD, this.gelfHost);
		gelfJson.put(GELFConst.SHORT_MESSAGE_FIELD, this.gelfShortMessage);
		gelfJson.put(GELFConst.FULL_MESSAGE_FIELD, this.gelfFullMessage);
		gelfJson.put(GELFConst.LEVEL_FIELD, this.gelfLevel);

		// add fields from the parsed json
		json.forEach((k, v) -> {
			gelfJson.put("_" + k, v);
		});

		return gelfJson.toJSONString();
	}

	@Override
	public void init() throws Exception {
		this.gelfFullMessage = getConfigProvider().getProperty(ConfigConst.GELF_FULL_MESSAGE_PROPERTY_NAME);
		this.gelfHost = getConfigProvider().getProperty(ConfigConst.GELF_HOST_PROPERTY_NAME);
		this.gelfLevel = getConfigProvider().getProperty(ConfigConst.GELF_LEVEL_PROPERTY_NAME);
		this.gelfShortMessage = getConfigProvider().getProperty(ConfigConst.GELF_SHORT_MESSAGE_PROPERTY_NAME);
		this.gelfVersion = getConfigProvider().getProperty(ConfigConst.GELF_VERSION_PROPERTY_NAME);		
	}
}

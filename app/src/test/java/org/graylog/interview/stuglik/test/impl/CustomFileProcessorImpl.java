package org.graylog.interview.stuglik.test.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.graylog.interview.stuglik.files.AbstractFileProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Szymon Stuglik <hi@szymonstuglik.com>
 *
 */
public class CustomFileProcessorImpl extends AbstractFileProcessor {

	@SuppressWarnings("unchecked")
	@Override
	protected void processFile(File file) {
		ObjectMapper mapper = new ObjectMapper();
		try (InputStream is = new FileInputStream(file)) {
			try {
				CustomMessage msg = (CustomMessage) mapper.readValue(is, CustomMessage.class);
				getGraylogDispatcher().submitMessage(msg);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		getGraylogDispatcher().notifyEndOfFileProcessing();
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub

	}

}

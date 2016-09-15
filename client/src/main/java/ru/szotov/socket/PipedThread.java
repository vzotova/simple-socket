package ru.szotov.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for pipe input stream to output
 * 
 * @author szotov
 *
 */
public class PipedThread extends Thread {

	private static final Logger LOG = LogManager.getLogger(PipedThread.class);
	
	private InputStream _input;
	private OutputStream _output;
	
	/**
	 * @param input
	 *            input stream
	 * @param output
	 *            output stream
	 */
	public PipedThread(InputStream input, OutputStream output) {
		_input = input;
		_output = output;
	}

	/**
	 * Write data from input stream to output
	 */
	@Override
	public void run() {
		int size = 0;
		byte[] buffer = new byte[1024];
		try {
			while (!isInterrupted() &&
					(size = _input.read(buffer)) != -1) {
				_output.write(buffer, 0, size);
			}
		} catch (IOException e) {
			LOG.error("Error while reading stream", e);
		}
	}
}

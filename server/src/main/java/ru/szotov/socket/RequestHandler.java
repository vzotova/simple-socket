package ru.szotov.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Request handler
 * 
 * @author szotov
 *
 */
public class RequestHandler extends Thread implements AutoCloseable {

	private static final Logger LOG = LogManager.getLogger(RequestHandler.class);

	private Socket _socket;
	
	/**
	 * @param socket
	 *            {@link Socket}
	 */
	public RequestHandler(Socket socket) {
		_socket = socket;
	}
	
	/**
	 * Reads input, parse number, decomposes into prime factors and then sends
	 * it to the output
	 */
	@Override
	public void run() {
		try {
			LOG.info("Starting session for [{}]", _socket.getInetAddress());
			listen(_socket);
		} catch (Exception e) {
			LOG.error("Error while listening socket", e);
		} finally {		
			close();
		}
	}

	private void listen(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		
		String line = reader.readLine();
		while (!isInterrupted() &&
				socket.isConnected() && 
				!socket.isClosed() && 
				line != null) {
			Long number = Utils.parseLong(line);
			if (number != null) {
				List<Long> primeFactors = Utils.decompose(number);
				sendResponse(writer, number, primeFactors);	
			} else {
				sendBadResponse(writer, line);
			}
			line = reader.readLine();
		}
	} 
	
	private void sendBadResponse(PrintWriter writer, String line) {
		writer.println(String.format("Line [%s] is not an integer", line));
		writer.flush();
	}

	private void sendResponse(PrintWriter writer, long number, List<Long> primeFactors) {
		writer.println(
				String.format("Factors for number [%d] is %s", number, primeFactors));
		writer.flush();
	}

	@Override
	public synchronized void close() {		
		if (_socket != null && !_socket.isClosed()) {
			try {
				_socket.close();
			} catch (IOException e) {
				LOG.error("Error while closing session", e);
			}
		}
		LOG.info("Connection closed");
	}

}

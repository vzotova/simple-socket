package ru.szotov.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.szotov.socket.Listener;
import ru.szotov.socket.Server;

/**
 * Tests for {@link Listener} class
 * 
 * @author szotov
 *
 */
public class ServerTest {

	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 0;
	private static final int MAX_SESSIONS = 2;

	private int _port;
	private Socket _firstSocket;
	private Socket _secondSocket;
	
	/**
	 * Initializing
	 * 
	 * @throws IOException
	 */
	@Before
	public void initialize() throws IOException {
		Server.main(String.valueOf(DEFAULT_PORT), String.valueOf(MAX_SESSIONS));
		Listener listener = Server.getListener();
		_port = listener.getLocalPort();
		while (_port == 0) {
			_port = listener.getLocalPort();
		}
		_firstSocket = getSocket();
		_secondSocket = getSocket();
	}

	private Socket getSocket() throws IOException {
		return new Socket(DEFAULT_HOST, _port);
	}

	/**
	 * Test working server with multiple connections
	 * 
	 * @throws IOException
	 */
	@Test
	public void testServer() throws IOException {
		BufferedReader reader = getReader(_firstSocket);
		PrintWriter writer = getWriter(_firstSocket);
		assertTrue(_firstSocket.isConnected());
		writer.println("10");
		writer.flush();
		assertEquals("Factors for number [10] is [2, 5]", reader.readLine());
		assertTrue(_secondSocket.isConnected());
		reader = getReader(_secondSocket);
		writer = getWriter(_secondSocket);
		writer.println("sdfsdf");
		writer.flush();
		assertEquals("Line [sdfsdf] is not an integer", reader.readLine());
	}

	private PrintWriter getWriter(Socket socket) throws IOException {
		return new PrintWriter(socket.getOutputStream());
	}

	private BufferedReader getReader(Socket socket) throws IOException {
		return new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	/**
	 * Close resources
	 * 
	 * @throws IOException
	 */
	@After
	public void close() throws IOException {
		_firstSocket.close();
		_secondSocket.close();
	}
}

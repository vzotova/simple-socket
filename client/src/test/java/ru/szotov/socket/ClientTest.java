package ru.szotov.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.szotov.socket.Client;

/**
 * Tests for {@link Client} class
 * 
 * @author szotov
 *
 */
public class ClientTest {

	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 0;
	
	private int _port;
	private ServerSocket _server;
	private Client _client;
	private Socket _socket;
	
	/**
	 * Initializing
	 * 
	 * @throws IOException
	 */
	@Before
	public void initialize() throws IOException {
		_server = new ServerSocket(DEFAULT_PORT);
		_port = _server.getLocalPort();
		Runnable runnable = () -> {
			try {
				_socket = _server.accept();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
		new Thread(runnable).start();
	}
	
	/**
	 * Test socket client
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 1000)
	public void testClient() throws IOException {
		String expected = "test";
		String expectedNewLine = expected + System.lineSeparator();
		System.setIn(new ByteArrayInputStream(expectedNewLine.getBytes()));
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));

		_client = new Client(DEFAULT_HOST, _port);
		_client.startSession();
		assertTrue(_socket.isConnected());
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(_socket.getInputStream()));
		assertEquals(expected, reader.readLine());

		output.reset();
		PrintWriter writer = new PrintWriter(_socket.getOutputStream());
		writer.println(expected);
		writer.flush();
		while(output.toString().isEmpty()) {
		}
		assertEquals(expectedNewLine, output.toString());
	}
	
	/**
	 * Test main method
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	@Test
	public void testMain() throws UnknownHostException, IOException {
		Client.main(DEFAULT_HOST, String.valueOf(_port));
		assertTrue(_socket.isConnected());
	}
	
	/**
	 * Close resources
	 * 
	 * @throws IOException
	 */
	@After
	public void close() throws IOException {
		if (_client != null) {
			_client.close();
		}
		if (_socket != null && !_socket.isClosed()) {
			_socket.close();
		}
		_server.close();
	}
	
}

package ru.szotov.socket;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ru.szotov.socket.Listener;
import ru.szotov.socket.RequestHandler;

/**
 * Tests for {@link Listener} class
 * 
 * @author szotov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ListenerTest {

	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 0;
	private static final int MAX_SESSIONS = 2;

	@Mock
	private RequestHandler _handler;
	@Mock
	private ExecutorService _threadPool;
	
	private Listener _listener;
	private int _port;
	private Socket _firstSocket;
	private Socket _secondSocket;
	
	/**
	 * Initializing
	 */
	@Before
	public void initialize() {
		_listener = new Listener(DEFAULT_PORT, MAX_SESSIONS) {
			@Override
			protected RequestHandler getRequestHandler(Socket socket) {
				return _handler;
			}
			@Override
			protected ExecutorService getThreadPool(int maxSessions) {
				return _threadPool;
			}
		};
		_listener.start();
		_port = _listener.getLocalPort();
		while (_port == 0) {
			_port = _listener.getLocalPort();
		}
	}

	private Socket getSocket() throws IOException {
		return new Socket(DEFAULT_HOST, _port);
	}
	
	/**
	 * Test for two simultaneously connections
	 * 
	 * @throws IOException
	 */
	@Test
	public void testTwoConnections() throws IOException {
		_firstSocket = getSocket();
		assertTrue(_firstSocket.isConnected());
		_secondSocket = getSocket();
		assertTrue(_secondSocket.isConnected());
		verify(_threadPool, times(2)).execute(_handler);
	}
	
	/**
	 * Close resources
	 * 
	 * @throws IOException
	 */
	@After
	public void close() throws IOException {
		if (_firstSocket != null) {
			_firstSocket.close();
		}
		if (_secondSocket != null) {
			_secondSocket.close();
		}
		_listener.close();
	}
}

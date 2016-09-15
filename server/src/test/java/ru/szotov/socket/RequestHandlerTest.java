package ru.szotov.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ru.szotov.socket.RequestHandler;

/**
 * Tests for {@link RequestHandler} class
 * 
 * @author szotov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestHandlerTest {

	@Mock
	private Socket _socket;
	
	private RequestHandler _handler;
	private BufferedReader _reader;
	
	/**
	 * Initializing
	 * 
	 * @throws IOException
	 */
	@Before
	public void initialize() throws IOException {
		_handler = new RequestHandler(_socket);
		when(_socket.isClosed()).thenReturn(false);
		when(_socket.isConnected()).thenReturn(true);
		
		PipedInputStream pipeInput = new PipedInputStream();
	    _reader = new BufferedReader(new InputStreamReader(pipeInput));
	    BufferedOutputStream output = new BufferedOutputStream(
	            new PipedOutputStream(pipeInput));
	    when(_socket.getOutputStream()).thenReturn(output);
	}

	/**
	 * Test listening socket
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRun() throws IOException {
		when(_socket.getInputStream())
			.thenReturn(new ByteArrayInputStream("10\n".getBytes()));
		_handler.run();
		assertEquals("Factors for number [10] is [2, 5]", _reader.readLine());
		assertFalse(_reader.ready());
		verify(_socket, times(1)).close();
		
		when(_socket.getInputStream())
		.thenReturn(new ByteArrayInputStream("safsfd\n".getBytes()));
		_handler.run();
		assertEquals("Line [safsfd] is not an integer", _reader.readLine());
		assertFalse(_reader.ready());
		
		when(_socket.getInputStream())
			.thenReturn(new ByteArrayInputStream("-10\n+5\n".getBytes()));
		_handler.run();
		assertEquals("Factors for number [-10] is [-2, 5]", _reader.readLine());
		assertEquals("Factors for number [5] is [5]", _reader.readLine());
		assertFalse(_reader.ready());
	}
	
	/**
	 * Close resources
	 * 
	 * @throws IOException
	 */
	@After
	public void close() throws IOException {
		_reader.close();
	}
}

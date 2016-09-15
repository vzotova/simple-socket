package ru.szotov.socket;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

import ru.szotov.socket.PipedThread;

/**
 * Tests for {@link PipedThread} class
 * 
 * @author szotov
 *
 */
public class PipedThreadTest {
	
	/**
	 * Test connecting streams
	 */
	@Test
	public void testRun() {
		String expected = "test";
		ByteArrayOutputStream output = new ByteArrayOutputStream ();
		ByteArrayInputStream input = new ByteArrayInputStream(expected.getBytes());  
		PipedThread thread = new PipedThread(input, output);
		thread.run();
		assertEquals(expected, output.toString());
	}
	
}

package ru.szotov.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ru.szotov.socket.Utils;

/**
 * Tests for {@link Utils} class
 * 
 * @author szotov
 *
 */
public class UtilsTest {

	/**
	 * Test parsing long
	 */
	@Test
	public void testParseLong() {
		assertNull(Utils.parseLong("sdfasdf"));
		assertNull(Utils.parseLong("10.1"));
		assertNull(Utils.parseLong("10,1"));
		assertNull(Utils.parseLong(""));
		assertNull(Utils.parseLong(null));
		assertEquals(Long.valueOf(10L), Utils.parseLong("10"));
		assertEquals(Long.valueOf(-10L), Utils.parseLong("-10"));
		assertEquals(Long.valueOf(10L), Utils.parseLong("+10"));
	}
	
	/**
	 * Test checking integer
	 */
	@Test
	public void testIsInteger() {
		assertFalse(Utils.isInteger("asdasd"));
		assertFalse(Utils.isInteger("10.1"));
		assertFalse(Utils.isInteger("10,1"));
		assertFalse(Utils.isInteger(""));
		assertFalse(Utils.isInteger(null));
		assertTrue(Utils.isInteger("10"));
		assertTrue(Utils.isInteger("-10"));
		assertTrue(Utils.isInteger("+10"));
	}
	
	/**
	 * Test calculating prime factors
	 */
	@Test
	public void testDecompose() {
		List<Long> factors = new ArrayList<>();
		assertEquals(factors, Utils.decompose(1L));
		assertEquals(factors, Utils.decompose(-1L));
		factors.add(2L);
		assertEquals(factors, Utils.decompose(2L));
		
		factors.clear();
		factors.add(3L);
		factors.add(3L);
		factors.add(5L);
		factors.add(7L);
		factors.add(101L);
		assertEquals(factors, Utils.decompose(31815L));
		
		factors.clear();
		factors.add(-2L);
		factors.add(5L);
		assertEquals(factors, Utils.decompose(-10L));
	}
}

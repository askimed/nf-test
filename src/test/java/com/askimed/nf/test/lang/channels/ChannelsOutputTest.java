package com.askimed.nf.test.lang.channels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.askimed.nf.test.lang.channels.Channels;

public class ChannelsOutputTest {

	@Test
	public void testIntegerChannel() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/integer.json"), false);
		assertTrue(out.containsKey("outputCh"));

		List<Integer> expected = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1);
		assertEquals(expected, (List) out.get("outputCh"));
	}

	@Test
	public void testIntegerChannelAutoSort() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/integer.json"), true);
		assertTrue(out.containsKey("outputCh"));

		List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		assertEquals(expected, (List) out.get("outputCh"));
	}

	@Test
	public void testStringChannel() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/string.json"), false);
		assertTrue(out.containsKey("outputCh"));

		List<String> expected = Arrays.asList("z", "y", "x", "c", "b", "a");
		assertEquals(expected, (List) out.get("outputCh"));
	}

	@Test
	public void testStringChannelAutoSort() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/string.json"), true);
		assertTrue(out.containsKey("outputCh"));
		
		List<String> expected = Arrays.asList("a","b","c","x","y","z");
		assertEquals(expected, (List) out.get("outputCh"));
	}

	@Test
	public void testTuples() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/tuples.json"), true);
		assertTrue(out.containsKey("outputCh"));

		// List<Integer> expected = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1);
		// assertEquals(expected, (List) out.get("outputCh"));
	}
	
	@Test
	public void testNestedObjects() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/nested-objects.json"), true);
		assertTrue(out.containsKey("outputCh"));		
		
		Channels out2 = new Channels();
		out2.loadFromFile(new File("test-data/channels/nested-objects-random.json"), true);
		assertTrue(out2.containsKey("outputCh"));

		assertEquals((List) out.get("outputCh"), (List) out2.get("outputCh"));
	}
	
	@Test
	public void testNestedObjectsWithoutSorting() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/nested-objects.json"), false);
		assertTrue(out.containsKey("outputCh"));		
		
		Channels out2 = new Channels();
		out2.loadFromFile(new File("test-data/channels/nested-objects-random.json"), false);
		assertTrue(out2.containsKey("outputCh"));

		assertNotEquals((List) out.get("outputCh"), (List) out2.get("outputCh"));
	}
	
	
	@Test
	public void testObjects() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/objects.json"), true);
		assertTrue(out.containsKey("outputCh"));		

		Channels out2 = new Channels();
		out2.loadFromFile(new File("test-data/channels/objects-random.json"), true);
		assertTrue(out2.containsKey("outputCh"));

		assertEquals((List) out.get("outputCh"), (List) out2.get("outputCh"));
	}

	@Test
	public void testObjectsWithoutSorting() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/objects.json"), false);
		assertTrue(out.containsKey("outputCh"));		
		
		Channels out2 = new Channels();
		out2.loadFromFile(new File("test-data/channels/objects-random.json"), false);
		assertTrue(out2.containsKey("outputCh"));

		assertNotEquals((List) out.get("outputCh"), (List) out2.get("outputCh"));
	}

	@Test
	public void testObjectsList() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/objects-list.json"), true);
		assertTrue(out.containsKey("outputCh"));
		
		Channels out2 = new Channels();
		out2.loadFromFile(new File("test-data/channels/objects-list-random.json"), true);
		assertTrue(out2.containsKey("outputCh"));

		assertEquals((List) out.get("outputCh"), (List) out2.get("outputCh"));
	}

	@Test
	public void testObjectsListWithoutSorting() {
		Channels out = new Channels();
		out.loadFromFile(new File("test-data/channels/objects-list.json"), false);
		assertTrue(out.containsKey("outputCh"));
		
		Channels out2 = new Channels();
		out2.loadFromFile(new File("test-data/channels/objects-list-random.json"), false);
		assertTrue(out2.containsKey("outputCh"));

		assertNotEquals((List) out.get("outputCh"), (List) out2.get("outputCh"));
	}
	
}

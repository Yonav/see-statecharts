package com.yakindu.sct.se.test.unit;

import org.junit.Test;

import com.yakindu.sct.se.collection.ImmutableList;

public class ImmutableListTest {

	@Test
	public void create_noValues() {
		// arrange
		// act
		ImmutableList<String> actual = ImmutableList.create();
		// assert
		assert actual.isEmpty();
		assert actual.getNext() == null;
	}
	
	@Test
	public void create_oneValue() {
		// arrange
		String one = "1";
		// act
		ImmutableList<String> actual = ImmutableList.create(one);
		// assert
		assert !actual.isEmpty();
		assert actual.getValue().equals(one);
		assert actual.getNext().isEmpty();
	}
	
	@Test
	public void create_multipleValues() {
		// arrange
		String one = "1";
		String two = "2";
		String three = "3";
		// act
		ImmutableList<String> actual = ImmutableList.create(one, two, three);
		// assert
		assert !actual.isEmpty();
		assert three.equals(actual.getValue());
		assert two.equals(actual.getNext().getValue());
		assert one.equals(actual.getNext().getNext().getValue());
		assert actual.getNext().getNext().getNext().isEmpty();
	}
	
	@Test
	public void create_null() {
		// arrange
		// act
		ImmutableList<String> actual = ImmutableList.create(null);
		// assert
		assert actual.isEmpty();
		assert actual.getNext() == null;
	}
	
	@Test
	public void createReverse_noValues() {
		// arrange
		// act
		ImmutableList<String> actual = ImmutableList.createReverse();
		// assert
		assert actual.isEmpty();
		assert actual.getNext() == null;
	}
	
	@Test
	public void createReverse_oneValue() {
		// arrange
		String one = "1";
		// act
		ImmutableList<String> actual = ImmutableList.createReverse(one);
		// assert
		assert !actual.isEmpty();
		assert actual.getValue().equals(one);
		assert actual.getNext().isEmpty();
	}
	
	@Test
	public void createReverse_multipleValues() {
		// arrange
		String one = "1";
		String two = "2";
		String three = "3";
		// act
		ImmutableList<String> actual = ImmutableList.createReverse(one, two, three);
		// assert
		assert !actual.isEmpty();
		assert one.equals(actual.getValue());
		assert two.equals(actual.getNext().getValue());
		assert three.equals(actual.getNext().getNext().getValue());
		assert actual.getNext().getNext().getNext().isEmpty();
	}
	
	@Test
	public void createReverse_null() {
		// arrange
		// act
		ImmutableList<String> actual = ImmutableList.createReverse(null);
		// assert
		assert actual.isEmpty();
		assert actual.getNext() == null;
	}
	
	@Test
	public void prepend_null() {
		// arrange
		String one = "1";
		String _null = null;
		ImmutableList<String> toPrepend = ImmutableList.create(one);
		// act
		ImmutableList<String> actual = toPrepend.prepend(_null);
		//assert
		assert actual.equals(toPrepend);
	}
	
	@Test
	public void prepend_one() {
		// arrange
		String one = "1";
		String two = "2";
		ImmutableList<String> toPrepend = ImmutableList.create(one);
		// act
		ImmutableList<String> actual = toPrepend.prepend(two);
		//assert
		assert two.equals(actual.getValue());
		assert toPrepend.equals(actual.getNext());
	}
	
	@Test
	public void prepend_two() {
		// arrange
		String one = "1";
		String two = "2";
		String three = "3";

		ImmutableList<String> toPrepend = ImmutableList.create(one);
		// act
		ImmutableList<String> actual = toPrepend.prepend(two, three);
		//assert
		assert three.equals(actual.getValue());
		assert two.equals(actual.getNext().getValue());
		assert toPrepend.equals(actual.getNext().getNext());
	}
	
	@Test
	public void prependReverse_null() {
		// arrange
		String one = "1";
		String _null = null;
		ImmutableList<String> toPrepend = ImmutableList.create(one);
		// act
		ImmutableList<String> actual = toPrepend.prependReverse(_null);
		//assert
		assert actual.equals(toPrepend);
	}
	
	@Test
	public void prependReverse_one() {
		// arrange
		String one = "1";
		String two = "2";
		ImmutableList<String> toPrepend = ImmutableList.create(one);
		// act
		ImmutableList<String> actual = toPrepend.prependReverse(two);
		//assert
		assert two.equals(actual.getValue());
		assert toPrepend.equals(actual.getNext());
	}
	
	@Test
	public void prependReverse_two() {
		// arrange
		String one = "1";
		String two = "2";
		String three = "3";

		ImmutableList<String> toPrepend = ImmutableList.create(one);
		// act
		ImmutableList<String> actual = toPrepend.prependReverse(two, three);
		//assert
		assert two.equals(actual.getValue());
		assert three.equals(actual.getNext().getValue());
		assert toPrepend.equals(actual.getNext().getNext());
	}

}

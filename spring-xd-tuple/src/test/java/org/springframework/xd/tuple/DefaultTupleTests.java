/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.xd.tuple;

import static org.junit.Assert.*;
import static org.springframework.xd.tuple.TupleBuilder.tuple;
import static org.hamcrest.Matchers.*;

import java.awt.Color;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

public class DefaultTupleTests {


	@Test(expected=IllegalArgumentException.class)
	public void nullForNameArray() {
		List<Object> values = new ArrayList<Object>();
		values.add("bar");
		tuple().ofNamesAndValues(null, values);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void nullForValueArray() {
		List<String> names = new ArrayList<String>();
		names.add("foo");
		tuple().ofNamesAndValues(names, null);
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void notEqualNumberOfNamesAndValues() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Field names must be same length as values: names=[foo, oof], values=[bar]");
		List<String> names = new ArrayList<String>();
		names.add("foo");
		names.add("oof");
		List<Object> values = new ArrayList<Object>();
		values.add("bar");
		tuple().ofNamesAndValues(names, values);
	}
	
	@Test
	public void accessNonExistentEntry() {
		//thrown.expect(IllegalArgumentException.class);
		//thrown.expectMessage("Cannot access field [does-not-exist] from [foo]");
		Tuple tuple = TupleBuilder.tuple().of("foo", "bar");
		Object v = tuple.getValue("does-not-exist");
		assertThat(v, nullValue());
	}
	
	@Test
	public void singleEntry() {
		Tuple tuple = TupleBuilder.tuple().of("foo", "bar");
		assertThat(tuple.size(), equalTo(1));
		List<String> names = tuple.getFieldNames();
		assertThat(names.get(0), equalTo("foo"));
		assertThat((String)tuple.getValue("foo"), equalTo("bar"));
		assertThat(tuple.hasFieldName("foo"), equalTo(true));
		//assertThat(tuple.get("foo").asString(), equalTo("bar"));
	}
	
	
	
	@Test
	public void testId() {
		Tuple tuple1 = TupleBuilder.tuple().of("foo", "bar");
		assertThat(tuple1.getId(), notNullValue());
		Tuple tuple2 = TupleBuilder.tuple().of("foo", "bar");
		assertNotSame(tuple1.getId(), tuple2.getId());
	}
	
	@Test
	public void testTimestamp() throws Exception {
		Tuple tuple1 = TupleBuilder.tuple().of("foo", "bar");
		assertThat(tuple1.getTimestamp(), notNullValue());
		Thread.sleep(100L);
		Tuple tuple2 = TupleBuilder.tuple().of("foo", "bar");
		assertNotSame(tuple1.getTimestamp(), tuple2.getTimestamp());
	}
	

	@Test 
	public void twoEntries() {
		Tuple tuple = TupleBuilder.tuple().of("up", "down", "charm", "strange");
		assertTwoEntries(tuple);
	}

	/**
	 * @param tuple
	 */
	private void assertTwoEntries(Tuple tuple) {
		assertThat(tuple.size(), equalTo(2));
		assertThat(tuple.getFieldNames().get(0), equalTo("up"));
		assertThat(tuple.getFieldNames().get(1), equalTo("charm"));
		assertThat((String)tuple.getValue("up"), equalTo("down"));
		assertThat((String)tuple.getValue("charm"), equalTo("strange"));
	}
	
	@Test 
	public void threeEntries() {
		Tuple tuple = TupleBuilder.tuple().of("up", 1, "charm", 2, "top", 3 );
		assertThat(tuple.size(), equalTo(3));
		assertThat(tuple.getFieldNames().get(0), equalTo("up"));
		assertThat(tuple.getFieldNames().get(1), equalTo("charm"));
		assertThat(tuple.getFieldNames().get(2), equalTo("top"));
		// access by name
		assertThat((Integer)tuple.getValue("up"), equalTo(1));
		assertThat((Integer)tuple.getValue("charm"), equalTo(2));
		assertThat((Integer)tuple.getValue("top"), equalTo(3));
		// access by position
		assertThat((Integer)tuple.getValue(0), equalTo(1));
		assertThat((Integer)tuple.getValue(1), equalTo(2));
		assertThat((Integer)tuple.getValue(2), equalTo(3));
		// access from separate collection
		List<Object> values = tuple.getValues();
		assertThat((Integer)values.get(0), equalTo(1));
		assertThat((Integer)values.get(1), equalTo(2));
		assertThat((Integer)values.get(2), equalTo(3));
	}
	
	@Test 
	public void fourEntries() {
		Tuple tuple = TupleBuilder.tuple().of("up", 1, "charm", 2, "top", 3, "e", 4);
		assertThat(tuple.size(), equalTo(4));
		assertThat(tuple.getFieldNames().get(0), equalTo("up"));
		assertThat(tuple.getFieldNames().get(1), equalTo("charm"));
		assertThat(tuple.getFieldNames().get(2), equalTo("top"));
		assertThat(tuple.getFieldNames().get(3), equalTo("e"));
		// access by name
		assertThat((Integer)tuple.getValue("up"), equalTo(1));
		assertThat((Integer)tuple.getValue("charm"), equalTo(2));
		assertThat((Integer)tuple.getValue("top"), equalTo(3));
		assertThat((Integer)tuple.getValue("e"), equalTo(4));
		// access by position
		assertThat((Integer)tuple.getValue(0), equalTo(1));
		assertThat((Integer)tuple.getValue(1), equalTo(2));
		assertThat((Integer)tuple.getValue(2), equalTo(3));
		assertThat((Integer)tuple.getValue(3), equalTo(4));
	}
	
	@Test
	public void getValue() {
		Tuple tuple = TupleBuilder.tuple().of("up", 1, "charm", 2.0, "top", true );
		assertThat( tuple.getValue(0, Integer.class), equalTo(1));
		assertThat( tuple.getValue(0, String.class), equalTo("1"));
		assertThat( tuple.getValue(1, Double.class), equalTo(2.0D));
		assertThat( tuple.getValue(2, Boolean.class), equalTo(true));
		assertThat( tuple.getValue(2, String.class), equalTo("true"));
		
		assertThat( tuple.getValue("up", Integer.class), equalTo(1));
		assertThat( tuple.getValue("up", String.class), equalTo("1"));
		assertThat( tuple.getValue("charm", Double.class), equalTo(2.0D));
		assertThat( tuple.getValue("top", Boolean.class), equalTo(true));
		assertThat( tuple.getValue("top", String.class), equalTo("true"));
	}
	
	@Test
	public void testPrimitiveGetters() {
		Tuple tuple = TupleBuilder.tuple().of("up", "down", "charm", 2.0, "top", true );
		assertThat( tuple.getBoolean("top"), equalTo(true));
		assertThat( tuple.getBoolean(2), equalTo(true));
		assertThat( tuple.getBoolean("up", "down"), equalTo(true));
	}
	
	@Test
	public void testToString() {
		Tuple tuple = TupleBuilder.tuple().put("up", "down")
				 .put("charm", "strange")
				 .build();
		
		String tupleString = tuple.toString();
		assertThat(tupleString, containsString("DefaultTuple [names=[up, charm], values=[down, strange],"));
		assertThat(tupleString, containsString("id="));
		assertThat(tupleString, containsString("timestamp="));		
	}
	
	@Test
	public void testPutApi() {
		TupleBuilder builder = TupleBuilder.tuple();
		Tuple tuple = builder.put("up", "down")
							 .put("charm", "strange")
							 .build();
		assertTwoEntries(tuple);
	}
	
	@Test
	public void testEqualsAndHashCodeSunnyDay() {
		Tuple tuple1 = TupleBuilder.tuple().of("up", 1, "charm", 2, "top", 3 );
		Tuple tuple2 = TupleBuilder.tuple().of("up", 1, "charm", 2, "top", 3 );
		assertThat(tuple1, equalTo(tuple2));	
		assertThat(tuple1.hashCode(), equalTo(tuple2.hashCode()));
		assertThat(tuple1, not(sameInstance(tuple2)));
	}
	
	@Test
	public void testEqualsAndHashFailureCases() {
		Tuple tuple1 = TupleBuilder.tuple().of("up", 1, "charm", 2, "top", 3 );
		Tuple tuple2 = TupleBuilder.tuple().of("up", 2, "charm", 3, "top", 4 );
		assertThat(tuple1, not(equalTo((tuple2))));			
		assertThat(tuple1.hashCode(), not(equalTo(tuple2.hashCode())));
		
		tuple1 = TupleBuilder.tuple().of("up", 1, "charm", 2, "top", 3 );
		tuple2 = TupleBuilder.tuple().of("top", 1, "charm", 2, "up", 3 );
		assertThat(tuple1, not(equalTo((tuple2))));			
		assertThat(tuple1.hashCode(), not(equalTo(tuple2.hashCode())));
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testGetFieldTypes() {
		Tuple tuple = TupleBuilder.tuple().of("up", 1, "charm", 2, "top", 3 );
		Class[] expectedTypes = new Class[] { Integer.class, Integer.class, Integer.class };		
		assertThat(tuple.getFieldTypes(), equalTo(Arrays.asList(expectedTypes)));
		
		tuple = TupleBuilder.tuple().of("up", 1, "charm", 2.0f, "top", "bottom" );
		expectedTypes = new Class[] { Integer.class, Float.class, String.class };
		assertThat(tuple.getFieldTypes(), equalTo(Arrays.asList(expectedTypes)));
		
		tuple = TupleBuilder.tuple().of("up", 1, "charm", 2.0, "top", true );
		expectedTypes = new Class[] { Integer.class, Double.class, Boolean.class };
		assertThat(tuple.getFieldTypes(), equalTo(Arrays.asList(expectedTypes)));
		
		
	}
	
	@Test
	public void testGetString() {
		//test conversions of string, int, and float.
		Tuple tuple = TupleBuilder.tuple().of("up", "down", "charm", 2, "top", 2.0f );
		assertThat(tuple.getString("up"), equalTo("down"));
		assertThat(tuple.getString("charm"), equalTo("2"));
		assertThat(tuple.getString("top"), equalTo("2.0"));		
	}
	
	@Test
	public void testGetNullValue() {
		Tuple tuple = tuple().of("foo", null);
		//non primitive types will return null
		assertThat(tuple.getString("foo"), nullValue());
		assertThat(tuple.getBigDecimal("foo"), nullValue());		
		assertThat(tuple.getDate("foo"), nullValue());
		
		//primitive types will return default values
		assertThat(tuple.getChar("foo"), equalTo('\u0000'));
		assertThat(tuple.getBoolean("foo"), equalTo(false));
		byte b = 0;
		assertThat(tuple.getByte("foo"), equalTo(b));
		short s = 0;
		assertThat(tuple.getShort("foo"), equalTo(s));
		assertThat(tuple.getInt("foo"), equalTo(0));
		assertThat(tuple.getLong("foo"), equalTo(0L));
		assertThat(tuple.getFloat("foo"), equalTo(0f));
		assertThat(tuple.getDouble("foo"), equalTo(0d));

		

		
	}
	

	
	@Test
	public void testGetStringThatFails() {
		Tuple tuple = TupleBuilder.tuple().of("up", "down", "charm", 2, "top", 2.0f, "black", Color.black );
		thrown.expect(ConverterNotFoundException.class);
		thrown.expectMessage("No converter found capable of converting from type java.awt.Color to type java.lang.String");
		assertThat(tuple.getString("black"), equalTo("omg"));
	}
	
	@Test
	public void testSelection() {
		Tuple tuple = tuple().put("red", "rot")
				.put("brown", "braun")
				.put("blue", "blau")
				.put("yellow", "gelb")
				.put("beige", "beige")
				.build();
		Tuple selectedTuple = tuple.select("?[key.startsWith('b')]");
		assertThat(selectedTuple.size(), equalTo(3));
		
		selectedTuple = tuple.select("^[key.startsWith('b')]");
		assertThat(selectedTuple.size(), equalTo(1));
		assertThat(selectedTuple.getFieldNames().get(0), equalTo("brown"));
		assertThat(selectedTuple.getString(0), equalTo("braun"));

		selectedTuple = tuple.select("?[value.length() < 4]");
		assertThat(selectedTuple.size(), equalTo(1));
		assertThat(selectedTuple.getFieldNames().get(0), equalTo("red"));
		assertThat(selectedTuple.getString(0), equalTo("rot"));
	}
	
	@Test
	public void testReadByteWithDefault() {
		// with a value
		byte b = 1;
		Tuple t = tuple().of("foo", b);
		byte defaultByte = 2;
		assertTrue(t.getByte(0, defaultByte) == b);	
		assertTrue(t.getByte("foo", defaultByte) == b);		
		assertTrue(t.getByte("bar", defaultByte) == defaultByte);	
		
		// with a null value
		t = tuple().of("foo", null);
		assertTrue(t.getByte(0, defaultByte) == defaultByte);
		assertTrue(t.getByte("foo", defaultByte) == defaultByte);		
		assertTrue(t.getByte("bar", defaultByte) == defaultByte);
		
	}
	
	@Test
	public void testReadShortWithDefault() {
		// with a value
		short s = 1;
		Tuple t = tuple().of("foo", s);
		short defaultShort = 2;
		assertTrue(t.getShort(0, defaultShort) == s);	
		assertTrue(t.getShort("foo", defaultShort) == s);		
		assertTrue(t.getShort("bar", defaultShort) == defaultShort);	
		
		// with a null value
		t = tuple().of("foo", null);
		assertTrue(t.getShort(0, defaultShort) == defaultShort);
		assertTrue(t.getShort("foo", defaultShort) == defaultShort);		
		assertTrue(t.getShort("bar", defaultShort) == defaultShort);
		
	}
	
	@Test
	public void testReadIntWithDefault() {
		// with a value
		int i = 1;
		Tuple t = tuple().of("foo", i);
		int defaultInt = 2;
		assertTrue(t.getInt(0, defaultInt) == i);	
		assertTrue(t.getInt("foo", defaultInt) == i);		
		assertTrue(t.getInt("bar", defaultInt) == defaultInt);	
		
		// with a null value
		t = tuple().of("foo", null);
		assertTrue(t.getInt(0, defaultInt) == defaultInt);
		assertTrue(t.getInt("foo", defaultInt) == defaultInt);		
		assertTrue(t.getInt("bar", defaultInt) == defaultInt);
		
	}
	
	@Test
	public void testReadLongWithDefault() {
		// with a value
		long l = 1;
		Tuple t = tuple().of("foo", l);
		int defaultLong = 2;
		assertTrue(t.getLong(0, defaultLong) == l);	
		assertTrue(t.getLong("foo", defaultLong) == l);		
		assertTrue(t.getLong("bar", defaultLong) == defaultLong);	
		
		// with a null value
		t = tuple().of("foo", null);
		assertTrue(t.getLong(0, defaultLong) == defaultLong);
		assertTrue(t.getLong("foo", defaultLong) == defaultLong);		
		assertTrue(t.getLong("bar", defaultLong) == defaultLong);
		
	}
	
	@Test
	public void testReadFloatWithDefault() {
		// with a value
		float f = 1;
		Tuple t = tuple().of("foo", f);
		float defaultFloat = 2.0f;
		assertTrue(t.getFloat(0, defaultFloat) == f);	
		assertTrue(t.getFloat("foo", defaultFloat) == f);		
		assertTrue(t.getFloat("bar", defaultFloat) == defaultFloat);	
		
		// with a null value
		t = tuple().of("foo", null);
		assertTrue(t.getFloat(0, defaultFloat) == defaultFloat);
		assertTrue(t.getFloat("foo", defaultFloat) == defaultFloat);		
		assertTrue(t.getFloat("bar", defaultFloat) == defaultFloat);
		
	}
	
	@Test
	public void testReadDoubleWithDefault() {
		// with a value
		double d = 1;
		Tuple t = tuple().of("foo", d);
		double defaultDouble = 2.0d;
		assertTrue(t.getDouble(0, defaultDouble) == d);	
		assertTrue(t.getDouble("foo", defaultDouble) == d);		
		assertTrue(t.getDouble("bar", defaultDouble) == defaultDouble);	
		
		// with a null value
		t = tuple().of("foo", null);
		assertTrue(t.getDouble(0, defaultDouble) == defaultDouble);
		assertTrue(t.getDouble("foo", defaultDouble) == defaultDouble);		
		assertTrue(t.getDouble("bar", defaultDouble) == defaultDouble);
		
	}
	
	@Test
	public void testReadBigDecimalWithDefault() {
		// with a value
		BigDecimal bd = new BigDecimal(1);
		Tuple t = tuple().of("foo", bd);
		BigDecimal defaultBigDecimal = new BigDecimal(2);
		assertTrue(t.getBigDecimal(0, defaultBigDecimal) == bd);	
		assertTrue(t.getBigDecimal("foo", defaultBigDecimal) == bd);		
		assertTrue(t.getBigDecimal("bar", defaultBigDecimal) == defaultBigDecimal);	
		
		// with a null value
		t = tuple().of("foo", null);
		assertTrue(t.getBigDecimal(0, defaultBigDecimal) == defaultBigDecimal);
		assertTrue(t.getBigDecimal("foo", defaultBigDecimal) == defaultBigDecimal);		
		assertTrue(t.getBigDecimal("bar", defaultBigDecimal) == defaultBigDecimal);
		
	}
	
	@Test
	public void testReadDateWithDefault() throws InterruptedException {
		// with a value
		Date date = new Date();
		Tuple t = tuple().of("foo", date);
		Thread.sleep(1000);
		Date defaultDate = new Date();
		assertTrue(t.getDate(0, defaultDate) == date);	
		assertTrue(t.getDate("foo", defaultDate) == date);		
		assertTrue(t.getDate("bar", defaultDate) == defaultDate);	
		
		// with a null value
		t = tuple().of("foo", null);
		assertTrue(t.getDate(0, defaultDate) == defaultDate);
		assertTrue(t.getDate("foo", defaultDate) == defaultDate);		
		assertTrue(t.getDate("bar", defaultDate) == defaultDate);
		
	}
	
	@Test
	public void testReadDateWithPattern() throws ParseException, InterruptedException {
		Tuple t = tuple().of("foo", "24-12-2013");
		Date d = t.getDateWithPattern(0, "dd-MM-yyyy");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		assertEquals(dateFormat.parse("24-12-2013"), d);
		Thread.sleep(1000);
		Date defaultDate = new Date();
		assertTrue(t.getDateWithPattern("foo", "xyz-abc", defaultDate) == defaultDate);
		assertTrue(t.getDateWithPattern(0, "xyz-abc", defaultDate) == defaultDate);
		
	}
}

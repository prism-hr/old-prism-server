package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DomainObjectTest {
	@Test
	public void shouldBeEqualIfIdIsSame() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(1);
		DomainObject<Integer> objectTwo = new MyDomainObject();
		objectTwo.setId(1);
		assertTrue(objectOne.equals(objectTwo));
		assertTrue(objectTwo.equals(objectOne));
	}

	@Test
	public void shouldNotBeEqualIfDomainObjectIdIsNull() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		DomainObject<Integer> objectTwo = new MyDomainObject();
		assertFalse(objectOne.equals(objectTwo));
		assertFalse(objectTwo.equals(objectOne));

		objectOne.setId(1);
		assertFalse(objectOne.equals(objectTwo));
		assertFalse(objectTwo.equals(objectOne));

		objectOne.setId(null);
		objectTwo.setId(1);
		assertFalse(objectOne.equals(objectTwo));
		assertFalse(objectTwo.equals(objectOne));
	}

	@Test
	public void shouldNotBeEqualIfDomainObjectIdsAreDifferent() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(1);
		DomainObject<Integer> objectTwo = new MyDomainObject();
		objectTwo.setId(2);
		assertFalse(objectOne.equals(objectTwo));
		assertFalse(objectTwo.equals(objectOne));
	}

	@Test
	public void shouldNotBeSameIfOtherIsNull() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(1);
		assertFalse(objectOne.equals(null));

	}

	@Test
	public void shouldNotBeSameIfNotSameType() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(1);
		@SuppressWarnings("serial")
		DomainObject<Integer> objectTwo = new DomainObject<Integer>() {

			@Override
			public void setId(Integer id) {
				this.id = id;
			}

			@Override
			public Integer getId() {
				return id;
			}
		};
		objectOne.setId(1);
		objectTwo.setId(1);
		assertFalse(objectOne.equals(objectTwo));

	}

	@Test
	public void shouldHaveSameHashcodeIfDomainObjectNumberSame() {
		DomainObject<Integer> objectOne = new MyDomainObject();
		objectOne.setId(1);
		DomainObject<Integer> objectTwo = new MyDomainObject();
		objectTwo.setId(1);
		assertEquals(objectOne.hashCode(), objectTwo.hashCode());

	}
	
	
	@SuppressWarnings("serial")
	public class MyDomainObject extends DomainObject<Integer> {

		@Override
		public void setId(Integer id) {
			this.id = id;

		}

		@Override
		public Integer getId() {
			return id;
		}
	}
}

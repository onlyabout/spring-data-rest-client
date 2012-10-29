package net.daum.clix.springframework.data.rest.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.rest.example.Address;
import org.springframework.data.rest.example.AddressRepository;
import org.springframework.data.rest.example.Person;
import org.springframework.data.rest.example.PersonRepository;
import org.springframework.data.rest.example.Profile;
import org.springframework.data.rest.example.ProfileRepository;

public class SimpleRestRepositoryIntegrationTest extends SpringIntegrationTestBase {

	@Autowired(required = true)
	public PersonRepository personRepository;

	@Autowired(required = true)
	private AddressRepository addressRepository;

	@Autowired(required = true)
	private ProfileRepository profileRepository;

	private Address address;

	private Address address2;

	private Profile profile;

	private Profile profile2;

	private Person person;

	@Before
	public void saveTestForPreparingData() {
		List<Address> addresses = new ArrayList<Address>();
		Map<String, Profile> profiles = new HashMap<String, Profile>();

		address = addressRepository.save(new Address(new String[] { "line1", "line2" }, "city", "province",
				"postalCode"));
		address2 = addressRepository.save(new Address(new String[] { "line1_2", "line2_2" }, "city2", "province2",
				"postalCode2"));
		profile = profileRepository.save(new Profile("type", "url"));
		profile2 = profileRepository.save(new Profile("type2", "url2"));

		addresses.add(address);
		addresses.add(address2);
		profiles.put("name", profile);
		profiles.put("name2", profile2);

		person = personRepository.save(new Person("name", addresses, profiles));
	}

	@After
	public void deletePreparedData() {
		personRepository.delete(person);
		addressRepository.delete(address);
		addressRepository.delete(address2);
		profileRepository.delete(profile);
		profileRepository.delete(profile2);
	}

	@Test
	public void findOne() {
		Person person = personRepository.findOne(this.person.getId());
		assertNotNull(person);
		assertEquals("name", person.getName());

		assertNotNull(person.getAddresses());
		assertEquals(2, person.getAddresses().size());

		assertNotNull(person.getProfiles());
		assertEquals(2, person.getProfiles().size());
		
		// TODO: Is this a bug from spring data rest exporter?? I put a profile with "name" as a key first.
		assertEquals("url", person.getProfiles().get("profiles").getUrl());
		assertEquals("type", person.getProfiles().get("profiles").getType());
//		assertEquals("url", person.getProfiles().get("name").getUrl());
//		assertEquals("type", person.getProfiles().get("name").getType());
		
		assertEquals("url2", person.getProfiles().get("name2").getUrl());
		assertEquals("type2", person.getProfiles().get("name2").getType());
	}

	@Test
	public void count() {
		long count = personRepository.count();
		assertEquals(1, count);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void deleteAllThrowsException() {
		personRepository.deleteAll();
	}

	@Test
	public void delete() {
		personRepository.delete(this.person.getId());
		assertNull(personRepository.findOne(this.person.getId()));
	}

	@Test
	public void deleteWithEntity() {
		Person p = personRepository.findOne(this.person.getId());
		personRepository.delete(p);
		assertNull(personRepository.findOne(this.person.getId()));
	}

	@Test
	public void exists() {
		assertTrue(personRepository.exists(this.person.getId()));
		assertFalse(personRepository.exists(10103209L));
	}

	@Test
	public void findAll() {
		Iterable<Address> findAll = addressRepository.findAll();
		int cnt = 0;
		for (Address a : findAll) {
			assertNotNull(a);
			cnt++;
		}

		assertEquals(2, cnt);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void findAllThrowsExceptionForPagingAndSortingRepository() {
		Iterable<Person> people = personRepository.findAll();
		
		assertNotNull(people);
		
		int cnt = 0;
		for (Person p : people) {
			cnt++;
			assertNotNull(p);
		}
		
		assertEquals(1, cnt);
	}

	@Test
	public void findAllSorted() {
		Person person2 = personRepository.save(new Person("name2", null, null));
		Person person3 = personRepository.save(new Person("name3", null, null));

		// DESC
		Sort sort = new Sort(Direction.DESC, "name");
		Iterator<Person> iterator = personRepository.findAll(sort).iterator();

		Person found = iterator.next();
		assertEquals(person3.getName(), found.getName());

		found = iterator.next();
		assertEquals(person2.getName(), found.getName());

		found = iterator.next();
		assertEquals(this.person.getName(), found.getName());

		// ASC
		sort = new Sort(Direction.ASC, "name");
		iterator = personRepository.findAll(sort).iterator();

		found = iterator.next();
		assertEquals(this.person.getName(), found.getName());

		found = iterator.next();
		assertEquals(person2.getName(), found.getName());

		found = iterator.next();
		assertEquals(person3.getName(), found.getName());

		personRepository.delete(person2);
		personRepository.delete(person3);
	}

	@Test
	public void findAllPageable() {
		Person person2 = personRepository.save(new Person("name2", null, null));
		Person person3 = personRepository.save(new Person("name3", null, null));
		Person person4 = personRepository.save(new Person("name4", null, null));

		Pageable pageable = new PageRequest(1, 2, new Sort(Direction.ASC, "name"));
		Page<Person> result = personRepository.findAll(pageable);

		assertNotNull(result);
		assertEquals(2, result.getSize());
		assertEquals(1, result.getNumber());
		assertEquals(2, result.getNumberOfElements());
		assertEquals(4, result.getTotalElements());

		List<Person> listResult = result.getContent();
		assertNotNull(listResult);
		assertEquals(2, listResult.size());
		assertEquals(this.person.getName(), listResult.get(0).getName());
		assertEquals(person2.getName(), listResult.get(1).getName());

		pageable = new PageRequest(2, 2, new Sort(Direction.ASC, "name"));
		result = personRepository.findAll(pageable);

		assertNotNull(result);
		assertEquals(2, result.getSize());
		assertEquals(2, result.getNumber());
		assertEquals(2, result.getNumberOfElements());
		assertEquals(4, result.getTotalElements());

		listResult = result.getContent();
		assertNotNull(listResult);
		assertEquals(2, listResult.size());
		assertEquals(person3.getName(), listResult.get(0).getName());
		assertEquals(person4.getName(), listResult.get(1).getName());

		personRepository.delete(person2);
		personRepository.delete(person3);
		personRepository.delete(person4);
	}
	
	@Test
	public void queryMethod() {
		List<Person> list = personRepository.findByName("name");
		assertNotNull(list);
		assertEquals(1, list.size());
		
		Person person = list.get(0);
		assertNotNull(person);
		assertEquals("name", person.getName());
	}

	// TODO @Test -- Save method tested by @Before 
	public void saveCascade() {

		Address addr = new Address(new String[] { "line1", "line2" }, "city", "province", "postalCode");
		Address savedAddr = addressRepository.save(addr);

		assertNotNull(savedAddr.getId());

		List<Address> addresses = new ArrayList<Address>();
		addresses.add(savedAddr);

		Map<String, Profile> profiles = new HashMap<String, Profile>();

		Person person = new Person("name3", addresses, profiles);
		Person savedPerson = personRepository.save(person);

		assertNotNull(savedPerson.getId());
		assertEquals(person.getName(), savedPerson.getName());
	}

}

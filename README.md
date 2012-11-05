# Spring Data Web Exporter Client #

A Client library for the [Spring Data Web Exporter](https://github.com/SpringSource/spring-data-rest).

## Features ##

* Implementation of CRUD methods
* Dynamic http request generation from query method names
* Easy Spring integration with custom namespace

## Quick Start ##

### Building ###
Can be compiled using Apache Maven by running the following command:

    mvn package

### Testing ###

`**/*IntegrationTest.java` run on [Spring Data REST Exporter Example](https://github.com/SpringSource/spring-data-rest-webmvc) running on `http://localhoat:8080`.

### Using

This library has not deployed to any public maven repository. So build and install `jar` to your local repository by following command.

    mvn install

Add dependency to your `pom.xml`

```xml
<dependency>
  <groupId>net.daum.clix</groupId>
  <artifactId>spring-data-rest-cli</artifactId>
  <version>0.0.5-SNAPSHOT</version>
</dependency>
```

Set application context to scan package which has all of your repositories. An example can be found at `/src/test/resources/net/daum/clix/springframework/data/rest/client/application-context.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:jpa="http://www.springframework.org/schema/data/jpa" xmlns:repo="http://www.springframework.org/schema/data/repository"
    xmlns:context="http://www.springframework.org/schema/context" xmlns:p="http://www.springframework.org/schema/p"
    xmlns:rest="http://clix.biz.daum.net/schema/rest"
    xsi:schemaLocation="
       http://www.springframework.org/schema/beans 
       http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context-3.1.xsd
       http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa.xsd
       http://www.springframework.org/schema/data/repository http://www.springframework.org/schema/data/repository/spring-repository.xsd
       http://clix.biz.daum.net/schema/rest http://clix.biz.daum.net/schema/rest/spring-rest.xsd">

    <context:annotation-config />

    <bean id="restClient"
	class="net.daum.clix.springframework.data.rest.client.http.CommonsRestClient">
	<constructor-arg value="http://localhost:8080/data"/>
    </bean>

    <rest:repositories base-package="com.test.repositories" />
</beans>
```

Create an entity:

```java
@Entity
public class Person {/
  @Id
  @GeneratedValue(strategy = GenrationType.AUTO)
  private Long id;
  private String name;
  @OneToMany // OneToMany for Map, List, Collection and Set(working on it!) are supported!
  private List<Profile> profiles;

  // Getters and setters
}
```

Create a repository interface in `com.test.repositories`:

```java
public interface PersonRepository extends CrudRepository<Person, Long> {
  List<Person> findByName(@Param(name = "name") String name);
}
```

Write a test client
```java
@RunWith(SpringJUnit4TestRunner.class)
@ContextConfiguration("classpath:your-application-context.xml")
public class PersonRepositoryIntegrationTest {
  @Autowired PersonRepository repository;

  @Test
  public void testMethodName() {
    Person person = new Person("name");
    repository.save(person);

    Person saved = repository.findByName("name");
    assertNotNull(saved);
    assertEquals("name", saved.getName());
  }
}
```

Then you can use your restful web service([Spring Data Web Exporter](https://github.com/SpringSource/spring-data-rest)) as data access layer.

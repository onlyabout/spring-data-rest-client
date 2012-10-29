package net.daum.clix.springframework.data.rest.client.repository;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface RestRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

}

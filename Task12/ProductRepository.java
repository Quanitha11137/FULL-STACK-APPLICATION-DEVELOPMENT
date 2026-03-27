package edu.Task_12.Repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.Task_12.Entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer>{
	@Query("SELECT p FROM Product p where p.name=?1")
	Optional<Product>findByName(String name);
	
	@Query(value="SELECT *FROM Product",nativeQuery=true)
	List<Product>findAll();
	
	Optional<Product>findByCost(double cost);
}
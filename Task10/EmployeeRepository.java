package com.example.demo;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component

public class EmployeeRepository {
	private final Map<Integer,Employee> store =Map.of(
			101,new Employee(101,"Quani","CSE"),
			102,new Employee(102,"raaji","ECE"),
			103,new Employee(103,"Ashwin","IT")
	);
	public Employee findById(int id) {
		return store.get(id);

}
}
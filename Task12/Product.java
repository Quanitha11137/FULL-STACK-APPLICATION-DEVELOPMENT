package edu.Task_12.Entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Product {
	@Id
	int id;
	 String name;
	 double cost;
	 public Product() {
		 super();
	 }
	 public Product(int id,String name,double cost) {
		 super();
		 this.id=id;
		 this.name=name;
		 this.cost=cost;
	 }
	 public int getId() {
		 return id;
	 }
	 public void setId(int id) {
		 this.id=id;
	 }
	 public String getName() {
		 return name;
	 }
	 public void setName(String name) {
		 this.name=name;
	 }
	 public double getCost() {
		 return cost;
	 }
	 public void setCost(double cost) {
		 this.cost=cost;
	 }

}
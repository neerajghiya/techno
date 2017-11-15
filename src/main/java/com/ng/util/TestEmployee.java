import java.util.ArrayList;
import java.util.List;


public class TestEmployee {
	
	private String name;
	private String lname;
	private String address;
	
	private static Address add;
	private static Address add1;
	private static Address add2;
	
	private static List<Address> addList = new ArrayList<Address>();
	

	public static void main(String[] args) throws Exception {
		TestEmployee test = new TestEmployee();
		test.setName("NG ");
		test.setLname(null);
		test.setAddress("Metacube    ");
		add = new Address();
		
		add.setName("NG ");
		add.setLname(null);
		add.setAddress("Metacube    ");
		
		test.setAdd(add);
		
		
		//Address add1 = new Address();
		add1 = new Address();
		
		add1.setName("NG ");
		add1.setLname(null);
		add1.setAddress("Metacube    ");
		add1.setNumber(4);
		addList.add(add1);
		
		//Address add2 = new Address();
		add2 = new Address();
		
		add2.setName("NG ");
		add2.setLname(null);
		add2.setAddress("Metacube    ");
		add2.setNumber(5);
		addList.add(add2);
		
		test.setAddList(addList);
		
		System.out.println(test);
		
		test = (TestEmployee)SpaceUtil.trimReflective(test);
		
		
		System.out.println(test);
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getLname() {
		return lname;
	}


	public void setLname(String lname) {
		this.lname = lname;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	


	public Address getAdd() {
		return add;
	}


	public void setAdd(Address add) {
		this.add = add;
	}


	


	public static List<Address> getAddList() {
		return addList;
	}


	public static void setAddList(List<Address> addList) {
		TestEmployee.addList = addList;
	}


	@Override
	public String toString() {
		return "TestEmployee [name=" + name + ", lname=" + lname + ", address="
				+ address + ", getAdd()=" + getAdd() + ", "+ getAddList()+"]";
	}

}

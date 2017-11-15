
public class Address {
	
	private String name;
	private String lname;
	private String address;
	private Integer number;
	

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


	


	public Integer getNumber() {
		return number;
	}


	public void setNumber(Integer number) {
		this.number = number;
	}


	@Override
	public String toString() {
		return "Address [name=" + name + ", lname=" + lname + ", address="
				+ address + ", number=" + number + "]";
	}


	

}

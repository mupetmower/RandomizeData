package code;

public class RandomData {
	
	private String firstName;
	private String lastName;
	private String dob;
	private String email;
	private String ssn;
	private String phone;
	private String username;
	private String password;
	private String streetAddress;
	private String addrLine2;
	private String city;
	private String state;
	private String stateAbbrv;
	private String zip;
	private String sentence;
	private String company;
	private String bankAccount;
	private String nums10;
	private String nums5;
	private String url;
	private String department;
	private String ipaddress;
	//private String urlLong;	
	
	
	public RandomData() {}
	
	public RandomData(String firstName, String lastName, String dob, String email, String ssn, String phone, String username,
			String password, String streetAddress, String addrLine2, String city, String state, String stateAbbrv, String zip,
			String sentence, String company, String bankAccount, String nums10, String nums5, String url, String department, String ipaddress) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.email = email;
		this.ssn = ssn;
		this.phone = phone;
		this.username = username;
		this.password = password;
		this.streetAddress = streetAddress;
		this.addrLine2 = addrLine2;
		this.city = city;
		this.state = state;
		this.stateAbbrv = stateAbbrv;
		this.zip = zip;
		this.sentence = sentence;
		this.company = company;
		this.bankAccount = bankAccount;
		this.nums10 = nums10;
		this.nums5 = nums5;
		this.url = url;
		this.department = department;
		this.ipaddress = ipaddress;
		//this.urlLong = urlLong;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFullName() {
		StringBuilder sb = new StringBuilder(firstName);
		sb.append(" ");
		sb.append(lastName);
		return sb.toString();
	}
	
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	public String getDob() {
		return dob;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	
	public String getAddrLine2() {
		return addrLine2;
	}

	public void setAddrLine2(String addrLine2) {
		this.addrLine2 = addrLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateAbbrv() {
		return stateAbbrv;
	}

	public void setStateAbbrv(String stateAbbrv) {
		this.stateAbbrv = stateAbbrv;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getSentence() {
		return sentence;
	}
	
	public String getSentenceTruncated(int maxLength) {
		String s = sentence;
		if (sentence.length() > maxLength)
			s = sentence.substring(0, maxLength-1);
		return s;
	}
	
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getNums10() {
		return nums10;
	}

	public void setNums10(String nums10) {
		this.nums10 = nums10;
	}

	public String getNums5() {
		return nums5;
	}

	public void setNums5(String nums5) {
		this.nums5 = nums5;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/*public String getUrlLong() {
		return urlLong;
	}

	public void setUrlLong(String urlLong) {
		this.urlLong = urlLong;
	}*/

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
}

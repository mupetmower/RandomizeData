package code;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockarooConnector {

	private final String BASE_URL = "https://my.api.mockaroo.com/?";
	private final String API_CALL = "randomdata.json";
	private final String PARAM_SEPERATOR = "&";
	private final String KEY_PARAM = "key=f7b4bd00";
	private final String KEY = "f7b4bd00";
	private final String NUM_RECORDS_PARAM = "num_records=";
	
	private final String RANDOMDATA_API = "https://my.api.mockaroo.com/randomdata.xml?key=f7b4bd00&num_records=";
	private final String RANDOMDATA_SCHEMA = "https://api.mockaroo.com/api/dbc87e30?key=f7b4bd00&count=";
	
	private int numRecords;
	private URL url;
		
	private RandomData[] randomData;
	
	public static final int NUM_RECORDS_IN_FILE = 30000;
	private Random rand;
	
	public MockarooConnector(int numRecords) throws MalformedURLException {
		/*if (numRecords > 5000) {
			numRecords = 5000;
			System.out.println("Free Mockaroo can only get 5000 rows per request. Using 5000 as numRecords.");
		}*/
		//Not checking for now since using file with 20k records instead.
		this.numRecords = numRecords;
		rand = new Random(369);
		initUrl();
	}
	
	
	public void quickTest(int numRecords) throws IOException {
		String url = RANDOMDATA_API + numRecords;
		doPost(url);
	}
	
	private void initUrl() throws MalformedURLException {
		String url = RANDOMDATA_SCHEMA + numRecords;
		this.url = new URL(url);;
	}


	public RandomData[] getFromDefaultFile() throws JsonParseException, JsonMappingException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream in = classLoader.getResourceAsStream("resources/randomdata.json");
		return getFromStream(in);
	}
	
	public RandomData[] getFromFile(String filePath) throws JsonParseException, JsonMappingException, IOException {
		return getFromFile(new File(filePath));
	}

	public RandomData[] getFromFile(File file) throws JsonParseException, JsonMappingException, IOException {
		long startTime = System.currentTimeMillis();
		ObjectMapper om = new ObjectMapper();
		RandomData[] mockData = om.readValue(file, RandomData[].class);
		long duration = System.currentTimeMillis() - startTime;
		System.out.println(String.format("Time(in ms) to instantiate %d Mockaroo objects from File: %d", mockData.length, duration));

		randomData = mockData;
		return mockData;
	}

	public RandomData[] getFromStream(InputStream in) throws JsonParseException, JsonMappingException, IOException {
		long startTime = System.currentTimeMillis();
		ObjectMapper om = new ObjectMapper();
		RandomData[] mockData = om.readValue(in, RandomData[].class);
		long duration = System.currentTimeMillis() - startTime;
		System.out.println(String.format("Time(in ms) to instantiate %d Mockaroo objects from File: %d", mockData.length, duration));

		randomData = mockData;
		return mockData;
	}
	
	public RandomData[] doPost() throws Exception {
		if (url == null) throw new Exception("No Url.");
		return doPost(url);
	}
	
	public RandomData[] doPost(String urlString) throws IOException {
		URL url = new URL(urlString);		
		return doPost(url);
	}
	
	public RandomData[] doPost(URL url) throws IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type", "application/json");
		
		long startTime = System.currentTimeMillis();
		InputStream response = con.getInputStream();
		long duration = System.currentTimeMillis() - startTime;
		System.out.println(String.format("Time(in ms) to retrieve from Mockaroo (con.getInputStream()): %d", duration));
		
		startTime = System.currentTimeMillis();
		while (response.read() != -1) {
			
		}
		duration = System.currentTimeMillis() - startTime;
		System.out.println(String.format("Time(in ms) read(possibly generate) from Mockaroo: %d", duration));
		
		response = con.getInputStream();
		
		startTime = System.currentTimeMillis();
		ObjectMapper om = new ObjectMapper();
		RandomData[] mockData = om.readValue(response, RandomData[].class);
		duration = System.currentTimeMillis() - startTime;
		System.out.println(String.format("Time(in ms) to instantiate Mockaroo objects: %d", duration));
		
//		for (RandomData randomData : mockData) {
//			System.out.println(randomData.getFirstName());
//		}
		
		randomData = mockData;
		return mockData;
	}

	public int getRandomIndex() {
		return rand.nextInt(NUM_RECORDS_IN_FILE-5); //-5 just for now to be safe.
	}
	
	public Object getColumnRandom(String randomDataType) {
		int index = rand.nextInt(NUM_RECORDS_IN_FILE-5); //-5 just for now to be safe.
		return getColumn(randomDataType, index);
	}
	
	public Object getColumnRandom(String randomDataType, int truncateLength) {
		int index = rand.nextInt(NUM_RECORDS_IN_FILE-5); //-5 just for now to be safe.
		return getColumn(randomDataType, index, truncateLength);
	}
	
	public Object getColumn(String randomDataType, int index) {
		return getColumn(randomDataType, index, -1);
	}


	//change types to enum
	public Object getColumn(String randomDataType, int index, int truncateLength) {
		switch (randomDataType.trim().toLowerCase()) {
			case "firstname":				
				return randomData[index].getFirstName();
			case "lastname":				
				return randomData[index].getLastName();
			case "email":				
				return randomData[index].getEmail();
			case "dob":				
				return randomData[index].getDob();
			case "ssn":				
				return randomData[index].getSsn();
			case "phone":				
				return randomData[index].getPhone();
			case "username":				
				return randomData[index].getUsername();
			case "password":				
				return randomData[index].getPassword();
			case "streetaddress":				
				return randomData[index].getStreetAddress();
			case "addrline2":				
				return randomData[index].getAddrLine2();
			case "city":				
				return randomData[index].getCity();
			case "state":				
				return randomData[index].getState();
			case "zip":				
				return randomData[index].getZip();
			case "sentence":	
				if (truncateLength > 0)
					return randomData[index].getSentenceTruncated(truncateLength);
				else
					return randomData[index].getSentence();
			case "company":				
				return randomData[index].getCompany();
			case "bankaccount":				
				return randomData[index].getBankAccount();
			case "nums10":				
				return randomData[index].getNums10();
			case "nums5":				
				return randomData[index].getNums5();
			case "url":				
				return randomData[index].getUrl();
			case "fullname":
				return randomData[index].getFullName();
			default:
				return null;
		}
	}
	
}

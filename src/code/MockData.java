package code;

import java.util.ArrayList;
import java.util.List;

public class MockData {
	private List<RandomData> randomData = new ArrayList<RandomData>();
	
	public MockData() {
		
	}
	
	public void setRandomData(List<RandomData> randomData) {
		this.randomData = randomData;
	}
	
	public List<RandomData> getRandomData() {
		return this.randomData;
	}
	
}

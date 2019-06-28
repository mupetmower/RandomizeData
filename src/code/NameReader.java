package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NameReader {
	private BufferedReader br;
	private boolean gonnaDoRandom = false;
	List<String> names = new ArrayList<String>();
	
	private Random rand;
	
	public NameReader() throws FileNotFoundException {
		init();
	}
	
	private void init() throws FileNotFoundException {
		ClassLoader classLoader = getClass().getClassLoader();
		File lastNamesFile = new File(classLoader.getResource("resources/last-names.txt").getFile());
		FileReader fr = new FileReader(lastNamesFile);
		br = new BufferedReader(fr);
	}
	
	public String getNextName() throws Exception {
		if (gonnaDoRandom) 
			throw new Exception("Cannot get next name from list of names with this instance anymore. It has been prepared for random name generation.");
		
		if (br.ready())
			return br.readLine();
		else
			throw new Exception("No names in stream");
	}
	
	public void prepareForRandomNames(int seed) throws Exception {
		if (gonnaDoRandom) 
			throw new Exception("Already prepared for random name generation.");
		
		while (br.ready()) {
			names.add(br.readLine());
		}
		br.close();
		
		rand = new Random(seed);
		gonnaDoRandom = true;
	}
	
	public String getRandomNextName() {
		return names.get(rand.nextInt(names.size()));
	}
	
	public void close() throws IOException {
		br.close();
	}

}

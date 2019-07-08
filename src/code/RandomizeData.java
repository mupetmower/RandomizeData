package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import code.DBConnector.DBType;

public class RandomizeData {
	
	public static void main(String[] args) {
		/*int i = 0;
		String dbType = args[i++];
		String dbHost = args[i++];
		String dbName = args[i++];
		String dbUsername = args[i++];
		String dbPassword = args[i++];
		String table = args[i++];
		String idCol = args[i++];
		String column = args[i++];
		String randomType = args[i++];
		int truncateLength = -1;
		if (args.length > i) {
			try {
				truncateLength = Integer.parseInt(args[i++]);
			} catch (NumberFormatException ex) {
				System.out.println("Optional Truncate Length was not a number. Exiting.");
			}
		}*/

		Params params = new Params(args);
        params.printParams();
		
		long startTime = System.currentTimeMillis();
		if (params.multiCol) {
			randomize(params);
		} else {
			go(params);
		}
		long duration = System.currentTimeMillis() - startTime;
		System.out.println(String.format("Total time(in ms): %d", duration));
	}

	private static void randomize(Params params) {
		try {
			DBConnector db = new DBConnector(DBConnector.getDBTypeFromString(params.dbType), params.dbHost, params.dbName, params.dbUsername, params.dbPassword);

			long startTime = System.currentTimeMillis();
			List<Object> ids = db.retrieveIds(params.table, params.idCol, params.columnTypes);
			int idCount = ids.size();
			long duration = System.currentTimeMillis() - startTime;
			System.out.println(String.format("Time(in ms) to retrieve %d Ids: %d", idCount, duration));

			if (idCount <= 0) {
				System.out.println("No records to update.");
				return;
			}

			if (idCount > DBConnector.MAX_RECORD_COUNT) {
				System.out.println("Too many records to update. Cannot run.");
				return;
			}

			int numRand = idCount;
			/*if (idCount > DBConnector.MAX_RECORD_COUNT) {
				numRand = DBConnector.MAX_RECORD_COUNT;
				ids = ids.subList(0, DBConnector.MAX_RECORD_COUNT - 1);
			} else if (idCount < 2) {
				numRand = 2;
			}*/

			MockarooConnector mock = new MockarooConnector(numRand);

			startTime = System.currentTimeMillis();
			//RandomData[] randomData = mock.doPost();
			RandomData[] randomData = mock.getFromDefaultFile();
			duration = System.currentTimeMillis() - startTime;
			System.out.println(String.format("Time(in ms) to retrieve and instantiate %d Mock Records: %d", numRand, duration));

			db.prepUpdateBatches(params.table, params.idCol, params.columnTypes);

			startTime = System.currentTimeMillis();
			if (idCount >= MockarooConnector.NUM_RECORDS_IN_FILE) {
				//more records to change than available randomData records, so use random record from random data for each.
				for (Object id : ids) {
					int randMockIndex = mock.getRandomIndex(); //get randomIndex here to use for all since multiCol.
					for (Map.Entry<String, String> item : params.columnTypes.entrySet()) {
						Object newValue = mock.getColumn(item.getValue(), randMockIndex, params.truncateLengths.getOrDefault(item.getKey(), -1));
						db.addToUpdateBatchForCol(params.table, item.getKey(), newValue.toString(), params.idCol, id.toString());
					}
				}

			} else {
				//use randomData records as is since enough exist.
				for (int i = 0; i < idCount; i++) {
					for (Map.Entry<String, String> item : params.columnTypes.entrySet()) {
						Object newValue = mock.getColumn(item.getValue(), i, params.truncateLengths.getOrDefault(item.getKey(), -1));
						db.addToUpdateBatchForCol(params.table, item.getKey(), newValue.toString(), params.idCol, ids.get(i).toString());
					}
				}
			}
			duration = System.currentTimeMillis() - startTime;
			System.out.println(String.format("Time(in ms) to add %d update statements to batch: %d", idCount, duration));

			startTime = System.currentTimeMillis();
			db.executeUpdateBatches();
			duration = System.currentTimeMillis() - startTime;
			System.out.println(String.format("Time(in ms) to run updateBatch for %d records: %d", idCount, duration));

			startTime = System.currentTimeMillis();
			db.commitBatch();
			System.out.println(String.format("Time(in ms) to commit updateBatch: %d", duration));


			System.out.println("Updated " + idCount + " records from " + params.table);
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

	private static void go(Params params) {
		go(params.dbType, params.dbHost, params.dbName, params.dbUsername, params.dbPassword,
				params.table, params.idCol, params.column, params.randomType, params.truncateLength);
	}

	private static void go(String table, String idCol, String column, String randomType, int truncateLength) {
		go("mysql", "localhost", "h20_live_copy_20190323", "root", "1nn0Dev", table, idCol, column, randomType, truncateLength);
	}
	
	private static void go(String dbType, String dbHost, String dbName, String dbUsername, String dbPassword,
			String table, String idCol, String column, String randomType, int truncateLength) {
		try {
			DBConnector db = new DBConnector(DBConnector.getDBTypeFromString(dbType), dbHost, dbName, dbUsername, dbPassword);

			boolean isTextType = randomType.trim().toLowerCase().equals("sentence");

			long startTime = System.currentTimeMillis();
			List<Object> ids = db.retrieveIds(table, idCol, column, isTextType);
			int idCount = ids.size();
			long duration = System.currentTimeMillis() - startTime;
			System.out.println(String.format("Time(in ms) to retrieve %d Ids: %d", idCount, duration));
			
			if (idCount <= 0) {
				System.out.println("No records to update.");
				return;
			}
			
			if (idCount > DBConnector.MAX_RECORD_COUNT) {
				System.out.println("Too many records to update. Cannot run.");
				return;
			}
			
			int numRand = idCount;
/*			if (idCount > DBConnector.MAX_RECORD_COUNT) {
				numRand = DBConnector.MAX_RECORD_COUNT;
				ids = ids.subList(0, DBConnector.MAX_RECORD_COUNT - 1);
			} else if (idCount < 2) {
				numRand = 2;
			}*/
			
			MockarooConnector mock = new MockarooConnector(numRand);
			
			startTime = System.currentTimeMillis();
			//RandomData[] randomData = mock.doPost();
			RandomData[] randomData = mock.getFromDefaultFile();
			duration = System.currentTimeMillis() - startTime;
			System.out.println(String.format("Time(in ms) to retrieve and instantiate %d Mock Records: %d", numRand, duration));
			
			db.prepUpdateBatch(table, idCol, column);
			
			startTime = System.currentTimeMillis();
			if (idCount >= MockarooConnector.NUM_RECORDS_IN_FILE) {
				//more records to change than available randomData records, so use random record from random data for each.
				for (Object id : ids) {
					Object newValue = mock.getColumnRandom(randomType, truncateLength);
					db.addToUpdateBatch(table, column, newValue.toString(), idCol, id.toString());
				}
				
			} else {
				//use randomData records as is since enough exist.
				for (int i = 0; i < idCount; i++) {
					Object newValue = mock.getColumn(randomType, i, truncateLength);
					db.addToUpdateBatch(table, column, newValue.toString(), idCol, ids.get(i).toString());
				}
			}
			duration = System.currentTimeMillis() - startTime;
			System.out.println(String.format("Time(in ms) to add %d update statements to batch: %d", idCount, duration));
			
			startTime = System.currentTimeMillis();
			db.executeUpdateBatch();
			duration = System.currentTimeMillis() - startTime;
			System.out.println(String.format("Time(in ms) to run updateBatch for %d records: %d", idCount, duration));
			
			startTime = System.currentTimeMillis();
			db.commitBatch();
			System.out.println(String.format("Time(in ms) to commit updateBatch: %d", duration));
			
			
			System.out.println("Updated " + idCount + " records from " + table);
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
	
	
	private static void test() {
		try {
			MockarooConnector con = new MockarooConnector(10);
			con.quickTest(10);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}

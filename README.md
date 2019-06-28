# RandomizeData


## How to Run

Once .jar is built, this is how to run the program for now:

1. Open command line/terminal
2. Change directory to .jar file location
   - `cd [path\to\RandomizeData.jar]`
3. Run the program with the following command
   - All args must be in this order
   - All args are needed except for `[maxSentenceLength]`
   - `[mockDataCol]` spelling must match the column name in Mockaroo Schema exactly, but character's case does not matter
   - `[dbType]` must be 'mysql' or 'sqlserver' for now.
   - `java -jar RandomizeData.jar [dbType] [hostAddress] [dbName] [dbUsername] [dbPassword] [tableName] [tablePKIdColName] [colToUpdate] [mockDataCol] [(optional)maxSentenceLength]`
   - example: `java -jar RandomiseData.jar sqlserver 127.0.0.1 cis cis_user cis_pass account account_id bankaccountnumber bankaccount`

## Mockaroo Schema
There is a file put into src/resources called **randomdata.json** which is used in the program as a default file to use for now. It contains 30k mock records in json format. For now, the program is set to use only this default file since the Free version of mockaroo only allows 5k records at a time. Also, since this program is currently setup to run once per column, and since some tables in our databases have over 1 million rows (some much more), it takes far too long to generate all of that mock data each time for each column.


**SCHEMA NAME:**   randomdata

**POST URL:**     https://api.mockaroo.com/api/dbc87e30?count=1000&key=f7b4bd00

**Columns:**
- firstName
- lastName
- dob
- email
- ssn
- phone
- username
- password
- streetAddress
- addrLine2
- city
- state
- zip
- sentence
- company
- bankAccount
- nums10
- nums5
- url

**Columns not in default File yet:**
- department
- ipaddress


## NOTES
- For now, do not run this program for any address/location columns if the table has more than one location-type column. These will be done seperately. The reason for this is because we want the location information to all match on one record. If we update the records per column, then the data will not match and you could have city, state, and zip that do not go together. So these will be ran seperately with a change to the program.

package code;

import java.util.HashMap;
import java.util.Map;

public class Params {
    public String dbType;
    public String dbHost;
    public String dbName;
    public String dbUsername;
    public String dbPassword;
    public String table;
    public String idCol;
    public String column;
    public String randomType;

    public int truncateLength;

    public boolean multiCol = false;
    public Map<String, String> columnTypes = new HashMap<String, String>();     //change types to enum

    public Map<String, Integer> truncateLengths = new HashMap<String, Integer>();


    public Params(String... args) {
        parseArgs(args);

    }

    public void parseArgs(String... args) {
        int i = 0;
        dbType = args[i++];
        dbHost = args[i++];
        dbName = args[i++];
        dbUsername = args[i++];
        dbPassword = args[i++];
        table = args[i++];
        idCol = args[i++];


        if (args[i].equals("-m")) {
            multiCol = true;
            i++;
        }

        if (!multiCol) {
            column = args[i++];
            randomType = args[i++];

            if (args.length > i) {
                String truncateStr = args[i];
                if (truncateStr.equals("-t")) {
                    truncateStr = args[++i];
                } else if (truncateStr.startsWith("-t") && truncateStr.length() > 2) {
                    truncateStr = truncateStr.substring(2, truncateStr.length());
                } else {
                    truncateLength = -1;
                }

                try {
                    i++;
                    truncateLength = Integer.parseInt(truncateStr);
                } catch (NumberFormatException ex) {
                    System.out.println("Optional Truncate Length was not a number. Exiting.");
                }
            }

        } else {
            while (i < args.length) {
                String col = args[i++];
                String type = args[i++];
                columnTypes.put(col, type);
                boolean isTruncated = false;
                if (args.length > i) {
                    String truncateStr = args[i];
                    if (truncateStr.equals("-t")) {
                        isTruncated = true;
                        truncateStr = args[++i];
                    } else if (truncateStr.startsWith("-t") && truncateStr.length() > 2) {
                        isTruncated = true;
                        truncateStr = truncateStr.substring(2, truncateStr.length());
                    }

                    if (isTruncated) {
                        i++;
                        try {
                            truncateLengths.put(col, Integer.parseInt(truncateStr));
                        } catch (NumberFormatException ex) {
                            System.out.println("Optional Truncate Length was not a number. Exiting.");
                        }
                    } else {
                        truncateLengths.put(col, -1);
                    }
                }
            }
        }
    }



    public String printParams() {
        StringBuilder sb = new StringBuilder("Params: \n");
        sb.append("dbType: ");
        sb.append(dbType);
        sb.append("\n");
        sb.append("dbHost: ");
        sb.append(dbHost);
        sb.append("\n");
        sb.append("dbName: ");
        sb.append(dbName);
        sb.append("\n");
        sb.append("dbUsername: ");
        sb.append(dbUsername);
        sb.append("\n");
        sb.append("dbPassword: ");
        sb.append(dbPassword);
        sb.append("\n");
        sb.append("table: ");
        sb.append(table);
        sb.append("\n");
        sb.append("idCol: ");
        sb.append(idCol);
        sb.append("\n");

        sb.append("multiCol: ");
        sb.append(multiCol);
        sb.append("\n");

        if (multiCol) {
            for (Map.Entry<String, String> item : columnTypes.entrySet()) {
                sb.append("col: ");
                sb.append(item.getKey());
                sb.append("\n");
                sb.append("type: ");
                sb.append(item.getValue());
                sb.append("\n");

                sb.append("truncateLength: ");
                sb.append(truncateLengths.get(item.getKey()));
                sb.append("\n");
            }
        } else {
            sb.append("col: ");
            sb.append(column);
            sb.append("\n");
            sb.append("randomType: ");
            sb.append(randomType);
            sb.append("\n");
        }

        System.out.println(sb.toString());
        return sb.toString();
    }

}

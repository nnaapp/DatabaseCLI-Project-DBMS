package com.helper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

// THESE ARE NOT DIRECT SOURCE FILES
// I HAVE COMPILED THESE INTO A JAR LIBRARY TO BE USED IN FUTURE
// THEY ARE INCLUDED TO PROVE I WROTE THEM

// Class for ResultSet parsing related methods
public class ResultSetParse
{
    /**
     * Prints the contents of a ResultSe
     * @param res ResultSet to be printed
     */
    public static void PrintResultSet(ResultSet res)
    {
        try
        {
            // Do not proceed if set is empty
            if (!res.next())
            {
                System.out.println("\nNo results.");
                return;
            }

            // Get metadata of set, for amount of columns
            ResultSetMetaData resmd = res.getMetaData();
            int columnCount = resmd.getColumnCount();

            // Print out the labels of every column, as well as an N column
            // for the row number
            System.out.print("\nN, ");
            for (int i = 1; i <= columnCount; i++)
            {
                System.out.print(resmd.getColumnLabel(i));
                if (i != columnCount) { System.out.print(", "); }
            }
            System.out.println("");

            // ResultSets are 1-indexed, not 0-indexed
            int count = 1;
            // Print every column in every row using column index based on columnCount
            do
            {
                System.out.print(count);
                for (int i = 1; i <= columnCount; i++)
                {
                    System.out.print(", ");
                    String columnStr = res.getString(i);
                    System.out.print(columnStr);
                }
                System.out.println("");
                count++;
            } while(res.next());
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Basic method that checks irregularity (null set, generally they will be non-null but empty
     * or along those lines)
     * @param res ResultSet to be validated
     * @return True if valid (non-null), false if invalid (null)
     */
    public static boolean CheckResultsValid(ResultSet res)
    {
        return res == null ? false : true;
    }
}

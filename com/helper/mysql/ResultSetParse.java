package com.helper.mysql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetParse
{
    public static void PrintResultSet(ResultSet res)
    {
        try
        {
            if (!res.next())
            {
                System.out.println("\nNo results.");
                return;
            }

            ResultSetMetaData resmd = res.getMetaData();
            int columnCount = resmd.getColumnCount();

            System.out.print("\nN, ");
            for (int i = 1; i <= columnCount; i++)
            {
                System.out.print(resmd.getColumnLabel(i));
                if (i != columnCount) { System.out.print(", "); }
            }
            System.out.println("");

            int count = 1;
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

    public static boolean CheckResultsValid(ResultSet res)
    {
        return res == null ? false : true;
    }
}

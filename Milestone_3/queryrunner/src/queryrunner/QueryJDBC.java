/*
 * Restaurant Solutions (Group 2)
 * Celeste Broderick, Pabi Dhaliwal, Eric Nunn, Fang (Jenny) Yuan
 * CPSC 5021, Seattle University
 */
package queryrunner;

import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The QueryJDBC class allows a connection to be made between the Java
 * application and SQL server, and allows queries to be run.
 *
 * @author mckeem
 */

public class QueryJDBC
{
   public Connection m_conn = null;
   static final String DB_DRV = "com.mysql.jdbc.Driver";
   String m_error = "";
   String m_url;
   String m_user;
   String[] m_headers;
   String[][] m_allRows;
   int m_updateAmount = 0;

   QueryJDBC()
   {
      m_updateAmount = 0;
   }

   public String GetError()
   {
      return m_error;
   }

   public String[] GetHeaders()
   {
      return this.m_headers;
   }

   public String[][] GetData()
   {
      return this.m_allRows;
   }

   public int GetUpdateCount()
   {
      return m_updateAmount;
   }

   public boolean ExecuteQuery(String szQuery, String[] parms,
         boolean[] likeparms)
   {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      int nColAmt;
      boolean bOK = true;
      // Try to get the columns and the amount of columns
      try
      {

         preparedStatement = this.m_conn.prepareStatement(szQuery);

         int nParamAmount = parms.length;

         for (int i = 0; i < nParamAmount; i++)
         {
            String parm = parms[i];
            if (likeparms[i] == true)
            {
               parm += "%";
            }
            preparedStatement.setString(i + 1, parm);

         }
         resultSet = preparedStatement.executeQuery();

         ResultSetMetaData rsmd = resultSet.getMetaData();
         nColAmt = rsmd.getColumnCount();
         m_headers = new String[nColAmt];

         for (int i = 0; i < nColAmt; i++)
         {
            m_headers[i] = rsmd.getColumnLabel(i + 1);
         }
         int amtRow = 0;
         while (resultSet.next())
         {
            amtRow++;
         }
         if (amtRow > 0)
         {
            this.m_allRows = new String[amtRow][nColAmt];
            resultSet.beforeFirst();
            int nCurRow = 0;
            while (resultSet.next())
            {
               for (int i = 0; i < nColAmt; i++)
               {
                  m_allRows[nCurRow][i] = resultSet.getString(i + 1);
               }
               nCurRow++;
            }
         } else
         {
            this.m_allRows = new String[1][nColAmt];
            for (int i = 0; i < nColAmt; i++)
            {
               m_allRows[0][i] = "";
            }
         }

         preparedStatement.close();
         resultSet.close();
      }

      catch (SQLException ex)
      {
         bOK = false;
         this.m_error = "SQLException: " + ex.getMessage();
         this.m_error += "SQLState: " + ex.getSQLState();
         this.m_error += "VendorError: " + ex.getErrorCode();

         System.out.println("SQLException: " + ex.getMessage());
         System.out.println("SQLState: " + ex.getSQLState());
         System.out.println("VendorError: " + ex.getErrorCode());
         return false;

      }

      return true;
   }

   public boolean ExecuteUpdate(String szQuery, String[] parms)
   {
      PreparedStatement preparedStatement = null;

      boolean bOK = true;
      m_updateAmount = 0;

      // Try to get the columns and the amount of columns
      try
      {
         preparedStatement = this.m_conn.prepareStatement(szQuery);

         int nParamAmount = parms.length;

         for (int i = 0; i < nParamAmount; i++)
         {
            preparedStatement.setString(i + 1, parms[i]);
         }

         m_updateAmount = preparedStatement.executeUpdate();
         preparedStatement.close();
      }

      catch (SQLException ex)
      {
         bOK = false;
         this.m_error = "SQLException: " + ex.getMessage();
         this.m_error += "SQLState: " + ex.getSQLState();
         this.m_error += "VendorError: " + ex.getErrorCode();

         System.out.println("SQLException: " + ex.getMessage());
         System.out.println("SQLState: " + ex.getSQLState());
         System.out.println("VendorError: " + ex.getErrorCode());
         return false;

      }

      return true;
   }

   public boolean ConnectToDatabase(String host, String user, String pass,
         String database)
   {
      String url;

      url = "jdbc:mysql://";
      url += host;
      url += ":3306/";
      url += database;
      url += "?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
      try
      {

         Class.forName(DB_DRV).newInstance();
         m_conn = DriverManager.getConnection(url, user, pass);

      } catch (SQLException ex)
      {
         m_error = "SQLException: " + ex.getMessage() + ex.getSQLState()
               + ex.getErrorCode();
         return false;
      } catch (Exception ex)
      {
         // handle the error
         m_error = "SQLException: " + ex.getMessage();
      }

      return true;
   }

   /*
    * Closes out the connection to the database.If successful returns true. If a
    * connection can not be closed a the reason will be printed but will return
    * false.
    */
   public boolean CloseDatabase()
   {
      try
      {

         m_conn.close();

      } // Exception handling if connection can not be closed.
      catch (SQLException ex)
      {

         m_error = "SQLException: " + ex.getMessage();
         m_error = "SQLState: " + ex.getSQLState();
         m_error = "VendorError: " + ex.getErrorCode();
         return false;
      } catch (Exception ex)
      {
         m_error = "Error was " + ex.toString();
         return false;
      }

      return true;
   }
}

package main;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

public class Main extends HttpServlet {
	// Creating the data base in the SQL
	public boolean createSchema() throws ServletException {
		try {
			System.out.println("Schema Create - Begin");
			Class.forName(Settings.dbDriver).newInstance();

			// Defining the connection to SQL
			Connection conn = DriverManager.getConnection(Settings.dbUrl,
					Settings.dbUserName, Settings.dbPassword);
			Statement s = (Statement) conn.createStatement();

			// Execute the connection and print a msg alerting it
			s.executeUpdate("CREATE DATABASE " + Settings.dbName);
			System.out.println("DB Create - END");

			conn = DriverManager.getConnection(
					Settings.dbUrl + Settings.dbName, Settings.dbUserName,
					Settings.dbPassword);

			Statement stmt = (Statement) conn.createStatement();

			// setting the tables and their content
			String createUserTypesTable = "CREATE TABLE IF NOT EXISTS account_type ("
					+ "idaccounttype INT NOT NULL AUTO_INCREMENT ,"
					+ "description VARCHAR(45) NULL ,"
					+ "PRIMARY KEY (idaccounttype))";

			String createUserTable = "CREATE TABLE IF NOT EXISTS users ("
					+ "idsystem INT NOT NULL AUTO_INCREMENT ,"
					+ "idpersonal VARCHAR(10) NULL ,"
					+ "firstname VARCHAR(45) NULL ,"
					+ "lastname VARCHAR(45) NULL ,"
					+ "password VARCHAR(45) NULL ,"
					+ "accountactive BINARY NULL DEFAULT 1,"
					+ "subscriptiondate DATETIME NULL,"
					+ "accounttype INT NULL ,"
					+ "balance INT NULL,"
					+ "PRIMARY KEY (idsystem), "
					+ "FOREIGN KEY (accounttype) REFERENCES account_type (idaccounttype))";

			String createPriceCodesTable = "CREATE TABLE IF NOT EXISTS code_price ("
					+ "idCodeSystem INT NOT NULL AUTO_INCREMENT ,"
					+ "code INT NULL ,"
					+ "price DOUBLE(5,2) NULL ,"
					+ "PRIMARY KEY (idCodeSystem))";
			// +
			// "FOREIGN KEY (accounttype) REFERENCES account_type (idaccounttype))";
			System.out.println(createPriceCodesTable);
			stmt.execute(createUserTypesTable);
			stmt.execute(createUserTable);
			stmt.execute(createPriceCodesTable);

			System.out.println("Schema Create - End");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean populateSchema() throws ServletException {
		try {
			System.out.println("Populate Initial Data - Begin");
			Class.forName(Settings.dbDriver).newInstance();

			Connection conn = DriverManager.getConnection(Settings.dbUrl,
					Settings.dbUserName, Settings.dbPassword);

			conn = DriverManager.getConnection(
					Settings.dbUrl + Settings.dbName, Settings.dbUserName,
					Settings.dbPassword);

			Statement stmt = (Statement) conn.createStatement();

			String populateUserTypesTable = "INSERT INTO account_type (description) VALUES"
					+ "('Regular'),"
					+ "('Student'),"
					+ "('Pensioner'),"
					+ "('Handicapped');";

			String populateUsers = "INSERT INTO users (idpersonal, firstname, lastname, password, subscriptiondate, accounttype, balance) VALUES"
					+ "('012345678', 'David', 'Cohen', 'cohen', '2013-01-01', '1', '350'),"
					+ "('300394780', 'Lilac', 'Orenshtein', 'lilac', '2013-01-01', '2', '200'),"
					+ "('032764785', 'Eviatar', 'Levi', 'eviatar', '2013-03-03', '3', '150');";

			String populateKodPrices = "INSERT INTO code_price (code, price) VALUES"
					+ "('1', '5.50'),('2', '6.60'),('3', '9.20'),"
					+ "('4', '10.20'),('5', '11.80'),('6', '14.00'),"
					+ "('7', '16.00'),('8', '18.00'),('11', '24.00');";
			System.out.println(populateKodPrices);
			stmt.execute(populateUserTypesTable);
			stmt.execute(populateUsers);
			stmt.execute(populateKodPrices);
			System.out.println("Populate Initial Data - End");

			getTable();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean getTable() throws ServletException{
	boolean success = true;
	System.out.println("Getting tables from the SQL - Begin");
	try{
	Class.forName(Settings.dbDriver).newInstance();

	Connection conn = DriverManager.getConnection(Settings.dbUrl,Settings.dbUserName, Settings.dbPassword);

	conn = DriverManager.getConnection(Settings.dbUrl + Settings.dbName, Settings.dbUserName,
			Settings.dbPassword);
	
	Statement stmt = (Statement) conn.createStatement();
	String query = "SELECT * from users";

	ResultSet result = stmt.executeQuery(query);
	  while ( result.next() ) {
          int numColumns = result.getMetaData().getColumnCount();
          for ( int i = 1 ; i <= numColumns ; i++ ) {
             // Column numbers start at 1.
             // Also there are many methods on the result set to return
             //  the column as a particular type. Refer to the Sun documentation
             //  for the list of valid conversions.
             System.out.println( "COLUMN " + i + " = " + result.getObject(i) );
          }
      }
	/*while(result.next()){
		System.out.println(result.getInt(1));
		System.out.println(result.getString(2));
		System.out.println(result.getString(3));
		System.out.println(result.getString(4));
		System.out.println(result.getString(5));
		
		/**Example of an array list object
		 * ArrayList offerList=null;
				while(rs.next())
				{
				  
					offerList =new ArrayList();
					offerList.add(rs.getInt(1));
					offerList.add(rs.getString(2));
					offerList.add(rs.getString(3));
					offerList.add(rs.getBoolean(4));
				
				}		
	}*/
	System.out.println(result);

	}
	catch(Exception e){
		e.printStackTrace();
	}
	
	return success;
	
}

	public boolean deleteDatabase() throws ServletException {
		try {
			System.out.println("DB Delete - Begin");
			Class.forName(Settings.dbDriver).newInstance();

			Connection conn = DriverManager.getConnection(Settings.dbUrl,
					Settings.dbUserName, Settings.dbPassword);
			Statement stmt = (Statement) conn.createStatement();

			stmt.executeUpdate("DROP DATABASE " + Settings.dbName);
			System.out.println("DB Delete - END");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet");

		response.setContentType("text/html");

		String cmd = request.getParameter("cmd");
		String nextPage = "/admin.jsp";
		switch (cmd) {
		case "create":
			if (createSchema() == true) {
				response.setHeader("msgType", "success");
				response.setHeader("msg", "Database was successfully created!");
			} else {
				response.setHeader("msgType", "danger");
				response.setHeader("msg",
						"There was a problem creating the database!");
			}
			break;
		case "populate":
			if (populateSchema() == true) {
				response.setHeader("msgType", "success");
				response.setHeader("msg",
						"Database was successfully populated with initial data!");
			} else {
				response.setHeader("msgType", "danger");
				response.setHeader("msg",
						"There was a problem populating the database with initial data!");
			}
			break;
		case "delete":
			if (deleteDatabase()) {
				response.setHeader("msgType", "success");
				response.setHeader("msg", "Database was successfully deleted!");
			} else {
				response.setHeader("msgType", "danger");
				response.setHeader("msg",
						"There was a problem deleting the database!");
			}
			break;
		default:
			System.out.println("No value 'cmd' detected in URL");
			response.setHeader("msgType", "danger");
			response.setHeader("msg", "No matching 'cmd' could be found!");
			break;
		}

		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher(nextPage);
		dispatcher.forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost");
		String nextJSP = "/index.jsp";
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher(nextJSP);
		dispatcher.forward(request, response);
	}
}

package de.melody.datamanagment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.melody.core.Constants;
import de.melody.tools.ConsoleLogger;

public class LiteSQL {
	
	private Connection conn;
	private Statement stmt;
	
	public LiteSQL(){
		conn = null;
		try {
			//Fileloader
			File databasefile = new File("database.db");
			if(databasefile.exists()) {
				InputStream link = new FileInputStream(databasefile.getAbsoluteFile().getPath());
				File file = new File(Constants.TEMP_DIRECTORY + "/" +"OLD_database.db");
				Files.copy(link, file.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
			}else {
				InputStream link = getClass().getResourceAsStream(Constants.STORAGE_DATABASE_URL);
				Files.copy(link, databasefile.getAbsoluteFile().toPath());	
			}
			//
			String url = "jdbc:sqlite:"+ databasefile.getPath();
			conn = DriverManager.getConnection(url);
			
			ConsoleLogger.info("SQLDatabase", "Connection to the database established");
			stmt = conn.createStatement();
		
		} catch (SQLException | IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			if(isConnected()) {
				conn.close();
				ConsoleLogger.info("SQLDatabase", "Connection to the database disconnected");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	 public boolean isConnected() {
	    return (conn == null ? false : true);
	 } 
	 
	 public Connection getConnection() {
	    return conn;
	 }
	 
	public void onUpdate(String sql) {
		try {
			ConsoleLogger.debug("LITESQL onUpdate", sql);
			stmt.execute(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
		
	public ResultSet onQuery(String sql) {
		try {
			ConsoleLogger.debug("LITESQL onQuery", sql);
			return stmt.executeQuery(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		}
			return null;
	}
}

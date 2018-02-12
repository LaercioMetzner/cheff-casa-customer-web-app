/* 
 * cheff.casa is a platform that aims to cover all the business process
 * involved in operating a restaurant or a food delivery service. 
 *
 * Copyright (C) 2018  Laercio Metzner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information please visit http://cheff.casa
 * or mail us via our e-mail contato@cheff.casa
 * or even mail-me via my own personal e-mail laercio.metzner@gmail.com 
 * 
 */
package casa.cheff.customerwebapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

	public static String url;
	public static String driverName;
	public static String username;
	public static String password;
	private static Connection con;

	public static void set(String... args) {
		try {
			url = args[0];
			driverName = args[1];
			username = args[2];
			password = args[3];
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	public static Connection getConnection(final String TAG)
			throws RuntimeException {
		try {
			Class.forName(driverName);
			try {
				con = DriverManager.getConnection(url, username, password);
			} catch (SQLException ex) {
				throw new RuntimeException(
						"Falha ao criar a conexão com o banco de dados. TAG: "
								+ TAG + "MSG = " + ex + " URL = "
								+ url);
			}
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("Driver não encontrado. TAG: " + TAG);
		}
		return con;
	}

}
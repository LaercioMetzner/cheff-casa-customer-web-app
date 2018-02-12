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
package casa.cheff.customerwebapp.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import casa.cheff.customerwebapp.db.ConnectionManager;

public class GeneralPurposeDAO {

	private static final String TAG = "GENERAL_PURPOSE_DAO";

	public Date getDBDate() throws RuntimeException {
		Connection conn = ConnectionManager.getConnection(TAG);
		Statement stmt = null;
		ResultSet rs = null;
		Date returnValue = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT NOW() now");
			if (rs.next()) {
				returnValue = new Date(rs.getTimestamp("now").getTime());
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar a data atual do banco de dados. TAG = "
							+ TAG + " MSG = " + e.getMessage());
		}
		return returnValue;
	}
}

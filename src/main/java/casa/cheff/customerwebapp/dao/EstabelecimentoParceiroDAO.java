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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import casa.cheff.customerwebapp.bean.db.EstabelecimentoParceiro;
import casa.cheff.customerwebapp.bean.db.Tamanho;
import casa.cheff.customerwebapp.db.ConnectionManager;

public class EstabelecimentoParceiroDAO {

	public static String TAG = "ESTABELECIMENTO_PARCEIRO_DAO";

	public void insertEstabelecimentoParceiro(
			EstabelecimentoParceiro estabelecimentoParceiro) {
		int codTamanho = -1;
		String sqlInsert = "INSERT INTO estabelecimento_parceiro ( cod_estabelecimento                          "
				+ "                           , cod_parceiro                                    "
				+ "                           , dat_cadastro)                                "
				+ "       VALUES (?, ?, ?)                                             ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert);
			pstmt.setInt(1, estabelecimentoParceiro.codEstabelecimento);
			pstmt.setInt(2, estabelecimentoParceiro.codParceiro);
			pstmt.setDate(3, new java.sql.Date(estabelecimentoParceiro.datCadastro.getTime()));//new java.sql.Date(new Date().getTime()));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao inserir um tamanho no DB. TAG = " + TAG
							+ " MSG = " + e.getMessage());
		} finally {
			try {
				if (pstmt != null) {
					if (!pstmt.isClosed()) {
						pstmt.close();
					}
				}
				if (conn != null) {
					if (!conn.isClosed()) {
						conn.close();
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException(
						"Erro ao fechar a conexão com o DB em insertEstabelecimentoParceiro: "
								+ e.getMessage());
			}
		}
	}
}

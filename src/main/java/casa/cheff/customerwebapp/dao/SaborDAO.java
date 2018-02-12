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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import casa.cheff.customerwebapp.bean.db.IUser;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.bean.db.Sabor;
import casa.cheff.customerwebapp.db.ConnectionManager;

public class SaborDAO {

	private static final String TAG = "SABOR_DAO";
	
	public List<Sabor> getSaboresAtivos(int codEstabelecimento) {
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Sabor> sabores = new ArrayList<Sabor>();
		String selectSQL = "				SELECT *                                                                    "
				+ "				  FROM sabor sab                                                            "
				+ "				 WHERE sab.cod_estabelecimento = ?                                          ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codEstabelecimento);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Sabor sabor = new Sabor();
				sabor.codSabor = rs.getInt("cod_sabor");
				sabor.codEstabelecimento = rs.getInt("cod_estabelecimento");
				sabor.nomSabor = rs.getString("nom_sabor");
				sabor.desSabor = rs.getString("des_sabor");
				sabor.indAtivo = rs.getString("ind_ativo");
				sabor.datCadastro = rs.getDate("dat_cadastro");
				sabores.add(sabor);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os sabores do banco de dados. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em getSaboresAtivos. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return sabores;
	}

	public List<Sabor> getSabores(int codEstabelecimento, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Sabor> sabores = new ArrayList<Sabor>();
		String selectSQL = "				SELECT *                                                                    "
				+ "				  FROM sabor sab                                                            "
				+ "				 WHERE sab.cod_estabelecimento = ?                                          "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = sab.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codEstabelecimento);
			pstmt.setInt(2, parceiro.codParceiro);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Sabor sabor = new Sabor();
				sabor.codSabor = rs.getInt("cod_sabor");
				sabor.codEstabelecimento = rs.getInt("cod_estabelecimento");
				sabor.nomSabor = rs.getString("nom_sabor");
				sabor.desSabor = rs.getString("des_sabor");
				sabor.indAtivo = rs.getString("ind_ativo");
				sabor.datCadastro = rs.getDate("dat_cadastro");
				sabores.add(sabor);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os sabores do banco de dados. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em getSabores. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return sabores;
	}

	public int insertSabor(Sabor sabor) {
		int codCardapio = -1;
		String sqlInsert = "INSERT INTO sabor ( cod_estabelecimento                          "
				+ "                           , nom_sabor                                    "
				+ "                           , des_sabor                                    "
				+ "                           , ind_ativo                                    "
				+ "                           , dat_cadastro)                                "
				+ "       VALUES (?, ?, ?, ?, ?)                                             ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, sabor.codEstabelecimento);
			pstmt.setString(2, sabor.nomSabor);
			pstmt.setString(3, sabor.desSabor);
			pstmt.setString(4, sabor.indAtivo);
			pstmt.setDate(5, new java.sql.Date(new Date().getTime()));
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				codCardapio = rs.getInt(1);
			} else {
				throw new RuntimeException(
						"não foram geradas as chaves em insertSabor...");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao inserir um sabor no DB. TAG = "
					+ TAG + " MSG = " + e.getMessage());
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
						"Erro ao fechar a conexão com o DB em insertSabor: "
								+ e.getMessage());
			}
		}
		return codCardapio;
	}

	public int updateSabor(Sabor sabor, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		int qtyUpdated = 0;
		String sqlUpdate = "UPDATE sabor sab                                                   "
				+ "   SET sab.nom_sabor = ?                                                    "
				+ "     , sab.des_sabor = ?                                                    "
				+ "     , sab.ind_ativo = ?                                                    "
				+ " WHERE sab.cod_sabor = ?                                                    "
				+ "   AND sab.cod_estabelecimento = ?                                          "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = sab.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setString(1, sabor.nomSabor);
			pstmt.setString(2, sabor.desSabor);
			pstmt.setString(3, sabor.indAtivo);
			pstmt.setInt(4, sabor.codSabor);
			pstmt.setInt(5, sabor.codEstabelecimento);
			pstmt.setInt(6, parceiro.codParceiro);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao atualizar um sabor no DB. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em updateSabor: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}
}

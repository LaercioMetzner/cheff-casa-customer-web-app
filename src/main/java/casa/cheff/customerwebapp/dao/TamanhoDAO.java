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
import casa.cheff.customerwebapp.bean.db.Tamanho;
import casa.cheff.customerwebapp.db.ConnectionManager;


public class TamanhoDAO {

	private static final String TAG = "TAMANHO_DAO";

	public List<Tamanho> getTamanhos(int codEstabelecimento, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Tamanho> tamanhos = new ArrayList<Tamanho>();
		String selectSQL = "SELECT *                                                                "
				+ "  FROM tamanho tam                                                      "
				+ " WHERE tam.cod_estabelecimento = ?                                      "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = tam.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codEstabelecimento);
			pstmt.setInt(2, parceiro.codParceiro);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Tamanho tamanho = new Tamanho();
				tamanho.codTamanho = rs.getInt("cod_tamanho");
				tamanho.codEstabelecimento = rs.getInt("cod_estabelecimento");
				tamanho.nomTamanho = rs.getString("nom_tamanho");
				tamanho.desTamanho = rs.getString("des_tamanho");
				tamanho.indAtivo = rs.getString("ind_ativo");
				tamanho.datCadastro = rs.getDate("dat_cadastro");
				tamanhos.add(tamanho);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os tamanhos do banco de dados. TAG = "
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
						"Erro ao fechar a conexão com o DB em getTamanhos. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return tamanhos;
	}

	public int insertTamanho(Tamanho tamanho) {
		int codTamanho = -1;
		String sqlInsert = "INSERT INTO tamanho ( cod_estabelecimento                          "
				+ "                           , nom_tamanho                                    "
				+ "                           , des_tamanho                                    "
				+ "                           , ind_ativo                                    "
				+ "                           , dat_cadastro)                                "
				+ "       VALUES (?, ?, ?, ?, ?)                                             ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, tamanho.codEstabelecimento);
			pstmt.setString(2, tamanho.nomTamanho);
			pstmt.setString(3, tamanho.desTamanho);
			pstmt.setString(4, tamanho.indAtivo);
			pstmt.setDate(5, new java.sql.Date(new Date().getTime()));
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				codTamanho = rs.getInt(1);
			} else {
				throw new RuntimeException(
						"não foram geradas as chaves em insertTamanho...");
			}
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
						"Erro ao fechar a conexão com o DB em insertTamanho: "
								+ e.getMessage());
			}
		}
		return codTamanho;
	}

	public int updateTamanho(Tamanho tamanho, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		int qtyUpdated = 0;
		String sqlUpdate = "UPDATE tamanho tam                                                     "
				+ "   SET tam.nom_tamanho = ?                                              "
				+ "     , tam.des_tamanho = ?                                              "
				+ "     , tam.ind_ativo = ?                                                "
				+ " WHERE tam.cod_tamanho = ?                                              "
				+ "   AND tam.cod_estabelecimento = ?                                      "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = tam.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setString(1, tamanho.nomTamanho);
			pstmt.setString(2, tamanho.desTamanho);
			pstmt.setString(3, tamanho.indAtivo);
			pstmt.setInt(4, tamanho.codTamanho);
			pstmt.setInt(5, tamanho.codEstabelecimento);
			pstmt.setInt(6, parceiro.codParceiro);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao atualizar um tamanho no DB. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em updateTamanho: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}

	public List<Tamanho> getTamanhosAtivos(int codEstabelecimento) {
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Tamanho> tamanhos = new ArrayList<Tamanho>();
		String selectSQL = "SELECT *                                                                "
				+ "  FROM tamanho tam                                                      "
				+ " WHERE tam.cod_estabelecimento = ?                                      ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codEstabelecimento);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Tamanho tamanho = new Tamanho();
				tamanho.codTamanho = rs.getInt("cod_tamanho");
				tamanho.codEstabelecimento = rs.getInt("cod_estabelecimento");
				tamanho.nomTamanho = rs.getString("nom_tamanho");
				tamanho.desTamanho = rs.getString("des_tamanho");
				tamanho.indAtivo = rs.getString("ind_ativo");
				tamanho.datCadastro = rs.getDate("dat_cadastro");
				tamanhos.add(tamanho);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os tamanhos do banco de dados. TAG = "
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
						"Erro ao fechar a conexão com o DB em getTamanhos. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return tamanhos;
	}
}

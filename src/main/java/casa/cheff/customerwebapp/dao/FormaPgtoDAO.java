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

import casa.cheff.customerwebapp.bean.db.FormaPgto;
import casa.cheff.customerwebapp.bean.db.IUser;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.bean.db.Sabor;
import casa.cheff.customerwebapp.db.ConnectionManager;

public class FormaPgtoDAO {

	private static final String TAG = "SABOR_DAO";
	
	public List<FormaPgto> getFormasPgtoAtivos(int codEstabelecimento) {
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<FormaPgto> sabores = new ArrayList<FormaPgto>();
		String selectSQL = "				SELECT *                                                                    "
				+ "				  FROM forma_pgto fp                                                            "
				+ "				 WHERE fp.cod_estabelecimento = ?    AND ind_ativo = 'S'                                      ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codEstabelecimento);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				FormaPgto formaPgto = new FormaPgto();
				formaPgto.codFormaPgto = rs.getInt("cod_forma_pgto");
				formaPgto.codEstabelecimento = rs.getInt("cod_estabelecimento");
				formaPgto.nomFormaPgto = rs.getString("nom_forma_pgto");
				formaPgto.indAtivo = rs.getString("ind_ativo");
				formaPgto.datCadastro = rs.getDate("dat_cadastro");
				sabores.add(formaPgto);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar FormaPgto do banco de dados. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em getFormasPgtoAtivos. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return sabores;
	}

	public List<FormaPgto> getFormasPgto(int codEstabelecimento, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<FormaPgto> sabores = new ArrayList<FormaPgto>();
		String selectSQL = "				SELECT *                                                                    "
				+ "				  FROM forma_pgto sab                                                            "
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
				FormaPgto formaPgto = new FormaPgto();
				formaPgto.codFormaPgto = rs.getInt("cod_forma_pgto");
				formaPgto.codEstabelecimento = rs.getInt("cod_estabelecimento");
				formaPgto.nomFormaPgto = rs.getString("nom_forma_pgto");
				formaPgto.indAtivo = rs.getString("ind_ativo");
				formaPgto.datCadastro = rs.getDate("dat_cadastro");
				sabores.add(formaPgto);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar FormaPgto do banco de dados. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em FormaPgto. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return sabores;
	}

	public int insertFormaPgto(FormaPgto sabor) {
		int codCardapio = -1;
		String sqlInsert = "INSERT INTO forma_pgto ( cod_estabelecimento                          "
				+ "                           , nom_forma_pgto                                    "
				+ "                           , ind_ativo                                    "
				+ "                           , dat_cadastro)                                "
				+ "       VALUES (?, ?, ?, ?)                                             ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, sabor.codEstabelecimento);
			pstmt.setString(2, sabor.nomFormaPgto);
			pstmt.setString(3, sabor.indAtivo);
			pstmt.setDate(4, new java.sql.Date(new Date().getTime()));
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				codCardapio = rs.getInt(1);
			} else {
				throw new RuntimeException(
						"não foram geradas as chaves em insertFormaPgto...");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao inserir FormaPgto no DB. TAG = "
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
						"Erro ao fechar a conexão com o DB em insertFormaPgto: "
								+ e.getMessage());
			}
		}
		return codCardapio;
	}

	public int updateFormaPgto(FormaPgto sabor, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		int qtyUpdated = 0;
		String sqlUpdate = "UPDATE forma_pgto sab                                                   "
				+ "   SET sab.nom_forma_pgto = ?                                                    "
				+ "     , sab.ind_ativo = ?                                                    "
				+ " WHERE sab.cod_forma_pgto = ?                                                    "
				+ "   AND sab.cod_estabelecimento = ?                                          "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = sab.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setString(1, sabor.nomFormaPgto);
			pstmt.setString(2, sabor.indAtivo);
			pstmt.setInt(3, sabor.codFormaPgto);
			pstmt.setInt(4, sabor.codEstabelecimento);
			pstmt.setInt(5, parceiro.codParceiro);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao atualizar FormaPgto no DB. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em updateFormaPgto: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}
}

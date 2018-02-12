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

import org.eclipse.jetty.util.security.Credential.MD5;

import casa.cheff.customerwebapp.bean.db.Parceiro;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.db.ConnectionManager;

public class ParceiroDAO {

	private static final String TAG = "CARDAPIO_DAO";

	public List<Parceiro> getParceirosByToken(int codParceiro) {
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Parceiro> parceiros = new ArrayList<Parceiro>();
		String selectSQL = "SELECT cod_parceiro                                            "
				+ "     , des_email                                                  "
				+ "     , 'huehue' des_senha                                         "
				+ "     , nom_usuario_web                                            "
				+ "     , num_cpf_cnpj                                               "
				+ "     , des_endereco                                               "
				+ "     , des_complemento                                            "
				+ "     , des_bairro                                                 "
				+ "     , nom_cidade                                                 "
				+ "     , ind_forma_pgto                                             "
				+ "     , num_banco                                                  "
				+ "     , num_agencia                                                "
				+ "     , num_conta                                                  "
				+ "     , ind_ativo                                                  "
				+ "     , dat_cadastro                                               "
				+ "  FROM parceiro prc                                               "
				+ " WHERE prc.cod_parceiro = ?                                       "
				+ "	   OR EXISTS (SELECT 1                                           "
				+ "		  		    FROM parceiro admin                              "
				+ "				   WHERE admin.ind_admin = 'S'                       "
				+ "				     AND admin.cod_parceiro = ?)                     "
				+ " ORDER BY 1 DESC                                                  ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codParceiro);
			pstmt.setInt(2, codParceiro);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Parceiro parceiro = new Parceiro();
				parceiro.codParceiro = rs.getInt("cod_parceiro");
				parceiro.desEmail = rs.getString("des_email");
				parceiro.desSenha = rs.getString("des_senha");
				parceiro.nomParceiro = rs.getString("nom_usuario_web");
				parceiro.numCpfCnpj = rs.getString("num_cpf_cnpj");
				parceiro.desEndereco = rs.getString("des_endereco");
				parceiro.desComplemento = rs.getString("des_complemento");
				parceiro.desBairro = rs.getString("des_bairro");
				parceiro.nomCidade = rs.getString("nom_cidade");
				parceiro.indFormaPgto = rs.getString("ind_forma_pgto");
				parceiro.numBanco = rs.getString("num_banco");
				parceiro.numAgencia = rs.getString("num_agencia");
				parceiro.numConta = rs.getString("num_conta");
				parceiro.indAtivo = rs.getString("ind_ativo");
				//parceiro.datCadastro = new Date(rs.getDate("dat_cadastro")
				//		.getTime());
				parceiros.add(parceiro);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os parceeiros do banco de dados. TAG = "
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
						"Erro ao fechar a conexão com o DB em getParceiros. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return parceiros;
	}

	public int insertParceiro(Parceiro parceiro) {
		int codCardapio = -1;
		String sqlInsert = " INSERT INTO parceiro        "
				+ " (des_email,                          "
				+ " des_senha,                           "
				+ " nom_usuario_web,                     "
				+ " num_cpf_cnpj,                        "
				+ " des_endereco,                        "
				+ " des_complemento,                     "
				+ " des_bairro,                          "
				+ " nom_cidade,                          "
				+ " ind_forma_pgto,                      "
				+ " num_banco,                           "
				+ " num_agencia,                         "
				+ " num_conta,                           "
				+ " ind_ativo,                           "
				+ " dat_cadastro)                        "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, parceiro.desEmail);
			pstmt.setString(2, parceiro.desSenha);
			pstmt.setString(3, parceiro.nomParceiro);
			pstmt.setString(4, parceiro.numCpfCnpj);
			pstmt.setString(5, parceiro.desEndereco);
			pstmt.setString(6, parceiro.desComplemento);
			pstmt.setString(7, parceiro.desBairro);
			pstmt.setString(8, parceiro.nomCidade);
			pstmt.setString(9, parceiro.indFormaPgto);
			pstmt.setString(10, parceiro.numBanco);
			pstmt.setString(11, parceiro.numAgencia);
			pstmt.setString(12, parceiro.numConta);
			pstmt.setString(13, parceiro.indAtivo);
			pstmt.setDate(14, new java.sql.Date(new Date().getTime()));
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				codCardapio = rs.getInt(1);
			} else {
				throw new RuntimeException(
						"não foram geradas as chaves em insertParceiro...");
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao inserir um parceiro no DB. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em insertParceiro: "
								+ e.getMessage());
			}
		}
		return codCardapio;
	}

	public int updateParceiro(Parceiro parceiro) {
		int qtyUpdated = 0;
		String sqlUpdate = " UPDATE parceiro                 "
				+ " SET                                         "
				+ " des_email = ?,                              "
				+ " des_senha = COALESCE(?, des_senha),         "
				+ " nom_usuario_web = ?,                        "
				+ " num_cpf_cnpj = ?,                           "
				+ " des_endereco = ?,                           "
				+ " des_complemento = ?,                        "
				+ " des_bairro = ?,                             "
				+ " nom_cidade = ?,                             "
				+ " ind_forma_pgto = ?,                         "
				+ " num_banco = ?,                              "
				+ " num_agencia = ?,                            "
				+ " num_conta = ?,                              "
				+ " ind_ativo = ?                               "
				+ " WHERE cod_parceiro = ?;                  ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setString(1, parceiro.desEmail);
			pstmt.setString(2, parceiro.desSenha);
			pstmt.setString(3, parceiro.nomParceiro);
			pstmt.setString(4, parceiro.numCpfCnpj);
			pstmt.setString(5, parceiro.desEndereco);
			pstmt.setString(6, parceiro.desComplemento);
			pstmt.setString(7, parceiro.desBairro);
			pstmt.setString(8, parceiro.nomCidade);
			pstmt.setString(9, parceiro.indFormaPgto);
			pstmt.setString(10, parceiro.numBanco);
			pstmt.setString(11, parceiro.numAgencia);
			pstmt.setString(12, parceiro.numConta);
			pstmt.setString(13, parceiro.indAtivo);
			pstmt.setInt(14, parceiro.codParceiro);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao atualizar um parceiro no DB. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em updateParceiro: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}

	public ParceiroAuth getParceiroByToken(String token) {
		Connection conn = ConnectionManager.getConnection(TAG);
		ParceiroAuth parceiro = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String selectSQL = "SELECT cod_parceiro                                            "
				+ "     , des_email                                                  "
				+ "     , des_senha                                                  "
				+ "     , nom_usuario_web                                            "
				+ "     , num_cpf_cnpj                                               "
				+ "     , des_endereco                                               "
				+ "     , des_complemento                                            "
				+ "     , des_bairro                                                 "
				+ "     , nom_cidade                                                 "
				+ "     , ind_forma_pgto                                             "
				+ "     , num_banco                                                  "
				+ "     , num_agencia                                                "
				+ "     , num_conta                                                  "
				+ "     , ind_ativo                                                  "
				+ "     , dat_cadastro                                               "
				+ "     , ind_admin                                                  "
				+ "  FROM parceiro prc                                               "
				+ " WHERE prc.aut_token = ?                                          ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setString(1, token);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				parceiro = new ParceiroAuth();
				parceiro.codParceiro = rs.getInt("cod_parceiro");
				parceiro.desEmail = rs.getString("des_email");
				parceiro.desSenha = rs.getString("des_senha");
				parceiro.nomParceiro = rs.getString("nom_usuario_web");
				parceiro.numCpfCnpj = rs.getString("num_cpf_cnpj");
				parceiro.desEndereco = rs.getString("des_endereco");
				parceiro.desComplemento = rs.getString("des_complemento");
				parceiro.desBairro = rs.getString("des_bairro");
				parceiro.nomCidade = rs.getString("nom_cidade");
				parceiro.indFormaPgto = rs.getString("ind_forma_pgto");
				parceiro.numBanco = rs.getString("num_banco");
				parceiro.numAgencia = rs.getString("num_agencia");
				parceiro.numConta = rs.getString("num_conta");
				parceiro.indAtivo = rs.getString("ind_ativo");
				parceiro.indAdmin = rs.getString("ind_admin");
				// TODO verify
				//parceiro.datCadastro = new Date(rs.getDate("dat_cadastro")
				//		.getTime());

			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Erro ao buscar os parceeiros do banco de dados através do token. TAG = "
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
						"Erro ao fechar a conexão com o DB em getParceiros através do token. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return parceiro;
	}

	public String getParceiroTokenByUsernamePassword(String userName,
			String password) {
		Connection conn = ConnectionManager.getConnection(TAG);
		String token = null;
		PreparedStatement pstmtSelect = null, pstmtUpdate = null;
		ResultSet rs = null;
		String sql = "SELECT prc.cod_parceiro, prc.aut_token                "
				+ "  FROM parceiro prc                                               "
				+ " WHERE prc.des_email = ?                                          "
				+ "   AND prc.des_senha = ?                                          ";
		try {
			pstmtSelect = conn.prepareStatement(sql);
			pstmtSelect.setString(1, userName);
			pstmtSelect.setString(2, password);
			rs = pstmtSelect.executeQuery();
			if (rs.next()) {
				token = rs.getString("aut_token");
				int codParceiro = rs.getInt("cod_parceiro");
				if (token == null | "".equals(token)) {
					token = MD5.digest(codParceiro + password + new Date());
					token = token.substring(4);
					sql = "UPDATE parceiro                "
							+ "   SET aut_token = ?       "
							+ " WHERE cod_parceiro = ? ";
					pstmtUpdate = conn.prepareStatement(sql);
					pstmtUpdate.setString(1, token);
					pstmtUpdate.setInt(2, codParceiro);
					int res = pstmtUpdate.executeUpdate();
					if (res == 0) {
						token = null;
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os parceeiros do banco de dados através do usuário. TAG = "
							+ TAG + " MSG = " + e.getMessage());
		} finally {
			try {
				if (pstmtSelect != null) {
					if (!pstmtSelect.isClosed()) {
						pstmtSelect.close();
					}
				}
				if (pstmtUpdate != null) {
					if (!pstmtUpdate.isClosed()) {
						pstmtUpdate.close();
					}
				}
				if (conn != null) {
					if (!conn.isClosed()) {
						conn.close();
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException(
						"Erro ao fechar a conexão com o DB em getParceiros através do usuário. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return token;
	}
}

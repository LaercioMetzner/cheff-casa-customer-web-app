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

import casa.cheff.customerwebapp.bean.db.IUser;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.bean.db.Sabor;
import casa.cheff.customerwebapp.bean.db.Usuario;
import casa.cheff.customerwebapp.db.ConnectionManager;

public class UsuarioDAO {

	private static final String TAG = "USUARIO_DAO";

	public Usuario getUsuarioByFbId(String facebookId){
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String selectSQL = "				SELECT *                                                                    "
				+ "				  FROM usuario                                                            "
				+ "				 WHERE des_facebook_id = ?                                          ";
		Usuario usuario = null;
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setString(1, facebookId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				usuario = new Usuario();
				usuario.codUsuario = rs.getInt("cod_usuario");
				usuario.nomUsuario = rs.getString("nom_usuario");
				usuario.numFone = rs.getString("num_fone");
				usuario.desEmail = rs.getString("des_email");
				usuario.desFacebookId = rs.getString("des_facebook_id");
				usuario.desEndereco = rs.getString("des_endereco");
				usuario.desComplemento = rs.getString("des_complemento");
				usuario.desBairro = rs.getString("des_bairro");
				usuario.nomCidade = rs.getString("nom_cidade");
				usuario.indAtivo = rs.getString("ind_ativo");
				usuario.datCadastro = new Date(rs.getDate("dat_cadastro").getTime());
				usuario.latPadrao = rs.getFloat("lat_padrao");
				usuario.lonPadrao = rs.getFloat("lon_padrao");
				usuario.latUltima = rs.getFloat("lat_ultima");
				usuario.lonUltima = rs.getFloat("lon_ultima");
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar usuario por facebook do banco de dados. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em getUsuarioByFbId. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return usuario;

	}
	

	public int insertUsuario(Usuario usuario) {
		int codUsuario = -1;
		String sqlInsert = " INSERT INTO `jfood`.`usuario`                                              "
				+ "(`num_cpf`,                                              "
				+ "`nom_usuario`,                                              "
				+ "`num_fone`,                                              "
				+ "`des_email`,                                              "
				+ "`des_facebook_id`,                                              "
				+ "`des_endereco`,                                              "
				+ "`des_complemento`,                                              "
				+ "`des_bairro`,                                              "
				+ "`nom_cidade`,                                              "
				+ "`ind_ativo`,                                              "
				+ "`dat_cadastro`,                                              "
				+ "`lat_padrao`,                                              "
				+ "`lon_padrao`,                                              "
				+ "`lat_ultima`,                                              "
				+ "`lon_ultima`)                                              "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, usuario.numCPF);
			pstmt.setString(2, usuario.nomUsuario);
			pstmt.setString(3, usuario.numFone);
			pstmt.setString(4, usuario.desEmail);
			pstmt.setString(5, usuario.desFacebookId);
			pstmt.setString(6, usuario.desEndereco);
			pstmt.setString(7, usuario.desComplemento);
			pstmt.setString(8, usuario.desBairro);
			pstmt.setString(9, usuario.nomCidade);
			pstmt.setString(10, usuario.indAtivo);
			pstmt.setDate(11, new java.sql.Date(usuario.datCadastro.getTime()));
			pstmt.setFloat(12, usuario.latPadrao);
			pstmt.setFloat(13, usuario.lonPadrao);
			pstmt.setFloat(14, usuario.latUltima);
			pstmt.setFloat(15, usuario.lonUltima);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				codUsuario = rs.getInt(1);
			} else {
				throw new RuntimeException(
						"não foram geradas as chaves em insertUsuario...");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao inserir um Usuario no DB. TAG = "
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
						"Erro ao fechar a conexão com o DB em insertUsuario: "
								+ e.getMessage());
			}
		}
		return codUsuario;
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

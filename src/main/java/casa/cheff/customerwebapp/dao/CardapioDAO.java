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

import casa.cheff.customerwebapp.bean.db.Cardapio;
import casa.cheff.customerwebapp.bean.db.CardapioComEstabelecimento;
import casa.cheff.customerwebapp.bean.db.IUser;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.db.ConnectionManager;

public class CardapioDAO {

	private static final String TAG = "CARDAPIO_DAO";

	public List<Cardapio> getCardapiosAtivos(int codEstabelecimento) {
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Cardapio> cardapios = new ArrayList<Cardapio>();
		String selectSQL = "SELECT c.*                                  "
				+ "  FROM cardapio c                                    "
				+ " WHERE c.ind_ativo = 'S'                             "
				+ "   AND c.cod_estabelecimento = ?                     "
				+ " ORDER BY num_ordem                                  ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codEstabelecimento);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Cardapio cardapio = new Cardapio();
				cardapio.codCardapio = rs.getInt("cod_cardapio");
				cardapio.codEstabelecimento = rs.getInt("cod_estabelecimento");
				cardapio.indAtivo = rs.getString("ind_ativo");
				cardapio.nomCardapio = rs.getString("nom_cardapio");
				cardapio.numOrdem = rs.getInt("num_ordem");
				cardapio.datCadastro = rs.getDate("dat_cadastro");
				cardapios.add(cardapio);
			}
		} catch (SQLException e) {

			throw new RuntimeException(
					"Erro ao buscar os cardapios do banco de dados. TAG = " + TAG + " MSG = " + e.getMessage());
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
				throw new RuntimeException("Erro ao fechar a conexão com o DB em getCardapiosAtivos. TAG = " + TAG
						+ " MSG = " + e.getMessage());
			}
		}
		return cardapios;
	}

	public List<CardapioComEstabelecimento> getCardapios(int codEstabelecimento, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<CardapioComEstabelecimento> cardapios = new ArrayList<CardapioComEstabelecimento>();
		String selectSQL = "SELECT c.* " + "     , e.nom_estabelecimento                          "
				+ "  FROM cardapio c                                     "
				+ "     , estabelecimento e                              "
				+ " WHERE c.cod_estabelecimento = e.cod_estabelecimento  "
				+ "   AND c.cod_estabelecimento = ?                      "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = e.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           "
				+ " ORDER BY num_ordem                                   ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codEstabelecimento);
			pstmt.setInt(2, parceiro.codParceiro);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				CardapioComEstabelecimento cardapio = new CardapioComEstabelecimento();
				cardapio.codCardapio = rs.getInt("cod_cardapio");
				cardapio.codEstabelecimento = rs.getInt("cod_estabelecimento");
				cardapio.nomEstabelecimento = rs.getString("nom_estabelecimento");
				cardapio.indAtivo = rs.getString("ind_ativo");
				cardapio.nomCardapio = rs.getString("nom_cardapio");
				cardapio.numOrdem = rs.getInt("num_ordem");
				cardapio.datCadastro = rs.getDate("dat_cadastro");
				cardapios.add(cardapio);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os cardapios do banco de dados. TAG = " + TAG + " MSG = " + e.getMessage());
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
						"Erro ao fechar a conexão com o DB em getCardapios. TAG = " + TAG + " MSG = " + e.getMessage());
			}
		}
		return cardapios;
	}

	public int insertCardapio(Cardapio cardapio) {
		int codCardapio = -1;
		String sqlInsert = "INSERT INTO cardapio ( cod_estabelecimento                       "
				+ "                            , nom_cardapio                                "
				+ "                            , num_ordem                                   "
				+ "                            , ind_ativo                                   "
				+ "                            , dat_cadastro)                               "
				+ "       VALUES (?, ?, ?, ?, ?)                                             ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, cardapio.codEstabelecimento);
			pstmt.setString(2, cardapio.nomCardapio);
			pstmt.setInt(3, cardapio.numOrdem);
			pstmt.setString(4, cardapio.indAtivo);
			pstmt.setDate(5, new java.sql.Date(new Date().getTime()));
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				codCardapio = rs.getInt(1);
			} else {
				throw new RuntimeException("não foram geradas as chaves em insertCardapio...");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao inserir um cardapio no DB. TAG = " + TAG + " MSG = " + e.getMessage());
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
				throw new RuntimeException("Erro ao fechar a conex�o com o DB em insertCardapio: " + e.getMessage());
			}
		}
		return codCardapio;
	}

	public int updateCardapio(Cardapio cardapio, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		int qtyUpdated = 0;
		String sqlUpdate = "UPDATE cardapio car                                             "
				+ "   SET nom_cardapio = ?                                                  "
				+ "     , num_ordem = ?                                                     "
				+ "     , ind_ativo = ?                                                     "
				+ " WHERE cod_cardapio = ?                                                  "
				+ "   AND cod_estabelecimento = ?                                           "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = car.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setString(1, cardapio.nomCardapio);
			pstmt.setInt(2, cardapio.numOrdem);
			pstmt.setString(3, cardapio.indAtivo);
			pstmt.setInt(4, cardapio.codCardapio);
			pstmt.setInt(5, cardapio.codEstabelecimento);
			pstmt.setInt(6, parceiro.codParceiro);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao atualizar um cardapio no DB. TAG = " + TAG + " MSG = " + e.getMessage());
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
				throw new RuntimeException("Erro ao fechar a conexão com o DB em updateCardapio: " + e.getMessage());
			}
		}
		return qtyUpdated;
	}
}

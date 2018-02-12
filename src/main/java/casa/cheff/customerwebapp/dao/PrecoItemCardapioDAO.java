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
import casa.cheff.customerwebapp.bean.db.ItemCardapio;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.bean.db.PrecoItemCardapio;
import casa.cheff.customerwebapp.db.ConnectionManager;

public class PrecoItemCardapioDAO {

	private static final String TAG = "	PRECO_ITEM_CARDAPIO_DAO";

	public List<PrecoItemCardapio> getPrecostItemCardapioAtivos(
			int codEstabelecimento, int codCardapio, int codItemCardapio) {
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<PrecoItemCardapio> precoItensCardapio = new ArrayList<PrecoItemCardapio>();
		String selectSQL = "SELECT prec.cod_preco, prec.cod_item_cardapio                                                  "
				+ "     , prec.cod_cardapio                                                        "
				+ "     , prec.cod_estabelecimento                                                 "
				+ "     , prec.cod_sabor                                                           "
				+ "     , prec.cod_tamanho                                                         "
				+ "     , prec.qtd_sabor                                                           "
				+ "     , prec.val_preco                                                           "
				+ "     , prec.cod_legado                                                          "
				+ "     , prec.cod_barra                                                           "
				+ "     , prec.ind_ativo                                                           "
				+ "     , prec.dat_cadastro                                                        "
				+ "     , sab.nom_sabor                                                            "
				+ "     , tam.nom_tamanho                                                          "
				+ "     , itm.nom_item_cardapio                                                    "
				+ "  FROM preco_item_cardapio prec                                                 "
				+ " INNER JOIN item_cardapio itm ON prec.cod_item_cardapio = itm.cod_item_cardapio "
				+ "                     AND prec.cod_cardapio = itm.cod_cardapio                   "
				+ "                     AND prec.cod_estabelecimento = itm.cod_estabelecimento     "
				+ "  LEFT JOIN sabor sab ON prec.cod_sabor = sab.cod_sabor                         "
				+ "                     AND prec.cod_estabelecimento = sab.cod_estabelecimento     "
				+ "  LEFT JOIN tamanho tam ON prec.cod_tamanho = tam.cod_tamanho                   "
				+ "                       AND prec.cod_estabelecimento = tam.cod_estabelecimento   "
				+ " WHERE prec.ind_ativo = 'S'                                                     "
				+ "   AND prec.cod_cardapio = ?                                                    "
				+ "   AND prec.cod_estabelecimento = ?                                             "
				+ "   AND prec.cod_item_cardapio = ?                                               ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codCardapio);
			pstmt.setInt(2, codEstabelecimento);
			pstmt.setInt(3, codItemCardapio);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				PrecoItemCardapio precoItemCardapio = new PrecoItemCardapio();
				precoItemCardapio.codItemCardapio = rs
						.getInt("cod_item_cardapio");
				precoItemCardapio.codPreco = rs.getInt("cod_preco");
				precoItemCardapio.codCardapio = rs.getInt("cod_cardapio");
				precoItemCardapio.codEstabelecimento = rs
						.getInt("cod_estabelecimento");
				precoItemCardapio.valPreco = rs.getFloat("val_preco");
				precoItemCardapio.codSabor = rs.getInt("cod_sabor");
				precoItemCardapio.codTamanho = rs.getInt("cod_tamanho");
				precoItemCardapio.qtdSabor = rs.getInt("qtd_sabor");
				precoItemCardapio.indAtivo = rs.getString("ind_ativo");
				precoItemCardapio.datCadastro = rs.getDate("dat_cadastro");
				precoItemCardapio.nomSabor = rs.getString("nom_sabor");
				precoItemCardapio.desTamanho = rs.getString("nom_tamanho");
				precoItemCardapio.nomItemCardapio = rs.getString("nom_item_cardapio");
				precoItemCardapio.codLegado = rs.getString("cod_legado");
				precoItemCardapio.codBarra = rs.getString("cod_barra");
				precoItensCardapio.add(precoItemCardapio);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os preços dos itens do cardapio do banco de dados. TAG = "
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
						"Erro ao fechar a conexão com o DB em getPrecostItemCardapioAtivos. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return precoItensCardapio;
	}

	
	public List<PrecoItemCardapio> getPrecostItemCardapio(
			int codEstabelecimento, int codCardapio, int codItemCardapio,
			IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<PrecoItemCardapio> precoItensCardapio = new ArrayList<PrecoItemCardapio>();
		String selectSQL = "SELECT prec.cod_preco, prec.cod_item_cardapio                                                  "
				+ "     , prec.cod_cardapio                                                        "
				+ "     , prec.cod_estabelecimento                                                 "
				+ "     , prec.cod_sabor                                                           "
				+ "     , prec.cod_tamanho                                                         "
				+ "     , prec.qtd_sabor                                                           "
				+ "     , prec.val_preco                                                           "
				+ "     , prec.ind_ativo                                                           "
				+ "     , prec.cod_legado                                                          "
				+ "     , prec.cod_barra                                                           "
				+ "     , prec.dat_cadastro                                                        "
				+ "     , sab.nom_sabor                                                            "
				+ "     , tam.nom_tamanho                                                          "
				+ "     , itm.nom_item_cardapio                                                    "
				+ "  FROM preco_item_cardapio prec                                                 "
				+ " INNER JOIN item_cardapio itm ON prec.cod_item_cardapio = itm.cod_item_cardapio "
				+ "                     AND prec.cod_cardapio = itm.cod_cardapio                   "
				+ "                     AND prec.cod_estabelecimento = itm.cod_estabelecimento     "
				+ "  LEFT JOIN sabor sab ON prec.cod_sabor = sab.cod_sabor                         "
				+ "                     AND prec.cod_estabelecimento = sab.cod_estabelecimento     "
				+ "  LEFT JOIN tamanho tam ON prec.cod_tamanho = tam.cod_tamanho                   "
				+ "                       AND prec.cod_estabelecimento = tam.cod_estabelecimento   "
				+ " WHERE prec.cod_cardapio = ?                                                    "
				+ "   AND prec.cod_estabelecimento = ?                                             "
				+ "   AND prec.cod_item_cardapio = ?                                               "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = prec.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codCardapio);
			pstmt.setInt(2, codEstabelecimento);
			pstmt.setInt(3, codItemCardapio);
			pstmt.setInt(4, parceiro.codParceiro);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				PrecoItemCardapio precoItemCardapio = new PrecoItemCardapio();
				precoItemCardapio.codItemCardapio = rs
						.getInt("cod_item_cardapio");
				precoItemCardapio.codPreco = rs.getInt("cod_preco");
				precoItemCardapio.codCardapio = rs.getInt("cod_cardapio");
				precoItemCardapio.codEstabelecimento = rs
						.getInt("cod_estabelecimento");
				precoItemCardapio.valPreco = rs.getFloat("val_preco");
				precoItemCardapio.codSabor = rs.getInt("cod_sabor");
				precoItemCardapio.codTamanho = rs.getInt("cod_tamanho");
				precoItemCardapio.qtdSabor = rs.getInt("qtd_sabor");
				precoItemCardapio.indAtivo = rs.getString("ind_ativo");
				precoItemCardapio.datCadastro = rs.getDate("dat_cadastro");
				precoItemCardapio.nomSabor = rs.getString("nom_sabor");
				precoItemCardapio.desTamanho = rs.getString("nom_tamanho");
				precoItemCardapio.nomItemCardapio = rs.getString("nom_item_cardapio");
				precoItemCardapio.codLegado = rs.getString("cod_legado");
				precoItemCardapio.codBarra = rs.getString("cod_barra");
				precoItensCardapio.add(precoItemCardapio);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os preços dos itens do cardapio do banco de dados. TAG = "
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
						"Erro ao fechar a conexão com o DB em gePrecostItemCardapio. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return precoItensCardapio;
	}

	public int insertPrecoItemCardapio(PrecoItemCardapio precoItemCardapio) {
		int codPrecoItemCardapio = -1;
		String sqlInsert = "INSERT INTO preco_item_cardapio ( cod_item_cardapio "
				+ ", cod_cardapio                                      "
				+ ", cod_estabelecimento                               "
				+ ", cod_sabor                                         "
				+ ", cod_tamanho                                       "
				+ ", qtd_sabor                                         "
				+ ", val_preco                                         "
				+ ", ind_ativo                                         "
				+ ", dat_cadastro                                      "
				+ ", cod_legado                                        "
				+ ", cod_barra)                                         "
				+ "VALUES                                              "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);                  ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, precoItemCardapio.codItemCardapio);
			pstmt.setInt(2, precoItemCardapio.codCardapio);
			pstmt.setInt(3, precoItemCardapio.codEstabelecimento);
			pstmt.setInt(4, precoItemCardapio.codSabor);
			pstmt.setInt(5, precoItemCardapio.codTamanho);
			pstmt.setInt(6, precoItemCardapio.qtdSabor);
			pstmt.setFloat(7, precoItemCardapio.valPreco);
			pstmt.setString(8, precoItemCardapio.indAtivo);
			pstmt.setDate(9, new java.sql.Date(new Date().getTime()));
			pstmt.setString(10, precoItemCardapio.codLegado);
			pstmt.setString(11, precoItemCardapio.codBarra);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				codPrecoItemCardapio = rs.getInt(1);
			} else {
				throw new RuntimeException(
						"não foram geradas as chaves em insertPrecoItemCardapio...");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao inserir um preço no DB. TAG = "
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
						"Erro ao fechar a conexão com o DB em insertPrecoItemCardapio: "
								+ e.getMessage());
			}
		}
		return codPrecoItemCardapio;
	}

	public int updatePrecoItemCardapio(PrecoItemCardapio precoItemCardapio, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		int qtyUpdated = 0;
		String sqlUpdate =  "UPDATE preco_item_cardapio p "
				+ "   SET cod_item_cardapio = ?"
				+ "     , cod_cardapio = ?"
				+ "     , cod_estabelecimento = ?"
				+ "     , cod_sabor = ?"
				+ "     , cod_tamanho = ?"
				+ "     , qtd_sabor = ?"
				+ "     , val_preco = ?"
				+ "     , ind_ativo = ?"
				+ "     , cod_legado = ?"
				+ "     , cod_barra = ?"
			//	+ "     , dat_cadastro = ?"
				+ " WHERE cod_preco = ?"
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = p.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setInt(1, precoItemCardapio.codItemCardapio);
			pstmt.setInt(2, precoItemCardapio.codCardapio);
			pstmt.setInt(3, precoItemCardapio.codEstabelecimento);
			pstmt.setInt(4, precoItemCardapio.codSabor);
			pstmt.setInt(5, precoItemCardapio.codTamanho);
			pstmt.setInt(6, precoItemCardapio.qtdSabor);
			pstmt.setFloat(7, precoItemCardapio.valPreco);
			pstmt.setString(8, precoItemCardapio.indAtivo);
			pstmt.setString(9, precoItemCardapio.codLegado);
			pstmt.setString(10, precoItemCardapio.codBarra);
			pstmt.setInt(11, precoItemCardapio.codPreco);
			pstmt.setInt(12, parceiro.codParceiro);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao atualizar um Preço no DB. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em updatePrecoItemCardapio: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}
}

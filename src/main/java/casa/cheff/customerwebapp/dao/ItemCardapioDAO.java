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
import casa.cheff.customerwebapp.db.ConnectionManager;

public class ItemCardapioDAO {

	private static final String TAG = "ITEM_CARDAPIO_DAO";
	
	
	
	public List<ItemCardapio> getItensCardapioAtivos(int codEstabelecimento,
			int codCardapio) {
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<ItemCardapio> itensCardapio = new ArrayList<ItemCardapio>();
		String selectSQL = " SELECT ic.*                                                          "
				+ "               , foto.des_source                                               "
				+ "   FROM item_cardapio ic                                              "
			    + "  LEFT JOIN flickr_photo  foto                                        " 
                + "    ON foto.cod_photo_id = ic.cod_photo_id_item                       "
			    + "   AND foto.num_label = (SELECT MAX(ph.num_label)                     "
			    + "                           FROM flickr_photo ph                       "
			    + "                          WHERE ph.cod_photo_id = foto.cod_photo_id   "
			    + "                            AND ph.num_label <> 5)                    "
				+ "  WHERE ic.ind_ativo = 'S'                                            "
				+ "    AND ic.cod_cardapio = ?                                           "
				+ "    AND ic.cod_estabelecimento = ?                                    "
				+ " ORDER BY num_ordem                                                   ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codCardapio);
			pstmt.setInt(2, codEstabelecimento);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ItemCardapio itemCardapio = new ItemCardapio();
				itemCardapio.codItemCardapio = rs.getInt("cod_item_cardapio");
				itemCardapio.codCardapio = rs.getInt("cod_cardapio");
				itemCardapio.codEstabelecimento = rs
						.getInt("cod_estabelecimento");
				itemCardapio.indAtivo = rs.getString("ind_ativo");
				itemCardapio.nomItemCardapio = rs
						.getString("nom_item_cardapio");
				itemCardapio.desItemCardapio = rs
						.getString("des_item_cardapio");
				itemCardapio.numOrdem = rs.getInt("num_ordem");
				itemCardapio.datCadastro = rs.getDate("dat_cadastro");
				itemCardapio.codPhotoIdItem = rs.getString("cod_photo_id_item");
				itemCardapio.desSourcePhoto = rs.getString("des_source");
				itensCardapio.add(itemCardapio);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os itens do cardapio do banco de dados. TAG = "
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
						"Erro ao fechar a conexão com o DB em getCardapios. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return itensCardapio;
	}

	public List<ItemCardapio> getItensCardapio(int codEstabelecimento,
			int codCardapio, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<ItemCardapio> itensCardapio = new ArrayList<ItemCardapio>();
		String selectSQL = " SELECT ic.*                                                          "
				+ "               , foto.des_source                                               "
				+ "   FROM item_cardapio ic                                              "
			    + "  LEFT JOIN flickr_photo  foto                                        " 
                + "    ON foto.cod_photo_id = ic.cod_photo_id_item                       "
			    + "   AND foto.num_label = 1                                             "
				+ "  WHERE ic.cod_cardapio = ?                                           "
				+ "    AND ic.cod_estabelecimento = ?                                    "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = ic.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codCardapio);
			pstmt.setInt(2, codEstabelecimento);
			pstmt.setInt(3, parceiro.codParceiro);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ItemCardapio itemCardapio = new ItemCardapio();
				itemCardapio.codItemCardapio = rs.getInt("cod_item_cardapio");
				itemCardapio.codCardapio = rs.getInt("cod_cardapio");
				itemCardapio.codEstabelecimento = rs
						.getInt("cod_estabelecimento");
				itemCardapio.indAtivo = rs.getString("ind_ativo");
				itemCardapio.nomItemCardapio = rs
						.getString("nom_item_cardapio");
				itemCardapio.desItemCardapio = rs
						.getString("des_item_cardapio");				
				itemCardapio.numOrdem = rs.getInt("num_ordem");
				itemCardapio.datCadastro = rs.getDate("dat_cadastro");
				itemCardapio.codPhotoIdItem = rs.getString("cod_photo_id_item");
				itemCardapio.desSourcePhoto = rs.getString("des_source");
				itensCardapio.add(itemCardapio);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os itens do cardapio do banco de dados. TAG = "
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
						"Erro ao fechar a conexão com o DB em getCardapios. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return itensCardapio;
	}

	public int insertItemCardapio(ItemCardapio itemCardapio) {
		int codItemCardapio = -1;
		String sqlInsert = " INSERT INTO item_cardapio                                             "
				+ "        (cod_cardapio,                                                 "
				+ "         cod_estabelecimento,                                          "
				+ "         nom_item_cardapio,                                            "
				+ "         ind_ativo,                                                    "
				+ "         dat_cadastro,                                                 "
				+ "         num_ordem,                                                    "
				+ "         cod_photo_id_item, des_item_cardapio)                                            "
				+ "         VALUES                                                        "
				+ "         (?, ?, ?, ?, ?, ?, ?, ?);                                        ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, itemCardapio.codCardapio);
			pstmt.setInt(2, itemCardapio.codEstabelecimento);
			pstmt.setString(3, itemCardapio.nomItemCardapio);
			pstmt.setString(4, itemCardapio.indAtivo);
			pstmt.setDate(5, new java.sql.Date(new Date().getTime()));
			pstmt.setInt(6, itemCardapio.numOrdem);
			pstmt.setString(7, itemCardapio.codPhotoIdItem);
			pstmt.setString(8, itemCardapio.desItemCardapio);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				codItemCardapio = rs.getInt(1);
			} else {
				throw new RuntimeException(
						"não foram geradas as chaves em insertItemCardapio...");
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao inserir um item de cardapio no DB. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em insertItemCardapio: "
								+ e.getMessage());
			}
		}
		return codItemCardapio;
	}

	public int updateItemCardapio(ItemCardapio itemCardapio, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		int qtyUpdated = 0;
		String sqlUpdate = " UPDATE item_cardapio ic                                     "
				+ "    SET ic.nom_item_cardapio = ?                                      "
				+ "      , ic.ind_ativo = ?                                              "
				// +
				// "      , ic.dat_cadastro = ?                                          "
				+ "      , ic.num_ordem = ?                                              "
				+ "      , cod_photo_id_item = ? , ic.des_item_cardapio = ?                                         "
				+ "  WHERE ic.cod_item_cardapio = ?                                      "
				+ "    AND ic.cod_cardapio = ?                                           "
				+ "    AND ic.cod_estabelecimento = ?                                    "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = ic.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setString(1, itemCardapio.nomItemCardapio);
			pstmt.setString(2, itemCardapio.indAtivo);
			pstmt.setInt(3, itemCardapio.numOrdem);
			pstmt.setString(4, itemCardapio.codPhotoIdItem);
			pstmt.setString(5, itemCardapio.desItemCardapio);
			pstmt.setInt(6, itemCardapio.codItemCardapio);
			pstmt.setInt(7, itemCardapio.codCardapio);
			pstmt.setInt(8, itemCardapio.codEstabelecimento);
			pstmt.setInt(9, parceiro.codParceiro);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao atualizar um cardapio no DB. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em updateItemCardapio: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}
}

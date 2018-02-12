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
import casa.cheff.customerwebapp.json.Pedido;

public class PedidoDAO {

	public static String TAG = "PEDIDO_DAO";

	public int insertPedido(Pedido pedido) {
		int codPedido = -1;
		String sqlInsert = " INSERT INTO pedido            "
				+ " (cod_usuario,                 "
				+ " cod_estabelecimento,          "
				+ " ind_status,                   "
				+ " dat_pedido,                   "
				+ " dat_conhecimento,             "
				+ " dat_saida_entrega,            "
				+ " dat_conf_entrega,             "
				+ " dat_avaliacao,                "
				+ " ind_avaliacao,                "
				+ " obs_avaliacao,                "
				+ " dat_cancelamento,             "
				+ " obs_cancelamento,             "
				+ " obj_dados_pedido)             "
				+ " VALUES                        "
				+ " (?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, pedido.codUsuario);
			pstmt.setInt(2, pedido.codEstabelecimento);
			pstmt.setString(3, pedido.indStatus);
			pstmt.setTimestamp(4,
					pedido.datPedido != null ? new java.sql.Timestamp(
							pedido.datPedido.getTime())
							: new java.sql.Timestamp(new Date().getTime()));
			pstmt.setTimestamp(5,
					pedido.datConhecimento != null ? new java.sql.Timestamp(
							pedido.datConhecimento.getTime()) : null);
			pstmt.setTimestamp(6,
					pedido.datSaidaEntrega != null ? new java.sql.Timestamp(
							pedido.datSaidaEntrega.getTime()) : null);
			pstmt.setTimestamp(7,
					pedido.datConfEntrega != null ? new java.sql.Timestamp(
							pedido.datConfEntrega.getTime()) : null);
			pstmt.setTimestamp(8,
					pedido.datAvaliacao != null ? new java.sql.Timestamp(
							pedido.datAvaliacao.getTime()) : null);
			pstmt.setString(9, pedido.indAvaliacao);
			pstmt.setString(10, pedido.obsAvaliacao);
			pstmt.setTimestamp(11,
					pedido.datCancelamento != null ? new java.sql.Timestamp(
							pedido.datCancelamento.getTime()) : null);
			pstmt.setString(12, pedido.obsCancelamento);
			pstmt.setString(13, pedido.objDadosPedido);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				codPedido = rs.getInt(1);
			} else {
				throw new RuntimeException(
						"não foram geradas as chaves em insertPedido...");
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao inserir um pedido no DB. TAG = " + TAG + " MSG = "
							+ e.getMessage());
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
						"Erro ao fechar a conexão com o DB em insertPedido: "
								+ e.getMessage());
			}
		}
		return codPedido;

	}

	public List<Pedido> getPedidos(int codEstabelecimento, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Pedido> pedidos = new ArrayList<Pedido>();
		String selectSQL = "SELECT cod_pedido                              "
				+ "	, cod_usuario                                          "
				+ "	, cod_estabelecimento                                  "
				+ "	, ind_status                                           "
				+ "	, dat_pedido                                           "
				+ "	, dat_conhecimento                                     "
				+ "	, dat_saida_entrega                                    "
				+ "	, dat_conf_entrega                                     "
				+ "	, dat_avaliacao                                        "
				+ "	, ind_avaliacao                                        "
				+ "	, obs_avaliacao                                        "
				+ "	, dat_cancelamento                                     "
				+ "	, obs_cancelamento                                     "
				+ "	, obj_dados_pedido                                     "
				+ "	, nom_entregador                                       "
				+ "	, des_veiculo                                          "
				+ "  FROM pedido p                                          "
				+ " WHERE cod_estabelecimento = ?                          "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = p.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           "
				+ " ORDER BY cod_pedido DESC                               ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, codEstabelecimento);
			pstmt.setInt(2, parceiro.codParceiro);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Pedido pedido = new Pedido();
				pedido.codPedido = rs.getInt("cod_pedido");
				pedido.codUsuario = rs.getInt("cod_usuario");
				pedido.codEstabelecimento = rs.getInt("cod_estabelecimento");
				pedido.indStatus = rs.getString("ind_status");
				pedido.datPedido = rs.getTimestamp("dat_pedido");
				pedido.datConhecimento = rs.getTimestamp("dat_conhecimento");
				pedido.datSaidaEntrega = rs.getTimestamp("dat_saida_entrega");
				pedido.datConfEntrega = rs.getTimestamp("dat_conf_entrega");
				pedido.datAvaliacao = rs.getTimestamp("dat_avaliacao");
				pedido.indAvaliacao = rs.getString("ind_avaliacao");
				pedido.obsAvaliacao = rs.getString("obs_avaliacao");
				pedido.datCancelamento = rs.getTimestamp("dat_cancelamento");
				pedido.obsCancelamento = rs.getString("obs_cancelamento");
				pedido.objDadosPedido = rs.getString("obj_dados_pedido");
				pedido.nomEntregador = rs.getString("nom_entregador");
				pedido.desVeiculo = rs.getString("des_veiculo");
				pedidos.add(pedido);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os pedidos do banco de dados. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em getPedidos. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return pedidos;
	}

	// ///////////////////

	public List<Pedido> getPedidosByFacebookUserIdBetweenDateRange(
			String fbUserId, Date dini, Date dfin) {
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Pedido> pedidos = new ArrayList<Pedido>();
		String selectSQL = "SELECT p.cod_pedido                              "
				+ "	, p.cod_usuario                                          "
				+ "	, p.cod_estabelecimento                                  "
				+ "	, p.ind_status                                           "
				+ "	, p.dat_pedido                                           "
				+ "	, p.dat_conhecimento                                     "
				+ "	, p.dat_saida_entrega                                    "
				+ "	, p.dat_conf_entrega                                     "
				+ "	, p.dat_avaliacao                                        "
				+ "	, p.ind_avaliacao                                        "
				+ "	, p.obs_avaliacao                                        "
				+ "	, p.dat_cancelamento                                     "
				+ "	, p.obs_cancelamento                                     "
				+ "	, p.obj_dados_pedido                                     "
				+ "	, p.des_veiculo                                          "
				+ "	, p.nom_entregador                                       "
				+ "  FROM pedido p, usuario u                                "
				+ " WHERE p.cod_usuario = u.cod_usuario                      "
				+ "   AND des_facebook_id = ?                                "
				+ "   AND DATE(dat_pedido) BETWEEN  COALESCE(?, DATE(dat_pedido)) AND COALESCE(?, DATE(dat_pedido)) "
				+ " ORDER BY cod_pedido DESC                               ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setString(1, fbUserId);
			if (dini != null)
				pstmt.setTimestamp(2, new java.sql.Timestamp(dini.getTime()));
			else
				pstmt.setTimestamp(2, null);
			if (dfin != null)
				pstmt.setTimestamp(3, new java.sql.Timestamp(dfin.getTime()));
			else
				pstmt.setTimestamp(3, null);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Pedido pedido = new Pedido();
				pedido.codPedido = rs.getInt("cod_pedido");
				pedido.codUsuario = rs.getInt("cod_usuario");
				pedido.codEstabelecimento = rs.getInt("cod_estabelecimento");
				pedido.indStatus = rs.getString("ind_status");
				pedido.datPedido = rs.getTimestamp("dat_pedido");
				pedido.datConhecimento = rs.getTimestamp("dat_conhecimento");
				pedido.datSaidaEntrega = rs.getTimestamp("dat_saida_entrega");
				pedido.datConfEntrega = rs.getTimestamp("dat_conf_entrega");
				pedido.datAvaliacao = rs.getTimestamp("dat_avaliacao");
				pedido.indAvaliacao = rs.getString("ind_avaliacao");
				pedido.obsAvaliacao = rs.getString("obs_avaliacao");
				pedido.datCancelamento = rs.getTimestamp("dat_cancelamento");
				pedido.obsCancelamento = rs.getString("obs_cancelamento");
				pedido.objDadosPedido = rs.getString("obj_dados_pedido");
				pedido.desVeiculo = rs.getString("des_veiculo");
						pedido.nomEntregador = rs.getString("nom_entregador");
				pedidos.add(pedido);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os pedidos do banco de dados. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em getPedidosByFacebookUserId. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return pedidos;
	}

	public int fecharPedido(int codPedido) {
		int qtyUpdated = 0;
		String sqlUpdate = " UPDATE pedido                               "
				+ "    SET dat_conf_entrega = ?                 "
				+ "      , ind_status = 'C'                         "
				+ "  WHERE cod_pedido = ? AND ind_status NOT IN ('X')                       ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setTimestamp(1,
					new java.sql.Timestamp(new java.util.Date().getTime()));
			pstmt.setInt(2, codPedido);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao fechar um pedido. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em fecharPedido: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}

	public int cancelarPedido(int codPedido, String obsCancelamento) {
		int qtyUpdated = 0;
		String sqlUpdate = " UPDATE pedido                               "
				+ "    SET dat_cancelamento = ?                 "
				+ "      , obs_cancelamento = ?                     "
				+ "      , ind_status = 'X'                         "
				+ "  WHERE cod_pedido = ? AND ind_status NOT IN ('C')                      ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setTimestamp(1,
					new java.sql.Timestamp(new java.util.Date().getTime()));
			pstmt.setString(2, obsCancelamento);
			pstmt.setInt(3, codPedido);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao cancelar um pedido. TAG = "
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
						"Erro ao fechar a conexão com o DB em cancelarPedido: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}

	public int conhecerPedido(int codPedido) {
		int qtyUpdated = 0;
		String sqlUpdate = " UPDATE pedido                               "
				+ "    SET dat_conhecimento = ?                 "
				+ "      , ind_status = 'R'                         "
				+ "  WHERE cod_pedido = ?                       ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setTimestamp(1,
					new java.sql.Timestamp(new java.util.Date().getTime()));
			pstmt.setInt(2, codPedido);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao marcar um pedido como conhecido. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em conhecerPedido: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}

	public int enviarPedidoEntrega(int codPedido, String nomEntregador, String desVeiculo) {
		int qtyUpdated = 0;
		String sqlUpdate = " UPDATE pedido                               "
				+ "    SET dat_saida_entrega = ?                 "
				+ "      , ind_status = 'S'                         "
				+ "      , nom_entregador = ?                         "
				+ "      , des_veiculo = ?                         "
				+ "  WHERE cod_pedido = ?                       ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setTimestamp(1,
					new java.sql.Timestamp(new java.util.Date().getTime()));
			pstmt.setString(2, nomEntregador);
			pstmt.setString(3, desVeiculo);
			pstmt.setInt(4, codPedido);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao enviar um pedido para entrega. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em enviarPedidoEntrega: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}

}

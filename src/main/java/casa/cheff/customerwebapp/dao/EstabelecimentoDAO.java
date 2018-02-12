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

import casa.cheff.customerwebapp.bean.db.DistanciaEstabelecimento;
import casa.cheff.customerwebapp.bean.db.Estabelecimento;
import casa.cheff.customerwebapp.bean.db.IUser;
import casa.cheff.customerwebapp.bean.db.Parceiro;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.db.ConnectionManager;

public class EstabelecimentoDAO {

	private static final String TAG = "ESTABELECIMENTO_DAO";

	public List<Estabelecimento> getEstabelecimentos(IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		String query = "SELECT estb.*                                                "
				+ "     , logo.des_source     desSourceLogotipo                      "
				+ "     , capa.des_source     desSourceCapa                          "
				+ "  FROM estabelecimento estb                                       "
				+ "  LEFT JOIN flickr_photo  logo                                    "
				+ "    ON logo.cod_photo_id = estb.cod_photo_id_logotipo             "
				+ "   AND logo.num_label = 1                                         "
				+ "  LEFT JOIN flickr_photo  capa                                    "
				+ "    ON capa.cod_photo_id = estb.cod_photo_id_capa                 "
				+ "   AND capa.num_label = 1                                         "
				+ " WHERE EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = estb.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Estabelecimento> estabelecimentos = new ArrayList<Estabelecimento>();
		try {
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, parceiro.codParceiro);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Estabelecimento estabelecimento = new Estabelecimento();
				estabelecimento.codEstabelecimento = rs
						.getInt("cod_estabelecimento");
				estabelecimento.nomEstabelecimento = rs
						.getString("nom_estabelecimento");
				estabelecimento.numCnpj = rs.getString("num_cnpj");
				estabelecimento.desEstabelecimento = rs
						.getString("des_estabelecimento");
				estabelecimento.indAtivo = rs.getString("ind_ativo");
				estabelecimento.numFone1 = rs.getString("num_fone_1");
				estabelecimento.numFone2 = rs.getString("num_fone_2");
				estabelecimento.nomPessoaContato = rs
						.getString("nom_pessoa_contato");
				estabelecimento.desEndereco = rs.getString("des_endereco");
				estabelecimento.desComplemento = rs
						.getString("des_complemento");
				estabelecimento.desBairro = rs.getString("des_bairro");
				estabelecimento.nomCidade = rs.getString("nom_cidade");
				estabelecimento.indFormaPgto = rs.getString("ind_forma_pgto");
				estabelecimento.numBanco = rs.getString("num_banco");
				estabelecimento.numAgencia = rs.getString("num_agencia");
				estabelecimento.numContaDv = rs.getString("num_conta_dv");
				estabelecimento.latEstabelecimento = rs
						.getFloat("lat_estabelecimento");
				estabelecimento.lonEstabelecimento = rs
						.getFloat("lon_estabelecimento");
				estabelecimento.obsEstabelecimento = rs
						.getString("obs_estabelecimento");
				estabelecimento.valEntrega = rs.getFloat("val_entrega");
				estabelecimento.maxDistanciaRaio = rs
						.getInt("max_distancia_raio");
				estabelecimento.desEmail = rs.getString("des_email");
				estabelecimento.urlFacebook = rs.getString("url_facebook");
				estabelecimento.urlInstagram = rs.getString("url_instagram");
				estabelecimento.urlTwitter = rs.getString("url_twitter");
				estabelecimento.urlPinterest = rs.getString("url_pinterest");
				estabelecimento.urlWebsite = rs.getString("url_website");
				estabelecimento.numWhatsapp = rs.getString("num_whatsapp");
				estabelecimento.datCadastro = new Date(rs.getDate(
						"dat_cadastro").getTime());
				estabelecimento.valMensalidade = rs.getFloat("val_mensalidade");
				estabelecimento.codPhotoIdLogotipo = rs
						.getString("cod_photo_id_logotipo");
				estabelecimento.desSourceLogotipo = rs
						.getString("desSourceLogotipo");
				estabelecimento.codPhotoIdCapa = rs
						.getString("cod_photo_id_capa");
				estabelecimento.desSourceCapa = rs.getString("desSourceCapa");
				estabelecimento.indStatus = rs.getString("ind_status");
				estabelecimento.codPhotoIdCapa = rs
						.getString("cod_photo_id_capa");
				estabelecimento.codPhotoIdLogotipo = rs
						.getString("cod_photo_id_logotipo");
				estabelecimentos.add(estabelecimento);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os estabelecimentos do banco de dados. TAG = "
							+ TAG + " MSG = " + e.getMessage());
		} finally {
			try {
				if (stmt != null) {
					if (!stmt.isClosed()) {
						stmt.close();
					}
				}
				if (conn != null) {
					if (!conn.isClosed()) {
						conn.close();
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException(
						"Erro ao fechar a conexão com o DB em getEstabelecimentos. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return estabelecimentos;
	}

	public Estabelecimento getEstabelecimento(int codEstabelecimento) {
		String query = "SELECT estb.*                                                  "
				+ "     , logo.des_source     desSourceLogotipo                        "
				+ "     , capa.des_source     desSourceCapa                            "
				+ "  FROM estabelecimento estb                                         "
				+ "  LEFT JOIN flickr_photo  logo                                      "
				+ "    ON logo.cod_photo_id = estb.cod_photo_id_logotipo               "
				+ "   AND logo.num_label = (SELECT MAX(ph.num_label)                   "
				+ "                           FROM flickr_photo ph                     "
				+ "                          WHERE ph.cod_photo_id = logo.cod_photo_id "
				+ "                            AND ph.num_label <> 5)                  "
				+ "  LEFT JOIN flickr_photo  capa                                      "
				+ "    ON capa.cod_photo_id = estb.cod_photo_id_capa                   "
				+ "   AND capa.num_label = (SELECT MAX(ph.num_label)                   "
				+ "                           FROM flickr_photo ph                     "
				+ "                          WHERE ph.cod_photo_id = capa.cod_photo_id "
				+ "                            AND ph.num_label <> 5)                  "
				+ " WHERE estb.cod_estabelecimento = ?                                 ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Estabelecimento estabelecimento = null;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, codEstabelecimento);
			rs = stmt.executeQuery();
			if (rs.next()) {
				estabelecimento = new Estabelecimento();
				estabelecimento.codEstabelecimento = rs
						.getInt("cod_estabelecimento");
				estabelecimento.nomEstabelecimento = rs
						.getString("nom_estabelecimento");
				estabelecimento.numCnpj = rs.getString("num_cnpj");
				estabelecimento.desEstabelecimento = rs
						.getString("des_estabelecimento");
				estabelecimento.indAtivo = rs.getString("ind_ativo");
				estabelecimento.numFone1 = rs.getString("num_fone_1");
				estabelecimento.numFone2 = rs.getString("num_fone_2");
				estabelecimento.nomPessoaContato = rs
						.getString("nom_pessoa_contato");
				estabelecimento.desEndereco = rs.getString("des_endereco");
				estabelecimento.desComplemento = rs
						.getString("des_complemento");
				estabelecimento.desBairro = rs.getString("des_bairro");
				estabelecimento.nomCidade = rs.getString("nom_cidade");
				estabelecimento.indFormaPgto = rs.getString("ind_forma_pgto");
				estabelecimento.numBanco = rs.getString("num_banco");
				estabelecimento.numAgencia = rs.getString("num_agencia");
				estabelecimento.numContaDv = rs.getString("num_conta_dv");
				estabelecimento.latEstabelecimento = rs
						.getFloat("lat_estabelecimento");
				estabelecimento.lonEstabelecimento = rs
						.getFloat("lon_estabelecimento");
				estabelecimento.obsEstabelecimento = rs
						.getString("obs_estabelecimento");
				estabelecimento.valEntrega = rs.getFloat("val_entrega");
				estabelecimento.maxDistanciaRaio = rs
						.getInt("max_distancia_raio");
				estabelecimento.desEmail = rs.getString("des_email");
				estabelecimento.urlFacebook = rs.getString("url_facebook");
				estabelecimento.urlInstagram = rs.getString("url_instagram");
				estabelecimento.urlTwitter = rs.getString("url_twitter");
				estabelecimento.urlPinterest = rs.getString("url_pinterest");
				estabelecimento.urlWebsite = rs.getString("url_website");
				estabelecimento.numWhatsapp = rs.getString("num_whatsapp");
				estabelecimento.datCadastro = new Date(rs.getDate(
						"dat_cadastro").getTime());
				estabelecimento.valMensalidade = rs.getFloat("val_mensalidade");
				estabelecimento.valEntrega = rs.getFloat("val_entrega");
				estabelecimento.codPhotoIdCapa = rs
						.getString("cod_photo_id_capa");
				estabelecimento.codPhotoIdLogotipo = rs
						.getString("cod_photo_id_logotipo");
				estabelecimento.desSourceCapa = rs.getString("desSourceCapa");
				estabelecimento.desSourceLogotipo = rs
						.getString("desSourceLogotipo");
				estabelecimento.indStatus = rs.getString("ind_status");
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar o estabelecimento do banco de dados. TAG = "
							+ TAG + " MSG = " + e.getMessage());
		} finally {
			try {
				if (stmt != null) {
					if (!stmt.isClosed()) {
						stmt.close();
					}
				}
				if (conn != null) {
					if (!conn.isClosed()) {
						conn.close();
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException(
						"Erro ao fechar a conexão com o DB em getEstabelecimento. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return estabelecimento;
	}

	public List<DistanciaEstabelecimento> getEstabelecimentosWthLatLonAndFbId(
			double latitude, double longitude, String desFbId) {
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<DistanciaEstabelecimento> estabelecimentos = new ArrayList<DistanciaEstabelecimento>();
		try {
			pstmt = conn
					.prepareStatement("SELECT 111.1111 *                                      "
							+ "    DEGREES(ACOS(COS(RADIANS(? /*a.Latitude*/))                                      "
							+ "         * COS(RADIANS(e.lat_estabelecimento /*b.Latitude*/))                                      "
							+ "         * COS(RADIANS(? /*a.Longitude*/ - e.lon_estabelecimento /*b.Longitude*/))                                      "
							+ "         + SIN(RADIANS(? /*a.Latitude*/))                                      "
							+ "         * SIN(RADIANS(e.lat_estabelecimento /*b.Latitude*/)))) distancia                                      "
							+ "     , logo.des_source                                      "
							+ "     , e.*                                       "
							+ "  FROM estabelecimento e                                      "
							+ "  LEFT JOIN flickr_photo  logo                                                                          "
							+ "    ON logo.cod_photo_id = e.cod_photo_id_logotipo                                                     "
							+ "   AND logo.num_label = (SELECT MAX(ph.num_label)                                                         "
							+ "                           FROM flickr_photo ph                                                           "
							+ "                          WHERE ph.cod_photo_id = logo.cod_photo_id                                       "
							+ "                            AND ph.num_label <> 5)                                                        "
							+ " WHERE (ind_ativo = 'S'                                      "
							+ "   AND 111.1111 *                                      "
							+ "    DEGREES(ACOS(COS(RADIANS(? /*a.Latitude*/))                                      "
							+ "         * COS(RADIANS(e.lat_estabelecimento /*b.Latitude*/))                                      "
							+ "         * COS(RADIANS(? /*a.Longitude*/ - e.lon_estabelecimento /*b.Longitude*/))                                      "
							+ "         + SIN(RADIANS(? /*a.Latitude*/))                                      "
							+ "         * SIN(RADIANS(e.lat_estabelecimento /*b.Latitude*/)))) <= e.max_distancia_raio) OR                                      "
							+ "       EXISTS (SELECT 1                                                               "
							+ "                 FROM usuario_estabelecimento ue                                      "
							+ "                    , usuario u                                                 "
							+ "                WHERE ue.cod_usuario = u.cod_usuario                             "
							+ "                  AND ue.cod_estabelecimento = e.cod_estabelecimento AND u.des_facebook_id = ?)                       "
							+ " ORDER BY 1");

			pstmt.setDouble(1, latitude);
			pstmt.setDouble(2, longitude);
			pstmt.setDouble(3, latitude);

			pstmt.setDouble(4, latitude);
			pstmt.setDouble(5, longitude);
			pstmt.setDouble(6, latitude);
			
			pstmt.setString(7, desFbId);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				DistanciaEstabelecimento estabelecimento = new DistanciaEstabelecimento();
				estabelecimento.distancia = rs.getDouble("distancia");
				estabelecimento.codEstabelecimento = rs
						.getInt("cod_estabelecimento");
				estabelecimento.nomEstabelecimento = rs
						.getString("nom_estabelecimento");
				// estabelecimento.numCnpj = rs.getString("num_cnpj");
				estabelecimento.desEstabelecimento = rs
						.getString("des_estabelecimento");
				// estabelecimento.indAtivo = rs.getString("ind_ativo");
				estabelecimento.desSourceLogotipo = rs.getString("des_source");
				estabelecimento.numFone1 = rs.getString("num_fone_1");
				// estabelecimento.numFone2 = rs.getString("num_fone_2");
				// estabelecimento.nomPessoaContato = rs
				// .getString("nom_pessoa_contato");
				estabelecimento.desEndereco = rs.getString("des_endereco");
				estabelecimento.desComplemento = rs
						.getString("des_complemento");
				estabelecimento.desBairro = rs.getString("des_bairro");
				estabelecimento.nomCidade = rs.getString("nom_cidade");
				// estabelecimento.indFormaPgto =
				// rs.getString("ind_forma_pgto");
				// estabelecimento.numBanco = rs.getString("num_banco");
				// estabelecimento.numAgencia = rs.getString("num_agencia");
				// estabelecimento.numContaDv = rs.getString("num_conta_dv");
				estabelecimento.valEntrega = rs.getFloat("val_entrega");
				estabelecimento.maxDistanciaRaio = rs
						.getInt("max_distancia_raio");
				estabelecimento.latEstabelecimento = rs
						.getFloat("lat_estabelecimento");
				estabelecimento.lonEstabelecimento = rs
						.getFloat("lon_estabelecimento");
				estabelecimento.obsEstabelecimento = rs
						.getString("obs_estabelecimento");
				estabelecimento.desEmail = rs.getString("des_email");
				estabelecimento.urlFacebook = rs.getString("url_facebook");
				estabelecimento.urlInstagram = rs.getString("url_instagram");
				estabelecimento.urlTwitter = rs.getString("url_twitter");
				estabelecimento.urlPinterest = rs.getString("url_pinterest");
				estabelecimento.urlWebsite = rs.getString("url_website");
				estabelecimento.numWhatsapp = rs.getString("num_whatsapp");
				// estabelecimento.datCadastro = new Date(rs.getDate(
				// "dat_cadastro").getTime());
				// estabelecimento.valMensalidade =
				// rs.getFloat("val_mensalidade");
				// TODO : implement OnLine and OffLine stuff
				estabelecimento.indStatus = rs.getString("ind_status");
				estabelecimento.indSource = "jfood";
				estabelecimentos.add(estabelecimento);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar os estabelecimenots com raio do banco de dados com geolocaliza��o. TAG = "
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
						"Erro ao fechar a conex�o com o DB em getEstabelecimentosWthLatLonAndRadius. TAG "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return estabelecimentos;
	}

	public int insertEstabelecimento(Estabelecimento estabelecimento) {
		int codEstabelecimento = -1;
		String sqlInsert = "INSERT INTO estabelecimento ( nom_estabelecimento                                        "
				+ "                            , num_cnpj                                                            "
				+ "                            , des_estabelecimento                                                 "
				+ "                            , ind_ativo                                                           "
				+ "                            , num_fone_1                                                          "
				+ "                            , num_fone_2                                                          "
				+ "                            , nom_pessoa_contato                                                  "
				+ "                            , des_endereco                                                        "
				+ "                            , des_complemento                                                     "
				+ "                            , des_bairro                                                          "
				+ "                            , nom_cidade                                                          "
				+ "                            , ind_forma_pgto                                                      "
				+ "                            , num_banco                                                           "
				+ "                            , num_agencia                                                         "
				+ "                            , num_conta_dv                                                        "
				+ "                            , lat_estabelecimento                                                 "
				+ "                            , lon_estabelecimento                                                 "
				+ "                            , obs_estabelecimento                                                 "
				+ "                            , des_email                                                           "
				+ "                            , url_facebook                                                        "
				+ "                            , url_instagram                                                       "
				+ "                            , url_twitter                                                         "
				+ "                            , url_pinterest                                                       "
				+ "                            , url_website                                                         "
				+ "                            , num_whatsapp                                                        "
				+ "                            , dat_cadastro                                                        "
				+ "                            , val_mensalidade                                                     "
				+ "                            , cod_parceiro, cod_photo_id_logotipo, cod_photo_id_capa, val_entrega, max_distancia_raio, ind_status)             "
				+ "       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlInsert,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, estabelecimento.nomEstabelecimento);
			pstmt.setString(2, estabelecimento.numCnpj);
			pstmt.setString(3, estabelecimento.desEstabelecimento);
			pstmt.setString(4, estabelecimento.indAtivo);
			pstmt.setString(5, estabelecimento.numFone1);
			pstmt.setString(6, estabelecimento.numFone2);
			pstmt.setString(7, estabelecimento.nomPessoaContato);
			pstmt.setString(8, estabelecimento.desEndereco);
			pstmt.setString(9, estabelecimento.desComplemento);
			pstmt.setString(10, estabelecimento.desBairro);
			pstmt.setString(11, estabelecimento.nomCidade);
			pstmt.setString(12, estabelecimento.indFormaPgto);
			pstmt.setString(13, estabelecimento.numBanco);
			pstmt.setString(14, estabelecimento.numAgencia);
			pstmt.setString(15, estabelecimento.numContaDv);
			pstmt.setDouble(16, estabelecimento.latEstabelecimento);
			pstmt.setDouble(17, estabelecimento.lonEstabelecimento);
			pstmt.setString(18, estabelecimento.obsEstabelecimento);
			pstmt.setString(19, estabelecimento.desEmail);
			pstmt.setString(20, estabelecimento.urlFacebook);
			pstmt.setString(21, estabelecimento.urlInstagram);
			pstmt.setString(22, estabelecimento.urlTwitter);
			pstmt.setString(23, estabelecimento.urlPinterest);
			pstmt.setString(24, estabelecimento.urlWebsite);
			pstmt.setString(25, estabelecimento.numWhatsapp);
			pstmt.setDate(26, new java.sql.Date(new java.util.Date().getTime()));
			pstmt.setDouble(27, estabelecimento.valMensalidade);
			pstmt.setInt(28, estabelecimento.codParceiro);
			pstmt.setString(29, estabelecimento.codPhotoIdLogotipo);
			pstmt.setString(30, estabelecimento.codPhotoIdCapa);
			pstmt.setDouble(31, estabelecimento.valEntrega);
			pstmt.setInt(32, estabelecimento.maxDistanciaRaio);
			pstmt.setString(33, estabelecimento.indStatus);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				codEstabelecimento = rs.getInt(1);
			} else {
				throw new RuntimeException(
						"Não foram geradas as chaves em insertEstabelecimento...");
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao inserir um estabelecimento no DB. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em insertEstabelecimento: "
								+ e.getMessage());
			}
		}

		return codEstabelecimento;
	}

	public int updateEstabelecimento(Estabelecimento estabelecimento, IUser user) {
		ParceiroAuth parceiro = (ParceiroAuth) user;
		int qtyUpdated = 0;
		String sqlUpdate = "UPDATE estabelecimento e SET nom_estabelecimento = ?                                         "
				+ "                            , num_cnpj = ?                                                          "
				+ "                            , des_estabelecimento = ?                                               "
				+ "                            , ind_ativo = ?                                                         "
				+ "                            , num_fone_1 = ?                                                        "
				+ "                            , num_fone_2 = ?                                                        "
				+ "                            , nom_pessoa_contato = ?                                                "
				+ "                            , des_endereco = ?                                                      "
				+ "                            , des_complemento = ?                                                   "
				+ "                            , des_bairro = ?                                                        "
				+ "                            , nom_cidade = ?                                                        "
				+ "                            , ind_forma_pgto = ?                                                    "
				+ "                            , num_banco = ?                                                         "
				+ "                            , num_agencia = ?                                                       "
				+ "                            , num_conta_dv = ?                                                      "
				+ "                            , lat_estabelecimento = ?                                               "
				+ "                            , lon_estabelecimento = ?                                               "
				+ "                            , obs_estabelecimento = ?                                               "
				+ "                            , des_email = ?                                                         "
				+ "                            , url_facebook = ?                                                      "
				+ "                            , url_instagram = ?                                                     "
				+ "                            , url_twitter = ?                                                       "
				+ "                            , url_pinterest = ?                                                     "
				+ "                            , url_website = ?                                                       "
				+ "                            , num_whatsapp = ?                                                      "
				+ "                            , dat_cadastro = ?                                                      "
				+ "                            , val_mensalidade = ?"
				+ "                            , cod_photo_id_logotipo = ?   "
				+ "                            , cod_photo_id_capa = ?   "
				+ "                            , val_entrega = ?   "
				+ "                            , max_distancia_raio = ?   "
				+ "                            , ind_status = ?   "
				+ "       WHERE cod_estabelecimento = ?                                                                "
				+ "   AND EXISTS (SELECT 1                                              "
				+ "                 FROM estabelecimento_parceiro ep                    "
				+ "                WHERE ep.cod_estabelecimento = e.cod_estabelecimento "
				+ "                  AND ep.cod_parceiro = ?)                           ";
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setString(1, estabelecimento.nomEstabelecimento);
			pstmt.setString(2, estabelecimento.numCnpj);
			pstmt.setString(3, estabelecimento.desEstabelecimento);
			pstmt.setString(4, estabelecimento.indAtivo);
			pstmt.setString(5, estabelecimento.numFone1);
			pstmt.setString(6, estabelecimento.numFone2);
			pstmt.setString(7, estabelecimento.nomPessoaContato);
			pstmt.setString(8, estabelecimento.desEndereco);
			pstmt.setString(9, estabelecimento.desComplemento);
			pstmt.setString(10, estabelecimento.desBairro);
			pstmt.setString(11, estabelecimento.nomCidade);
			pstmt.setString(12, estabelecimento.indFormaPgto);
			pstmt.setString(13, estabelecimento.numBanco);
			pstmt.setString(14, estabelecimento.numAgencia);
			pstmt.setString(15, estabelecimento.numContaDv);
			pstmt.setDouble(16, estabelecimento.latEstabelecimento);
			pstmt.setDouble(17, estabelecimento.lonEstabelecimento);
			pstmt.setString(18, estabelecimento.obsEstabelecimento);
			pstmt.setString(19, estabelecimento.desEmail);
			pstmt.setString(20, estabelecimento.urlFacebook);
			pstmt.setString(21, estabelecimento.urlInstagram);
			pstmt.setString(22, estabelecimento.urlTwitter);
			pstmt.setString(23, estabelecimento.urlPinterest);
			pstmt.setString(24, estabelecimento.urlWebsite);
			pstmt.setString(25, estabelecimento.numWhatsapp);
			pstmt.setDate(26, new java.sql.Date(new java.util.Date().getTime()));
			pstmt.setDouble(27, estabelecimento.valMensalidade);
			pstmt.setString(28, estabelecimento.codPhotoIdLogotipo);
			pstmt.setString(29, estabelecimento.codPhotoIdCapa);
			pstmt.setDouble(30, estabelecimento.valEntrega);
			pstmt.setInt(31, estabelecimento.maxDistanciaRaio);
			pstmt.setString(32, estabelecimento.indStatus);
			pstmt.setInt(33, estabelecimento.codEstabelecimento);
			pstmt.setInt(34, parceiro.codParceiro);
			qtyUpdated = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao atualizar um estabelecimento no DB. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em updateEstabelecimento: "
								+ e.getMessage());
			}
		}
		return qtyUpdated;
	}
}

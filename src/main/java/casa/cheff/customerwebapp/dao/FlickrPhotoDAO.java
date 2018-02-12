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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import casa.cheff.customerwebapp.bean.db.FlickrPicture;
import casa.cheff.customerwebapp.bean.db.Sabor;
import casa.cheff.customerwebapp.db.ConnectionManager;

import com.flickr4java.flickr.photos.Size;

public class FlickrPhotoDAO {

	private static final String TAG = "FLICKR_PHOTO_DAO";

	public FlickrPicture getFlickrPicture(String photoId, int label) {
		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		FlickrPicture flickrPicture = null;
		String selectSQL = "				SELECT *                                                                    "
				+ "				  FROM flickr_photo                                                            "
				+ "				 WHERE cod_photo_id = ? AND num_label = ?                                          ";
		try {
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setString(1, photoId);
			pstmt.setInt(2, label);
			rs = pstmt.executeQuery(); 
			if (rs.next()) {
				flickrPicture = new FlickrPicture();
				flickrPicture.codPhotoId = rs.getString("cod_photo_id");
				flickrPicture.numLabel = rs.getInt("num_label");
				flickrPicture.desSource = rs.getString("des_source");
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"Erro ao buscar uma foto do banco de dados. TAG = " + TAG
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
						"Erro ao fechar a conexão com o DB em getFlickrPicture. TAG = "
								+ TAG + " MSG = " + e.getMessage());
			}
		}
		return flickrPicture;
	}

	
	public int insertPhotoSizes(String photoId, Collection<Size> sizes) {
		int qtyInserted = 0;
		String sqlInsert = " INSERT INTO flickr_photo                                             "
				+ "           ( cod_photo_id                                                      "
				+ "           , num_label                                                         "
				+ "           , des_source)                                                       "
				+ " 	 VALUES                                                                   "
				+ "           ( ?                                                                 "
				+ "           , ?                                                                 "
				+ "           , ?)		                                                          ";

		Connection conn = ConnectionManager.getConnection(TAG);
		PreparedStatement pstmt = null;
		try {
			for (Iterator<Size> it = sizes.iterator(); it.hasNext();) {
				Size size = it.next();
				try {
					pstmt = conn.prepareStatement(sqlInsert);
					pstmt.setString(1, photoId);
					pstmt.setInt(2, size.getLabel());
					pstmt.setString(3, size.getSource());
					pstmt.executeUpdate();
				} catch (SQLException e) {
					throw new RuntimeException(
							"Erro ao inserir uma foto no DB. TAG = " + TAG
									+ " MSG = " + e.getMessage());
				} finally {
					try {
						if (pstmt != null) {
							if (!pstmt.isClosed()) {
								pstmt.close();
							}
						}
					} catch (SQLException e) {
						throw new RuntimeException(
								"Erro ao fechar um pstmt com o DB em insertPhotoSizes: "
										+ e.getMessage());
					}
				}
				qtyInserted++;
			}
		} finally {
			try {
				if (conn != null) {
					if (!conn.isClosed()) {
						conn.close();
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException(
						"Erro ao fechar a conexão com o DB em insertPhotoSizes: "
								+ e.getMessage());
			}
		}
		return qtyInserted;
	}

}

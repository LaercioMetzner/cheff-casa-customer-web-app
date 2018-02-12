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
package casa.cheff.customerwebapp.resource;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Param;
import se.walkercrou.places.Photo;
import se.walkercrou.places.Place;
import casa.cheff.customerwebapp.Main;
import casa.cheff.customerwebapp.bean.db.Cardapio;
import casa.cheff.customerwebapp.bean.db.DistanciaEstabelecimento;
import casa.cheff.customerwebapp.bean.db.Estabelecimento;
import casa.cheff.customerwebapp.bean.db.EstabelecimentoParceiro;
import casa.cheff.customerwebapp.bean.db.FormaPgto;
import casa.cheff.customerwebapp.bean.db.ItemCardapio;
import casa.cheff.customerwebapp.bean.db.Parceiro;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.bean.db.PrecoItemCardapio;
import casa.cheff.customerwebapp.bean.db.Sabor;
import casa.cheff.customerwebapp.bean.db.Tamanho;
import casa.cheff.customerwebapp.dao.CardapioDAO;
import casa.cheff.customerwebapp.dao.EstabelecimentoDAO;
import casa.cheff.customerwebapp.dao.EstabelecimentoParceiroDAO;
import casa.cheff.customerwebapp.dao.FormaPgtoDAO;
import casa.cheff.customerwebapp.dao.ItemCardapioDAO;
import casa.cheff.customerwebapp.dao.ParceiroDAO;
import casa.cheff.customerwebapp.dao.PrecoItemCardapioDAO;
import casa.cheff.customerwebapp.dao.SaborDAO;
import casa.cheff.customerwebapp.dao.TamanhoDAO;
import casa.cheff.customerwebapp.json.EstabelecimentoDetail;

import com.google.gson.Gson;

public class EstabelecimentoResource implements Resource {

	private class AsyncGetSabores extends Thread {
		private List<EstabelecimentoDetail.Sabor> sabores;
		private int codEstabelecimento;
		public boolean isDone;
		public Exception shit;

		public void run() {
			try {
				List<Sabor> sabores = new SaborDAO()
						.getSaboresAtivos(codEstabelecimento);
				for (Iterator<Sabor> iterator = sabores.iterator(); iterator
						.hasNext();) {
					Sabor sabor = iterator.next();
					EstabelecimentoDetail.Sabor saborDet = new EstabelecimentoDetail.Sabor();
					saborDet.codSabor = sabor.codSabor;
					saborDet.nomSabor = sabor.nomSabor;
					saborDet.desSabor = sabor.desSabor;
					this.sabores.add(saborDet);
				}
				isDone = true;
			} catch (Exception e) {
				shit = e;
			}
		}

		public AsyncGetSabores(List<EstabelecimentoDetail.Sabor> sabores,
				int codEstabelecimento) {
			this.sabores = sabores;
			this.codEstabelecimento = codEstabelecimento;
		}
	}
	
	private class AsyncGetTamanhos extends Thread {
		private List<EstabelecimentoDetail.Tamanho> tamanhos;
		private int codEstabelecimento;
		public boolean isDone;
		public Exception shit;

		public void run() {
			try {
				List<Tamanho> tamanhos = new TamanhoDAO()
						.getTamanhosAtivos(codEstabelecimento);
				for (Iterator<Tamanho> iterator = tamanhos.iterator(); iterator
						.hasNext();) {
					Tamanho tamanho = iterator.next();
					EstabelecimentoDetail.Tamanho tamanhoDet = new EstabelecimentoDetail.Tamanho();
					tamanhoDet.codTamanho = tamanho.codTamanho;
					tamanhoDet.nomTamanho = tamanho.nomTamanho;
					tamanhoDet.desTamanho = tamanho.desTamanho;
					this.tamanhos.add(tamanhoDet);
				}
				isDone = true;
			} catch (Exception e) {
				shit = e;
			}
		}

		public AsyncGetTamanhos(List<EstabelecimentoDetail.Tamanho> tamanhos,
				int codEstabelecimento) {
			this.tamanhos = tamanhos;
			this.codEstabelecimento = codEstabelecimento;
		}
	}

	
	private class AsyncGetFormasPgto extends Thread {
		private List<EstabelecimentoDetail.FormaPgto> formasPgto;
		private int codEstabelecimento;
		public boolean isDone;
		public Exception shit;

		public void run() {
			try {
				List<FormaPgto> formasPgto = new FormaPgtoDAO()
						.getFormasPgtoAtivos(codEstabelecimento);
				for (Iterator<FormaPgto> iterator = formasPgto.iterator(); iterator
						.hasNext();) {
					FormaPgto formaPgto = iterator.next();
					EstabelecimentoDetail.FormaPgto formaPgtoDet = new EstabelecimentoDetail.FormaPgto();
					formaPgtoDet.codFormaPgto = formaPgto.codFormaPgto;
					formaPgtoDet.nomFormaPgto = formaPgto.nomFormaPgto;
					this.formasPgto.add(formaPgtoDet);
				}
				isDone = true;
			} catch (Exception e) {
				shit = e;
			}
		}

		public AsyncGetFormasPgto(List<EstabelecimentoDetail.FormaPgto> formasPgto,
				int codEstabelecimento) {
			this.formasPgto = formasPgto;
			this.codEstabelecimento = codEstabelecimento;
		}
	}
	
	@Override
	public void addRoutes() {

		get(Main.API_PUBLIC + "/estabelecimentos/:codEstabelecimento",
				(req, res) -> {
					res.type(Main.APPLICATION_TYPE_JSON_UTF8);
					int codEstabelecimento = Integer.parseInt(req
							.params(":codestabelecimento"));

					String loadCardapios = "y";
					try {
						loadCardapios = req.queryMap("loadcardapios").value();
					} catch (Exception e) {
					}

					Estabelecimento estabelecimento = new EstabelecimentoDAO()
							.getEstabelecimento(codEstabelecimento);
					EstabelecimentoDetail details = null;

					if (estabelecimento != null
							&& "S".equals(estabelecimento.indAtivo)) {
						details = new EstabelecimentoDetail(estabelecimento);
					}
					
					List<EstabelecimentoDetail.FormaPgto> formasPgto = new ArrayList<EstabelecimentoDetail.FormaPgto>();
					AsyncGetFormasPgto asyncGetFormasPgto = new AsyncGetFormasPgto(
							formasPgto, codEstabelecimento);
					asyncGetFormasPgto.start();

					if (!"n".equals(loadCardapios)) {
						List<EstabelecimentoDetail.Sabor> sabores = new ArrayList<EstabelecimentoDetail.Sabor>();
						AsyncGetSabores asyncGetSabores = new AsyncGetSabores(
								sabores, codEstabelecimento);
						asyncGetSabores.start();
						List<EstabelecimentoDetail.Tamanho> tamanhos = new ArrayList<EstabelecimentoDetail.Tamanho>();
						AsyncGetTamanhos asyncGetTamanhos = new AsyncGetTamanhos(
								tamanhos, codEstabelecimento);
						asyncGetTamanhos.start();
						List<Cardapio> cardapios = new CardapioDAO()
								.getCardapiosAtivos(codEstabelecimento);
						for (Iterator<Cardapio> iterator = cardapios.iterator(); iterator
								.hasNext();) {
							Cardapio cardapio = (Cardapio) iterator.next();
							EstabelecimentoDetail.Cardapio cardapioDetail = details.new Cardapio();
							cardapioDetail.codCardapio = cardapio.codCardapio;
							cardapioDetail.nomCardapio = cardapio.nomCardapio;
							cardapioDetail.numOrdem = cardapio.numOrdem;
							List<ItemCardapio> itensCardapio = new ItemCardapioDAO()
									.getItensCardapioAtivos(
											estabelecimento.codEstabelecimento,
											cardapio.codCardapio);
							for (Iterator<ItemCardapio> itCar = itensCardapio
									.iterator(); itCar.hasNext();) {
								ItemCardapio itemCardapio = (ItemCardapio) itCar
										.next();
								EstabelecimentoDetail.Cardapio.ItemCardapio itemCardDet = cardapioDetail.new ItemCardapio();
								itemCardDet.codItemCardapio = itemCardapio.codItemCardapio;
								itemCardDet.nomItemCardapio = itemCardapio.nomItemCardapio;
								itemCardDet.numOrdem = itemCardapio.numOrdem;
								itemCardDet.desSourcePhoto = itemCardapio.desSourcePhoto;
								itemCardDet.desItemCardapio = itemCardapio.desItemCardapio;
								List<PrecoItemCardapio> precos = new PrecoItemCardapioDAO()
										.getPrecostItemCardapioAtivos(
												estabelecimento.codEstabelecimento,
												cardapio.codCardapio,
												itemCardapio.codItemCardapio);
								for (Iterator itPrec = precos.iterator(); itPrec
										.hasNext();) {
									PrecoItemCardapio precoItemCardapio = (PrecoItemCardapio) itPrec
											.next();
									EstabelecimentoDetail.Cardapio.ItemCardapio.PrecoItemCardapio precDetail = itemCardDet.new PrecoItemCardapio();
									precDetail.codPreco = precoItemCardapio.codPreco;
									precDetail.valPreco = precoItemCardapio.valPreco;
									precDetail.qtdSabor = precoItemCardapio.qtdSabor;
									precDetail.codBarra = precoItemCardapio.codBarra;
									precDetail.codLegado = precoItemCardapio.codLegado;
									itemCardDet.precos.add(precDetail);
									saborzinho: while (asyncGetSabores.shit == null) {
										while (!asyncGetSabores.isDone){
											Thread.sleep(100);
											continue saborzinho;
										}
										for (Iterator<casa.cheff.customerwebapp.json.EstabelecimentoDetail.Sabor> itSab = sabores
												.iterator(); itSab.hasNext();) {
											EstabelecimentoDetail.Sabor sabor = itSab
													.next();
											if (sabor.codSabor == precoItemCardapio.codSabor) {
												precDetail.sabor = sabor;
												break saborzinho;
											}
										}
										break;
									}
									tamainho: while (asyncGetTamanhos.shit == null) {
										while (!asyncGetTamanhos.isDone){
											Thread.sleep(100);
											continue tamainho;
										}
										for (Iterator<casa.cheff.customerwebapp.json.EstabelecimentoDetail.Tamanho> itTm = tamanhos
												.iterator(); itTm.hasNext();) {
											EstabelecimentoDetail.Tamanho tamanho = itTm
													.next();
											if (tamanho.codTamanho == precoItemCardapio.codTamanho) {
												precDetail.tamanho = tamanho;
												break tamainho;
											}
										}
										break;
									}
									if (asyncGetSabores.shit != null)
										throw asyncGetSabores.shit;
								}
								cardapioDetail.itens.add(itemCardDet);
							}
							details.cardapios.add(cardapioDetail);
						}
					}
					formsPgto: while (asyncGetFormasPgto.shit == null) {
						while (!asyncGetFormasPgto.isDone){
							Thread.sleep(100);
							continue formsPgto;
						}
						details.formasPgto = formasPgto;
						break formsPgto;
					}
					res.status(200);
					return details;
				}, new Gson()::toJson);

		get(Main.API_PUBLIC + "/estabelecimentos",
				(req, res) -> {
					res.type(Main.APPLICATION_TYPE_JSON_UTF8);
					double latitude = 0, longitude = 0;
					try {
						latitude = req.queryMap("lat").doubleValue();
						longitude = req.queryMap("lon").doubleValue();
						// distancia = req.queryMap("dist").doubleValue();
					} catch (Exception e) {
						throw new RuntimeException(
								"Erro ao ober os parâmetros get!");
					}
					String desFbId = "";
					try {
						desFbId = req.queryMap("fbid").value();
					} catch (Exception e) {
					}
					List<Place> places = null;
					try {
						GooglePlaces placesClient = new GooglePlaces(
								Main.PLACES_API_KEY);
						placesClient.setDebugModeEnabled(false);
						places = placesClient.getNearbyPlaces(latitude,
								longitude, 1000, 10,
								new Param("type").value("restaurant")); //
					} catch (Exception e2) {
						System.out.println(e2);
					}
					List<DistanciaEstabelecimento> distEstabelecimentos = new EstabelecimentoDAO()
							.getEstabelecimentosWthLatLonAndFbId(latitude,
									longitude, desFbId);
					int i = 0;
					if (places != null) {
						for (Place place : places) {
							DistanciaEstabelecimento estabelecimento = new DistanciaEstabelecimento();
							if (i++ < 5) {
								GooglePlaces placesClient = new GooglePlaces(
										Main.PLACES_API_KEY);
								place = placesClient.getPlaceById(place
										.getPlaceId()); // place.getDetails();
							}
							estabelecimento.distancia = 0; // TODO : mudar
							estabelecimento.codEstabelecimento = 0; // TODO :
																	// mudar
							estabelecimento.nomEstabelecimento = place
									.getName();
							estabelecimento.desEstabelecimento = null; // TODO:
																		// mudar
							estabelecimento.numFone1 = place.getPhoneNumber();
							estabelecimento.desEndereco = place.getVicinity();
							estabelecimento.desComplemento = null; // TODO:
																	// mudar
							estabelecimento.desBairro = null; // TODO: mudar
							estabelecimento.nomCidade = null; // TODO: mudar
							estabelecimento.latEstabelecimento = (float) place
									.getLatitude();
							estabelecimento.lonEstabelecimento = (float) place
									.getLongitude();
							estabelecimento.urlWebsite = place.getWebsite();
							String indStatus = place.getPhoneNumber();
							indStatus = indStatus == null ? "" : indStatus;
							indStatus = "".equals(indStatus) ? place
									.getWebsite() : indStatus;
							estabelecimento.indStatus = indStatus;// "OffLine";
							estabelecimento.indSource = "google";
							for (Photo photo : place.getPhotos()) {
								estabelecimento.desSourceLogotipo = "https://maps.googleapis.com/maps/api/place/photo?photoreference="
										+ photo.getReference()
										+ "&sensor=false&maxheight=300&maxwidth=300&key="
										+ Main.IMAGE_LOADER_API_KEY;
								break;
							}
							distEstabelecimentos.add(estabelecimento);
						}
					}
					res.status(200);
					return distEstabelecimentos;
				}, new Gson()::toJson);

		get(Main.API_PROTECTED + "/estabelecimentos/:codestabelecimento",
				(req, res) -> {
					res.type(Main.APPLICATION_TYPE_JSON_UTF8);
					int codEstabelecimento = Integer.parseInt(req
							.params(":codestabelecimento"));
					Estabelecimento estabelecimentos = new EstabelecimentoDAO()
							.getEstabelecimento(codEstabelecimento);
					res.status(200);
					return estabelecimentos;
				}, new Gson()::toJson);

		get(Main.API_PROTECTED + "/estabelecimentos",
				(req, res) -> {
					res.type(Main.APPLICATION_TYPE_JSON_UTF8);
					ParceiroAuth parceiro = (ParceiroAuth) req
							.attribute(Main.PARCEIRO_ATRIBUTE);
					List<Estabelecimento> estabelecimentos = new EstabelecimentoDAO()
							.getEstabelecimentos(parceiro);
					res.status(200);
					return estabelecimentos;
				}, new Gson()::toJson);

		post(Main.API_PROTECTED + "/estabelecimentos",
				(req, res) -> {
					try {
						Estabelecimento estabelecimento = new Gson().fromJson(
								req.body(), Estabelecimento.class);
						ParceiroAuth parceiro = (ParceiroAuth) req
								.attribute(Main.PARCEIRO_ATRIBUTE);
						estabelecimento.codParceiro = parceiro.codParceiro;
						int codEstabelecimento = new EstabelecimentoDAO()
								.insertEstabelecimento(estabelecimento);
						res.status(201);
						res.header("codEstabelecimento",
								Integer.toString(codEstabelecimento));

						Parceiro parceiro_ = new Parceiro();
						parceiro_.datCadastro = new Date();
						parceiro_.desBairro = estabelecimento.desBairro;
						parceiro_.desComplemento = estabelecimento.desComplemento;
						parceiro_.desEmail = estabelecimento.desEmail;
						parceiro_.desEndereco = estabelecimento.desEndereco;
						parceiro_.desSenha = "5ee5905f9a952150fdbf86f7ce78bab6";
						parceiro_.indAtivo = estabelecimento.indAtivo;
						parceiro_.indFormaPgto = estabelecimento.indFormaPgto;
						parceiro_.nomCidade = estabelecimento.nomCidade;
						parceiro_.nomParceiro = estabelecimento.nomEstabelecimento;
						parceiro_.numAgencia = estabelecimento.numAgencia;
						parceiro_.numBanco = estabelecimento.numBanco;
						parceiro_.numConta = estabelecimento.numContaDv;
						parceiro_.numCpfCnpj = estabelecimento.numCnpj;
						ParceiroDAO parceiroDAO = new ParceiroDAO();
						int codparceiro = parceiroDAO.insertParceiro(parceiro_);

						EstabelecimentoParceiro estabelecimentoParceiro = new EstabelecimentoParceiro();
						estabelecimentoParceiro.codEstabelecimento = codEstabelecimento;
						estabelecimentoParceiro.codParceiro = codparceiro;
						estabelecimentoParceiro.datCadastro = new Date();

						EstabelecimentoParceiroDAO estabelecimentoParceiroDAO = new EstabelecimentoParceiroDAO();
						estabelecimentoParceiroDAO
								.insertEstabelecimentoParceiro(estabelecimentoParceiro);

					} catch (Exception e) {
						throw new RuntimeException(
								"Ocorreu um erro ao processar a requisição POST para /estabelecimentos: "
										+ e.getMessage());
					}
					return "";
				});

		put(Main.API_PROTECTED + "/estabelecimentos/:codestabelecimento",
				(req, res) -> {
					ParceiroAuth parceiro = (ParceiroAuth) req
							.attribute(Main.PARCEIRO_ATRIBUTE);
					try {
						Estabelecimento estabelecimento = new Gson().fromJson(
								req.body(), Estabelecimento.class);
						if (!req.params(":codestabelecimento")
								.equals(String
										.valueOf(estabelecimento.codEstabelecimento))) {
							throw new Exception(
									"Não é o mesmo estabelecimento a ser editado !!");
						}
						int qtyUpdated = new EstabelecimentoDAO()
								.updateEstabelecimento(estabelecimento,
										parceiro);
						if (qtyUpdated > 0) {
							res.status(204);
						} else {
							res.status(404);
						}
					} catch (Exception e) {
						throw new RuntimeException(
								"Ocorreu um erro ao processar a requisi��o PUT para /estabelecimentos: "
										+ e.getMessage());
					}
					return "";
				});

	}

}

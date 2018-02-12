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


-- phpMyAdmin SQL Dump
-- version 4.0.10.12
-- http://www.phpmyadmin.net
--
-- Servidor: localhost:3306
-- Tempo de Geração: 01/10/2017 às 22:10
-- Versão do servidor: 5.5.52
-- Versão do PHP: 5.3.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Banco de dados: `jfood`
--

DELIMITER $$
--
-- Funções
--
CREATE DEFINER=`bd8a2b0a9c9a46`@`localhost` FUNCTION `Geo`(lat_ini FLOAT(18,10), lon_ini FLOAT(18,10),lat_fim FLOAT(18,10), lon_fim FLOAT(18,10)) RETURNS float(18,10)
BEGIN
DECLARE Theta FLOAT(18,10);
DECLARE Dist FLOAT(18,10);
DECLARE Miles FLOAT(18,10);
DECLARE kilometers FLOAT(18,10);

SET Theta = lon_ini - lon_fim;
SET Dist  = SIN(RADIANS(lat_ini)) * SIN(RADIANS(lat_fim)) +  COS(RADIANS(lat_ini)) * COS(RADIANS(lat_fim)) * COS(RADIANS(Theta));
SET Dist  = ACOS(Dist);
SET Dist  = DEGREES(Dist);
SET Miles = Dist * 60 * 1.1515;
SET kilometers = Miles * 1.609344;

RETURN kilometers;

END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estrutura para tabela `auth`
--

CREATE TABLE IF NOT EXISTS `auth` (
  `des_token` varchar(64) NOT NULL,
  `usuario_cod_usuario` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`des_token`,`usuario_cod_usuario`),
  KEY `fk_auth_usuario_idx` (`usuario_cod_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estrutura para tabela `cardapio`
--

CREATE TABLE IF NOT EXISTS `cardapio` (
  `cod_cardapio` int(11) NOT NULL AUTO_INCREMENT,
  `cod_estabelecimento` int(11) NOT NULL DEFAULT '0',
  `nom_cardapio` varchar(45) NOT NULL,
  `num_ordem` int(11) NOT NULL,
  `ind_ativo` varchar(2) DEFAULT NULL,
  `dat_cadastro` datetime NOT NULL,
  PRIMARY KEY (`cod_cardapio`,`cod_estabelecimento`),
  KEY `fk_cardapio_estabelecimento_idx` (`cod_estabelecimento`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=21 ;


-- --------------------------------------------------------

--
-- Estrutura para tabela `estabelecimento`
--

CREATE TABLE IF NOT EXISTS `estabelecimento` (
  `cod_estabelecimento` int(11) NOT NULL AUTO_INCREMENT,
  `nom_estabelecimento` varchar(45) NOT NULL,
  `num_cnpj` varchar(18) NOT NULL,
  `des_estabelecimento` varchar(200) DEFAULT NULL,
  `ind_ativo` varchar(2) NOT NULL,
  `ind_status` varchar(45) DEFAULT NULL,
  `num_fone_1` varchar(15) NOT NULL,
  `num_fone_2` varchar(15) DEFAULT NULL,
  `nom_pessoa_contato` varchar(45) NOT NULL,
  `cod_parceiro` int(11) DEFAULT NULL,
  `des_endereco` varchar(100) NOT NULL,
  `des_complemento` varchar(100) DEFAULT NULL,
  `des_bairro` varchar(45) NOT NULL,
  `nom_cidade` varchar(45) NOT NULL,
  `ind_forma_pgto` varchar(2) DEFAULT NULL COMMENT '		',
  `num_banco` varchar(10) DEFAULT NULL,
  `num_agencia` varchar(10) DEFAULT NULL,
  `num_conta_dv` varchar(20) DEFAULT NULL,
  `lat_estabelecimento` float DEFAULT NULL,
  `lon_estabelecimento` float DEFAULT NULL,
  `max_distancia_raio` int(3) DEFAULT NULL,
  `obs_estabelecimento` varchar(1000) DEFAULT NULL,
  `des_email` varchar(100) NOT NULL,
  `url_facebook` varchar(300) DEFAULT NULL,
  `url_instagram` varchar(300) DEFAULT NULL,
  `url_twitter` varchar(300) DEFAULT NULL,
  `url_pinterest` varchar(300) DEFAULT NULL,
  `url_website` varchar(300) DEFAULT NULL,
  `num_whatsapp` varchar(17) DEFAULT NULL,
  `dat_cadastro` datetime NOT NULL,
  `val_mensalidade` decimal(8,2) NOT NULL,
  `val_entrega` decimal(8,2) DEFAULT NULL,
  `cod_photo_id_logotipo` varchar(100) DEFAULT NULL,
  `cod_photo_id_capa` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`cod_estabelecimento`),
  KEY `fk_parceiro_estabelecimento_idx` (`cod_parceiro`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=14 ;

-- --------------------------------------------------------

--
-- Estrutura para tabela `estabelecimento_parceiro`
--

CREATE TABLE IF NOT EXISTS `estabelecimento_parceiro` (
  `cod_estabelecimento` int(11) NOT NULL,
  `cod_parceiro` int(11) NOT NULL,
  `dat_cadastro` datetime DEFAULT NULL,
  PRIMARY KEY (`cod_estabelecimento`,`cod_parceiro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estrutura para tabela `flickr_photo`
--

CREATE TABLE IF NOT EXISTS `flickr_photo` (
  `cod_photo_id` varchar(100) NOT NULL,
  `num_label` int(11) NOT NULL,
  `des_source` varchar(200) NOT NULL,
  PRIMARY KEY (`cod_photo_id`,`num_label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estrutura para tabela `forma_pgto`
--

CREATE TABLE IF NOT EXISTS `forma_pgto` (
  `cod_forma_pgto` int(11) NOT NULL AUTO_INCREMENT,
  `cod_estabelecimento` int(11) NOT NULL DEFAULT '0',
  `nom_forma_pgto` varchar(45) NOT NULL,
  `ind_ativo` varchar(2) NOT NULL,
  `dat_cadastro` datetime NOT NULL,
  PRIMARY KEY (`cod_forma_pgto`,`cod_estabelecimento`),
  KEY `fk_forma_pgto_estabelecimento_idx` (`cod_estabelecimento`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;


---------------------------------------------------

--
-- Estrutura para tabela `item_cardapio`
--

CREATE TABLE IF NOT EXISTS `item_cardapio` (
  `cod_item_cardapio` int(11) NOT NULL AUTO_INCREMENT,
  `cod_cardapio` int(11) NOT NULL DEFAULT '0',
  `cod_estabelecimento` int(11) NOT NULL DEFAULT '0',
  `nom_item_cardapio` varchar(45) NOT NULL,
  `des_item_cardapio` varchar(200) DEFAULT NULL,
  `ind_ativo` varchar(2) NOT NULL,
  `dat_cadastro` datetime NOT NULL,
  `num_ordem` int(11) DEFAULT NULL,
  `cod_photo_id_item` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`cod_item_cardapio`,`cod_cardapio`,`cod_estabelecimento`),
  KEY `fk_item_cardapio_cardapio_idx` (`cod_cardapio`,`cod_estabelecimento`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=119 ;


--
-- Estrutura para tabela `item_valor_fracionado`
--

CREATE TABLE IF NOT EXISTS `item_valor_fracionado` (
  `qtd_sabor` int(11) NOT NULL,
  `cod_item_cardapio` int(11) NOT NULL DEFAULT '0',
  `cod_cardapio` int(11) NOT NULL DEFAULT '0',
  `cod_estabelecimento` int(11) NOT NULL DEFAULT '0',
  `val_adicional` decimal(8,2) NOT NULL,
  `ind_ativo` varchar(2) NOT NULL,
  `obs_valor_qtd_sabor` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`qtd_sabor`,`cod_item_cardapio`,`cod_cardapio`,`cod_estabelecimento`),
  KEY `fk_fracionado_item_cardapio_idx` (`cod_item_cardapio`,`cod_cardapio`,`cod_estabelecimento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estrutura para tabela `parceiro`
--

CREATE TABLE IF NOT EXISTS `parceiro` (
  `cod_parceiro` int(11) NOT NULL AUTO_INCREMENT,
  `des_email` varchar(100) NOT NULL,
  `des_senha` varchar(35) NOT NULL,
  `aut_token` varchar(35) DEFAULT NULL,
  `nom_usuario_web` varchar(45) NOT NULL,
  `num_cpf_cnpj` varchar(14) NOT NULL,
  `des_endereco` varchar(100) DEFAULT NULL,
  `des_complemento` varchar(100) DEFAULT NULL,
  `des_bairro` varchar(45) DEFAULT NULL,
  `nom_cidade` varchar(45) DEFAULT NULL,
  `ind_forma_pgto` varchar(2) DEFAULT NULL,
  `num_banco` varchar(10) DEFAULT NULL,
  `num_agencia` varchar(10) DEFAULT NULL,
  `num_conta` varchar(20) DEFAULT NULL,
  `ind_admin` varchar(2) DEFAULT 'N',
  `ind_ativo` varchar(2) DEFAULT NULL,
  `dat_cadastro` datetime DEFAULT NULL,
  PRIMARY KEY (`cod_parceiro`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=20 ;

--
-- Estrutura para tabela `pedido`
--

CREATE TABLE IF NOT EXISTS `pedido` (
  `cod_pedido` int(11) NOT NULL AUTO_INCREMENT,
  `cod_usuario` int(11) NOT NULL DEFAULT '0',
  `cod_estabelecimento` int(11) NOT NULL DEFAULT '0',
  `ind_status` varchar(2) NOT NULL,
  `dat_pedido` datetime NOT NULL,
  `dat_conhecimento` datetime DEFAULT NULL,
  `dat_saida_entrega` datetime DEFAULT NULL,
  `dat_conf_entrega` datetime DEFAULT NULL,
  `dat_avaliacao` datetime DEFAULT NULL,
  `ind_avaliacao` varchar(2) DEFAULT NULL,
  `obs_avaliacao` varchar(200) DEFAULT NULL,
  `dat_cancelamento` datetime DEFAULT NULL,
  `obs_cancelamento` varchar(200) DEFAULT NULL,
  `obj_dados_pedido` longtext NOT NULL,
  `nom_entregador` varchar(100) DEFAULT NULL,
  `des_veiculo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`cod_pedido`,`cod_usuario`,`cod_estabelecimento`),
  KEY `fk_pedido_usuario_idx` (`cod_usuario`),
  KEY `fk_pedido_estabelecimento_idx` (`cod_estabelecimento`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6022 ;

--
-- Estrutura para tabela `preco_item_cardapio`
--

CREATE TABLE IF NOT EXISTS `preco_item_cardapio` (
  `cod_item_cardapio` int(11) NOT NULL DEFAULT '0',
  `cod_cardapio` int(11) NOT NULL DEFAULT '0',
  `cod_estabelecimento` int(11) NOT NULL DEFAULT '0',
  `cod_sabor` int(11) NOT NULL DEFAULT '0',
  `cod_tamanho` int(11) NOT NULL DEFAULT '0',
  `qtd_sabor` int(11) DEFAULT '0',
  `val_preco` decimal(8,2) NOT NULL,
  `ind_ativo` varchar(2) NOT NULL,
  `dat_cadastro` datetime NOT NULL,
  `cod_preco` int(11) NOT NULL AUTO_INCREMENT,
  `cod_legado` varchar(100) DEFAULT NULL,
  `cod_barra` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`cod_preco`),
  KEY `fk_sabor_item_tam_frac_idx` (`cod_sabor`,`cod_estabelecimento`),
  KEY `fk_tam_item_sabor_frac_idx` (`cod_tamanho`,`cod_estabelecimento`),
  KEY `fk_item_sabor_tam_frac_idx` (`qtd_sabor`,`cod_item_cardapio`,`cod_cardapio`,`cod_estabelecimento`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=283 ;

--
-- Estrutura para tabela `sabor`
--

CREATE TABLE IF NOT EXISTS `sabor` (
  `cod_sabor` int(11) NOT NULL AUTO_INCREMENT,
  `cod_estabelecimento` int(11) NOT NULL DEFAULT '0',
  `nom_sabor` varchar(45) NOT NULL,
  `des_sabor` varchar(300) DEFAULT NULL,
  `ind_ativo` varchar(2) NOT NULL,
  `dat_cadastro` datetime NOT NULL,
  PRIMARY KEY (`cod_sabor`,`cod_estabelecimento`),
  KEY `fk_sabor_estabelecimento_idx` (`cod_estabelecimento`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=134 ;

-- --------------------------------------------------------

--
-- Estrutura para tabela `tamanho`
--

CREATE TABLE IF NOT EXISTS `tamanho` (
  `cod_tamanho` int(11) NOT NULL AUTO_INCREMENT,
  `cod_estabelecimento` int(11) NOT NULL DEFAULT '0',
  `nom_tamanho` varchar(45) NOT NULL,
  `des_tamanho` varchar(100) DEFAULT NULL,
  `ind_ativo` varchar(2) NOT NULL,
  `dat_cadastro` datetime NOT NULL,
  PRIMARY KEY (`cod_tamanho`,`cod_estabelecimento`),
  KEY `fk_tamanho_estabelecimento_idx` (`cod_estabelecimento`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=46 ;

--
-- Estrutura para tabela `usuario`
--

CREATE TABLE IF NOT EXISTS `usuario` (
  `cod_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `num_cpf` varchar(11) DEFAULT NULL,
  `nom_usuario` varchar(100) NOT NULL,
  `num_fone` varchar(15) NOT NULL,
  `des_email` varchar(100) DEFAULT NULL,
  `des_facebook_id` varchar(200) DEFAULT NULL,
  `des_endereco` varchar(100) DEFAULT NULL,
  `des_complemento` varchar(100) DEFAULT NULL,
  `des_bairro` varchar(45) DEFAULT NULL,
  `nom_cidade` varchar(45) DEFAULT NULL,
  `ind_ativo` varchar(2) DEFAULT NULL,
  `dat_cadastro` datetime DEFAULT NULL,
  `lat_padrao` float DEFAULT NULL,
  `lon_padrao` float DEFAULT NULL,
  `lat_ultima` float DEFAULT NULL,
  `lon_ultima` float DEFAULT NULL,
  PRIMARY KEY (`cod_usuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=10 ;

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuario_estabelecimento`
--

CREATE TABLE IF NOT EXISTS `usuario_estabelecimento` (
  `cod_usuario` int(11) NOT NULL,
  `cod_estabelecimento` int(11) NOT NULL,
  PRIMARY KEY (`cod_usuario`,`cod_estabelecimento`),
  KEY `fk_cod_estabelecimento_idx` (`cod_estabelecimento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Restrições para tabelas `auth`
--
ALTER TABLE `auth`
  ADD CONSTRAINT `fk_auth_usuario` FOREIGN KEY (`usuario_cod_usuario`) REFERENCES `usuario` (`cod_usuario`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Restrições para tabelas `cardapio`
--
ALTER TABLE `cardapio`
  ADD CONSTRAINT `fk_cardapio_estabelecimento` FOREIGN KEY (`cod_estabelecimento`) REFERENCES `estabelecimento` (`cod_estabelecimento`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Restrições para tabelas `estabelecimento`
--
ALTER TABLE `estabelecimento`
  ADD CONSTRAINT `fk_parceiro_estabelecimento` FOREIGN KEY (`cod_parceiro`) REFERENCES `parceiro` (`cod_parceiro`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Restrições para tabelas `forma_pgto`
--
ALTER TABLE `forma_pgto`
  ADD CONSTRAINT `fk_forma_pgto_estabelecimento` FOREIGN KEY (`cod_estabelecimento`) REFERENCES `estabelecimento` (`cod_estabelecimento`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Restrições para tabelas `item_cardapio`
--
ALTER TABLE `item_cardapio`
  ADD CONSTRAINT `fk_item_cardapio_cardapio` FOREIGN KEY (`cod_cardapio`, `cod_estabelecimento`) REFERENCES `cardapio` (`cod_cardapio`, `cod_estabelecimento`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Restrições para tabelas `item_valor_fracionado`
--
ALTER TABLE `item_valor_fracionado`
  ADD CONSTRAINT `fk_fracionado_item_cardapio` FOREIGN KEY (`cod_item_cardapio`, `cod_cardapio`, `cod_estabelecimento`) REFERENCES `item_cardapio` (`cod_item_cardapio`, `cod_cardapio`, `cod_estabelecimento`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Restrições para tabelas `pedido`
--
ALTER TABLE `pedido`
  ADD CONSTRAINT `fk_pedido_estabelecimento` FOREIGN KEY (`cod_estabelecimento`) REFERENCES `estabelecimento` (`cod_estabelecimento`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_pedido_usuario` FOREIGN KEY (`cod_usuario`) REFERENCES `usuario` (`cod_usuario`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Restrições para tabelas `sabor`
--
ALTER TABLE `sabor`
  ADD CONSTRAINT `fk_sabor_estabelecimento` FOREIGN KEY (`cod_estabelecimento`) REFERENCES `estabelecimento` (`cod_estabelecimento`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Restrições para tabelas `tamanho`
--
ALTER TABLE `tamanho`
  ADD CONSTRAINT `fk_tamanho_estabelecimento` FOREIGN KEY (`cod_estabelecimento`) REFERENCES `estabelecimento` (`cod_estabelecimento`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Restrições para tabelas `usuario_estabelecimento`
--
ALTER TABLE `usuario_estabelecimento`
  ADD CONSTRAINT `fk_cod_estabelecimento` FOREIGN KEY (`cod_estabelecimento`) REFERENCES `estabelecimento` (`cod_estabelecimento`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_cod_usuario` FOREIGN KEY (`cod_usuario`) REFERENCES `usuario` (`cod_usuario`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

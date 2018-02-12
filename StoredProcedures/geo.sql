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


delimiter //
CREATE FUNCTION Geo(lat_ini FLOAT(18,10), lon_ini FLOAT(18,10),lat_fim FLOAT(18,10), lon_fim FLOAT(18,10))
RETURNS FLOAT(18,10)
NOT DETERMINISTIC
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

END;//
delimiter ;

/*******************************************************************************
 * Copyright (C) 2021 Vangel V. Ajanovski
 *     
 * This file is part of the dbLearnStar system (hereinafter: dbLearn*).
 *     
 * dbLearn* is free software: you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *     
 * dbLearn* is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
 * details.
 *     
 * You should have received a copy of the GNU General Public License along 
 * with dbLearn*.  If not, see <https://www.gnu.org/licenses/>.
 * 
 ******************************************************************************/

define(["jquery"], function($) {

	$(document).ready(function() {
		window.editor = CodeMirror
			.fromTextArea(
				document.getElementById('editorarea'),
				{
					mode: 'text/x-pgsql',
					indentWithTabs: true,
					smartIndent: true,
					lineNumbers: true,
					matchBrackets: true,
					theme: "monokai",
					autofocus: true,
					completeSingle: true,
					extraKeys: {
						"Ctrl-Space": "autocomplete",
					},
					hintOptions: {
						tables: {
							apteki: ["a_id","aime"],
							apteki_lokacii: ["a_id","a_reden_br","adresa","tel_broj"],
							farmakomi: ["fk_id","fk_ime"],
							farmakomi_broevi: ["fk_id","fk_reden_br","tel_broj"],
							lekovi: ["fk_id","prod_id","l_ime","sostav"],
							prodava: ["fk_id","prod_id","a_id","cena"],
							dogovori: ["fk_id","a_id","datum_skl","l_id","datum_ist","sodrzhina"],
							recepti: ["p_l_id","d_l_id","datum_izd","fk_id","prod_id","doza"],
							matichen_na: ["p_l_id","m_datum_poch","m_datum_kraj","d_l_id"],
							doktori: ["l_id","rab_iskustvo","specijalnost"],
							pacienti: ["l_id","p_adresa"],
							supervizori: ["l_id"],
							banki: ["banka_id","b_naziv","b_adresa","grad_id"],
							gradovi: ["grad_id","g_naziv"],
							vraboteni: ["banka_id","vrab_br","v_embg","v_ime","godina_vrab"],
							banki: ["banka_id","b_naziv","b_adresa","grad_id"],
							ekspozituri: ["banka_id","eksp_br","e_naziv","e_adresa","grad_id"],
							rabotni_mesta: ["rabmesto_id","rm_naziv"],
							raboti_kako: ["banka_id","vrab_br","rabmesto_id"],
							odgovorni_lica: ["smetka_br","trans_br","banka_id","vrab_br"],
							supervizor: ["lid"],
							recept: ["recid", "datum", "doza", "liddok", "lidpac", "mbroj", "lekid"],
							uchenici: ["uidentifikator", "uchid"],
							studenti: ["br_chovek", "vid_studii", "br_chovek_sovetnik", "br_oddel"],
							proekti: ["br_proekt", "ime_proekt", "br_chovek"],
							oblasti: ["br_oblast", "ime_oblast"],
							predmeti: ["pid", "pime", "pkrediti"],
							prijavnilistovi: ["sid", "semestar", "status"],
							nasoki: ["nid", "nime", "nmaxkrediti", "vkupnokreditidiplomiranje"],
							realizacii: ["kod", "data", "mesto"],
							ocenuvachi: ["oidentifikator"],
							natprevari: ["kod", "mesec", "opseg", "rang", "naziv"],
							apteka: ["aptid", "aptime", "aptadresa"],
							uchilishta: ["uchid", "uchnaziv", "uchadresa"],
							edinici: ["eid", "eime"],
							mentori: ["midentifikator"],
							upisi: ["sid", "redbr", "semestar_od", "semestar_do", "zapishan_na_nasoka_nid"],
							uchestvuval: ["kod", "data", "uchid", "uidentifikator", "midentifikator"],
							lek: ["lekid", "lekime", "sostav"],
							nastavnici: ["nidentifikator", "uchid"],
							bilshef: ["br_oddel", "datum_od", "br_chovek"],
							studenti: ["sid", "sindeks", "sime", "semail", "matichna_eid"],
							fkompanija: ["fkomid", "fkomime"],
							znae: ["uidentifikator", "oid"],
							profesori: ["br_chovek", "specijalnost", "titula"],
							istrazhuvachi: ["br_chovek", "br_proekt"],
							oblasti: ["oid", "onaziv"],
							asistentiistrazhuvachi: ["br_chovek_stud", "br_chovek_prof", "br_proekt"],
							matichen: ["liddok", "lidpac", "mbroj"],
							ima: ["kod", "oid"],
							prodavaprodukt: ["aptid", "fkomid", "prodid", "cena"],
							ekvivalencii: ["predmet_pid", "ekvivalenten_na_pid"],
							fkomtelbroj: ["fkomid", "telbroj"],
							pacient: ["lid", "pacmbr", "pacadresa", "datumragj"],
							apttelbroj: ["aptid", "telbroj"],
							zakazhano: ["kod", "data", "uchid"],
							chovek: ["lid", "lime"],
							osvoeni: ["kod", "data", "uidentifikator", "midentifikator", "oidentifikator", "mesto", "bodovi"],
							formiraniod: ["eid", "nid"],
							sponzori: ["br_sponzor", "ime_sponzor"],
							doktor: ["lid", "docmbr", "rabiskustvo", "specijalnost"],
							ocenuva: ["kod", "data", "oidentifikator"],
							raboti: ["br_chovek", "br_oddel", "procent"],
							davapari: ["br_proekt", "br_sponzor"],
							zadolzhitelni: ["nid", "pid"],
							produkt: ["fkomid", "prodid", "maxcena", "lekid", "prodime"],
							dogovor: ["fkomid", "aptid", "lid", "datumpoc", "datumkraj", "sodrzhina"],
							oddeli: ["br_oddel", "ime_oddel", "br_chovek", "br_oblast"],
							shefovi: ["br_oddel", "datum_od", "datum_do"],
							listaizbrani: ["sid", "semestar", "pid", "ocenka"],
							uchestvuval_vo: ["l_id", "p_id", "uloga"],
							aktivnosti: ["a_id", "a_naslov", "a_opis", "a_procenka_chasovi_rabota", "a_glavna_aktivnost_id", "p_id"],
							zapisnici: ["l_id", "z_reden_broj", "z_naslov", "z_opis", "z_rabotel_chasovi", "a_id", "datum_podnesen"],
							evaluacii: ["l_id", "z_reden_broj", "e_reden_broj", "e_naslov", "e_opis", "e_ocenka", "e_izvrshil_l_id", "datum_ocenuvanje"],
							lugje: ["identifikator", "ime", "adresa", "br_chovek", "embg", "ime_chovek", "vozrast"],
							lugje: ["l_id", "ime", "prezime", "email", "datum_ragjanje"],
							lugje: ["l_id", "ime", "prezime", "embg", "datum_ragj"],
							proekti: ["p_id", "p_naslov", "p_opis"],
						}
					}
				});

	});

});

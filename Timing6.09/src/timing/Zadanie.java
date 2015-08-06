package timing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;



public class Zadanie extends JLabel implements ActionListener, Serializable {

	public static DateFormat formatyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
	static String dzisiejszaData = Przelicznik.formatyyyyMMdd.format(new Date());
	static String wybranaData = null;

	/*
	 * S1 - pierwszy system liczenia czasu s1CzasTrwaniaFaktyczny = z licznika
	 * // faktyczny czas trwania zadania w ms brany z licznika s1CzasPauz = z
	 * dat spauzowania i odpauzowania // laczny czas pauz wyliczany z momentow
	 * pauzowania i odpauzowywania s1CzasTotal = s1CzasTrwania + s1CzasPauz //
	 * total to wyliczona suma czasu trwania z licznika i wyliczonego czasu pauz
	 * 
	 * S2 - drugi, kontrolny, system liczenia czasu s2CzasTrwania = s2CzasTotal
	 * - s2CzasPauz // wyliczany z wyliczany total - wyliczany czas pauz
	 * s2CzasPauz = z dat spauzowania i odpauzowania // laczny czas pauz
	 * wyliczany z momentow pauzowania i odpauzowywania s2CzasTotal = z dat
	 * startu i endu zadania // total wyliczany z momentow startu i stopu
	 * zadania
	 * 
	 * 
	 * S3 - trzeci, kontrolny, system liczenia czasu s3CzasTrwania = s2CzasTotal
	 * - s2CzasPauz s3CzasPauz = BRAK, chyba że dodamy jako parametr s3CzasTotal
	 * = z dat startu i endu zadania
	 */

	/* ZADANIE STATIKI */
	static int autoCounter = 1;

	public static Zadanie aktualneZadanie;
	static ArrayList<Zadanie> wszystkieZadania = new ArrayList<Zadanie>();
	static ArrayList daty = new ArrayList<>();
	public static ArrayList opisy = new ArrayList<>();

	/* POMOCNICZE */

	/* long nanoStart; */

	/* ZADANIE ATRYBUTY */
	int absoluteId;
	int iDZadania;
	String dataZadania;
	String taskName = "______";
	int ilePauzZadania = 0;
	String ilePauzZadaniaS = "";
	String czasRozpoczeciaS = "__:__:__";
	String czasZakonczeniaS = "__:__:__";
	Date dataRozpoczecia; // potrzebne do wyliczania ręcznego
	Date dataZakonczenia; // potrzebne do wyliczania czasu trwania z dat
	long s1CzasTrwaniaFaktyczny = 0;
	String s1CzasTrwaniaFaktycznyS = "00:00:00";
	long s1CzasTotal;
	String s1CzasTotalS = "";
	long s1CzasPauz = 0;
	String s1CzasPauzS = "";
	long s2CzasTotal;
	String s2CzasTotalS = "";
	long s3CzasTotal;
	String s3CzasTotalS = "00:00:00";
	String przebiegZadaniaMeldunek = "";
	boolean wCiagu = true;

	/* ZADANIE TIMER */
	private javax.swing.Timer timer = new javax.swing.Timer(100, this);
	boolean running = false;
	private long initTimeLicznika = System.currentTimeMillis();
	private long startTimeLicznika;
	private long pauseTimeLicznika;
	long licznikBiezacy;
	String licznikFormatMilisekund;
	String licznikFormatZegarowy;
	String terminSpauzowania;
	String terminOdpauzowania;
	long tempCzasPauz;
	Date dataSpauzowania; // wyliczanie pauzy
	Date dataOdpauzowania; // wyliczanie pauzy

	
	
	/* KONSTRUKTOR DLA ZADAN Z LICZNIKA */
	public Zadanie() {
		licznikBiezacy = System.currentTimeMillis() - initTimeLicznika;
		licznikFormatMilisekund = "" + (System.currentTimeMillis() - initTimeLicznika);
		licznikFormatZegarowy = Przelicznik.formatZDziesiatymiSekundy(System.currentTimeMillis() - initTimeLicznika);
		setText(licznikFormatZegarowy + "  |  " + licznikFormatMilisekund);
		setText(licznikFormatZegarowy + "  |  " + licznikFormatMilisekund);
	}

	/* KONSTRUKTOR DLA ZADAN Z PALCA */
	public Zadanie(String dataMoja, String nowyOpis, String otwarcie, String zamkniecie) throws ParseException,
			FileNotFoundException, IOException {
		super();
		Zadanie noweZadanie = new Zadanie();

		noweZadanie.absoluteId = autoCounter++;
		int ileZadanTegoDnia = 0;
		
		for (Zadanie z : wszystkieZadania) {
			if (z.dataZadania.equalsIgnoreCase(dataMoja)) {
				ileZadanTegoDnia++;
			}
		}
		noweZadanie.iDZadania = ileZadanTegoDnia + 1;
		Zadanie.wszystkieZadania.add(noweZadanie);

		noweZadanie.dataZadania = dataMoja;
		noweZadanie.czasRozpoczeciaS = otwarcie;
		noweZadanie.czasZakonczeniaS = zamkniecie;
		noweZadanie.taskName = nowyOpis;
		if (!daty.contains(noweZadanie.dataZadania)) {
			daty.add(noweZadanie.dataZadania);
		}

		if (!opisy.contains(noweZadanie.taskName)) {
			opisy.add(noweZadanie.taskName);
		}
		noweZadanie.s3CzasTotalS = noweZadanie.wyliczRecznie(otwarcie, zamkniecie);
		noweZadanie.s1CzasPauzS = "n.d.";
		noweZadanie.s1CzasTrwaniaFaktycznyS = "n.d.";
		noweZadanie.ilePauzZadaniaS = "n.d.";
		noweZadanie.s1CzasTotalS = "n.d.";
		Program.zapiszWszystkoDo();
	}
	
	// Utworz nowe zadanie
	public static Zadanie utworzNoweZadanie() {
		aktualneZadanie = new Zadanie();
		aktualneZadanie.absoluteId = autoCounter++;
		aktualneZadanie.iDZadania = getIleZadanDoneToday(dzisiejszaData) + 1;
		aktualneZadanie.dataZadania = Przelicznik.formatyyyyMMdd.format(new Date());
		wszystkieZadania.add(aktualneZadanie);
		if (!daty.contains(aktualneZadanie.dataZadania)) {
			daty.add(aktualneZadanie.dataZadania);
		}
		return aktualneZadanie;
	}

	// Zamknij bieżace zadanie
	public static void zamknijBiezaceZadanie() {
		aktualneZadanie = null;
	}
	
	public static void usunRzecz(Zadanie biezace) {
		wszystkieZadania.remove(biezace);
	}

	/* INTERFEJS */

	// startZadanie()

	// nadajNazweZadaniu()

	// nadpiszNazweZadania()

	// stopStartZadanie()

	// pauzujZadanie()

	// odpauzujZadanie()

	// dajRaport

	/* LOGIKA BIZNESOWA */



	// Wylicz, ile zadan wykonano tego dnia
	public static int getIleZadanDoneToday(String data) {
		int ile = 0;
		for (Zadanie z : wszystkieZadania) {
			if (z.dataZadania.equalsIgnoreCase(data) && (z != aktualneZadanie)) {
				ile++;
			}
		}
		return ile;
	}

	// Czy dane zadanie jest aktywne
	public boolean czyAktywneZadanie() {
		return this == aktualneZadanie;
	}

	// Zmień opis zadania
	public void zmienOpis(String text) {
		this.taskName = text;
		if (!opisy.contains(text)) {
			opisy.add(text);
		}
	}

	// Wylicz czas zadania do wpisania do Redmine
	public String wyliczCzasZadaniaDoRedmine() {
		String czasZadaniaDoRedmine = "";
		if (s1CzasTrwaniaFaktyczny != 0) {
			// System.out.println("!!! dla zadania istnieje czas faktyczny");
			czasZadaniaDoRedmine = s1CzasTrwaniaFaktycznyS;

		}
		else {
			// System.out.println("!!! dla zadania nie istnieje czas faktyczny - zostanie użyty s3CzasTotalS");
			czasZadaniaDoRedmine = s3CzasTotalS;
		}
		// System.out.println(toString() + "    " + czasZadaniaDoRedmine);
		return czasZadaniaDoRedmine;

	}
	
	public String wyswietlPrzebiegZadaniaMeldunek() {
		return przebiegZadaniaMeldunek;
	}

	// toString dla listy zadan

	// s1CzasTrwaniaFaktycznyS - ten jest naliczany do RM
	// s3CzasTotalS - czas trwania ze sparsowanych dat
	public String toString() {
		return dataZadania + " " + " Zadanie " + "#" +absoluteId + " dzienne: "+iDZadania + " | " + czasRozpoczeciaS + "-" + czasZakonczeniaS + " | " + taskName
				+ " | " + s1CzasTrwaniaFaktycznyS + " | " + s1CzasTotalS + " | " + "Pauz: " + s1CzasPauzS;
	}

	public void wyswietlMeldunekONadaniuOpisu(String text) {
		przebiegZadaniaMeldunek += "- Dodano opis: " + text + "\n";
	}

	/* TIMER */

	@Override
	public void actionPerformed(ActionEvent e) {
		licznikBiezacy = System.currentTimeMillis() - startTimeLicznika;
		licznikFormatMilisekund = "" + (System.currentTimeMillis() - startTimeLicznika);
		licznikFormatZegarowy = Przelicznik.formatZDziesiatymiSekundy(System.currentTimeMillis() - startTimeLicznika);
		setText(licznikFormatZegarowy);
	}

	// Uruchom licznik
	public void startCounter() {
		utworzNoweZadanie();
		if (running == false) {
			startTimeLicznika = System.currentTimeMillis();
			/* nanoStart = System.nanoTime(); */
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			aktualneZadanie.dataRozpoczecia = new Date(timestamp.getTime());
			aktualneZadanie.czasRozpoczeciaS = Przelicznik.timeClockFormat.format(timestamp);
			przebiegZadaniaMeldunek = "- Otwarto zadanie " + aktualneZadanie.iDZadania + ". Czas rozpoczęcia zadania: ["
					+ aktualneZadanie.czasRozpoczeciaS + "]\n";
		}
		else {
			startTimeLicznika = System.currentTimeMillis() - (pauseTimeLicznika - startTimeLicznika);
		}
		timer.start();
		running = true;
	}

	// Zatrzymaj licznik
	public void stopCounter() {
		timer.stop();
		long ile = System.nanoTime();
		running = false;

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		aktualneZadanie.dataZakonczenia = new Date(timestamp.getTime());

		/* long odDo = (ile - nanoStart) / 1000000; */
		String licznik = getText();
		aktualneZadanie.s1CzasTrwaniaFaktyczny = licznikBiezacy;
		// aktualneZadanie.s1CzasTrwaniaFaktycznyS =
		// Przelicznik.formatZSekundamiStatic(aktualneZadanie.s1CzasTrwaniaFaktyczny)
		// + ", " + aktualneZadanie.s1CzasTrwaniaFaktyczny; // z milisekundami
		aktualneZadanie.s1CzasTrwaniaFaktycznyS = Przelicznik.formatZSekundamiStatic(aktualneZadanie.s1CzasTrwaniaFaktyczny);
		aktualneZadanie.czasZakonczeniaS = Przelicznik.timeClockFormat.format(timestamp);

		przebiegZadaniaMeldunek += "- Zamknięto zadanie " + aktualneZadanie.iDZadania + ". Czas zakończenia zadania: ["
				+ aktualneZadanie.czasZakonczeniaS + "]\n";
		przebiegZadaniaMeldunek += "- Podsumowanie czasu" + "\n";
		przebiegZadaniaMeldunek += "  Czas trwania faktyczny (z licznika) w milisekundach: ["
				+ aktualneZadanie.s1CzasTrwaniaFaktyczny + "] i w formacie zegarowym: ["
				+ aktualneZadanie.s1CzasTrwaniaFaktycznyS + "]\n";

		aktualneZadanie.s1CzasTotal = aktualneZadanie.s1CzasTrwaniaFaktyczny + aktualneZadanie.s1CzasPauz;
		// aktualneZadanie.s1CzasTotalS =
		// Przelicznik.formatZSekundamiStatic(aktualneZadanie.s1CzasTotal) +
		// ", " + aktualneZadanie.s1CzasTotal; // z milisekundami
		aktualneZadanie.s1CzasTotalS = Przelicznik.formatZSekundamiStatic(aktualneZadanie.s1CzasTotal);

		// aktualneZadanie.s1CzasPauzS =
		// Przelicznik.formatZSekundamiStatic(tempCzasPauz) + ", " +
		// tempCzasPauz;
		aktualneZadanie.s1CzasPauzS = Przelicznik.formatZSekundamiStatic(tempCzasPauz);

		przebiegZadaniaMeldunek += "  czas pauz: " + aktualneZadanie.s1CzasPauz + "\n";
		przebiegZadaniaMeldunek += "  TOTALs1: " + "czas pauz: " + aktualneZadanie.s1CzasPauz + " + czas trwania z licznika: "
				+ +aktualneZadanie.s1CzasTrwaniaFaktyczny + "  =  " + aktualneZadanie.s1CzasTotal + "\n";

		try {
			aktualneZadanie.s3CzasTotalS = aktualneZadanie.wyliczRecznie(aktualneZadanie.czasRozpoczeciaS,
					aktualneZadanie.czasZakonczeniaS);
			aktualneZadanie.s3CzasTotal = Przelicznik.skonwertujStringToLong(aktualneZadanie.s3CzasTotalS);

		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		aktualneZadanie.przebiegZadaniaMeldunek += "  CzasOdDos3: w milisekundach [" + aktualneZadanie.s3CzasTotal
				+ "] i w formacie zegatowym: [" + aktualneZadanie.s3CzasTotalS + "]\n";

		try {
			aktualneZadanie.s3CzasTotalS = aktualneZadanie.wyliczRecznie(aktualneZadanie.czasRozpoczeciaS,
					aktualneZadanie.czasZakonczeniaS);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		zamknijBiezaceZadanie();
		tempCzasPauz = 0;

	}

	// Spauzuj licznik
	public void pauzeCounter() {
		pauseTimeLicznika = System.currentTimeMillis();
		timer.stop();
		terminSpauzowania = Przelicznik.timeClockFormat.format(new Date());
		System.out.println("---czas spauzowania: " + terminSpauzowania + ", " + getText());
		przebiegZadaniaMeldunek += "---czas spauzowania: " + terminSpauzowania + ", " + getText() + "\n";
		aktualneZadanie.ilePauzZadania++;
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		dataSpauzowania = new Date(timestamp.getTime());
	}

	// Odpauzuj licznik
	public void unpauzeCounter() {
		if (running == false) {
			startTimeLicznika = System.currentTimeMillis();
		}
		else {
			startTimeLicznika = System.currentTimeMillis() - (pauseTimeLicznika - startTimeLicznika);
		}

		timer.start();
		running = true;

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		dataOdpauzowania = new Date(timestamp.getTime());
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		terminOdpauzowania = Przelicznik.timeClockFormat.format(new Date());
		System.out.println("---czas odpauzowania: " + terminOdpauzowania + ", " + getText());
		przebiegZadaniaMeldunek += "---czas odpauzowania: " + terminOdpauzowania + ", " + getText() + "\n";
		long czasTrwaniaPauzy = dataOdpauzowania.getTime() - dataSpauzowania.getTime();
		System.out.println("czas trwania tej pauzy, w milisekundach: " + czasTrwaniaPauzy);
		przebiegZadaniaMeldunek += "czas trwania tej pauzy, w milisekundach: " + czasTrwaniaPauzy + "\n";
		tempCzasPauz += czasTrwaniaPauzy;
		aktualneZadanie.s1CzasPauz = tempCzasPauz;
		System.out.println("!!! aktualneZadanie.s1CzasPauz: " + aktualneZadanie.s1CzasPauz);
		// aktualneZadanie.s1CzasPauzS =
		// Przelicznik.formatZSekundamiStatic(tempCzasPauz) + ", " +
		// tempCzasPauz; // z milisekundami
		aktualneZadanie.s1CzasPauzS = Przelicznik.formatZSekundamiStatic(tempCzasPauz);
		System.out.println("- łączny czas trwania pauz zsumowany, w milisekundach: " + aktualneZadanie.s1CzasPauz);
		przebiegZadaniaMeldunek += "- łączny czas trwania pauz zsumowany, w milisekundach: " + aktualneZadanie.s1CzasPauz + "\n";
	}

	/* RAPORTOWE */

	// Wylicz sume czasu dla taskow o tym samym opisie
	public static String wyliczSumeDlaOpisu(String dzien) {
		String info = "Suma dla " + dzien + "\n";
		long sumaGodzinOpisu = 0;
		for (int i = 0; i < opisy.size(); i++) {
			for (Zadanie z : wszystkieZadania) {
				if (z.dataZadania.equalsIgnoreCase(dzien)) {
					if (z.taskName.equalsIgnoreCase(opisy.get(i).toString())) {
						sumaGodzinOpisu += Przelicznik.skonwertujStringToLong(z.wyliczCzasZadaniaDoRedmine());
					}
				}
			}

			if (sumaGodzinOpisu != 0) {
				info += "- " + opisy.get(i).toString() + ": " + Przelicznik.formatZSekundamiStatic(sumaGodzinOpisu)
						+ " | Redmine: " + Przelicznik.zaokraglijDoPolGodziny(sumaGodzinOpisu) + "\n";
			}
			sumaGodzinOpisu = 0;
		}

		System.out.println(info);
		return info;
	}
	
	public static ArrayList<Zadanie> getZadaniaDnia(String data) {
		
		ArrayList<Zadanie> zadaniaTegoDnia = null;
		for (Zadanie z : wszystkieZadania) {
			if(z.dataZadania.equalsIgnoreCase(data)) {
				zadaniaTegoDnia.add(z);
			}
			
		}
		
		
		return zadaniaTegoDnia;
		
	}
	
	public static Zadanie getZadaniePoAbsoluteID(int absolute) {
		Zadanie poszukiwane = null;
		for (Zadanie z : wszystkieZadania) {
			if(z.absoluteId == absolute) {
				poszukiwane = z;
			}
		}
		return poszukiwane;
		
	}

	// Wyswietl podsumowanie dnia z podliczeniem do Redmine dla podanej daty
	public static String wyswietlPodsumowanieDnia(String data) {
		String info = "PODSUMOWANIE " + data + "\n";
		String grupaZDnia = "";
		long sumaGodzinDnia = 0;

		long sumaGodzinOpisu = 0;

		for (Zadanie z : wszystkieZadania) {
			if (z.dataZadania.equalsIgnoreCase(data)) {
				System.out.println("zgadza sie = equals");
				grupaZDnia += z.taskName + " | " + z.wyliczCzasZadaniaDoRedmine() + "\n";
				if (z.wCiagu == false) {
					grupaZDnia += "------stop------" + "\n";
				}
				sumaGodzinDnia += Przelicznik.skonwertujStringToLong(z.wyliczCzasZadaniaDoRedmine());
			}
		}
		info += grupaZDnia;
		info += wyliczSumeDlaOpisu(data);
		info += "SUMA DLA " + data + ": " + Przelicznik.formatZSekundamiStatic(sumaGodzinDnia) + " | Redmine: "
				+ Przelicznik.zaokraglijDoPolGodziny(sumaGodzinDnia) + "\n" + "\n";
		System.out.println("SUMA DLA " + data + ": " + Przelicznik.formatZSekundamiStatic(sumaGodzinDnia) + " | Redmine: "
				+ Przelicznik.zaokraglijDoPolGodziny(sumaGodzinDnia) + "\n");

		return info;
	}


	// Wyswietl podsumowanie TOTAL z podliczeniem dziennym do Redmine
	public static String wyswietlZadaniaGrupowanePoDacie() {
		String info = "";
		String grupaZDnia = "";
		long sumaGodzinDnia = 0;

		long sumaGodzinOpisu = 0;

		if (wszystkieZadania.isEmpty()) {
			info = "Brak zadań";
		}

		else {
			for (int i = 0; i < daty.size(); i++) {
				info += daty.get(i).toString() + "\n";
				for (Zadanie z : wszystkieZadania) {
					if (z.dataZadania.equalsIgnoreCase(daty.get(i).toString()) && (z != aktualneZadanie)) {
						grupaZDnia += "Zadanie " + "#" + z.absoluteId + " dzienne: "+ z.iDZadania + " | " + z.czasRozpoczeciaS + "-" + z.czasZakonczeniaS + " | "
								+ z.taskName + " | " + z.wyliczCzasZadaniaDoRedmine() + "\n";
						sumaGodzinDnia += Przelicznik.skonwertujStringToLong(z.wyliczCzasZadaniaDoRedmine());
					}
				}
				info += grupaZDnia;
				info += wyliczSumeDlaOpisu(daty.get(i).toString());
				info += "SUMA DLA " + daty.get(i).toString() + ": " + Przelicznik.formatZSekundamiStatic(sumaGodzinDnia)
						+ " | Redmine: " + Przelicznik.zaokraglijDoPolGodziny(sumaGodzinDnia) + "\n" + "\n";
				System.out.println("SUMA DLA " + daty.get(i).toString() + ": "
						+ Przelicznik.formatZSekundamiStatic(sumaGodzinDnia) + " | Redmine: "
						+ Przelicznik.zaokraglijDoPolGodziny(sumaGodzinDnia) + "\n");

				grupaZDnia = "";
				sumaGodzinDnia = 0;
			}
		}
		return info;
	}

	// Wyswietl zadania dzisiejsze
	public static String wyswietlZadaniaDlaDaty(String data) {
		int ile = 0;
		String info = "";

		info += data;
		for (Zadanie z : wszystkieZadania) {

			if (z.dataZadania.equalsIgnoreCase(data)) {
				ile++;
				info += z.toString() + "\n";
			}

		}
		
		if (ile == 0) {
			info = "brak wykonanych zadan dla tej daty " + data;
		}

		return info;
	}


	// updatuj metryczke
	public String dayTaskStatusUpdate() {
		String status = "";
		String IDZadania = "";
		String przerw = "";
		String zadan = "";
		if ((aktualneZadanie != null) && (ilePauzZadania > 0)) {
			przerw = " Przerw: " + ilePauzZadania;
		}

		int ileWykonanychDzis = getIleZadanDoneToday(dzisiejszaData);
		zadan = " Wykonanych dziś zadań: " + ileWykonanychDzis;
		dzisiejszaData = Przelicznik.formatyyyyMMdd.format(new Date());
		status = "Dziś mamy: " + dzisiejszaData + IDZadania + przerw + zadan;
		return status;
	}

	/* ODTWARZANIE, BACKUP, DODAWANIE RĘCZNIE */


	public void zmienDateZadania(int numer, String dataMoja) {
		for (Zadanie z : wszystkieZadania) {
			if ((z.iDZadania == numer) && z.dataZadania.equalsIgnoreCase(dataMoja)) {
				System.out.println(z.toString());
				System.out.println("Zmiana daty zadania nr " + z.iDZadania);
				System.out.println("Z: " + z.dataZadania + " na: " + dataMoja);
				z.dataZadania = dataMoja;
				System.out.println(z.toString());
			}
		}
	}

	public void zmienOpisZadania(int numer, String dataMoja, String nowyOpis) {
		for (Zadanie z : Zadanie.wszystkieZadania) {
			if ((z.iDZadania == numer) && z.dataZadania.equalsIgnoreCase(dataMoja)) {
				System.out.println(z.toString());
				System.out.println("Zmiana opisu zadania nr " + z.iDZadania + " z dnia " + z.dataZadania);
				System.out.println("Z: " + z.taskName + " na: " + nowyOpis);
				z.taskName = nowyOpis;
				System.out.println(z.toString());
			}
		}
	}

	public String wyliczRecznie(String otwarcie, String zamkniecie) throws ParseException {
		long temp = s3CzasTotalSZeSparsowanychCzasow(otwarcie, zamkniecie);
		String tempString = Przelicznik.formatZSekundamiStatic(temp);
		return tempString;
	}

	public long s3CzasTotalSZeSparsowanychCzasow(String czas1, String czas2) {
		Date start = null;
		Date end = null;
		try {
			start = Przelicznik.timeClockFormat.parse(czas1);
			end = Przelicznik.timeClockFormat.parse(czas2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return (end.getTime() - start.getTime());
	}



	/* EKSTENSJA */

	public static int getAutoCounter() {
		return autoCounter;
	}

	public static void setAutocounter(int autocounter) {
		Zadanie.autoCounter = autocounter;
	}

	public static void pokazEkstensje() throws FileNotFoundException, IOException, ClassNotFoundException {
		System.out.println("----------------------");
		System.out.println("Ekstensja klasy: " + Zadanie.class);
		for (Zadanie z : wszystkieZadania) {
			String spacja = "";
			if (z.iDZadania < 10) {
				spacja = " ";
			}
			System.out.println(spacja + z.toString());
		}
	}

	public static void wczytajEkstensje(ObjectInputStream in) throws FileNotFoundException, IOException, ClassNotFoundException {
		wszystkieZadania = (ArrayList<Zadanie>) in.readObject();
		daty = (ArrayList) in.readObject();
		opisy = (ArrayList) in.readObject();
		int numer = in.readInt();
		Zadanie.setAutocounter(numer);
	}

	public static void zapiszEkstensje(ObjectOutputStream out) throws IOException {
		out.writeObject(wszystkieZadania);
		out.writeObject(daty);
		out.writeObject(opisy);
		out.writeInt(getAutoCounter());
	}
}
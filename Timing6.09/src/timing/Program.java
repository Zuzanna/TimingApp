package timing;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;




import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;


public class Program {
	
	final static String appNameVersion = "TimingApp6.09";
	final static String currentUser = System.getProperty("user.name");
	public static boolean firstUse = true;
	
	private static String tempDir = System.getProperty ("java.io.tmpdir"); 
	final static String ekstensjaPath = tempDir + appNameVersion + ".bin"; 
	final static String pdfPath = tempDir + appNameVersion + "-statystyki";

	public static final String FONTBOLD = "c:/windows/fonts/arialbd.ttf";
	public static final String FONTNORMAL = "c:/windows/fonts/arial.ttf";

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, ClassNotFoundException, DocumentException {
		try {
			wczytajWszystkoDo();
			firstUse = false;
		} catch (Exception e1) {
			firstUse = true;
		}		
		inicjalizujAll();
		zapiszWszystkoDo();	
	}

	// Wczytaj ekstensje
	static void wczytajWszystkoDo() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(ekstensjaPath));
		Zadanie.wczytajEkstensje(in);
		in.close();
	}


	// Otworz glowne okno
	private static void inicjalizujAll() {
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyFrameStart();
                MyFrameStart.lZadaniaLocal.setSelectedIndex(Zadanie.getIleZadanDoneToday(Zadanie.dzisiejszaData) - 1); 
                MyFrameStart.lZadaniaLocal.ensureIndexIsVisible(Zadanie.getIleZadanDoneToday(Zadanie.dzisiejszaData) - 1);
            }
        });
	}
	
	
	// Zapisz ekstensje
	static void zapiszWszystkoDo() throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ekstensjaPath));
		Zadanie.zapiszEkstensje(out);
		out.close();
	}
	
	// Daj instrukcje obslugi
	public static String instrukcja() {
		String info = "";
		info += "Kliknij START, by rozpocząć nowe zadanie" + "\n";
		info += "Wpisz opis zadania i kliknij ENTER." + "\n";
		info += "Kliknij STOP, by zamknąć zadanie." + "\n";
		info += "Kliknij ZAKOŃCZ/ZACZNIJ, by zamknąć zadanie i od razu otworzyć nowe." + "\n";
		info += "Kliknij PAUZA, aby przerwać bieżące zadanie, następnie PLAY, aby je wznowić." + "\n\n";
		info += "W tym oknie będziesz widzieć raport z zadań z podliczeniem czasu";
		return info;
	}
	
	// Generuj raport txt za dzień
	public static void saveToTXTFile() {
		String path = tempDir + appNameVersion + "-txtFile";
		try {
			File fileDir = new File(path + ".txt");

			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "UTF8"));
			String content = Zadanie.wyswietlPodsumowanieDnia(Zadanie.dzisiejszaData);
			out.append("STATYSTYKI wygenerowane " + Przelicznik.formatyyyyMMdd.format(new Date())
					+ ", " + Przelicznik.timeClockFormat.format(new Date())).append("\r\n");;
			out.append(content).append("\r\n");
			out.flush();
			out.close();
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	// Otworz plik PDF z raportem
	public static void openPDF() {
		File file2 = new File(pdfPath  + ".pdf");
		generatePDF(file2);

		try {
			if (file2.exists()) {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(file2);
				}
				else {
					System.out.println("Awt Desktop is not supported!");
				}
			}
			else {
				System.out.println("File is not exists!");
			}
			System.out.println("Done. PDF generated, can be found here: " + pdfPath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Generuj raport PDF
	private static void generatePDF(File file2) {
		BaseFont fb;
		BaseFont fn;
		Rectangle pagesize = new Rectangle();
		
		com.itextpdf.text.Document document2 = new com.itextpdf.text.Document(PageSize.A4, 50, 50, 50, 50);
		document2.addCreationDate();

		try {
			PdfWriter writer = PdfWriter.getInstance(document2, new FileOutputStream(file2));
			document2.open();
			fb = BaseFont.createFont(FONTBOLD, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			fn = BaseFont.createFont(FONTNORMAL, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			Paragraph a = new Paragraph(wyczyscZPolskichZnakow("STATYSTYKI wygenerowane " + Przelicznik.formatyyyyMMdd.format(new Date())
					+ ", " + Przelicznik.timeClockFormat.format(new Date())), new Font(fb, 12));
			document2.add(a);
			String czyszczony = wyczyscZPolskichZnakow(Zadanie.wyswietlZadaniaGrupowanePoDacie());
			document2.add(new Paragraph(czyszczony, new Font(fn, 12)));
			document2.close();

		} catch (FileNotFoundException | DocumentException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Konwertuj polskie znaki do UTF do PDF
	private static String wyczyscZPolskichZnakow(String all) {
		String all_n = all.replaceAll("ń", "\u0144");
		String all_o = all_n.replaceAll("ó", "\u00F3");
		String all_e = all_o.replaceAll("ę", "\u0119");
		String all_a = all_e.replaceAll("ą", "\u0105");
		String all_z1 = all_a.replaceAll("ż", "\u017C");
		String all_z2 = all_z1.replaceAll("ź", "\u017A");
		String all_s = all_z2.replaceAll("ś", "\u015B");
		String all_fin = all_s.replaceAll("ł", "\u0142");
		String all_fin2 = all_fin.replaceAll("Ó", "\u00D3");
		String all_fin3 = all_fin2.replaceAll("Ą", "\u0104");
		String all_fin4 = all_fin3.replaceAll("Ę", "\u0118");
		String all_fin5 = all_fin4.replaceAll("Ł", "\u0141");
		String all_fin6 = all_fin5.replaceAll("Ś", "\u015A");
		String all_fin7 = all_fin6.replaceAll("Ć", "\u0106");
		String all_fin8 = all_fin7.replaceAll("Ż", "\u017B");
		String all_fin9 = all_fin8.replaceAll("Ź", "\u0179");
		String all_fin10 = all_fin9.replaceAll("Ń", "\u0143");
		String all_fin11 = all_fin10.replaceAll("ć", "\u0107");
		return all_fin11;
	}

}
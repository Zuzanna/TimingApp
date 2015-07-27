package timing;


import helpers.ExclusionDateRange;
import helpers.JCalendarDialog;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



























import com.itextpdf.text.DocumentException;

public class MyFrameStart extends JFrame {
	
	JDialog d1;
	final static JButton bOpenDialog = new JButton("Dodaj zadanie z palca");
	
	JButton bCalendar = new JButton("Wybierz date");
	
	static Zadanie wybraneZadanie = null;	
	public static String wybranaData = Zadanie.dzisiejszaData;
	
	public final static JTextField tdateField = new JTextField(12);

	JButton bSaveToTXT = new JButton("Zapisz jako TXT");
	// utwórz elementy kluczowe

	ClockLabel clock = new ClockLabel();
	final static Zadanie otwarteZadanie = new Zadanie();
	private static final String PlayString = "Play";
	private static final String PauseString = "Pauza";
	final static JButton bStart = new JButton("Start");	
	final static JButton bStop = new JButton("Stop");	
	final static JButton bStopStart = new JButton("Zakończ/zacznij");
	final static JButton bPauza = new JButton(PauseString);	
	final static JTextField fOpisZadania = new JTextField(20);	

	final static JButton bOpenGeneratePDF = new JButton("Wygeneruj i otwórz raport PDF");
	final static JTextField fDataIleWykonanychJuz = new JTextField(40);
	
	// lista i nawigacja po liscie
	final static JList lZadaniaLocal = new JList(ModelZadan.listModelZadan);
	final static JButton bWyswietlMetryczke = new JButton("Wyświetl metryczkę");
	
	//final static JButton bChooseDate = new JButton("Wybierz date");
	
	// info
	final static JLabel binPath = new JLabel();
	final static JLabel  fInfoTxt = new JLabel();
	
	
	
	
	private static final String WszystkieString = "Wyświetl wszystkie zadania w systemie";
	private static final String DzisiejszeString = "Wyświetl tylko dzisiejsze zadania";
	final static JButton bWyswietlZadania = new JButton(WszystkieString);

	final static JTextArea tPrzebiegZadaniaDisplayArea = new JTextArea(5, 50);
	

	public final static JTextArea tResultDisplay = new JTextArea(20, 50);


	//z FrameCheckOutCalendar
	public final JFrame frame = null;
	public final JTextField dateField = new JTextField(12);
	

	
	
	public MyFrameStart() {
		// skonfiguruj frame
		super(Program.appNameVersion);
		setDefaultCloseOperation(MyFrameStart.EXIT_ON_CLOSE);
		

		
		setSize(700, 650);
		setLocation(50, 50);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setVisible(true);
		
		
		


	
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(lZadaniaLocal);
		
		fDataIleWykonanychJuz.setText(otwarteZadanie.dayTaskStatusUpdate()); // żeby już na starcie nr wykonanych zadań dnia był aktualny
		
		ModelZadan.aktualizujListeZadan();
		ModelZadan.ustawParametryWyswietlaniaListyZadan(lZadaniaLocal);	
		
		JScrollPane scrolltxtarea = new JScrollPane(tResultDisplay);
		scrolltxtarea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		add(clock);
		add(bStart);
		add(fOpisZadania);
		add(bStop);
		add(bStopStart);
		add(bPauza);
		add(otwarteZadanie);
		add(binPath);
		//add(fInfoTxt); // dodana wersja
		add(fDataIleWykonanychJuz);	
		add(scrollPane);
		add(bWyswietlMetryczke);
		add(scrolltxtarea);
		
		

		
		
		ustawOknoNaStart();
		
		String welcomeUser = "Cześć, " + Program.currentUser +"!";
		
		if (Program.firstUse) {
			//JOptionPane.showMessageDialog(null, Program.instrukcja());
			JOptionPane.showMessageDialog(null, Program.instrukcja(), welcomeUser, JOptionPane.INFORMATION_MESSAGE);
			//JOptionPane.showMessageDialog(null, Program.instrukcja() "Hi, " + Program.currentUser +"!");
		}
		
		bCalendar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {
				JCalendarDialog dialog = new JCalendarDialog(frame);
	            dialog.addExclusionDateRange(new ExclusionDateRange("M/d/yy",
	                    "2/16/15", "2/16/15"));
	            dialog.setDialogTitle("Appointment Date");
	            //dialog.setExclusionDaysOfWeek(Calendar.SATURDAY, Calendar.SUNDAY);
	            dialog.setLocale(Locale.ENGLISH);
	            dialog.createDialog();
	            if (dialog.getReturnCode() == JCalendarDialog.OK_PRESSED) {
	            	dateField.setText(dialog.getFormattedSelectedDate());
	            	tdateField.setText(dialog.getFormattedSelectedDate());
	            	MyFrameStart.tdateField.setText(dialog.getFormattedSelectedDate());
	            	wybranaData = dialog.getFormattedSelectedDate();
	            	//MyFrameStart.wybranaData = wybranaDataZKalendarza;
	            	ModelZadan.wyswietlListeZadan(wybranaData);
	            	MyFrameStart.tResultDisplay.setText(Zadanie.wyswietlPodsumowanieDnia(wybranaData));
	            	//lZadaniaLocal.clearSelection();
	            	MyFrameStart.lZadaniaLocal.setSelectedIndex(Zadanie.getIleZadanDoneToday(wybranaData) - 1); 
	                MyFrameStart.lZadaniaLocal.ensureIndexIsVisible(Zadanie.getIleZadanDoneToday(wybranaData) - 1);
	            }
			}
		});
		
/*		bChooseDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {
		
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						new FrameCheckOutCalendar();
					}
				});

			}
		});*/

		bSaveToTXT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {
		
				Program.saveToTXTFile();

			}
		});
		
		bOpenDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {
		
				createAndShowGUI();

			}
		});
		
		
		// dodaj listenery do elementów
		
		bWyswietlMetryczke.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						new MyFrameZadanie();
					}
				});
			}
		});

		bStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tPrzebiegZadaniaDisplayArea.setText("");
				otwarteZadanie.startCounter();
				ModelZadan.aktualizujListeZadan(); // dopisuję
				//tResultDisplay.setText(Zadanie.wyswietlZadaniaDzisiejsze());
				tResultDisplay.setText(Zadanie.wyswietlPodsumowanieDnia(Zadanie.dzisiejszaData));
				fDataIleWykonanychJuz.setText(otwarteZadanie.dayTaskStatusUpdate());
				tPrzebiegZadaniaDisplayArea.setText(otwarteZadanie.wyswietlPrzebiegZadaniaMeldunek());
				ustawPrzyciskiPoStarcie();
				
				//lZadaniaLocal.ensureIndexIsVisible(lZadaniaLocal.getSelectedIndex(ModelZadan.listModelZadan.getSize() - 1));
				
				lZadaniaLocal.setSelectedIndex(Zadanie.getIleZadanDoneToday(wybranaData));  
				lZadaniaLocal.ensureIndexIsVisible(lZadaniaLocal.getSelectedIndex());
				
				//lZadaniaLocal.ensureIndexIsVisible(ModelZadan.listModelZadan.getSize() - 1);
			}

			private void ustawPrzyciskiPoStarcie() {
				bStart.setEnabled(false);
				fOpisZadania.setEnabled(true);
				bStop.setEnabled(true);
				bPauza.setEnabled(true);
				bStopStart.setEnabled(true);
			}
		});

		fOpisZadania.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {	
				otwarteZadanie.wyswietlMeldunekONadaniuOpisu(fOpisZadania.getText());
				Zadanie.aktualneZadanie.taskName = fOpisZadania.getText();
				tPrzebiegZadaniaDisplayArea.setText(otwarteZadanie.wyswietlPrzebiegZadaniaMeldunek()); // SOLVED 4
				
				if (!Zadanie.opisy.contains(Zadanie.aktualneZadanie.taskName)) {
					Zadanie.opisy.add(Zadanie.aktualneZadanie.taskName);
				}

				ModelZadan.aktualizujListeZadan(); // dopisuję
				//tResultDisplay.setText(Zadanie.wyswietlZadaniaDzisiejsze());
				tResultDisplay.setText(Zadanie.wyswietlPodsumowanieDnia(Zadanie.dzisiejszaData));
				
				lZadaniaLocal.setSelectedIndex(Zadanie.getIleZadanDoneToday(wybranaData));  
				lZadaniaLocal.ensureIndexIsVisible(lZadaniaLocal.getSelectedIndex());
			}
		});



		bStopStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {
				otwarteZadanie.stopCounter();
				tPrzebiegZadaniaDisplayArea.setText("");
				otwarteZadanie.startCounter();
				ModelZadan.aktualizujListeZadan(); // dopisuję
				//tResultDisplay.setText(Zadanie.wyswietlZadaniaDzisiejsze());
				tResultDisplay.setText(Zadanie.wyswietlPodsumowanieDnia(Zadanie.dzisiejszaData));
				fDataIleWykonanychJuz.setText(otwarteZadanie.dayTaskStatusUpdate());
				tPrzebiegZadaniaDisplayArea.setText(otwarteZadanie.wyswietlPrzebiegZadaniaMeldunek());
				
				lZadaniaLocal.setSelectedIndex(Zadanie.aktualneZadanie.iDZadania - 1);  
				lZadaniaLocal.ensureIndexIsVisible(Zadanie.aktualneZadanie.iDZadania - 1);
				
				try {
					Program.zapiszWszystkoDo();
				} catch (IOException e) {
					e.printStackTrace();
				}
	
			}

		});

		bStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ustawPrzyciskiPoStopie();
				Zadanie.aktualneZadanie.wCiagu = false;
				//otwarteZadanie.wCiagu = false;
				otwarteZadanie.stopCounter();	
				
				
				ModelZadan.aktualizujListeZadan(); // dopisuję
				//tResultDisplay.setText(Zadanie.wyswietlZadaniaDzisiejsze());
				tResultDisplay.setText(Zadanie.wyswietlPodsumowanieDnia(Zadanie.dzisiejszaData));
				fDataIleWykonanychJuz.setText(otwarteZadanie.dayTaskStatusUpdate());
				tPrzebiegZadaniaDisplayArea.setText("");
				ModelZadan.aktualizujListeZadan();
				
				/*lZadaniaLocal.setSelectedIndex(Zadanie.aktualneZadanie.iDZadania - 1);  
				lZadaniaLocal.ensureIndexIsVisible(Zadanie.aktualneZadanie.iDZadania - 1);*/
				
				lZadaniaLocal.setSelectedIndex(Zadanie.getIleZadanDoneToday(Zadanie.dzisiejszaData) - 1);  
				lZadaniaLocal.ensureIndexIsVisible(lZadaniaLocal.getSelectedIndex() - 1);
				
				try {
					Program.zapiszWszystkoDo();
				} catch (IOException ef) {
					ef.printStackTrace();
				}
			}

			private void ustawPrzyciskiPoStopie() {
				bPauza.setEnabled(false);
				bStop.setEnabled(false);
				bStopStart.setEnabled(false);
				bStart.setEnabled(true);
				fOpisZadania.setEnabled(false);
			}
		});

		bPauza.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if (PauseString.equals(cmd)) {
					otwarteZadanie.pauzeCounter();
					fDataIleWykonanychJuz.setText(otwarteZadanie.dayTaskStatusUpdate());
					tPrzebiegZadaniaDisplayArea.setText(otwarteZadanie.wyswietlPrzebiegZadaniaMeldunek());
					ModelZadan.aktualizujListeZadan();
					ustawPrzyciskiPoPauzie();
				} else {
					otwarteZadanie.unpauzeCounter();
					ustawPrzyciskiPoUnPauzie();
					fDataIleWykonanychJuz.setText(otwarteZadanie.dayTaskStatusUpdate());
					tPrzebiegZadaniaDisplayArea.setText(otwarteZadanie.wyswietlPrzebiegZadaniaMeldunek());
					ModelZadan.aktualizujListeZadan();
				}
				ModelZadan.aktualizujListeZadan(); // dopisuję
				//tResultDisplay.setText(Zadanie.wyswietlZadaniaDzisiejsze());
				tResultDisplay.setText(Zadanie.wyswietlPodsumowanieDnia(Zadanie.dzisiejszaData));
			}

			private void ustawPrzyciskiPoPauzie() {
				fDataIleWykonanychJuz.setText("---czas spauzowania: " + otwarteZadanie.terminSpauzowania);
				bPauza.setText(PlayString);
				bStop.setEnabled(false);
				bStopStart.setEnabled(false);
			}

			private void ustawPrzyciskiPoUnPauzie() {
				bPauza.setText(PauseString);
				bStop.setEnabled(true);
				bStopStart.setEnabled(true);
			}
		});
		
		bWyswietlZadania.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if (WszystkieString.equals(cmd)) {
					tResultDisplay.setText(Zadanie.wyswietlZadaniaGrupowanePoDacie());
					bWyswietlZadania.setText(DzisiejszeString);
					//ModelZadan.aktualizujListeZadan();
					ModelZadan.wyswietlListeZadan(wybranaData);
				} else {
					ModelZadan.aktualizujListeZadan(); // dopisuję
					tResultDisplay.setText(Zadanie.wyswietlZadaniaDlaDaty(Zadanie.dzisiejszaData));
					//ModelZadan.aktualizujListeZadan();
					ModelZadan.wyswietlListeZadan(wybranaData);
					bWyswietlZadania.setText(WszystkieString);
				}

			}
		});
		
		bOpenGeneratePDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Program.openPDF();
			}
		});
		
		
		lZadaniaLocal.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()) {
					wybraneZadanie = (Zadanie) lZadaniaLocal.getSelectedValue();
					if (wybraneZadanie != null) {
						bWyswietlMetryczke.setEnabled(true);
						MyFrameZadanie.ogladaneWlasnieZadanie = wybraneZadanie;
						
						int wybranyIndex = lZadaniaLocal.getSelectedIndex();
						lZadaniaLocal.ensureIndexIsVisible(wybranyIndex);
						
						System.out.println("obiekt zaznaczony ma indeks: " + wybranyIndex + ", oraz brzmi " +lZadaniaLocal.getSelectedValue());
					} else {
						bWyswietlMetryczke.setEnabled(false);
					}
				}
			}
		});
	}

	private void createAndShowGUI() {
/*		setTitle("JDialog Example");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new FlowLayout());*/

		// Must be called before creating JDialog for
		// the desired effect
		JDialog.setDefaultLookAndFeelDecorated(true);

		// A perfect constructor, mostly used.
		// A dialog with current frame as parent
		// a given title, and modal
		d1 = new JDialog(this, "Dodaj z palca zadanie", true);

		// Set size
		d1.setSize(400, 400);
		d1.setLocation(100, 100);

		// Set some layout
		d1.setLayout(new FlowLayout());
		
		JButton bDodaj = new JButton("Zapisz");
		final JTextField nazwa = new JTextField(20);
		final JTextField data = new JTextField(20);
		final JTextField start = new JTextField(20);
		final JTextField end = new JTextField(20);

	
		d1.add(new JLabel("Opis zadania"));
		d1.add(nazwa);
		d1.add(new JLabel("Data zadania (YYYY-MM-DD)"));
		d1.add(data);
		d1.add(new JLabel("Start (hh:mm:ss)"));
		d1.add(start);
		d1.add(new JLabel("Stop (hh:mm:ss)"));
		d1.add(end);
		d1.add(bDodaj);
		
		bDodaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {
		
				try {
					Zadanie nowe = new Zadanie(data.getText(), nazwa.getText(), start.getText(), end.getText());
					wybranaData = data.getText();
					//d1.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					MyFrameStart.tResultDisplay.setText(Zadanie.wyswietlPodsumowanieDnia(wybranaData));
					ModelZadan.wyswietlListeZadan(wybranaData);
					d1.dispose();
				} catch (ParseException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		//Zadanie nowe = new Zadanie("2015-07-23", "Abak", "10:00:00", "12:00:00");

/*		setSize(700, 650);
		setLocation(50, 50);*/
/*		setVisible(true);*/

		// Like JFrame, JDialog isn't visible, you'll
		// have to make it visible
		// Remember to show JDialog after its parent is
		// shown so that its parent is visible
		d1.setVisible(true);
	}
	
	
	public static void ustawPrzyciskiPoStarcie() {
		bStart.setEnabled(false);
		fOpisZadania.setEnabled(true);
		bStop.setEnabled(true);
		bPauza.setEnabled(true);
		bStopStart.setEnabled(true);
	}
	
	
	// umieśc elementy w screenie
	private void ustawOknoNaStart() {
		

		String dzisiejszaData = Przelicznik.formatyyyyMMdd.format(new Date());
		
		fOpisZadania.setEnabled(false);
		bStop.setEnabled(false);	
		bStopStart.setEnabled(false);
		bPauza.setEnabled(false);
		
		fDataIleWykonanychJuz.setEditable(false);
		fDataIleWykonanychJuz.setText("Dziś mamy: " + dzisiejszaData + " Wykonanych dziś zadań: " + Zadanie.getIleZadanDoneToday(Zadanie.dzisiejszaData));
		
		tPrzebiegZadaniaDisplayArea.setEditable(false);
		tPrzebiegZadaniaDisplayArea.setText("Dane programu będą przechowywane tu: "+Program.ekstensjaPath +".\nW tym oknie będziesz widzieć logi przebiegu zadania");
		binPath.setText("Dane programu są tu: "+Program.ekstensjaPath);

		bWyswietlMetryczke.setEnabled(false);

		tResultDisplay.setEditable(false);
		//tResultDisplay.setText(Zadanie.wyswietlZadaniaDzisiejsze()); // gdy brak zadań w systemie, wyświetla instrukcję
		tResultDisplay.setText(Zadanie.wyswietlPodsumowanieDnia(Zadanie.dzisiejszaData));
		
		//fInfoTxt.setText("by Zuzanna Malińska, " + Program.wersjaProgramu);
		
		tdateField.setText(dzisiejszaData); // aby na starcie wyswietlac
		
		
		//add(tPrzebiegZadaniaDisplayArea);

		//add(lZadaniaLocal);
				
		//add(tResultDisplay);		
		//add(bWyswietlZadania);
		add(bOpenGeneratePDF);
		add(bSaveToTXT);
		add(tdateField);
		//add(bChooseDate);
		add(bCalendar);
		add(bOpenDialog);
/*		
		lZadaniaLocal.setSelectedIndex(Zadanie.getIleZadanDoneToday());  
		lZadaniaLocal.ensureIndexIsVisible(lZadaniaLocal.getSelectedIndex());*/
	}
}
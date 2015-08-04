package timing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class MyFrameZadanie extends JFrame {

	public static Zadanie ogladaneWlasnieZadanie = MyFrameStart.wybraneZadanie;
	
	//static String wybranaData = ogladaneWlasnieZadanie.dataZadania;
	static String wybranaData = MyFrameStart.tdateField.getText();
	
	
	// utwórz elementy
	final static JLabel lIlePauz = new JLabel("Ile pauz");
	final static JLabel lDataZadania = new JLabel("Data zadania");
	final static JLabel lCzasTrwaniaS1Faktyczny = new JLabel("Czas trwania z licznika (s1CzasTrwaniaFaktycznyS) - jeśli n.d., zadanie importowane");
	final static JLabel lCzasOdDoS1Total = new JLabel("Czas s1CzasTotalS (czas pauz + czas z licznika)");
	final static JLabel lLacznyCzasPauz = new JLabel("Łączny czas pauz (s1CzasPauzS) - jeśli n.d., zadanie importowane");

	final static JLabel lCzasTrwanias3CzasTotalS = new JLabel("Czas trwania ze sparsowanych dat (s3CzasTotalS)");
	final static JLabel lOpis = new JLabel("Opis");
	final static JLabel lIdZadania = new JLabel("ID zadania");
	final static JLabel lOd = new JLabel("Od");
	final static JLabel lDo = new JLabel("Do");

	final static JButton bEdytuj = new JButton("Edytuj");
	final static JButton bZapisz = new JButton("Zapisz");
	final static JButton bPowrot = new JButton("Powrót");
	final static JTextField fIdZadania = new JTextField(8);
	final static JTextField fDataZadania = new JTextField(8);
	final static JTextField fOpis = new JTextField(45);
	final static JTextField fIlePauzZadania = new JTextField(5);
	final static JTextField fCzasRozpoczecia = new JTextField(8);
	final static JTextField fCzasZakonczenia = new JTextField(8);
	final static JTextField fCzasTrwaniaS1Faktyczny = new JTextField(40);
	final static JTextField fCzasOdDoS1Total = new JTextField(50);
	final static JTextField fLacznyCzasPauz = new JTextField(40);
	final static JTextField fCzasTrwanias3CzasTotalS = new JTextField(50);

	String oldOpis = "";

	public MyFrameZadanie() {
		// skonfiguruj frame
		super("Zadanie");
/*		JDialog dlg = new JDialog(parentWindow, modality);
		super(MyFrameStart, ModalityType.APPLICATION_MODAL);*/
		setDefaultCloseOperation(MyFrameStart.DISPOSE_ON_CLOSE);
		setSize(600, 600);
		setLocation(350, 150);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setVisible(true);

		ustawPrzyciskiNaStart();

		// dodaj listenery do elementów
		bPowrot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {
				dispose();
			}
		});

		bEdytuj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {
				fOpis.setEditable(true);
				bZapisz.setEnabled(true);
			}
		});


		bZapisz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent zdarz) {
				bEdytuj.setEnabled(false);
				int ile = 0;
				System.out.println(Zadanie.opisy);
				for (Zadanie z : Zadanie.wszystkieZadania) {
					if (z.taskName.equalsIgnoreCase(oldOpis)) {
						ile++;
					}
				}
				if (ile < 1) {
					Zadanie.opisy.remove(oldOpis);
				}
				System.out.println(Zadanie.opisy);
				ogladaneWlasnieZadanie.zmienOpis(fOpis.getText());
				
				//dodane, żeby na belce też się zmieniał opis, jeśli modyfikujemy opis bieżącego zadania
				if (ogladaneWlasnieZadanie == Zadanie.aktualneZadanie) {
					MyFrameStart.fOpisZadania.setText(fOpis.getText());
				}
				
				if (!Zadanie.opisy.contains(ogladaneWlasnieZadanie.taskName)) {
					Zadanie.opisy.add(ogladaneWlasnieZadanie.taskName);
				}

				System.out.println(Zadanie.opisy);
				
				//ModelZadan.aktualizujListeZadan();
				
				
				
				//ModelZadan.wyswietlListeZadan(wybranaData);
				//MyFrameStart.tResultDisplay.setText(Zadanie.wyswietlZadaniaDzisiejsze()); // konieczne dla solve 3
				//MyFrameStart.tResultDisplay.setText(Zadanie.wyswietlPodsumowanieDnia(wybranaData));
				
				wybranaData = ogladaneWlasnieZadanie.dataZadania;
				
				System.out.println("Wybrana data to " + wybranaData);
				
				
				//MyFrameStart.lZadaniaLocal.setSelectedIndex(ogladaneWlasnieZadanie.iDZadania - 1);  
				//MyFrameStart.lZadaniaLocal.ensureIndexIsVisible(ogladaneWlasnieZadanie.iDZadania - 1);
				
				ModelZadan.wyswietlListeZadan(wybranaData);
				MyFrameStart.tResultDisplay.setText(Zadanie.wyswietlPodsumowanieDnia(wybranaData));
				
		
				try {
					Program.zapiszWszystkoDo();
				} catch (IOException e) {
					e.printStackTrace();
				}
				resetujPolaFormularza();
			}

			private void resetujPolaFormularza() {
				fOpis.setText(ogladaneWlasnieZadanie.taskName);
				fOpis.setEditable(false);
				bZapisz.setEnabled(false);
				bEdytuj.setEnabled(true);
			}

		});
	}

	// umieśc elementy w screenie
	private void ustawPrzyciskiNaStart() {
		fDataZadania.setText(ogladaneWlasnieZadanie.dataZadania);
		fIdZadania.setText(String.valueOf(ogladaneWlasnieZadanie.iDZadania));
		fOpis.setText(ogladaneWlasnieZadanie.taskName);
		fCzasRozpoczecia.setText(ogladaneWlasnieZadanie.czasRozpoczeciaS);
		fCzasZakonczenia.setText(ogladaneWlasnieZadanie.czasZakonczeniaS);
		fCzasTrwaniaS1Faktyczny.setText(ogladaneWlasnieZadanie.s1CzasTrwaniaFaktycznyS + " | " + ogladaneWlasnieZadanie.s1CzasTrwaniaFaktyczny);
		fCzasOdDoS1Total.setText(ogladaneWlasnieZadanie.s1CzasTotalS + " | " + ogladaneWlasnieZadanie.s1CzasTotal);
		fIlePauzZadania.setText(Integer.toString(ogladaneWlasnieZadanie.ilePauzZadania) ); //SOLVED 2
		fLacznyCzasPauz.setText(ogladaneWlasnieZadanie.s1CzasPauzS + " | " + ogladaneWlasnieZadanie.s1CzasPauz);
		fCzasTrwanias3CzasTotalS.setText(ogladaneWlasnieZadanie.s3CzasTotalS + " | " + ogladaneWlasnieZadanie.s3CzasTotal);

		add(lDataZadania);
		add(fDataZadania);
		add(lIdZadania);
		add(fIdZadania);
		add(lOd);
		add(fCzasRozpoczecia);
		add(lDo);
		add(fCzasZakonczenia);
		add(lOpis);
		add(fOpis);
		add(lCzasTrwaniaS1Faktyczny);
		add(fCzasTrwaniaS1Faktyczny);
		add(lLacznyCzasPauz);
		add(fLacznyCzasPauz);
		add(lIlePauz);
		add(fIlePauzZadania);
		add(lCzasOdDoS1Total);
		add(fCzasOdDoS1Total);
		add(lCzasTrwanias3CzasTotalS);
		add(fCzasTrwanias3CzasTotalS);
		add(bPowrot);
		add(bEdytuj);
		add(bZapisz);


		fIdZadania.setEditable(false);
		fDataZadania.setEditable(false);
		fCzasRozpoczecia.setEditable(false);
		fCzasZakonczenia.setEditable(false);
		fCzasTrwaniaS1Faktyczny.setEditable(false);
		fCzasOdDoS1Total.setEditable(false);
		fIlePauzZadania.setEditable(false);
		fLacznyCzasPauz.setEditable(false);
		fOpis.setEditable(false);
		bZapisz.setEnabled(false);
		fCzasTrwanias3CzasTotalS.setEditable(false);
		oldOpis = fOpis.getText();
	}
}

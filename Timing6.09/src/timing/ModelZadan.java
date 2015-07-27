package timing;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

public class ModelZadan {

	
	final static DefaultListModel listModelZadan = new DefaultListModel();
	
/*	static void aktualizujListeZadan() {
		listModelZadan.removeAllElements();
		for (Zadanie a : Zadanie.wszystkieZadania) {
			listModelZadan.addElement(a);
		}
	}*/

	static void aktualizujListeZadan() {
		listModelZadan.removeAllElements();
		for (Zadanie a : Zadanie.wszystkieZadania) {
			if (a.dataZadania.equalsIgnoreCase(Zadanie.dzisiejszaData)) {
				listModelZadan.addElement(a);
			}
			
		}
	}
	
	public static void wyswietlListeZadan(String data) {
		listModelZadan.removeAllElements();
		for (Zadanie a : Zadanie.wszystkieZadania) {
			if (a.dataZadania.equalsIgnoreCase(data)) {
				listModelZadan.addElement(a);
			}
			
		}
	}

	public static void ustawParametryWyswietlaniaListyZadan(JList lksiazki) {
		MyFrameStart.lZadaniaLocal.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		MyFrameStart.lZadaniaLocal.setLayoutOrientation(JList.VERTICAL);
		MyFrameStart.lZadaniaLocal.setVisibleRowCount(5);
		
	}
/*	
	public static void widocznoscListy() {
		
		
		//JList wordList = getWordListScroller();
		int lastIndex = listModelZadan.getSize() - 1;
				
		if (lastIndex >= 0) {
		   wordList.ensureIndexIsVisible(lastIndex);
		}
		
	}*/
	
	public static void zaznaczZadanieNaLiscie(Zadanie nowyEgzemplarz) {
		int indexWanted = 0;
		for (Zadanie a : Zadanie.wszystkieZadania) {
			if (a == nowyEgzemplarz) {
				Object odpowiednik = a;
				indexWanted = ModelZadan.listModelZadan.indexOf(odpowiednik);
			}
		}
		MyFrameStart.lZadaniaLocal.setSelectedIndex(indexWanted);
		//System.out.println("Indeks nowo dodanego zadania na liście to: "+indexWanted);
	}

}

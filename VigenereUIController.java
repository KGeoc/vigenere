package vigenere;

import decrypts.wordFolders;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class VigenereUIController {

	private VigenereUI mainApp;

	private VigenereCipher vigenere;

	@FXML
	private TextArea givenText;
	@FXML
	private TextArea OutputText;
	@FXML
	private Button ActionBTN;
	@FXML
	private TextField keyLength;
	@FXML
	private TextField ioc;

	private int key;

	@FXML
	private void storeText(){
		vigenere.readAndStore(givenText.getText());

	}
	
	
	
	
	@FXML
	private void decrypt() {
		
	
		
		vigenere.checkOccurences(7);
		vigenere.removeSpaces();
		vigenere.Store();

		findKeyLength();
		vigenere.setKeyLength(key);
		vigenere.showDistances(3);
		vigenere.filter();
		getIOC();

		vigenere.indivOccurence(key);
////////this needs to be fixed?
//		OutputText.setText(vigenere.preRecursive().toString());
		
	/*	personData=FXCollections.observableArrayList(vigenere.preRecursive());
		
		passCode.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
        passText.setCellValueFactory(cellData -> cellData.getValue().decryptedCodeProperty());
*/
	}
	
    private ObservableList<wordFolders> personData = FXCollections.observableArrayList();
	@FXML
	private void findKeyLength() {
		vigenere.checkOccurences(7);
		vigenere.removeSpaces();
		vigenere.Store();
		key=vigenere.kasiski(7);
		keyLength.setText(String.valueOf(key));
	}

	@FXML
	private void getIOC() {
		ioc.setText(String.valueOf(vigenere.findIndexOfCoincidence()));
	}

	@FXML
    private TableView<wordFolders> Table;
    @FXML
    private TableColumn<wordFolders, String> passCode;
    @FXML
    private TableColumn<wordFolders, String> passText;
	
	
	@FXML
	private void initialize() {
		// Initialize the person table with the two columns.
	//	givenText.appendText("sadfsdaf");
		vigenere = new VigenereCipher();
		vigenere.implementTrie("words.txt");
		vigenere.implementTrie("names.txt");
	}
	public ObservableList<wordFolders> getPersonData() {
        return personData;
    }
	public void setMainApp(VigenereUI mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table

	}

}

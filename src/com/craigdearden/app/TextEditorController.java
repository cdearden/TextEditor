/*
 * Concepts:
 * try-with-resources
 */
package com.craigdearden.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Stack;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.util.Pair;

/**
 *
 * @author C1
 */
public class TextEditorController implements Initializable {

    //String fileName = 
    private static File curDoc = null;
    private static boolean unsavedChanges = false;
    private Stack<Change> changes = new Stack<>();
    private String clipBoard = null;

    @FXML
    private static TextArea textArea;
    @FXML
    private CheckMenuItem wordWrap;

    @FXML
    private void menu_file_new_CLICK(ActionEvent event) {

        if (unsavedChanges == true) {
            Optional<ButtonType> response = prompt("Save",
                    "Would you like to save your changes?");

            if (response.equals(ButtonType.YES)) {
                if (curDoc != null) {
                    save();
                } else {
                    saveAs();
                }
            } else if (response.equals(ButtonType.NO)) {
                textArea.clear();
                curDoc = null;
            }
        } else {
            textArea.clear();
            curDoc = null;
        }
    }

    @FXML
    private void menu_file_open_CLICK(ActionEvent event) {
        FileChooser fc = new FileChooser();
        curDoc = fc.showOpenDialog(null);
        curDoc.load();
    }

    @FXML
    private void menu_file_save_CLICK(ActionEvent event) {
        save();
    }

    @FXML
    private void menu_file_saveAs_CLICK(ActionEvent event) {
        saveAs();
    }

    @FXML
    private void menu_file_exit_CLICK(ActionEvent event) {
        exit();
    }

    @FXML
    private void menu_edit_undo_CLICK(ActionEvent event) {
        Change change = changes.pop();
        if (change.getAction() == Change.Action.ADD) {
            textArea.deleteText(change.getPosition(), change.getData().length());
        } else if (change.getAction() == Change.Action.REMOVE) {
            textArea.insertText(change.getPosition(), change.getData());
        }
        //Add further functionality for undo's
    }

    @FXML
    private void menu_edit_cut_CLICK(ActionEvent event) {
        clipBoard = textArea.getSelectedText();
        textArea.deleteText(textArea.getSelection());
    }

    @FXML
    private void menu_edit_copy_CLICK(ActionEvent event) {
        clipBoard = textArea.getSelectedText();
    }

    @FXML
    private void menu_edit_paste_CLICK(ActionEvent event) {
        int caretPos = textArea.caretPositionProperty().get();
        textArea.insertText(caretPos, clipBoard);
    }

    @FXML
    private void menu_edit_delete_CLICK(ActionEvent event) {
        textArea.deleteText(textArea.getSelection());
    }

    @FXML
    private void menu_edit_find_CLICK(ActionEvent event) {
        Optional<ButtonType> result = null;

        // Need to fix this code
        Dialog<Pair<String, String>> find = new Dialog<>(ButtonType.);
        find.setTitle("Find");
        ButtonType findNext = new ButtonType();
        find.showAndWait();
        
        
    }

    @FXML
    private void menu_edit_findnext_CLICK(ActionEvent event) {
        alert("Alert!", "Not yet implemented!");
    }

    @FXML
    private void menu_edit_replace_CLICK(ActionEvent event) {
        alert("Alert!", "Not yet implemented!");
    }

    @FXML
    private void menu_edit_goto_CLICK(ActionEvent event) {
        alert("Alert!", "Not yet implemented!");
    }

    @FXML
    private void menu_edit_selectall_CLICK(ActionEvent event) {
        alert("Alert!", "Not yet implemented!");
    }

    @FXML
    private void menu_edit_datetimestamp_CLICK(ActionEvent event) {
        alert("Alert!", "Not yet implemented!");
    }

    @FXML
    private void menu_format_wordwrap_CLICK(ActionEvent event) {
        if (textArea.wrapTextProperty().get() == false) {
            textArea.setWrapText(true);
            wordWrap.setSelected(true);
        } else {
            textArea.setWrapText(false);
            wordWrap.setSelected(false);
        }
    }

    @FXML
    private void menu_format_font_CLICK(ActionEvent event) {
        alert("Alert!", "Not yet implemented!");
    }

    @FXML
    private void menu_format_highlight_CLICK(ActionEvent event) {
        alert("Alert!", "Not yet implemented!");
    }

    @FXML
    private void menu_help_about_CLICK(ActionEvent event) {
        alert("Alert!", "Not yet implemented!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(
                    final ObservableValue<? extends String> observable,
                    final String oldValue, final String newValue) {
                unsavedChanges = true;

            }
        });

        textArea.setWrapText(true);
    }

    private Change difference(String oldContent, String newContent) {
        Change.Action action = null;
        String data = null;
        int position = 0;

        //Need code to obtain change 
        Change change = new Change(action, data, position);

        return change;
    }

    private static void save() throws FileNotFoundException, IOException {
        String text = textArea.getText();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(curDoc))) {
            bw.write(text);
        }
    }

    private static void saveAs() {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(curDoc);
        curDoc = fc.showSaveDialog(null);
        save();
    }

    private void load() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(curDoc))) {
            while (br.ready()) {
                textArea.appendText(br.readLine());
            }
        }
    }

    private void savePrompt() {

    }

    public static boolean getUnsavedChanges() {
        return unsavedChanges;
    }

    public static void exit() {
        if (unsavedChanges == true) {
            Optional<ButtonType> response = prompt("Save",
                    "Would you like to save your changes before exiting?");

            if (response.equals(ButtonType.YES)) {
                if (curDoc != null) {
                    save();
                } else {
                    saveAs();
                }
            } else if (response.equals(ButtonType.NO)) {
                Platform.exit();
            }
        } else {
            Platform.exit();
        }
    }

    private static void alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static Optional<ButtonType> prompt(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO,
                ButtonType.CANCEL);
        return alert.showAndWait();
    }

}

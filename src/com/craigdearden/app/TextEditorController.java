/*
 * Concepts:
 * try-with-resources
 */
package com.craigdearden.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author C1
 */
public class TextEditorController implements Initializable {

    private File curDoc = null;
    private static boolean unsavedChanges = false;
    private Stack<Change> changes = new Stack<>();
    private String clipBoard = null;

    @FXML
    private TextArea textArea;
    @FXML
    private CheckMenuItem wordWrap;

    @FXML
    private void menu_file_new_CLICK(ActionEvent event) {
        ButtonType response = null;
        if (unsavedChanges == true) {
            response = promptToSave();
        }

        if (response != null && !response.equals(ButtonType.CANCEL)) {
            textArea.clear();
            curDoc = null;
        }
    }

    @FXML
    private void menu_file_open_CLICK(ActionEvent event) {
        if (unsavedChanges == true) {
            promptToSave();
        }

        FileChooser fc = new FileChooser();
        curDoc = fc.showOpenDialog(null);
        if (curDoc != null) {
            load();
        }
    }

    @FXML
    private void menu_file_save_CLICK(ActionEvent event) {
        if (unsavedChanges == true && curDoc == null) {
            saveAs();
        } else if (unsavedChanges == true && curDoc != null) {
            save();
        }
        unsavedChanges = false;
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
        TextInputDialog find = new TextInputDialog();
        find.getDialogPane().getButtonTypes().addAll(ButtonType.PREVIOUS,
                ButtonType.NEXT, ButtonType.CANCEL);
        find.getDialogPane().lookupButton(ButtonType.NEXT).setDisable(
                true);
        find.getDialogPane().lookupButton(ButtonType.PREVIOUS).setDisable(
                true);
        find.show();
    }

    @FXML
    private void menu_edit_replace_CLICK(ActionEvent event) {
        alert("Alert!", "Not yet implemented!");
    }

    @FXML
    private void menu_edit_selectall_CLICK(ActionEvent event) {
        textArea.selectAll();
    }

    @FXML
    private void menu_edit_datetimestamp_CLICK(ActionEvent event) {
        int caretPosition = textArea.caretPositionProperty().get();
        SimpleDateFormat sdate = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss aaa");
        Date date = new Date();
        textArea.insertText(caretPosition, sdate.format(date));
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

        // Need to work on this code
        textArea.setStyle("-fx-font-size: 30px;" +
                "-fx-font-style: italic;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: fantasy;" +
                "-fx-text-fill: blue;" +
                "-fx-background-color: aqua");

        alert("Alert!", "Not yet implemented!");
    }

    @FXML
    private void menu_help_about_CLICK(ActionEvent event) {
        alert("About", "Text Editor V1.0");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(
                    final ObservableValue<? extends String> observable,
                    final String oldValue, final String newValue) {
                unsavedChanges = true;
                logDifference(oldValue, newValue);
                System.out.println(" Text Changed from  " + oldValue +
                        ".........\n");
                System.out.println(" Text Changed to  " + newValue +
                        ",,,,,,,,,,\n");
            }
        });

        textArea.setWrapText(true);
    }

    private Change logDifference(String oldContent, String newContent) {
        Change.Action action = null;
        String data = null;
        int position = 0;

        //Need code to obtain change 
        Change change = new Change(action, data, position);

        return change;
    }

    private ButtonType promptToSave() {
        Optional<ButtonType> response = prompt("Save",
                "Would you like to save your changes?");

        if (response.get().equals(ButtonType.YES) && curDoc != null) {
            save();
            unsavedChanges = false;
        } else if (response.get().equals(ButtonType.YES) && curDoc == null) {
            saveAs();
            unsavedChanges = false;
        } else if (response.get().equals(ButtonType.NO)) {
            unsavedChanges = false;
        }

        return response.get();
    }

    private void save() {
        String text = textArea.getText();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(curDoc))) {
            bw.write(text);
        } catch (IOException ex) {
            alert("Alert!", "Unable to save file.");
        }
    }

    private void saveAs() {
        FileChooser fc = new FileChooser();
        curDoc = fc.showSaveDialog(null);
        if (curDoc != null) {
            save();
        }
    }

    private void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(curDoc))) {
            while (br.ready()) {
                textArea.appendText(br.readLine() + '\n');
            }
        } catch (IOException ex) {
            alert("Alert!", "Unable to load file.");
        }
    }

    public static boolean getUnsavedChanges() {
        return unsavedChanges;
    }

    public void exit() {
        ButtonType response = null;
        
        if (unsavedChanges == true)
            response = promptToSave();
        
        if(response != null && response.equals(ButtonType.CANCEL))
            Platform.exit();
    }

    private void sendEmail(String to, String from, String host, String subject,
            String body) {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);

        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                    to));
            message.setSubject(subject);

            BodyPart newBodyPart = new MimeBodyPart();
            newBodyPart.setText(body);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(newBodyPart);

            newBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(curDoc);
            newBodyPart.setDataHandler(new DataHandler(source));
            newBodyPart.setFileName(curDoc.getName());
            multipart.addBodyPart(newBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException ex) {
            alert("Alert!", "Error. Message not sent!");
        }
    }

    private void alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> prompt(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO,
                ButtonType.CANCEL);
        return alert.showAndWait();
    }
}

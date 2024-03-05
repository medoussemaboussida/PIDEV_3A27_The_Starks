/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo4;

import com.example.demo4.entities.UserDetails;
import com.example.demo4.entities.evenement;
import com.example.demo4.entities.participant;

import java.io.File;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import com.example.demo4.services.evenementService;
import com.example.demo4.services.participantService;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author asus
 */
public class evenementController implements Initializable {

    int idabonn;
    @FXML
    private Label nomabonnLabel;
    @FXML
    private Label typeabonnLabel;
    @FXML
    private Label descriptionabonnLabel;
    @FXML
    private Label dateabonnLabel;
    @FXML
    private Button participerabonnButton;
    @FXML
    private Label nb_participantsLabel;
    
    UserDetails u=new UserDetails();
    participantService Ps=new participantService();
    @FXML
    private TextField idabonnF;
    @FXML
    private TextField iduserF;
    
    evenementService Ev=new evenementService();
    @FXML
    private ImageView imageview;
    @FXML
    private Label participantComplet;
    @FXML
    private TextField idPartField;
    @FXML
    private Button annulerButton;
    @FXML
    private Button likeButton;
     @FXML
    private Button deslikeButton;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        idabonnF.setVisible(false);
        deslikeButton.setVisible(false);
        likeButton.setVisible(true);
                iduserF.setVisible(false);
                participantComplet.setVisible(false);
                annulerButton.setVisible(false);
        likeButton.setOnAction(event -> {
    likeButton.setStyle("-fx-background-color: green;");
    deslikeButton.setVisible(false);
    PauseTransition delay = new PauseTransition(Duration.seconds(5));
});
deslikeButton.setVisible(true);
likeButton.setVisible(true);
deslikeButton.setOnAction(event -> {
    PauseTransition delay = new PauseTransition(Duration.seconds(5));
    delay.setOnFinished(abonn -> {
        likeButton.setStyle("-fx-background-color: green;");
        
    });
    delay.play();
    deslikeButton.setStyle("-fx-background-color: red;");
    likeButton.setVisible(false);
});



                

    }    
    private evenement eve=new evenement();
    
    public void setevenement(evenement e) {
        this.eve=e;
        nomabonnLabel.setText(e.getNom_ev());
        typeabonnLabel.setText(e.getType_ev());
        descriptionabonnLabel.setText(e.getDescription_ev());
        dateabonnLabel.setText(String.valueOf(e.getDate()));
        nb_participantsLabel.setText(String.valueOf(e.getCode_participant()));
        idabonnF.setText(String.valueOf(e.getId_ev()));
        iduserF.setText(String.valueOf(1));
         String path = e.getImage_ev();
         File file=new File(path);
         Image img = new Image(file.toURI().toString());
         imageview.setImage(img);

    }
    public void setIdabonn(int idabonn){
        this.idabonn=idabonn;
    }


    @FXML
    private void participerabonn(MouseEvent abonn) throws SQLException {

        LocalDate dateActuelle = LocalDate.now();
        Date dateSQL = Date.valueOf(dateActuelle);
        participant p=new participant(dateSQL,Integer.parseInt(iduserF.getText()),Integer.parseInt(idabonnF.getText()));
        
        Ps.ajouterparticipant(p);

        idPartField.setText(String.valueOf(27));
        annulerButton.setVisible(true);
       
        
        participerabonnButton.setVisible(false);
        
 
        }
    
    public void arreterabonn()
    {
        participerabonnButton.setVisible(false);
        participantComplet.setVisible(true);
    }

    @FXML
    private void annulerparticipant(ActionEvent abonn) throws SQLException {
        participant p=new participant();
        p.setId_participant(Integer.parseInt(idPartField.getText()));
        Ps.Deleteparticipant(p);
        participerabonnButton.setVisible(true);
        annulerButton.setVisible(false);
        
    }
    
    
}

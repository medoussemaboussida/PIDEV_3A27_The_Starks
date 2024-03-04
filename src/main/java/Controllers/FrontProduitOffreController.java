package Controllers;
import entities.*;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import Service.*;

import java.awt.*;
import java.awt.ScrollPane;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda;

public class FrontProduitOffreController implements Initializable {
    private final ProduitService ps = new ProduitService();
    String filepath = null, filename = null, fn = null;
    String uploads = "C:/xampp/htdocs/";
    FileChooser fc = new FileChooser();
    ObservableList<Produit> list = FXCollections.observableArrayList();
    public int idProduit;
    PanierService pns = new PanierService();

    public int getIdProduit() {
        return getIdProduit();
    }

    public void setIdProduit(int id) {
        this.idProduit = id;
    }

    private ObservableList<Produit> produitList;

    private ObservableList<Produit> originalProduitList;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Categorie> ComboProduitC;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchField.setOnAction(event -> search()); // Appelle la méthode search() lorsque "Entrée" est pressé
        // Appelle la méthode search() chaque fois que le texte change

        produitList = FXCollections.observableArrayList();

        int idUtilisateur = 1; // Remplacez cela par l'ID réel de l'utilisateur connecté
        UtilisateurService utilisateurService = new UtilisateurService();
        Utilisateur utilisateur = utilisateurService.getUserById(idUtilisateur);
        // Vérifiez si l'utilisateur est récupéré avec succès
        if (utilisateur != null) {
            // Afficher une alerte de bienvenue avec le nom de l'utilisateur
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bienvenue");
            alert.setHeaderText("Bienvenue " + utilisateur.getNomUser());
            alert.setContentText("Vous êtes connecté avec succès!\n"
                    + "Email: " + utilisateur.getEmailUser() + "\n"
                    + "Numéro de téléphone: " + utilisateur.getNumTel());
            alert.showAndWait();
        } else {
            // Gérer le cas où l'utilisateur n'a pas été trouvé
            System.out.println("Utilisateur non trouvé");
        }




        setCombo();
        ComboProduitC.setOnAction(this::filtrerProduit);
        // showProduitFront();
        showProduitFrontp();


    }


    public void setCombo() {
        CategorieService tabC = new CategorieService();
        List<Categorie> tabList = tabC.readCategorie();
        ArrayList<Categorie> cats = new ArrayList<>();
        for (Categorie c : tabList) {
            Categorie cat = new Categorie();
            cat.setIdCategorie(c.getIdCategorie());
            cat.setNomCategorie(c.getNomCategorie());
            cats.add(cat);
        }

        ObservableList<Categorie> choices = FXCollections.observableArrayList(cats);
        ComboProduitC.setItems(choices);

        ComboProduitC.setConverter(new StringConverter<Categorie>() {
            @Override
            public String toString(Categorie categorie) {
                if (categorie == null) {
                    return null;
                } else {
                    return categorie.getNomCategorie();
                }
            }

            @Override
            public Categorie fromString(String string) {
                // Vous pouvez implémenter cette méthode si nécessaire
                return null;
            }
        });
    }

    private List<Produit> temp;
    private List<PanierProduit> temp1;

    @FXML
    public void filtrerProduit(ActionEvent actionEvent) {
        Categorie selectedCategorie = ComboProduitC.getValue();
        int categorieId = selectedCategorie.getIdCategorie();
        temp = ps.readProduitByCategorie(categorieId);
        ObservableList<Produit> updatedList = FXCollections.observableArrayList(temp);

        // Clear the existing content in ListView
        listView.getItems().clear();

        // Add each product to ListView as GridPane
        for (int i = 0; i < updatedList.size(); i += 4) {
            GridPane productGridPane = createProductGridPane(
                    (i < updatedList.size()) ? updatedList.get(i) : null,
                    (i + 1 < updatedList.size()) ? updatedList.get(i + 1) : null,
                    (i + 2 < updatedList.size()) ? updatedList.get(i + 2) : null,
                    (i + 3 < updatedList.size()) ? updatedList.get(i + 3) : null
            );
            listView.getItems().add(productGridPane);
        }
    }

    @FXML
    private ImageView PanierImage;

    public void checkPanier(MouseEvent mouseEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FrontPanierCommande.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Votre Panier");
        stage.setScene(new Scene(root1));
        Node source = (Node) mouseEvent.getSource();
        stage.show();
    }



    @FXML
    private ListView<GridPane> listView;


    public void showProduitFrontp() {
        // Clear the existing content in ListView
        listView.getItems().clear();

        // Fetch the list of products with offers from your service
        OffreProduitService offreProduitService = new OffreProduitService();
        ObservableList<Produit> produitsOffre = offreProduitService.getAllProduitsOffre();

        originalProduitList = FXCollections.observableArrayList(produitsOffre);

        // Add each product to ListView
        for (int i = 0; i < produitsOffre.size(); i += 4) {
            GridPane gridPane = createProductGridPane(
                    produitsOffre.get(i),
                    (i + 1 < produitsOffre.size()) ? produitsOffre.get(i + 1) : null,
                    (i + 2 < produitsOffre.size()) ? produitsOffre.get(i + 2) : null,
                    (i + 3 < produitsOffre.size()) ? produitsOffre.get(i + 3) : null
            );
            listView.getItems().add(gridPane);
            //css
            gridPane.getStyleClass().add("grid-pane-product");
        }
    }

    private GridPane createProductGridPane(Produit produit1, Produit produit2, Produit produit3, Produit produit4) {
        GridPane gridPane = new GridPane();

        // Create and set up UI components for each product
        VBox vbox1 = createProductBox(produit1);
        VBox vbox2 = (produit2 != null) ? createProductBox(produit2) : new VBox(); // Empty VBox if no second product
        VBox vbox3 = (produit3 != null) ? createProductBox(produit3) : new VBox(); // Empty VBox if no third product
        VBox vbox4 = (produit4 != null) ? createProductBox(produit4) : new VBox(); // Empty VBox if no fourth product

        // Add components to GridPane
        gridPane.add(vbox1, 0, 0);
        gridPane.add(vbox2, 1, 0);
        gridPane.add(vbox3, 2, 0);
        gridPane.add(vbox4, 3, 0);

        // Set horizontal gap between columns
        gridPane.setHgap(10);

        return gridPane;
    }
    private VBox createProductBox(Produit produit) {
        VBox vbox = new VBox();
        float prix = produit.getPrix();
        float prixReduction = (prix * (100 - ps.getReduction(produit.getIdProduit()))) / 100;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.000"); // Format avec trois chiffres après la virgule
        String prixFormate = decimalFormat.format(prix);
        String prixReductionFormate = decimalFormat.format(prixReduction);

        // Create and set up UI components for each product
        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
        loadAndSetImage(imageView, produit.getImageProduit());

        Label nameLabel = new Label(produit.getNomProduit());
        Label priceLabel = new Label("Prix: " + prixFormate);
        //Label quantityLabel = new Label("Quantité en stock: " + produit.getQuantite());
        Label reductionLabel = new Label("Réduction: " + ps.getReduction(produit.getIdProduit()) + "%");
        Label priceReductionLabel = new Label("Prix aprés reduction: " + prixReductionFormate);

        Button addButton = new Button("+");
        addButton.getStyleClass().add("addbuttonPanier");
        nameLabel.getStyleClass().add("product-label");
        priceLabel.getStyleClass().add("product-label");
        reductionLabel.getStyleClass().add("product-label");
        priceReductionLabel.getStyleClass().add("product-label");
        // Add components to VBox
        vbox.getChildren().addAll(imageView, nameLabel, priceLabel, priceReductionLabel, addButton);

        // Set spacing and alignment as needed
        vbox.setSpacing(11);
        vbox.setAlignment(Pos.CENTER);


        addButton.setOnAction(event -> {
            int idUtilisateur = 1; // Remplacez cela par l'ID réel de l'utilisateur connecté
            UtilisateurService utilisateurService = new UtilisateurService();
            Utilisateur utilisateur = utilisateurService.getUserById(idUtilisateur);
            Panier panier = pns.selectPanierParUserId(utilisateur.getIdUser());
            Panier panierExistant = pns.selectPanierParUserId(utilisateur.getIdUser());

            if (panierExistant != null) {
                PanierProduitService panierProduitService = new PanierProduitService();
                panierProduitService.ajouterProduitAuPanier(panier, produit.getIdProduit());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avertissement");
                alert.setHeaderText(null);
                alert.setContentText("Produit ajouté dans votre panier .");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avertissement");
                alert.setHeaderText(null);
                alert.setContentText("Vous n'avez pas de panier. Veuillez créer un panier d'abord.");
                alert.showAndWait();
                pns.ajouterPanier(utilisateur.getIdUser());
            }
            // Rafraîchir la vue du produit
            showProduitFrontp();
        });



        return vbox;
    }
    private void loadAndSetImage(ImageView imageView, String imagePath) {
        // Charger et afficher l'image en utilisant le chemin complet du fichier
        File imageFile = new File(uploads + imagePath);
        if (imageFile.exists()) {
            Image image = new Image("file:///" + imageFile.getAbsolutePath());
            imageView.setImage(image);
            imageView.setFitWidth(190);
            imageView.setFitHeight(190);
        } else {
            // Gérer le cas où le fichier d'image n'existe pas
            System.out.println("Le fichier d'image n'existe pas : " + imagePath);
            imageView.setImage(null);
        }
    }
    @FXML
    void search() {
        String keyword = searchField.getText().trim().toLowerCase();
        List<Produit> filteredList = originalProduitList.stream()
                .filter(produit -> produit.getNomProduit().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        // Mettre à jour la liste des produits affichés dans la vue
        listView.getItems().clear();
        for (int i = 0; i < filteredList.size(); i += 4) {
            GridPane gridPane = createProductGridPane(
                    filteredList.get(i),
                    (i + 1 < filteredList.size()) ? filteredList.get(i + 1) : null,
                    (i + 2 < filteredList.size()) ? filteredList.get(i + 2) : null,
                    (i + 3 < filteredList.size()) ? filteredList.get(i + 3) : null
            );
            listView.getItems().add(gridPane);
            //css
            gridPane.getStyleClass().add("grid-pane-product");
        }
    }


    @FXML
    void showCallender(ActionEvent event) {
        Agenda agenda = new Agenda();

        loadPromosIntoAgenda(agenda);

        Stage calendarStage = new Stage();
        calendarStage.setScene(new Scene(agenda, 800, 600));
        calendarStage.setTitle("Calendrier des Offres Pour Cette Semaines");
        calendarStage.show();
    }


    OffreService offreService = new OffreService();

    private void loadPromosIntoAgenda(Agenda agenda) {
        // Fetch offres from your data source
        List<Offre> offres = offreService.getAllOffres();

        // Add offres to the agenda
        for (Offre offre : offres) {

            // Convert java.sql.Date to java.util.Date
            Date dateDebut = new Date(offre.getDateDebut().getTime());
            Date dateFin = new Date(offre.getDateFin().getTime());

            // Convertir les dates en LocalDateTime avec l'heure fixée à minuit
            LocalDateTime startDateTime = dateDebut.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
            LocalDateTime endDateTime = dateFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();

            // Créer un rendez-vous avec la date de début et de fin
            Agenda.Appointment appointment = new Agenda.AppointmentImplLocal()
                    .withStartLocalDateTime(startDateTime)
                    .withEndLocalDateTime(endDateTime)
                    .withSummary(offre.getNomOffre());

            // Ajouter le rendez-vous à l'agenda
            agenda.appointments().add(appointment);
        }
    }



}







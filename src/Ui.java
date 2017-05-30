import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.Stack;


public class Ui extends Application {
    private final static File CAISSE_FILE = new File("ressources/caisse.gif");
    private final static File CAISSEOK_FILE = new File("ressources/caisseOK.gif");
    private final static File MUR_FILE = new File("ressources/mur.gif");
    private final static File PERSO_FILE = new File("ressources/perso.gif");
    private final static File PERSO_ZONE_FILE = new File("ressources/persoZone.gif");
    private final static File VIDE_FILE = new File("ressources/vide.gif");
    private final static File ZONE_FILE = new File("ressources/zone.gif");
    private static Stack<Plateau> stack;
    ObservableList<CaseUi> caseUis;

    @Override
    public void start(Stage primaryStage) {
        GridPane board = new GridPane();

        // notifie seulement si on change le type de case
        caseUis = FXCollections.observableArrayList(
                (CaseUi caseui) -> new Observable[] {caseui.idPropertyProperty()} );

        caseUis.addListener((ListChangeListener<CaseUi>) change -> {
            while (change.next()) {
                if (change.wasUpdated()) {
                    for (CaseUi slot : change.getList()) {
                        ImageView iv = (ImageView) board.getChildren().get(19 * slot.getX() + slot.getX());
                        try{
                            iv.setImage(slot.getImageProperty());
                        }
                        catch (RuntimeException e){
                        }
                    }
                }
            }
        });

        // construction du plateau
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 19; col++) {
                CaseUi caseUi = new CaseUi(0, row, col);
                caseUis.add(caseUi);
                board.add(caseUi.image, col, row);
            }
        }

        // initialise la partie à l'état 0
        if(!stack.empty()) update(stack.pop());

        // touche entrée pour avancer d'un pas
        Scene scene = new Scene(board);
        scene.setOnKeyPressed(event -> {if(event.getCode() == KeyCode.ENTER && !stack.empty()) update(stack.pop());});

        scene.getStylesheets().add("sokoban.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void update(Plateau p){
        int cpt = 0;
        // check si il y a eu une modif sur une case
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 19; col++) {
                int caseId = p.getCaseStatut(row,col);
                if(caseUis.get(cpt).getIdProperty() != caseId)
                        caseUis.get(cpt).setIdProperty(caseId);
                cpt++;
            }
        }
    }

    public void showSolution(Stack<Plateau> stack) {
        this.stack = stack;
        launch();
    }

    public Image getImage(File f){
        return new Image(f.toURI().toString());
    }

    public ImageView getImageView(File f){
        return new ImageView(getImage(f));
    }


    // classe interne pour la gestion des case du plateau dans l'ui
    private class CaseUi{
        // objets observable
        private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
        private final IntegerProperty idProperty = new SimpleIntegerProperty();
        // coordonées case
        private int x, y;

        public int getIdProperty() {
            return idProperty.get();
        }

        public IntegerProperty idPropertyProperty() {
            return idProperty;
        }

        public void setIdProperty(int idProperty) {
            this.idProperty.set(idProperty);
            switch (idProperty){
                case Case.PERSO : setImageProperty(new Image(PERSO_FILE.toString())); break;
                case Case.CASE_VIDE : setImageProperty(new Image(VIDE_FILE.toString())); break;
                case Case.MUR : setImageProperty(new Image(MUR_FILE.toString())); break;
                case Case.CAISSE_SUR_ZONE : setImageProperty(new Image(CAISSEOK_FILE.toString())); break;
                case Case.CAISSE : setImageProperty(new Image(CAISSE_FILE.toString())); break;
                case Case.ZONE_RANGEMENT : setImageProperty(new Image(ZONE_FILE.toString())); break;
                case Case.PERSO_SUR_ZONE: setImageProperty(new Image(PERSO_ZONE_FILE.toString())); break;
                default: setImageProperty(new Image(VIDE_FILE.toString())); break;
            }
        }

        public ObjectProperty<Image> imageObjectProperty() {
            return imageProperty ;
        }

        public Image getImageProperty() {
            return imageProperty.get();
        }

        public void setImageProperty(Image imageProperty) {
            this.imageProperty.set(imageProperty);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        private final ImageView image = new ImageView();

        public CaseUi(int id, int x, int y) {
            this.x = x;
            this.y = y;
            image.imageProperty().bind(imageProperty);
            setIdProperty(id);
        }

        public Node getCaseUiContents() {
            return image ;
        }
    }
}
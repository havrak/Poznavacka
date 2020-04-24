package poznavacka;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
 *
 * @author havra, litos
 */
public class Poznavacka extends Application {

   @Override
   public void start(Stage stage) throws Exception {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
      Parent root = loader.load();
      Scene scene = new Scene(root);
      ((FXMLDocumentController) loader.getController()).setUp(stage);
      stage.setTitle("Poznavacka");
      stage.setScene(scene);
      stage.show();
      stage.setMinHeight(630);
      stage.setMinWidth(650);
      stage.setResizable(true);
   }

   public static void main(String[] args) {
      launch(args);
   }

}

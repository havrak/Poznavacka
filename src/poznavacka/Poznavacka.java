package poznavacka;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This program is free software. It comes without any warranty, to the extent
 * permitted by applicable law. You can redistribute it and/or modify it under
 * the terms of the Do What The Fuck You Want To Public License, Version 2, as
 * published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
 *
 * @author havra\
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

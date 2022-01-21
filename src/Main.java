import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    VBox root = new VBox();
    Image image;
    Steganography s = new Steganography();
    String text;

    public void start(Stage stage) {

        stage.setTitle("Steganography");

        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open image");
        MenuItem closeItem = new MenuItem("Close image");
        MenuItem openTxt = new MenuItem("Open new txt");
        MenuItem saveImage = new MenuItem("Save image");
        saveImage.setDisable(true);
        closeItem.setDisable(true);
        fileMenu.getItems().addAll(openItem, closeItem, openTxt, saveImage);

        Menu tools = new Menu("Tools");
        MenuItem hide = new MenuItem("Hide message");
        MenuItem extract = new MenuItem("Extract message");
        tools.getItems().addAll(hide, extract);
        hide.setDisable(true);
        extract.setDisable(true);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, tools);
        root.getChildren().add(menuBar);

        ImageView imgView = new ImageView(image);
        imgView.setFitHeight(300);
        imgView.setFitWidth(300);
        root.getChildren().add(imgView);

        openItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle (ActionEvent t){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Image File");
                fileChooser.setInitialDirectory(new File ("C:\\Users\\Ben Harrison\\Pictures"));
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    loadImageFile(file);
                    imgView.setImage(image);
                    openItem.setDisable(true);
                    closeItem.setDisable(false);
                    hide.setDisable(false);
                    extract.setDisable(false);
                    saveImage.setDisable(false);
                }
            }
        });

        closeItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle (ActionEvent t){
                // root.getChildren().remove(indexOfImageView);
                imgView.setImage(null);
                openItem.setDisable(false);
                closeItem.setDisable(true);
                hide.setDisable(true);
                extract.setDisable(true);
                saveImage.setDisable(true);
            }
        });

        saveImage.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle (ActionEvent t){
                try {
                    // retrieve image
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Image");
                    File file = fileChooser.showSaveDialog(stage);
                    File outputfile = new File("output.png");
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null),  "png", file);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });

        openTxt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle (ActionEvent t){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File("/Ben/Uni/Java/myJavaProjects/Year2/TeamProject/Steganography"));
                //fileChooser.setSelectedExtensionFilter();
                fileChooser.setTitle("Open txt file");
                File file = fileChooser.showOpenDialog(stage);
                Scanner myReader = null;
                try {
                    myReader = new Scanner(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String string = "";
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    string = string + data + "\n";
                    //System.out.println(data);
                }
                myReader.close();
                string = string.substring(0, string.length() - 1);
                System.out.println("Reading message");
                System.out.println(string);
                System.out.println("End message\n");
                text = string;
            }
        });

        hide.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle (ActionEvent t){
                s.setImage(image);
                s.encodeText(text);
                image = s.applySteganography();
                imgView.setImage(image);
            }
        });

        extract.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                s.extract(image);
            }
        });

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    private void loadImageFile(File file) {
        image = new Image("file:" + file.getPath());
    }

    public static void main(String[] args) {
        launch(args);
    }



}

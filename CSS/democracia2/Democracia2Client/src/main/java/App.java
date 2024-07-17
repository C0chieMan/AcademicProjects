package main.java;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
 
public class App extends Application {
    
    public static void main(String[] args){
        launch(args);
    }
    
    public void start(Stage stage) {
        stage.setTitle("Democracia2");
        stage.setScene(new Scene(new Browser("http://localhost:8080/"), 750, 500));     
        stage.show();
    }
    
    private class Browser extends Region {
         
    	private WebView browser;
         
        public Browser(String url) {
        	browser = new WebView();
        	browser.getEngine().load(url);
            getStyleClass().add("browser");
            getChildren().add(browser);
     
        }
     
        @Override
        protected void layoutChildren() {
            layoutInArea(browser, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
        }
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationproject;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author ksyus
 */

public class ApplicationProject extends Application {

    private ArrayList<User> users = new ArrayList<>(); 
    private ObservableList<Company> companies = FXCollections.observableArrayList();
    
    @Override
    public void start(Stage primaryStage) {
        Label nameLabel = new Label("Имя:");
        Label passwordLabel = new Label("Пароль:");

        TextField nameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button registerButton = new Button("Войти");
        
        //проверяем введенные данные после нажатия на кнопку
        registerButton.setOnAction(event -> {
            String name = nameField.getText().trim();
            String password = passwordField.getText();

            if (name.isEmpty() || password.isEmpty()) {
                System.out.println("Не все поля заполнены!");
                return;
            }
            
            boolean foundUser = false;
            User user = null;
            for (User u : users) {
                if (u.getName().equals(name)) {
                    foundUser = true;
                    System.out.println("_____________________________________________");
                    if (u.getPassword().equals(password)){
                        user = u;
                        System.out.println("Успешный вход в систему: " + user.getName());
                        break;
                    }
                    else{
                        System.out.println("Пароль не верный: " + u.getName());
                        return;
                    }
                }
            }
            if (foundUser == false){            
                user = new User(name, password);
                users.add(user);
                System.out.println("_____________________________________________");
                System.out.println("Добавлен пользователь: " + user.getName());
                System.out.println("Всего пользователей: " + users.size());
            }
            new MainWindow(user, companies).show();

        });

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(registerButton, 1, 2);

        Scene scene = new Scene(grid, 320, 200);

        // Подключаем CSS-файл
        scene.getStylesheets().add(getClass().getResource("registration-style.css").toExternalForm());
        
        primaryStage.setTitle("Вход в систему");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

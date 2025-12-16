/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationproject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author ksyus
 */

public class MainWindow {
    private final Stage stage;
    private final TableView<Company> table = new TableView<>();
    private final User user;
    private ObservableList<Company> companies;
    private ObservableList<Company> filtredCompanies = FXCollections.observableArrayList();
    private static final ObservableList<String> ACTIVITY_OPTIONS = FXCollections.observableArrayList(
        "Финансовые услуги",
        "IT и разработка",
        "Маркетинг и реклама",
        "Образование",
        "Производство"
    );
    private String lastFilterDate;
    private String lastFilterType = "Любой";
    private static final String PHONE_REGEX = 
        "^\\+?7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}$|^8\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}$"; 
    private static final String EMAIL_REGEX = 
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
    private static final String WEBSITE_REGEX = 
        "^(https?://)?(www\\.)?[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+(/.*)?$";

    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern WEBSITE_PATTERN = Pattern.compile(WEBSITE_REGEX);

    public MainWindow(User user, ObservableList<Company> companies) {
        this.stage = new Stage();
        this.user = user;
        this.companies = companies;
        initializeUI();
    }

    public void show() {
        stage.show();
    }

   private void initializeUI() {
        stage.setTitle("Управление компаниями");
        stage.setWidth(1100);
        stage.setHeight(700);

        setupTable();
        table.setItems(companies);
        BorderPane root = new BorderPane();
        root.setLeft(createLeftPanel());
        root.setCenter(table);

        Scene scene = new Scene(root);
        // Подключаем CSS, если есть
        scene.getStylesheets().add(getClass().getResource("registration-style.css").toExternalForm());
        stage.setScene(scene);
    }

    private void setupTable() {
        // Колонки
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Company, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Company, String> phoneCol = new TableColumn<>("Телефон");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Company, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Company, String> websiteCol = new TableColumn<>("Сайт");
        websiteCol.setCellValueFactory(new PropertyValueFactory<>("website"));

        TableColumn<Company, String> activityCol = new TableColumn<>("Вид деятельности");
        activityCol.setCellValueFactory(new PropertyValueFactory<>("activityType"));

        TableColumn<Company, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Company, String> respCol = new TableColumn<>("Ответственный");
        respCol.setCellValueFactory(new PropertyValueFactory<>("responsible"));

        table.getColumns().addAll(nameCol, phoneCol, emailCol, websiteCol, activityCol, dateCol, respCol);
        
        table.setRowFactory(tv -> {
            TableRow<Company> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    Company company = row.getItem();
                    showEditCompanyDialog(company, stage, false);
                }
            });
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null && newItem.isInDeal()) {
                row.setStyle("-fx-background-color: #f58b40;");
            } else {
                row.setStyle("");
            }
        });
            return row;
        });
        
    }

    private VBox createLeftPanel() {
        double buttonWidth = 180;

        Button btnCreate = new Button("Создать компанию");
        btnCreate.setOnAction(e -> showEditCompanyDialog(new Company("", "", "", "", "", null, user.getName()), stage, true));
        btnCreate.setPrefWidth(buttonWidth);

        Button btnFilter = new Button("Отфильтровать");
        btnFilter.setOnAction(e -> showFilterDialog());
        btnFilter.setPrefWidth(buttonWidth);


        Button btnExit = new Button("Выход");
        btnExit.setOnAction(e -> {stage.close();});
        btnExit.setPrefWidth(buttonWidth);

        Button btnDeal = new Button("Сделки");
        btnDeal.setOnAction(e -> {
            new DealsWindow(companies, table).show();
            } 
        );
        btnDeal.setPrefWidth(buttonWidth);
        
        VBox panel = new VBox(15, new Label("Добрый день, " + user.getName() + "!"), btnCreate, btnFilter, btnDeal, btnExit);
        panel.setPadding(new Insets(20));
        return panel;
    }
    
    private void showEditCompanyDialog(Company company, Stage owner, boolean isNew) {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle(isNew ? "Новая компания" : "Редактирование компании");
    dialog.setHeaderText("Данные компании");
    dialog.initOwner(owner);

    // Поля ввода
    TextField nameField = new TextField(company.getName());
    TextField phoneField = new TextField(company.getPhone());
    TextField emailField = new TextField(company.getEmail());
    TextField websiteField = new TextField(company.getWebsite());

    ComboBox<String> activityField = new ComboBox<>(ACTIVITY_OPTIONS);
    activityField.setPromptText("Выберите вид деятельности");
    if (company.getActivityType() != null) {
        activityField.setValue(company.getActivityType());
    }

    LocalDate initialDate = null;
    if (isNew && company.getDate() == null) {
        initialDate = LocalDate.now();
    } 
    DatePicker datePicker = new DatePicker(initialDate);


    // Компоновка
    GridPane grid = new GridPane();
    grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
    grid.addRow(0, new Label("Название:"), nameField);
    grid.addRow(1, new Label("Телефон:"), phoneField);
    grid.addRow(2, new Label("Email:"), emailField);
    grid.addRow(3, new Label("Сайт:"), websiteField);
    grid.addRow(4, new Label("Вид деятельности:"), activityField);
    grid.addRow(5, new Label("Дата:"), datePicker);

    dialog.getDialogPane().setContent(grid);
    if (isNew != true){
        dialog.getDialogPane().getButtonTypes().addAll(
        new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE),
        new ButtonType("Удалить", ButtonBar.ButtonData.OTHER),
        new ButtonType("Новая сделка", ButtonBar.ButtonData.NEXT_FORWARD),
        ButtonType.CANCEL
    );
    }
    else{
        dialog.getDialogPane().getButtonTypes().addAll(
        new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE),
        ButtonType.CANCEL
    );
    }
    
    dialog.setResultConverter(btn -> {
        if (btn.getButtonData() == ButtonBar.ButtonData.OTHER) {
            // Удаление
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Вы уверены, что хотите удалить компанию?\nЭто действие нельзя отменить.",
                ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Подтверждение удаления");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES){
                companies.remove(company);
                filtredCompanies.remove(company);
            }
            return ButtonType.CANCEL; 

        } else if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            // Валидация
            String comp_name = nameField.getText().trim();
            if (comp_name.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Название компании обязательно.").showAndWait();
                return null;
            }

            if (!validation_check(comp_name, phoneField.getText(), emailField.getText(), websiteField.getText(), datePicker.getValue(), isNew)){
                new Alert(Alert.AlertType.WARNING, "Данные введены некоректно.").showAndWait();
                return null;
            }
            // Обновление данных
            company.setName(comp_name);
            company.setPhone(phoneField.getText());
            company.setEmail(emailField.getText());
            company.setWebsite(websiteField.getText());
            company.setActivityType(activityField.getValue());
            company.setDate(datePicker.getValue());
            company.setResp(user.getName());
            

            // Добавление, если новая
            if (isNew) {
                companies.add(company);
            }
            else{
                table.refresh(); 
            }
            
            return ButtonType.OK;
        }
        else if (btn.getButtonData() == ButtonBar.ButtonData.NEXT_FORWARD){
            if (!company.isInDeal())
                company.setDealStatus(DealStatus.NEW);
            else{
                new Alert(Alert.AlertType.WARNING, "Компания уже задействована в работе.").showAndWait();
                return null;
            }
            table.refresh(); 
        }
        return ButtonType.CANCEL;
    });
    
    dialog.showAndWait();
}
    
private void showFilterDialog() {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Фильтрация");
    dialog.setHeaderText(null);

    // Поля ввода
    DatePicker datePicker = new DatePicker();
    if (lastFilterDate != null) {
        try {
            datePicker.setValue(LocalDate.parse(lastFilterDate, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } catch (Exception e) {
        }
    }

    ComboBox<String> typeCombo = new ComboBox<>(ACTIVITY_OPTIONS);
    typeCombo.setValue(lastFilterType);

    // Компоновка
    GridPane grid = new GridPane();
    grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
    grid.addRow(0, new Label("Дата:"), datePicker);
    grid.addRow(1, new Label("Вид деятельности:"), typeCombo);
    dialog.getDialogPane().setContent(grid);

    // Кнопки
    dialog.getDialogPane().getButtonTypes().addAll(
        new ButtonType("Применить", ButtonBar.ButtonData.OK_DONE),
        new ButtonType("Сбросить", ButtonBar.ButtonData.OTHER),
        ButtonType.CANCEL
    );

    // Обработка результата
    dialog.setResultConverter(btn -> {
        if (btn == null || btn == ButtonType.CANCEL) {
            return ButtonType.CANCEL;
        }

        if (btn.getButtonData() == ButtonBar.ButtonData.OTHER) {
            // Сброс
            lastFilterDate = null;
            lastFilterType = "Любой";
        } else if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            // Применить
            LocalDate selectedDate = datePicker.getValue();
            lastFilterDate = (selectedDate != null)
                ? selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : null;
            lastFilterType = typeCombo.getValue();
        }

        // Фильтрация
        applyFilter();
        return ButtonType.OK;
    });

    dialog.showAndWait();
}

// Отдельный метод для фильтрации
private void applyFilter() {
    filtredCompanies.clear();
    for (Company c : companies) {
        boolean dateOk = true;
        if (lastFilterDate != null && c.getDate() != null) {
            String companyDateStr = c.getFormattedDate(); // возвращает dd.MM.yyyy
            dateOk = companyDateStr.equals(lastFilterDate);
        } else if (lastFilterDate != null) {
            dateOk = false; // у компании нет даты, но фильтр задан
        }

        boolean typeOk = "Любой".equals(lastFilterType) ||
            (c.getActivityType() != null && c.getActivityType().equals(lastFilterType));

        if (dateOk && typeOk) {
            filtredCompanies.add(c);
        }
    }
    table.setItems(filtredCompanies);
}
    
boolean validation_check(String name, String phone, String email, String website, LocalDate date, boolean isNew){
    if (isNew){
        for (Company c : companies) 
            if (name.equalsIgnoreCase(c.getName()))
                return false;
    }
    if (!(phone == null) && !(phone.trim().isEmpty()) && !PHONE_PATTERN.matcher(phone.trim()).matches())
        return false;
    if (!(email == null) && !(email.trim().isEmpty()) && !EMAIL_PATTERN.matcher(email.trim()).matches())
        return false;  
    if (!(website == null) && !(website.trim().isEmpty()) && !WEBSITE_PATTERN.matcher(website.trim()).matches())
        return false;
    return true;
    
}

}
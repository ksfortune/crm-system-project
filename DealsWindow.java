/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationproject;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;

/**
 *
 * @author ksyus
 */
public class DealsWindow {

    private final Stage stage;
    private final ObservableList<Company> companies;
    private final ListView<Company> newColumn;
    private final ListView<Company> negotiationColumn;
    private final ListView<Company> fulfillmentColumn;
    private final ListView<Company> completedColumn;
    private final TableView<Company> table;


    public DealsWindow(ObservableList<Company> companies, TableView<Company> table) {
        this.companies = companies;
        this.stage = new Stage();
        this.newColumn = createColumn();
        this.negotiationColumn = createColumn();
        this.fulfillmentColumn = createColumn();
        this.completedColumn = createColumn();
        this.table = table;

        initialize();
        loadCompanies();
    }

    private ListView<Company> createColumn() {
        ListView<Company> list = new ListView<>();
        list.setPrefWidth(250);
        list.setPrefHeight(500);
        list.setCellFactory(lv -> new CompanyListCell());
        setupDragAndDrop(list);
        return list;
    }
    

    private void setupDragAndDrop(ListView<Company> targetList) {
        targetList.setOnDragOver(event -> {
            if (event.getGestureSource() != targetList &&
                event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
            table.refresh();

        });
        
        targetList.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String companyId = db.getString();
                Company draggedCompany = findCompanyById(companyId);
                if (draggedCompany != null) {
                    // Удаляем из всех списков
                    removeFromAllColumns(draggedCompany);
                    // Добавляем в целевой список
                    targetList.getItems().add(draggedCompany);
                    // Обновляем статус
                    DealStatus newStatus = getStatusByColumn(targetList);
                    draggedCompany.setDealStatus(newStatus);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
            table.refresh();

        });
    }

    private Company findCompanyById(String id) {
        return companies.stream()
            .filter(c -> c.getName().equals(id))
            .findFirst()
            .orElse(null);
    }

    private void removeFromAllColumns(Company company) {
        newColumn.getItems().remove(company);
        negotiationColumn.getItems().remove(company);
        fulfillmentColumn.getItems().remove(company);
        completedColumn.getItems().remove(company);
    }

    private DealStatus getStatusByColumn(ListView<Company> column) {
        if (column == newColumn) return DealStatus.NEW;
        if (column == negotiationColumn) return DealStatus.NEGOTIATION;
        if (column == fulfillmentColumn) return DealStatus.FULFILLMENT;
        return DealStatus.COMPLETED;
    }

    private void loadCompanies() {
        for (Company c : companies) {
            if (c.getDealStatus() == DealStatus.NEW) {
                newColumn.getItems().add(c);
            } else if (c.getDealStatus() == DealStatus.NEGOTIATION) {
                negotiationColumn.getItems().add(c);
            } else if (c.getDealStatus() == DealStatus.FULFILLMENT) {
                fulfillmentColumn.getItems().add(c);
            } else if (c.getDealStatus() == DealStatus.COMPLETED) {
                completedColumn.getItems().add(c);
            }
        }
    }

    private void initialize() {
        stage.setTitle("Сделки");
        stage.setWidth(1100);
        stage.setHeight(700);

        HBox columns = new HBox(10);
        columns.setPadding(new Insets(20));
        columns.setAlignment(Pos.TOP_LEFT);

        columns.getChildren().addAll(
            createColumnBox("Новый", newColumn),
            createColumnBox("Ведутся переговоры", negotiationColumn),
            createColumnBox("Выполняются обязательства", fulfillmentColumn),
            createColumnBox("Сделка завершена", completedColumn)
        );
        

        Scene scene = new Scene(columns);
        stage.setScene(scene);
    }

    private VBox createColumnBox(String title, ListView<Company> list) {
        Label header = new Label(title);
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10;");
        header.setAlignment(Pos.CENTER);

        VBox box = new VBox(5, header, list);
        box.setStyle("-fx-border-color: #ccc; -fx-border-width: 1px; -fx-padding: 10;");
        return box;
    }

    public void show() {
        stage.show();
    }

    // Ячейка для отображения компании
    private class CompanyListCell extends javafx.scene.control.ListCell<Company> {
        @Override
        protected void updateItem(Company company, boolean empty) {
            super.updateItem(company, empty);
            if (empty || company == null) {
                setText(null);
            } else {
                setText(company.getName());
                setOnDragDetected(event -> {
                    Dragboard db = startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(company.getName()); // уникальный идентификатор
                    db.setContent(content);
                    
                    event.consume();
                });
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && company != null) {
                        openCompanyInfoWindow(company); // ← вызов метода
                    }
                });
            }
        }
    }
    
    private void openCompanyInfoWindow(Company company) {
    // Создаём и показываем окно информации
    Stage infoStage = new Stage();
    infoStage.setTitle("Информация о компании: " + company.getName());
    infoStage.setWidth(500);
    infoStage.setHeight(450);

    // Панель с информацией
    GridPane infoGrid = new GridPane();
    infoGrid.setHgap(10);
    infoGrid.setVgap(8);
    infoGrid.setPadding(new Insets(15));
    infoGrid.add(new Label("Название:"), 0, 0);
    infoGrid.add(new Label(company.getName()), 1, 0);
    infoGrid.add(new Label("Телефон:"), 0, 1);
    infoGrid.add(new Label(company.getPhone() != null ? company.getPhone() : ""), 1, 1);
    infoGrid.add(new Label("Email:"), 0, 2);
    infoGrid.add(new Label(company.getEmail() != null ? company.getEmail() : ""), 1, 2);
    infoGrid.add(new Label("Сайт:"), 0, 3);
    infoGrid.add(new Label(company.getWebsite() != null ? company.getWebsite() : ""), 1, 3);
    infoGrid.add(new Label("Вид деятельности:"), 0, 4);
    infoGrid.add(new Label(company.getActivityType() != null ? company.getActivityType() : ""), 1, 4);
    infoGrid.add(new Label("Статус сделки:"), 0, 5);
    infoGrid.add(new Label(company.getDealStatus().getDisplayName()), 1, 5);
    infoGrid.add(new Label("Дата добавления:"), 0, 6);
    infoGrid.add(new Label(company.getFormattedDate()), 1, 6);

    // Поле для комментария
    TextArea commentArea = new TextArea(company.getComment());
    commentArea.setPrefRowCount(5);
    commentArea.setPromptText("Введите комментарий к сделке...");

    Button btnSave = new Button("Сохранить комментарий");
    btnSave.setOnAction(e -> {
        company.setComment(commentArea.getText());
        infoStage.close();
    });

    VBox root = new VBox(15, infoGrid, new Label("Комментарий:"), commentArea, btnSave);
    root.setPadding(new Insets(15));

    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("registration-style.css").toExternalForm());
    infoStage.setScene(scene);
    infoStage.showAndWait(); // модальное окно
}
}

package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.UITableColumnVO;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by fansen on 2017/6/16.
 */
public class SelectSequenceController extends BaseFXController {
    @FXML
    private TableView<UITableColumnVO> columnListView;
    @FXML
    private TableColumn<UITableColumnVO, String> columnNameColumn;


    private MainUIController mainUIController;

    private String tableName;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnNameColumn.setCellValueFactory(new PropertyValueFactory<>("columnName"));

        columnListView.setRowFactory(new Callback<TableView<UITableColumnVO>, TableRow<UITableColumnVO>>() {
            @Override
            public TableRow<UITableColumnVO> call(TableView<UITableColumnVO> param) {
                return new TableRowControl();
            }
        });


    }

    class TableRowControl extends TableRow<UITableColumnVO> {

        public TableRowControl() {
            super();
            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton().equals(MouseButton.PRIMARY)
                            && event.getClickCount() == 2
                            && TableRowControl.this.getIndex() < columnListView.getItems().size()) {
                        ok();
                    }
                }
            });
        }
    }

    @FXML
    public void ok(){
        UITableColumnVO focusedItem = columnListView.getFocusModel().getFocusedItem();
        if(null!=focusedItem){
            mainUIController.setSequenceText(focusedItem.getColumnName());
        }
        getDialogStage().close();
    }
    @FXML
    public void cancel(){
        getDialogStage().close();
    }

    public void setColumnList(ObservableList<UITableColumnVO> columns) {
        columnListView.setItems(columns);
    }

    public void setMainUIController(MainUIController mainUIController) {
        this.mainUIController = mainUIController;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}

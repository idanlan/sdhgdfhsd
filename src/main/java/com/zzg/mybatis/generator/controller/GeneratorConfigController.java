package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.model.UITableColumnVO;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.view.AlertUtil;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 管理GeneratorConfig的Controller
 *
 * Created by Owen on 8/21/16.
 */
public class GeneratorConfigController extends BaseFXController {

    private static final Logger _LOG = LoggerFactory.getLogger(GeneratorConfigController.class);

    @FXML
    private TableView<GeneratorConfig> configTable;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn opsColumn;

    private MainUIController mainUIController;

    private GeneratorConfigController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = this;
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        // 自定义操作列
        opsColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        opsColumn.setCellFactory(cell -> {
            return new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button btn1 = new Button("应用");
                        Button btn2 = new Button("删除");
                        HBox hBox = new HBox();
                        hBox.setSpacing(10);
                        hBox.getChildren().add(btn1);
                        hBox.getChildren().add(btn2);
                        btn1.setOnAction(event -> {
                            try {
                                // 应用配置
                                GeneratorConfig generatorConfig = ConfigHelper.loadGeneratorConfig(item.toString());
                                mainUIController.setGeneratorConfigIntoUI(generatorConfig);
                                controller.closeDialogStage();
                            } catch (Exception e) {
                                AlertUtil.showErrorAlert(e.getMessage());
                            }
                        });
                        btn2.setOnAction(event -> {
                            try {
                                // 删除配置
                                _LOG.debug("item: {}", item);
                                ConfigHelper.deleteGeneratorConfig(item.toString());
                                refreshTableView();
                            } catch (Exception e) {
                                AlertUtil.showErrorAlert(e.getMessage());
                            }
                        });
                        setGraphic(hBox);
                    }
                }
            };
        });
        refreshTableView();

        configTable.setRowFactory(new Callback<TableView<GeneratorConfig>, TableRow<GeneratorConfig>>() {
            @Override
            public TableRow<GeneratorConfig> call(TableView<GeneratorConfig> param) {
                return new GeneratorConfigController.TableRowControl();
            }
        });
    }

    class TableRowControl extends TableRow<GeneratorConfig> {

        public TableRowControl() {
            super();
            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton().equals(MouseButton.PRIMARY)
                            && event.getClickCount() == 2
                            && GeneratorConfigController.TableRowControl.this.getIndex() < configTable.getItems().size()) {
                        try {
                            GeneratorConfig focusedItem = configTable.getFocusModel().getFocusedItem();
                            if(null!=focusedItem){
                                GeneratorConfig generatorConfig = ConfigHelper.loadGeneratorConfig(focusedItem.getName());
                                mainUIController.setGeneratorConfigIntoUI(generatorConfig);
                                controller.closeDialogStage();
                            }
                        }catch (Exception e){
                            AlertUtil.showErrorAlert(e.getMessage());
                        }
                    }
                }
            });
        }
    }
    public void refreshTableView() {
        try {
            List<GeneratorConfig> configs = ConfigHelper.loadGeneratorConfigs();
            configTable.setItems(FXCollections.observableList(configs));
        } catch (Exception e) {
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    void setMainUIController(MainUIController mainUIController) {
        this.mainUIController = mainUIController;
    }

}

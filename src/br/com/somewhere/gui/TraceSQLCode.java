/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.somewhere.gui;

import br.com.somewhere.core.ResultBean;
import br.com.somewhere.core.SearchEngine;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author someone
 */
public class TraceSQLCode extends Application {
    private RadioButton btnRadioMT = new RadioButton();
    private RadioButton btnRadioRR = new RadioButton();
    private RadioButton btnRadioCBA = new RadioButton();
    private ToggleGroup radioGroup = new ToggleGroup();
    private Button btn = new Button();
    TextField textProcurar = new TextField();
    private TableView table;
    
    private Map<ENGINE, SearchEngine> engines = new HashMap<ENGINE, SearchEngine>();
    
    public enum ENGINE {
        MT("dir.mt.view"), RR("dir.rr.view"), CBA("dir.cba.view");
        String directory;
        
        ENGINE(String directory){
            this.directory = directory;
        }
        
        public String toString(){
            return this.directory;
        }
    }    
    
    private Properties properties = new Properties();
    
    private ENGINE getEngineSelected(){
        if(btnRadioMT.isSelected()){
            return ENGINE.MT;
        } else if(btnRadioRR.isSelected()){
            return ENGINE.RR;
        } else
            return ENGINE.CBA;
    }
    
    public TraceSQLCode(){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("trace.properties");
            this.properties.load(fis);
        } catch(Exception e){
        } finally {
            if(fis != null){
                try {fis.close();} catch(Exception e){}
            }
        }
    }
    
    private SearchEngine getEngine(ENGINE engine){
        if(!engines.containsKey(engine)){
            String directory = properties.getProperty(getEngineSelected().toString());
            engines.put(engine, new SearchEngine(new File(directory), new String[]{".sql",".map",".java"}));
        }
        return engines.get(engine);
    }
    
    protected TableView prepareTable(){
        TableView table = new TableView();        
        TableColumn columnLinha = new TableColumn("Linha");
        columnLinha.setCellValueFactory(new PropertyValueFactory<ResultBean,String>("fileNameLine"));
        columnLinha.setMinWidth(224);
        TableColumn columnSnippet = new TableColumn("Código");        
        columnSnippet.setCellValueFactory(new PropertyValueFactory<ResultBean,String>("snippet"));
        columnSnippet.setMinWidth(800);                
        table.getColumns().addAll(columnLinha,columnSnippet);
        return table;
    }
    
    protected ObservableList<ResultBean> prepareTableData(List<ResultBean> lista){
        ObservableList<ResultBean> resultado = FXCollections.observableArrayList();        
        for(ResultBean bean : lista){
            resultado.add(bean);
        }
        return resultado;
    }
    
    @Override
    public void start(Stage primaryStage) {
        final ObservableList<ResultBean> data = FXCollections.observableArrayList();        
        table = prepareTable();        
        table.setItems(data);
        
        Label labelProcura = new Label("Procurar:");       
        
        btn.setText("Buscar");
        
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        
        btn.setOnAction(new EventHandler<ActionEvent>() {            
            public void handle(ActionEvent event) {      
                data.clear();
                FXCollections.emptyObservableList();
                if(textProcurar.getText() == null || textProcurar.getText().isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("É necessário informar o texto de procura.");                    
                    alert.showAndWait();
                    return;
                }                
                List<ResultBean> resultado = getEngine(getEngineSelected()).search(textProcurar.getText());
                table.setItems(prepareTableData(resultado));
            }            
        });
        
        StackPane root = new StackPane();        
        root.getChildren().add(gridpane);
        //Linha 1
        gridpane.add(labelProcura, 0, 0);
        gridpane.add(textProcurar, 1, 0, 3, 1);
        gridpane.add(btn, 4, 0);            
        
        //Linha 2        
        btnRadioMT.setText("MT");
        btnRadioMT.setToggleGroup(radioGroup);
        btnRadioMT.setSelected(true);

        btnRadioRR.setText("RR");
        btnRadioRR.setToggleGroup(radioGroup);
        
        btnRadioCBA.setText("CBA");
        btnRadioCBA.setToggleGroup(radioGroup);
        
        gridpane.add(btnRadioMT , 0,1);
        gridpane.add(btnRadioRR , 1,1);
        gridpane.add(btnRadioCBA, 2,1);
        
        //Linha 3
        gridpane.add(table, 0, 2, 5, 1);        
        
        Scene scene = new Scene(root, 1024, 400);
        
        primaryStage.setTitle("Trace SQL Code");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

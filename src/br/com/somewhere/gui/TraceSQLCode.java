/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.somewhere.gui;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import br.com.somewhere.core.ResultBean;
import br.com.somewhere.core.SearchEngine;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author someone
 */
public class TraceSQLCode extends Application {
	private static final int WINDOW_WIDTH=800;
	private static final int WINDOW_HEIGHT=600;
    private RadioButton btnRadioMT = new RadioButton();
    private RadioButton btnRadioRR = new RadioButton();
    private RadioButton btnRadioCBA = new RadioButton();
    private ToggleGroup radioGroup = new ToggleGroup();
    private ToggleGroup groupConta = new ToggleGroup();
    
    private CheckBox checkVO = new CheckBox("VO");    
    private CheckBox checkIntegracoes = new CheckBox("Integrações (WS)");
    private CheckBox checkContabilidade = new CheckBox("Contabilidade");
    private CheckBox checkContratos = new CheckBox("Contratos");
    private CheckBox checkDocumentos = new CheckBox("Documentos");
    private CheckBox checkGestaoAp = new CheckBox("Gestão Aplicativos");
    private CheckBox checkPlanejamento = new CheckBox("Planejamento");
    private CheckBox checkPPA = new CheckBox("PPA");
    private CheckBox checkPTA = new CheckBox("PTA");
    private CheckBox checkProgramacao = new CheckBox("Prog. Financeira");
    private CheckBox checkPTAGerencial = new CheckBox("PTA Gerencial");
    private CheckBox checkTabelas = new CheckBox("Tabelas");
    private CheckBox checkRelatorios = new CheckBox("Relatorios");
    private CheckBox checkRelatoriosLOA = new CheckBox("Relatórios LOA");
    
    private RadioButton btnRadioSim = new RadioButton("Sim");
    private RadioButton btnRadioNao = new RadioButton("Não");
    
    private Scene scene = null; 
    
    private Button btn = new Button();
    private TextArea textProcurar = new TextArea();
    private Properties properties = new Properties();
    private SearchEngine engine = new SearchEngine();
    
    private ObservableList<ResultBean> data = FXCollections.observableArrayList();
    private TableView<ResultBean> table;
    
    public enum ENGINE {
        MT("dir.mt.view","dir.mt.java","dir.mt.pesquisa.sql.config"), 
        RR("dir.rr.view","dir.rr.java","dir.rr.pesquisa.sql.config"), 
        CBA("dir.cba.view","dir.cba.java","dir.cba.pesquisa.sql.config");
    	
        String directory;
        String java;
        String pesquisaSQLConfig;
        
        ENGINE(String directory, String java, String pesquisaSQLConfig){
            this.directory = directory;
            this.java = java;
            this.pesquisaSQLConfig = pesquisaSQLConfig;
        }
        
        public String toString(){
            return this.directory;
        }
    }    
    
    private String getViewDirectory(){
        if(btnRadioMT.isSelected()){
            return properties.getProperty(ENGINE.MT.directory);
        } else if(btnRadioRR.isSelected()){
        	return properties.getProperty(ENGINE.RR.directory);
        } else
        	return properties.getProperty(ENGINE.CBA.directory);
    }
    
    private String getJavaDirectory(){
        if(btnRadioMT.isSelected()){
            return properties.getProperty(ENGINE.MT.java);
        } else if(btnRadioRR.isSelected()){
        	return properties.getProperty(ENGINE.RR.java);
        } else
        	return properties.getProperty(ENGINE.CBA.java);
    }
    
    private String getPesquisaSQLConfigDirectory() {
    	if(btnRadioMT.isSelected()){
            return properties.getProperty(ENGINE.MT.pesquisaSQLConfig);
        } else if(btnRadioRR.isSelected()){
        	return properties.getProperty(ENGINE.RR.pesquisaSQLConfig);
        } else
        	return properties.getProperty(ENGINE.CBA.pesquisaSQLConfig);
    }
    
    public TraceSQLCode(){
        FileInputStream fis = null;
        File jarPath=new File(TraceSQLCode.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    	String propertiesPath=jarPath.getParentFile().getAbsolutePath();
        try {
            fis = new FileInputStream(propertiesPath + "/trace.properties");
            this.properties.load(fis);
        } catch(Exception e){
        } finally {
            if(fis != null){
                try {fis.close();} catch(Exception e){}
            }
        }
    }
    
	private class HTMLCell extends TableCell<ResultBean, String> {		
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (!empty) {
				WebView webView = new WebView();				
				webView.setMaxWidth(800);
				webView.setMaxHeight(30);
				WebEngine engine = webView.getEngine();
				setGraphic(webView);				
				engine.loadContent(item);
			}
		}
	}
    
    @SuppressWarnings("unchecked")
	protected TableView<ResultBean> prepareTable(){
        TableView<ResultBean> table = new TableView<ResultBean>();        
        TableColumn<ResultBean, String> columnLinha = new TableColumn<ResultBean,String>("Linha");
        columnLinha.setCellValueFactory(new PropertyValueFactory<ResultBean,String>("fileNameLine"));
        columnLinha.setMinWidth(224);
        
        TableColumn<ResultBean, String> columnSnippet = new TableColumn<ResultBean,String>("Código");        
        columnSnippet.setCellValueFactory(new PropertyValueFactory<ResultBean,String>("snippet"));
//        columnSnippet.setCellFactory(new Callback<TableColumn<ResultBean, String>, TableCell<ResultBean, String>>() {
//        	@Override
//        	public TableCell<ResultBean, String> call(TableColumn<ResultBean, String> param) {
//        		return new HTMLCell();
//        	}
//        });
//        columnSnippet.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ResultBean, String>, ObservableValue<String>>() {
//
//        	@Override
//        	public ObservableValue<String> call(CellDataFeatures<ResultBean, String> param) {
//        		return new SimpleObjectProperty<String>(param.getValue().getSnippet());
//        	}
//        });
	    
        columnSnippet.setMinWidth(800);        
        
        table.getColumns().addAll(columnLinha, columnSnippet);
        return table;
    }
    
    protected ObservableList<ResultBean> prepareTableData(Collection<ResultBean> lista){
        ObservableList<ResultBean> resultadoObservableList = FXCollections.observableArrayList();
        List<ResultBean> listaOrdenada = new ArrayList<ResultBean>(lista);
        Collections.sort(listaOrdenada);
        for(ResultBean bean : listaOrdenada){
            resultadoObservableList.add(bean);
        }
        return resultadoObservableList;
    }
    
    protected String getTextToSearch() {
    	if(btnRadioSim.isSelected()) {
    		String[] contas = textProcurar.getText().split(",");    		
    		StringBuilder contasRegex = new StringBuilder();
    		for(int i=0; i<contas.length; i++) {    			
    			contasRegex.append(".*" + contas[i].trim().replace(".", "\\.") + ".*");    				
    			if(i+1 < contas.length) {
    				contasRegex.append("|");
    			}
    		}
    		return contasRegex.toString();
    	} else 
    		return textProcurar.getText().trim().toLowerCase();
    }
    
    private void initComponents() {
    	   	checkVO.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if(checkVO.isSelected()) {
					if(getJavaDirectory() == null || getJavaDirectory().isEmpty()) {
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Erro");
						alert.setHeaderText("Erro na parametrização do programa");
						alert.setContentText("Não foi configurado o caminho para os fontes Java para o projeto.");
						alert.showAndWait();
						checkVO.setSelected(false);
						disableAllChecks(true);
						return;
					}					
					disableAllChecks(false);
				} else {
					disableAllChecks(true);
				}
			}
        });
    	
	   	groupConta.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if(btnRadioSim.isSelected()) {
					setPromptTextProcurar("6.2.1.3.2.01.01.00,6.2.1.3.1,4.2.1");
  				} else {
  					setPromptTextProcurar("%iden_contrato%");
  				}
			}
	   	});
    	
    	btn.setOnAction(new EventHandler<ActionEvent>() {            
            public void handle(ActionEvent event) {
            	if(checkVO.isSelected()) {            		
            		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            		alert.setTitle("Alerta");
            		alert.setHeaderText("");
            		alert.setContentText("Ao escanear os arquivos Java a consulta poderá demorar alguns minutos. Deseja continuar ?");
            		Optional<ButtonType> result = alert.showAndWait();
            		if(!result.isPresent() || result.get() != ButtonType.OK) {
            			return;
            		}
            	} 
            	
            	if(btnRadioSim.isSelected() && !Pattern.matches(".*d*\\.d.*", textProcurar.getText().toLowerCase())) {    			
        			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        			alert.setTitle("Alerta");
        			alert.setHeaderText("");
        			alert.setContentText("Você está realmente pesquisando Contas Contábeis ? Se sim, clique em OK, senão em Cancelar.");
        			Optional<ButtonType> result = alert.showAndWait();
    	    		if(!result.isPresent() || result.get() != ButtonType.OK) {
    	    			btnRadioNao.setSelected(true);
    	    		}
        		}
            	
            	Task<Object> task = new Task<Object>() {
					@Override
					protected Object call() throws Exception {
						//Task - inicio
		                data.clear();
		                FXCollections.emptyObservableList();
		                if(textProcurar.getText() == null || textProcurar.getText().isEmpty()){
		                    Alert alert = new Alert(Alert.AlertType.WARNING);
		                    alert.setHeaderText("");
		                    alert.setContentText("É necessário informar o texto a ser buscado.");                    
		                    alert.showAndWait();
		                    return null;
		                }              
		                scene.setCursor(Cursor.WAIT);
		                String toSearch = getTextToSearch();
		                Set<ResultBean> resultado = new HashSet<ResultBean>();
		                resultado.addAll(engine.search(getViewDirectory(),  new String[] {".sql",".map"}, toSearch));
		                if(btnRadioSim.isSelected()) {
		                	resultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/relatorios",  new String[] {".java"}, toSearch));
		                	resultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/relatoriosLOA",  new String[] {".java"}, toSearch));
		                }
		                
		                Map<String, ResultBean> indexResult = new HashMap<String, ResultBean>();
		                Set<ResultBean> tmpResultado = new HashSet<ResultBean>();
		                
		                if(checkVO.isSelected()) {
		                	resultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/vo", new String[] {"VO.java"}, toSearch));
		                	for(ResultBean searchMore : resultado) {
		                		if(indexResult.containsKey(searchMore.getFileName())) continue;
		                		tmpResultado.add(searchMore);
		                		String fileName = searchMore.getFileName().toLowerCase();
		                		tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/integracoes", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));                		
		                		
		                		if(checkContabilidade.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/negocios/contabilidade", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkContratos.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/negocios/contratos", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkDocumentos.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/negocios/documentos", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkGestaoAp.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/negocios/gestaoaplicativos", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkPlanejamento.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/negocios/planejamento", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkPPA.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/negocios/ppa", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkProgramacao.isSelected())                			
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/negocios/programacaofinanceira", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkPTA.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/negocios/pta", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkPTAGerencial.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/negocios/ptagerencial", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkTabelas.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/negocios/tabelas", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkRelatorios.isSelected() && !btnRadioSim.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/relatorios", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkRelatoriosLOA.isSelected() && !btnRadioSim.isSelected())
		                			tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/relatoriosLOA", new String[] {".java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		if(checkRelatorios.isSelected() || checkRelatoriosLOA.isSelected()) {
		                			tmpResultado.addAll(engine.search(getPesquisaSQLConfigDirectory(), new String[] {".xml"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                		}
		                		//Esse sempre executa se o checkVO estiver selecionado.
		                		tmpResultado.addAll(engine.search(getJavaDirectory() + "/br/gov/mt/cepromat/fiplan/vo", new String[] {"VO.java"}, ".*" + fileName.substring(0, fileName.indexOf(".")) + ".*"));
		                	}
		                	resultado.clear();
		                	resultado = tmpResultado;
		                }
		                
		                table.setItems(prepareTableData(resultado));
		                scene.setCursor(Cursor.DEFAULT);
						//Task fim
						return null;
					}
            		
            	};
            	Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
            }            
        });    	
    }
    
    @Override
    public void start(Stage primaryStage) {
    	primaryStage.resizableProperty().setValue(Boolean.FALSE);
        textProcurar.setPrefRowCount(10);
        textProcurar.setPrefColumnCount(100);
        textProcurar.setWrapText(true);
        textProcurar.setPrefWidth(450);        
        
        disableAllChecks(true);
        
        table = prepareTable();        
        table.setItems(data);
        
        Label labelProcura = new Label("Procurar:");       
        
        btn.setText("Buscar");
        
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(8));
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        
        initComponents();
        
//        StackPane root = new StackPane();        
        
        gridpane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT); // Default width and height
        gridpane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        
//        root.getChildren().add(gridpane);
        //Linha 1        
        gridpane.add(labelProcura, 0, 0);
        gridpane.add(textProcurar, 1, 0, 3, 1);
        gridpane.add(btn, 4, 0); 
        
        GridPane contacontabilpane = new GridPane();
        contacontabilpane.setPadding(new Insets(6));
        contacontabilpane.setHgap(10);
        contacontabilpane.setVgap(10);
        Label labelContacontabil = new Label("Conta Contábil");        
        contacontabilpane.add(labelContacontabil, 0, 0, 4, 1);
        btnRadioSim.setToggleGroup(groupConta);
        btnRadioNao.setToggleGroup(groupConta);
        btnRadioNao.setSelected(true);
        contacontabilpane.add(btnRadioSim, 0, 1, 2, 1);
        contacontabilpane.add(btnRadioNao, 3, 1, 2, 1);
        
        gridpane.add(contacontabilpane, 5, 0, 3, 1);
        
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
        gridpane.add(checkVO, 0,2,2,1);
        
        //Linha 4
        
        	GridPane panelCheck = new GridPane();	
	        panelCheck.setPadding(new Insets(8));
	        panelCheck.setHgap(10);
	        panelCheck.setVgap(10);
	        
	        panelCheck.add(checkIntegracoes, 0,0,2,1);
	        panelCheck.add(checkContabilidade, 2,0,2,1);
	        panelCheck.add(checkContratos, 4,0,2,1);
	        panelCheck.add(checkDocumentos, 6,0,2,1);
	        
	        panelCheck.add(checkGestaoAp, 0,1,2,1);
	        panelCheck.add(checkPlanejamento, 2,1,2,1);
	        panelCheck.add(checkPPA, 4,1,2,1);
	        panelCheck.add(checkPTA, 6,1,2,1);
	        
	        panelCheck.add(checkProgramacao, 0,2,2,1);
	        panelCheck.add(checkPTAGerencial, 2,2,2,1);
	        panelCheck.add(checkTabelas, 4,2,2,1);
	        panelCheck.add(checkRelatorios, 6,2,2,1);
	        
	        panelCheck.add(checkRelatoriosLOA, 0,3,2,1);
//	        panelCheck.add(new Label(""), 2,3,2,1);
//	        panelCheck.add(new Label(""), 4,3,2,1);
//	        panelCheck.add(new Label(""), 6,3,2,1);
        
        gridpane.add(panelCheck, 0, 3, 5, 1);
        
        /*
        private CheckBox  = new CheckBox("Relatórios LOA");
        */
        
        //Linha 5        
        gridpane.add(table, 0, 5, 8, 1);        
        
        scene = new Scene(gridpane, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        primaryStage.setTitle("Rastreia \"padrões\" no código das Views (.sql) e quais classe(s) Java a(s) utiliza(m)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void setPromptTextProcurar(String prompt) {
    	if(textProcurar.getText() == null || textProcurar.getText().isEmpty()) {    		
			textProcurar.setPromptText(prompt);
		}
    }

    private void disableAllChecks(boolean value) {
    	checkIntegracoes.setDisable(value);
    	checkContabilidade.setDisable(value);
    	checkContratos.setDisable(value);
    	checkDocumentos.setDisable(value);
    	checkGestaoAp.setDisable(value);
    	checkPlanejamento.setDisable(value);
    	checkPPA.setDisable(value);
    	checkPTA.setDisable(value);
    	checkProgramacao.setDisable(value);
    	checkPTAGerencial.setDisable(value);
    	checkTabelas.setDisable(value);
    	checkRelatorios.setDisable(value);
    	checkRelatoriosLOA.setDisable(value);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

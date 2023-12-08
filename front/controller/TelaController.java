package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TelaController implements Initializable, Observer {
    public Button btnNovoServent;
    public Button btnConsenso;
    public Label lblServents;
    public Label lblServentsConnected;
    public Label lblServentDisconnected;

    public int qtdServentsConnected;

    public int qtdde1;
    public int qtdde0;

    public int soma;

    public ArrayList<ServentController> controllers;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controllers = new ArrayList<>();
        btnNovoServent.setOnMouseClicked(event -> {
            createServentWindow();
        });

        btnConsenso.setOnMouseClicked(event -> {
            controllers.forEach(ServentController::sortear);
        });
    }

    public void createServentWindow(){
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/servent.fxml"));
                Parent root = loader.load(); // Carrego o layout da janela
                ServentController ctrl = loader.getController();
                ctrl.addObserver(this);
                ctrl.setSoma(soma);
                controllers.add(ctrl);
                Stage stage = new Stage(); // Instancio a janela
                stage.setTitle("Servent"); // Sou um titulo
                stage.setScene(new Scene(root)); // Passo o cenario a ser mostrado na janela
                stage.show(); // Mostro a janela
                stage.setOnCloseRequest(event -> {
                    controllers.remove(ctrl);
                    if(ctrl.isServentConnected()) 
                        qtdServentsConnected--;
                    atualizarLabelDeInfos();
                });

                atualizarLabelDeInfos();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void atualizarLabelDeInfos(){
        Platform.runLater(()->{
            lblServents.setText("Servents: "+controllers.size());
            lblServentsConnected.setText("Conectados: " + qtdServentsConnected);
            lblServentDisconnected.setText("Desconectados: " + (controllers.size() - qtdServentsConnected));
        });
    }


    @Override
    public synchronized void update(Observable o, Object arg) {
        ServentController controller = (ServentController) o;
        int state = controller.getState();

        switch(state){
            case 1: 
                controllers.forEach(controllerServ -> {
                    if(controllerServ.id == controller.id || !controllerServ.isServentConnected()){
                        return;
                    }
                    controllerServ.aceitarConexao();
                });
                qtdServentsConnected++;
                atualizarLabelDeInfos();
            break;
            case 2:
                if(controller.getNumeroSorteado() == 1){
                    qtdde1++;
                }else if (controller.getNumeroSorteado() == 0){
                    qtdde0++;
                }
                System.out.println("QTD DE 1:" + qtdde1);
                System.out.println("QTD DE 0:" + controllers.size());
                System.out.println("Size:" + controllers.size());
                if(qtdde1+qtdde0==controllers.size()){
                    int resultado = qtdde1 >= qtdde0 ? 1 : 0;
                    controllers.forEach(con -> {
                        con.receberResultado(resultado);
                        soma = con.getSoma();
                    });
                    qtdde1=0;
                    qtdde0=0;
                }

            break;
            case 0:
               qtdServentsConnected--;
               atualizarLabelDeInfos();
            break;
        }
    }

    
}

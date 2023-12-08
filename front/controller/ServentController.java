package controller;


import java.time.LocalTime;
import java.util.Observable;
import java.util.Random;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class ServentController extends Observable {

    private int state;
    
    public Button btnPrincipal;
    public Label lblSoma;
    public Label lblNumSorteado;
    public Label lblPrincipal;
    public Label lblStatus;

    public static int cont;
    public int id;

    public int numeroSorteado;
    public int soma;
    
    public ServentController(){
        id = cont++;
        state = 0;

    }

    public void pressionarBtnPrincipal(ActionEvent event){
        switch (state) {
            case 0:
                connect();
                break;
            case 2:
                desconnect();
            break;
            default:
                break;
        }
    }

    public void connect(){
        new Thread(() -> {
            state = 1;

            Platform.runLater(() -> {
                lblStatus.setText("Solicitando conexão");
                btnPrincipal.setDisable(true);
                btnPrincipal.setText("Esperando...");
            });

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setChanged();
            notifyObservers();

            waiting();


        }).start();
    }

    public void waiting(){
        new Thread(() -> {
            state = 2;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                lblStatus.setText("Conexao aceita");
            });
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                lblStatus.setText("");
                btnPrincipal.setDisable(false);
                btnPrincipal.setText("Desconectar");
            });
        }).start();
        
    }

    public void desconnect(){
        state = 0;
        btnPrincipal.setText("Conectar");
        setChanged();
        notifyObservers();
    }

    public void aceitarConexao(){
        new Thread(()->{
            Platform.runLater(() -> {
                lblStatus.setText("Recebendo request");
            });
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                lblStatus.setText("Conexao aceita");
            });
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                lblStatus.setText("");
            });
        }).start();
    }

    public void sortear(){
        new Thread(()->{
            Platform.runLater(() -> {
                lblPrincipal.setText("Sorteando um número");
            });
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.numeroSorteado = new Random(LocalTime.now().getNano()*id).nextInt(2);
            Platform.runLater(() -> {
                lblPrincipal.setText("O numero sorteado foi:" + numeroSorteado);
                lblNumSorteado.setText("N° sorteado: "+numeroSorteado);
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                lblPrincipal.setText("Aguardando resultado dos outros servents");
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            setChanged();
            notifyObservers();
    

        }).start();
    }

    public void receberResultado(int resultado){
        new Thread(()->{
            soma+=resultado;
            Platform.runLater(() -> {
                lblPrincipal.setText("O numero decido pela rede foi: "+resultado);
                lblSoma.setText("Soma: "+soma);
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                lblPrincipal.setText("");
            });
        }).start();
    }

    public int getState() {
        return state;
    }

    public int getNumeroSorteado() {
        return numeroSorteado;
    }

    public boolean isServentConnected(){
        return state == 2;
    }

    public int getSoma() {
        return soma;
    }

    public void setSoma(int soma) {
        this.soma = soma;
    }

}

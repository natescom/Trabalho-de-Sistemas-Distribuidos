package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map; 
import java.util.Observable;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Consumer;

import model.Message;
import model.NodeState;

public class ServentImp extends Thread implements Servent {

    // COMUNICATION VARIABLES //
    private ArrayList<Integer> connectedPorts;
    private final Integer myPort;

    // MAPA DE FUNCOES EXECUTAVEIS DE ACORDO A UMA RESPONSE //
    private Map<String, Consumer<Integer>> callback;
    private Comunicator comunicator;

    // VARIAVEIS DE ESTADO DO NO'
    private NodeState state;
    private Integer permissionsToConnect;

    private ServentImp(Integer myPort) {
        connectedPorts = new ArrayList<>();
        callback = new HashMap<>();
        comunicator = new Comunicator();
        state = NodeState.DISCONNECTED;

        this.myPort = myPort;

        Consumer<Integer> aceitarNovaConexar = (origin) -> {
            comunicator.connectWith(origin)
                    .send(myPort, Message.ACCEPT)
                    // NÂO É NECESSÁRIO ESPERAR UMA RESPOSTA //
                    .finish();
        };


        Consumer<Integer> mandarSalvarMinhaPort = (origin) -> {
            connectedPorts.add(origin);
            permissionsToConnect++;
            if (state.equals(NodeState.WAITING) && isReadyToConnect()) {
                connectToAll();
            } else if (state.equals(NodeState.CONNECTED)) {
                comunicator.connectWith(origin)
                        .send(myPort, Message.SAVE_MY_PORT)
                        .finish();
            }
        };
        Consumer<Integer> salvarPortExtrangeira = (origin) -> {
            connectedPorts.add(origin);
        };
        Consumer<Integer> deletarPortExtrangeira = (origin) -> {
            connectedPorts.remove(origin);
        };
        Consumer<Integer> jogar = (origin) -> {
            int i = new Random().nextInt(1);
            comunicator.connectWith(origin).send(myPort, Message.CONSENSO_RESP+";"+i);
        };

        callback.put(Message.JOIN, aceitarNovaConexar);
        callback.put(Message.ACCEPT, mandarSalvarMinhaPort);
        callback.put(Message.SAVE_MY_PORT, salvarPortExtrangeira);
        callback.put(Message.DEL_MY_PORT, deletarPortExtrangeira);
        callback.put(Message.CONSENSO, jogar);
    }

    public static Servent build(Integer myPort) {
        return new ServentImp(myPort);
    }

    @Override
    public void requestJoinGroup() {
        if (!state.equals(NodeState.DISCONNECTED)) {
            return;
        }
        state = NodeState.WAITING;
        comunicator.forEachPorts(port -> {
            comunicator.connectWith(port)
                        .send(myPort, Message.JOIN)
                        .recieve(callback) // RESPOSTA ESPERADA: ACEPT
                        .finish();
        });
    }

    @Override
    public void releaseGroup() {
        if (!state.equals(NodeState.CONNECTED)) {
            return;
        }
        connectedPorts.forEach(port -> {
            this.comunicator.connectWith(port)
                    .send(myPort, Message.DEL_MY_PORT)
                    .finish();
        });
        connectedPorts.clear();
        state = NodeState.DISCONNECTED;
    }

    @Override
    public boolean isReadyToConnect() {
        Integer quorum = connectedPorts.size() / 2;
        return permissionsToConnect > quorum;
    }

    @Override
    public void connectToAll() {
        connectedPorts.forEach(port -> {
            comunicator.connectWith(port)
                    .send(myPort, Message.SAVE_MY_PORT)
                    .finish();
        });
        state = NodeState.CONNECTED;
    }

    public Servent linkObs(Observable observable){
        observable.addObserver(comunicator);
        return this;
    }


    @Override
    public void consenso() {
        connectedPorts.forEach(port ->{
            comunicator.connectWith(port)
            .send(myPort, Message.CONSENSO)
            .recieve(callback)
            .finish();
        });
    }


}

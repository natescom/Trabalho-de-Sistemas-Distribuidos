package model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.function.Consumer;

public class PortData extends Observable {

    private ArrayList<Integer> ports;

    public PortData() {
        ports = new ArrayList();
    }

    public void add(Integer port){
        ports.add(port);
        setChanged();
        notifyObservers();
    }

    public void remove(Integer port){
        ports.remove(port);
        setChanged();
        notifyObservers();
    }

    public void clear(){
        ports.clear();
        setChanged();
        notifyObservers();
    }

    public ArrayList<Integer> getPorts() {
        return ports;
    }

    public void forEachPorts(Consumer<Integer> oConsumer){
        ports.forEach(oConsumer);
        setChanged();
        notifyObservers();
    }
    
}

package services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.function.Consumer;

import model.PortData;
/**
 * Responsavel pela comunicacao
 */
public class Comunicator implements Observer{
    private Socket socket;
    private PrintWriter sender;
    private Scanner reciever;

    private ArrayList<Integer> ports;

    public Comunicator connectWith(Integer port) {
        try {
            socket = new Socket("localhost", port);
            sender = new PrintWriter(socket.getOutputStream(), true);
            reciever = new Scanner(socket.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Comunicator send(Integer from, String message) {
        sender.println(from + "-" + message);
        return this;
    }

    public Comunicator recieve(Map<String, Consumer<Integer>> callback) {
        String[] responses = reciever.nextLine().split("-");
        Integer from = Integer.parseInt(responses[0]);
        String message = responses[1];
        callback.get(message).accept(from);
        return this;
    }

    public void finish() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        PortData newPortData = (PortData) o;
        ports = newPortData.getPorts();
    }

    public void forEachPorts(Consumer<Integer> oConsumer){
        ports.forEach(oConsumer);
    }

}

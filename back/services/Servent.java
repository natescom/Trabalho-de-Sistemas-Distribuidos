package services;

/**
 * Contem as funcoes que fazem o servent trocar seu estado
 */
public interface Servent {
    /**
     * Pergunta a rede distribuida se pode se conectar
     * Ele deve enviar uma mensagem para todos os colaboradores 
     * e quando a resposta de pelo menos a metade + 1 for positiva ele entra
     */
    public void requestJoinGroup();

    /**
     * Sair da rede distribuida
     */
    public void releaseGroup();


    /**
     * Verifica se esta pronto para conectar
     * Serve para verificar quando mandar uma mensagem em broadcast
     */
    public boolean isReadyToConnect();

    /**
     * Efetivamente connecta na rede distribuida
     */
    public void connectToAll();

    /*
     * Funcao que sera executada e se comunicara com outras servents
     */
    public void consenso();
}

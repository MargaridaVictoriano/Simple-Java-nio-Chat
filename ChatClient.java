import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ChatClient {

    // Variáveis relacionadas com a interface gráfica --- * NÃO MODIFICAR *
    JFrame frame = new JFrame("Chat Client");
    private JTextField chatBox = new JTextField();
    private JTextArea chatArea = new JTextArea();
    // --- Fim das variáveis relacionadas coma interface gráfica

    // Se for necessário adicionar variáveis ao objecto ChatClient, devem
    // ser colocadas aqui
    private Socket clientS;
    private String server;
    private int port;
    SocketChannel socketCh;



    // Método a usar para acrescentar uma string à caixa de texto
    // * NÃO MODIFICAR *
    public void printMessage(final String message) {
        chatArea.append(message);
    }


    // Construtor
    public ChatClient(String server, int port) throws IOException {

        // Inicialização da interface gráfica --- * NÃO MODIFICAR *
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(chatBox);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.setSize(500, 300);
        frame.setVisible(true);
        chatArea.setEditable(false);
        chatBox.setEditable(true);
        chatBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    newMessage(chatBox.getText());
                } catch (IOException ex) {
                } finally {
                    chatBox.setText("");
                }
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                chatBox.requestFocus();
            }
        });
        // --- Fim da inicialização da interface gráfica

        // Se for necessário adicionar código de inicialização ao
        // construtor, deve ser colocado aqui
        this.server = (InetAddress.getByName(server)).getHostAddress();
        this.port = port;


    }


    // Método invocado sempre que o utilizador insere uma mensagem
    // na caixa de entrada
    public void newMessage(String message) throws IOException {
        // PREENCHER AQUI com código que envia a mensagem ao servidor
        //remove spaces
        message = message.trim();
        System.out.println("SENDING: " + message);
        DataOutputStream msgToServer = new DataOutputStream(clientS.getOutputStream());
        msgToServer.write((message + "\n").getBytes("UTF-8"));
    }


    // Método principal do objecto
    public void run() throws IOException {
        // PREENCHER AQUI
        clientS = new Socket(server,port);
        new Thread(new Reader()).start();
    }


    // Instancia o ChatClient e arranca-o invocando o seu método run()
    // * NÃO MODIFICAR *
    public static void main(String[] args) throws IOException {
      ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]));
      client.run();
    }

    class Reader implements Runnable {
      public Reader() {

      }
      public void run() {
        try {
          BufferedReader data;
          boolean flag = true;
          while(flag){
            data = new BufferedReader(new InputStreamReader(clientS.getInputStream()));
            String msg = data.readLine();
            System.out.println("RECEIVED: " + msg);

            String[] tks = msg.split(" ");
            switch(tks[0]) {
              case "MESSAGE": {
                msg = msg.replaceFirst("MESSAGE","").replaceFirst(tks[1],"");
                msg = tks[1] + ":" + msg;
                break;
              }
              case "PRIVATE": {
                  break;
              }
              case "NEWNICK": {
                msg = tks[1] + " changed his nickname to: " + tks[2];
                break;
              }
              case "JOINED": {
                msg = tks[1] + " joined the room";
                break;
              }
              case "LEFT": {
                msg = tks[1] + " left the room";
                break;
              }
            }

            System.out.println("PRINTING: " + msg);

            if(msg.compareTo("BYE\n") == 0) {
              flag = false;
            }
            msg = msg + "\n";
            printMessage(msg);
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    }

}

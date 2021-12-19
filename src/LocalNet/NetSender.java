package LocalNet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetSender {
    Socket sock;
    PrintWriter output;
    public NetSender(String ip, int port) {
        try {
            sock = new Socket(ip, port);
            output = new PrintWriter(sock.getOutputStream());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void send(String s){
        output.println(s);
        output.flush();
    }

    public static void main(String[] args) {
        new NetSender("127.0.0.1",9089).send("P 2 4");
    }
}

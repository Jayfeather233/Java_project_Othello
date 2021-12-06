package LocalNet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket sock;
        PrintWriter output;
        BufferedReader input;

        try {
            sock = new Socket(InetAddress.getLocalHost(), 9890);
            output = new PrintWriter(sock.getOutputStream());
            input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String str;
            Scanner s = new Scanner(System.in);
            while (true) {
                System.out.println("Ecrire une chaine de caractere : ");
                str = s.nextLine();
                System.out.println("i want to reverse : " + str);
                output.println(str);
                output.flush();
                System.out.println("ch is sent");
                String rr = input.readLine();
                System.out.print(rr);
                System.out.println("reversed word is received");
            }
        } catch (UnknownHostException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

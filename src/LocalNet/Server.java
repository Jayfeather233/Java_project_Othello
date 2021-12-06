package LocalNet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        ServerSocket sock;
        Socket link;
        BufferedReader input;
        PrintWriter output;

        try {
            sock = new ServerSocket(9890);
            link = sock.accept();
            input = new BufferedReader(new InputStreamReader(link.getInputStream()));
            output = new PrintWriter(link.getOutputStream());
            String str;
            StringBuilder rstr;
            while (true) {
                System.out.println("entered while loop");
                str = input.readLine();
                System.out.println("we received : " + str);
                rstr = new StringBuilder();
                for (int i = 0; i < str.length(); i++)
                    rstr.insert(0, str.charAt(i));
                System.out.println("we will send to the client : " + rstr);
                output.println(rstr);

                output.flush();
                System.out.println("sent");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

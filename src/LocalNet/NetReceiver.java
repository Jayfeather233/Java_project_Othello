package LocalNet;

import components.ChessGridComponent;
import view.GameFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class NetReceiver {
    Receiver r;
    public NetReceiver(int port){
        r=new Receiver(port);
        new Thread(r).start();
    }
    public void end(){
        r.flg=false;
    }
}
class Receiver implements Runnable{
    boolean flg=true;
    int port;

    public Receiver(int port) {
        this.port=port;
    }

    @Override
    public void run() {
        ServerSocket sock;
        Socket link;
        BufferedReader input;

        try {
            sock = new ServerSocket(port);
            link = sock.accept();
            input = new BufferedReader(new InputStreamReader(link.getInputStream()));
            String str;
            while (flg) {
                System.out.println("entered while loop");
                str = input.readLine();
                System.out.println("we received : " + str);

                if(str.charAt(0)=='N'){
                    //请求连接
                    System.out.println("Get "+str);
                    if(checkReceivedIP(str)){
                        GameFrame.playWith(str.substring(1));
                    }else{
                        System.out.println("Received invalid IP.");
                    }
                }
                if(str.charAt(0)=='P'){
                    //下棋
                    if(checkReceivedPlay(str)){
                        String[] u=str.split(" ");
                        int col=Integer.parseInt(u[1]),row=Integer.parseInt(u[2]);
                        ChessGridComponent.netOn=false;
                        GameFrame.controller.getGamePanel().getChessGrids()[row][col].onMouseClicked();
                        ChessGridComponent.netOn=true;
                        System.out.println(str);
                    }else{
                        System.out.println("Invalid play.");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean checkReceivedPlay(String str) {
        String[] u=str.split(" ");
        if(u.length!=3) return false;
        for(int i=1;i<3;i++){
            String s=u[i];
            if(GameFrame.checkIsNumber(s)){
                return false;
            }
            int n=Integer.parseInt(s);
            if(n<0||n>8) return false;
        }
        return true;
    }

    private boolean checkReceivedIP(String str) {
        String[] u = str.substring(1).split("\\.");
        if (u.length != 4) return false;
        for (String s : u) {
            if (GameFrame.checkIsNumber(s)) {
                return false;
            }
            int t = Integer.parseInt(s);
            if (t < 0 || 255 < t) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Receiver r=new Receiver(9089);
        r.run();
    }
}

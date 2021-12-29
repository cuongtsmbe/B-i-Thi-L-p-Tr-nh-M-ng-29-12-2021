
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class ThongTinClient {
    private String nameClient;
    Socket s;
    String keyAES;
    boolean DangCho;//true là đang trạng thái chờ
    public ThongTinClient(String name,Socket s,String key){
        this.nameClient=name;
        this.s=s;
        this.keyAES=key;
        this.DangCho=true;
    }
    public String getnameclient(){
        return this.nameClient;
    }
    public Socket getsocket(){
        return this.s;
    }
    public String getkeyEAS(){
        return this.keyAES;
    }
    public void setkeyEAS(String key){
        this.keyAES=key;
    }
    public void setDangCho(){//cho user này vào trạng thái chờ
        this.DangCho=true;
    }
    public void setDangChat(){//cho user này vào trạng thái chat
        this.DangCho=false;
    }
            
}

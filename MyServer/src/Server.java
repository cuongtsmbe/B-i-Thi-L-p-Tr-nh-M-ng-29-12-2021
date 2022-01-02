

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

//https://alvinalexander.com/blog/post/jfc-swing/an-example-of-use-joptionpane-showconfirmdialog-method/
public class Server extends javax.swing.JFrame {

    ServerSocket ss;
    HashMap<Integer, Object> clientList = new HashMap();
    HashMap<Integer,Integer> communicate=new HashMap();//2 port trong server giao tiếp với nhau
    HashMap<Integer,ArrayList<Integer>> KhongGhep=new HashMap();//Danh sách port sẽ k đc ghép với port (key hashmap)
    boolean KtraUserMoi=false;//kiểm tra có user nào mới thêm vào không
    public Server() {
        
        try{
        //tạo cặp khóa RSA
        RSA.createRSA();
        initComponents();
        ss = new ServerSocket(6666);
        this.sStatus.setText("Server Started.");
        new ClientAccept().start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    class ClientAccept extends Thread {

        public void run() {
            while (true) {
                try {
                    msgBox.append("Server started on port 6666. Waiting for Client....\n");
                    Socket s = ss.accept();
                    msgBox.append("Client "+s.getInetAddress()+" connected at port "+s.getPort()+"\n");
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    String i = new BufferedReader(new InputStreamReader(s.getInputStream())).readLine();                    
                        //kiểm tra username đã có trong danh sách hay chưa   
                        for (Integer key : clientList.keySet()) {
                           
                             ThongTinClient Thongtin=(ThongTinClient) clientList.get(key);
                              if(i.toUpperCase().equals(Thongtin.getnameclient().toUpperCase())){
                                    out.write("username_exist");
                                    out.newLine();
                                    out.flush();
                                    throw new Exception("loi username ton tai");
                                    //System.out.println("Key: " + key+" : "+Thongtin.getnameclient());
                              }
                        }
                        out.write("username_no_exist");
                        out.newLine();
                        out.flush();
                        msgBox.append(i + " Da tham gia !\n");
                        //--kết nối user nói chuyện với nhau
                        clientList.put(s.getPort(), new ThongTinClient(i,s,null));
                        communicate.put(s.getPort(),-1);
                        
                        KhongGhep.put(s.getPort(),new ArrayList<Integer>());
                 
                        //gửi key RSA public cho client
                        System.out.println("Server gửi RSA public key:"+RSA.GetKeyPublicBase64());
                        out.write(RSA.GetKeyPublicBase64());
                        out.newLine();
                        out.flush();
                        //chờ nhận client gửi lại key AES
                        String messageRecieveFromClient = new BufferedReader(new InputStreamReader(s.getInputStream())).readLine();    
                        messageRecieveFromClient=RSA.GiaiMaDuLieu(messageRecieveFromClient);
                       
                        System.out.println("Đã nhận được key AES : "+messageRecieveFromClient);
                        
                        //cập nhật key AES cho user
                        ThongTinClient Obj=(ThongTinClient)clientList.get(s.getPort());
                        Obj.setkeyEAS(messageRecieveFromClient);
                        
                        
                        
                        KtraUserMoi=true;
                        new ReadMsg(s,i).start();
                       // new KiemTraGhepDoi(s,i).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
//    class KiemTraGhepDoi extends Thread{
//         Socket s;
//        String id;
//
//        public KiemTraGhepDoi(Socket s, String id) {
//            this.s = s;
//            this.id = id;
//        }
//        public void run(){
//             
//              int i=0;
//                 while(true){
//                     try {
//                        ThongTinClient cl=(ThongTinClient) clientList.get(s.getPort());
//                        System.out.println("abc"+(i++));
//                            if(cl.DangCho && KtraUserMoi==true){
//                                   KtraUserMoi=false;
//                                   ThucHienGhepDoi(s);
//                                   
//                             }
//
//                        } catch (IOException ex) {
//                          Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//               }
//        }
//    }
    class  ReadMsg extends Thread{
        Socket s;
        String id;

        public ReadMsg(Socket s, String id) {
            this.s = s;
            this.id = id;
        }
        public void run(){
            boolean chay=true;
            while (chay){
                 try {
                     ThongTinClient cl=(ThongTinClient) clientList.get(s.getPort());
                           //ghép đôi
                            if(cl.DangCho && KtraUserMoi==true){
                                   KtraUserMoi=false;
                                   ThucHienGhepDoi(s);
                                   
                             }
                    
                    //get key AES in hashmap
                    
                    String messageRecieveFromClient = new BufferedReader(new InputStreamReader(s.getInputStream())).readLine();                       
                    messageRecieveFromClient=AES.GiaiMaAES(messageRecieveFromClient,cl.getkeyEAS());
                    System.out.println(messageRecieveFromClient);
                    System.out.println("server received :"+messageRecieveFromClient);
                    if(!messageRecieveFromClient.contains("khongmuonGN")&&messageRecieveFromClient.contains("muonGN")){
                        //server thực hiện ghép đôi
                        DongYGhepNoi(s,messageRecieveFromClient); 
                    }
                   
                    if(messageRecieveFromClient.equals("exit")) {   
                        chay=false;
                        ThongBaoOutRoom(s,cl);
                        try{
                        clientList.remove(s.getPort());
                        }catch(Exception e){}
                         try{
                        communicate.remove(s.getPort());
                        }catch(Exception e){}
                          try{
                        KhongGhep.remove(s.getPort());
                        }catch(Exception e){}
                       
                        msgBox.append(id+" Đã thoát!!!");
                        s.close();
                       
                        
                    }
                    if(messageRecieveFromClient.equals("ls")){
                            System.out.println("--..--");
                            System.out.println(communicate);
                            
                            for (Integer port : clientList.keySet()) {
                                  ThongTinClient T=(ThongTinClient) clientList.get(port);
                                  //kiểm tra các port (user) đang ở trạng thái chờ 
                                     System.out.println(T.getsocket().getPort()+" : "+T.DangCho+" -- "+T.getnameclient());
                             }
                            System.out.println("--..--");
                            continue;
                    }
                    if(messageRecieveFromClient.equals("HUYCHO")){
                        ThongTinClient client=(ThongTinClient) clientList.get(s.getPort());
                        client.setDangCho();
                        continue;
                    }
                    if(messageRecieveFromClient.equals("thoatRoom")) {                        
                        KtraUserMoi=true;
                        cl.setDangCho();
                        System.out.println(id + "Da thoat khoi room");
                        ThongBaoOutRoom(s,cl);
                        continue;
                        
                    }
                    if(messageRecieveFromClient.contains("khongmuonGN")){
                        //không đồng ý ghép nối
                        //thông báo user đang chờ thoát chờ
                        messageRecieveFromClient=messageRecieveFromClient.split("khongmuonGN")[1];//get port;username
                        String StrportBiTuChoi=messageRecieveFromClient.split(";")[0];//get port
                        
                        int IntportBiTuChoi=Integer.parseInt(StrportBiTuChoi);

                        //thêm vào danh sách sẽ k đc ghép với s.getPort()
                        ArrayList DanhSachKhongghep=KhongGhep.get(s.getPort());
                        DanhSachKhongghep.add(IntportBiTuChoi);
                        KhongGhep.put(s.getPort(),DanhSachKhongghep);

			//thêm danh sách không ghép với user bị từ chối
			 ArrayList DanhSachKhongghepBiTuChoi=KhongGhep.get(IntportBiTuChoi);
                        DanhSachKhongghepBiTuChoi.add(s.getPort());
                        KhongGhep.put(IntportBiTuChoi,DanhSachKhongghepBiTuChoi);

                        ThongTinClient clBiTuChoi=(ThongTinClient) clientList.get(IntportBiTuChoi);
                        System.out.println(s.getPort() +" khongmuon GN "+StrportBiTuChoi);
                        BufferedWriter outUserBiTuChoi= new BufferedWriter(new OutputStreamWriter(clBiTuChoi.getsocket().getOutputStream()));
                        if(clBiTuChoi.DangCho==false && communicate.get(IntportBiTuChoi)==s.getPort()){
                            outUserBiTuChoi.write("BiTuChoiGhepDoi"+cl.getnameclient());
                            outUserBiTuChoi.newLine();
                            outUserBiTuChoi.flush();
                            KtraUserMoi=true;
                        }
                        continue;
                        
                    }
                    
                    if(!messageRecieveFromClient.contains("muonGN")){
                        //gửi tin nhắn 
                        System.out.println("------send---------");
                        GuiTinNhan(s,messageRecieveFromClient);
                    }
                                     
                } catch (Exception e) {
                   
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    private void ThongBaoOutRoom(Socket s,ThongTinClient cl){
        try{
        
         ThongTinClient userGhep=(ThongTinClient) clientList.get(communicate.get(s.getPort()));
                       
                        //nếu chung phòng thì setDangCho 2 người
                        if(userGhep.DangCho!=true&&cl.getsocket().getPort()==communicate.get(userGhep.getsocket().getPort())&&userGhep.getsocket().getPort()==communicate.get(cl.getsocket().getPort())){
                            userGhep.setDangCho();
                            
                            BufferedWriter outuserGhep = new BufferedWriter(new OutputStreamWriter(userGhep.getsocket().getOutputStream()));
                            outuserGhep.write(AES.MaHoaAES("Tbserver "+cl.getnameclient()+" đã rời phòng !",userGhep.getkeyEAS()));
                            outuserGhep.newLine();
                            outuserGhep.flush(); 
                        }
        }catch(Exception e){}
     
    }
    private void GuiTinNhan(Socket s,String message) throws IOException{
        ThongTinClient cl=(ThongTinClient) clientList.get(s.getPort());
        try{
            //kiểm tra 2user giao tiếp có được ghép đôi hay chưa
            //sẽ có Exception nếu s chưa kết nối với người nhận

        ThongTinClient nguoiNhan=(ThongTinClient) clientList.get(communicate.get(s.getPort()));
        if(cl.DangCho||nguoiNhan.DangCho || (cl.getsocket().getPort()!=communicate.get(nguoiNhan.getsocket().getPort())||nguoiNhan.getsocket().getPort()!=communicate.get(cl.getsocket().getPort()))){
            //cổng giao tiếp của người nhận không phải của người gửi 
            cl.setDangCho();
            KtraUserMoi=true;
            //nguoiNhan.setDangCho();
            throw new Error("Chưa được ghép đôi");
        }

        System.out.println("nguoi nhan : "+nguoiNhan.getnameclient());
        BufferedWriter outNguoiGui = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        BufferedWriter outNguoiNhan= new BufferedWriter(new OutputStreamWriter(nguoiNhan.getsocket().getOutputStream()));
        outNguoiNhan.write(AES.MaHoaAES(message,nguoiNhan.getkeyEAS()));
        outNguoiNhan.newLine();
        outNguoiNhan.flush();   
        outNguoiGui.write(AES.MaHoaAES("TBDAGUI",cl.getkeyEAS()));
        outNguoiGui.newLine();
        outNguoiGui.flush(); 
        }catch(Exception e){
            System.out.println("GuiTinNhan(Socket;String): "+e);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            out.write(AES.MaHoaAES("Tbserver:Không tìm thấy người đối thoại.Tin chưa được gửi.",cl.getkeyEAS()));
            out.newLine();
            out.flush();  
        }
        
    }
    

    private void DongYGhepNoi(Socket s,String PortAndUserNameMuonGhep) throws IOException{
//        System.out.println("DongYGhepNoi");
        try{
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                        //lấy port trong message trả về(vd: muonGN125;cuong)
                        Integer oldvalueA=null;
                        String StrPortAndUser=PortAndUserNameMuonGhep.split("muonGN")[1];
                        String[] portAndUserName=StrPortAndUser.split(";");
                        int portghepnoi=Integer.parseInt(portAndUserName[0]);
                        //cập nhật port sẽ giao tiếp(chat)
                        System.out.println(s.getPort()+" <-> "+portghepnoi);
                        ThongTinClient user=(ThongTinClient) clientList.get(s.getPort());
                        ThongTinClient userGhep=(ThongTinClient) clientList.get(portghepnoi);
                        //portghepdoi phải đang chờ hoặc đã đồng ý s.getPort() trc 
                        if(userGhep.DangCho==true || (userGhep.DangCho==false && communicate.get(portghepnoi)==s.getPort())){
                            if(user.DangCho==true){//nếu đang ngoài Room thì mới đồng ý được
                                oldvalueA=communicate.replace(s.getPort(),portghepnoi);//update value in hashmap
                               
                            }
                        }
                        
                       
                        if(oldvalueA!=null){
                            //nếu cập nhật thành công , null khi key không tồn tại trong hashmap
                            System.out.println("--.DongYGhepNoi.--"+user.getnameclient()+"---");
                            System.out.println(communicate);
                            
                            for (Integer port : clientList.keySet()) {
                                  ThongTinClient T=(ThongTinClient) clientList.get(port);
                                  
                                  if(T.getsocket().getPort()!=portghepnoi && T.getsocket().getPort()!=s.getPort()&&T.DangCho==false && communicate.get(T.getsocket().getPort())==s.getPort()){
                                      BufferedWriter outT = new BufferedWriter(new OutputStreamWriter(T.getsocket().getOutputStream()));
                                      //userGhep vào phòng khác
                                            KtraUserMoi=true;
                                          outT.write(AES.MaHoaAES("HUYCHO"+user.getnameclient(),T.getkeyEAS()));
                                          outT.newLine();
                                          outT.flush();
                                      
                                      
                                    }
                                 
                                 
                                    System.out.println(T.getsocket().getPort()+" : "+T.DangCho+" -- "+T.getnameclient());
                            }
                            //userGhep đã có trong phòng
                            //user join vào phòng
                            //thông báo cho userGhep biết
                            //thông báo cho user vào biết trong phòng đã có userGhep ->start
                            BufferedWriter outGhep = new BufferedWriter(new OutputStreamWriter(userGhep.getsocket().getOutputStream()));
                            BufferedWriter outUser = new BufferedWriter(new OutputStreamWriter(user.getsocket().getOutputStream()));
                            if(userGhep.DangCho==false && communicate.get(userGhep.getsocket().getPort())==s.getPort()){
                                    outGhep.write(AES.MaHoaAES("USERDAJOIN"+user.getnameclient(),userGhep.getkeyEAS()));
                                    outGhep.newLine();
                                    outGhep.flush();
                                    //thông báo start chat cho user
                                    outUser.write(AES.MaHoaAES("USERDAJOIN"+userGhep.getnameclient(),user.getkeyEAS()));
                                    outUser.newLine();
                                    outUser.flush();
                            } 
                            //thông báo user cần chờ userGhep vào
                            
                            
                            if(userGhep.DangCho==true){
                                    outUser.write(AES.MaHoaAES("THONGBAOCANCHO"+userGhep.getnameclient(),user.getkeyEAS()));
                                    outUser.newLine();
                                    outUser.flush();
                            }
                                  
                                  
                                   
                        }

                              
                            System.out.println("--..--");
                            user.setDangChat();
              
        }catch(Exception e){
            System.out.println("DongYGhepNoi(Socket,String):"+e);
        }
    }

    
   
    public void ThucHienGhepDoi(Socket s) throws IOException{  
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        for (Integer port : clientList.keySet()) {
            ThongTinClient user=(ThongTinClient) clientList.get(port);
            //kiểm tra các port (user) đang ở trạng thái chờ 
                if(port!=s.getPort()&&user.DangCho==true&&!KhongGhep.get(s.getPort()).contains(port)){
                    out.write("Thongbaoghepdoi"+port+";"+user.getnameclient());
                    out.newLine();
                    out.flush();
                    ThongBaoChoNguoiDuocGhepDoi(s,port);
                }

        }
    }
    
    private void ThongBaoChoNguoiDuocGhepDoi(Socket s,int port) throws IOException{
        //thông báo cho người ghép nối khác
        ThongTinClient user=(ThongTinClient) clientList.get(port);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(user.getsocket().getOutputStream()));
        ThongTinClient userGNoi=(ThongTinClient) clientList.get(s.getPort());
        out.write("Thongbaoghepdoi"+s.getPort()+";"+userGNoi.getnameclient());
        out.newLine();
        out.flush();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        msgBox = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        sStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MyServer");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        msgBox.setEditable(false);
        msgBox.setColumns(20);
        msgBox.setRows(5);
        jScrollPane1.setViewportView(msgBox);

        jLabel1.setText("Server status:");

        sStatus.setText("..........................................");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 238, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sStatus))
                .addGap(37, 37, 37)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        Iterator itrIterator = clientList.keySet().iterator();
        while (itrIterator.hasNext()){
            ThongTinClient client = (ThongTinClient) clientList.get(itrIterator.next());
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getsocket().getOutputStream()));
                        out.write(AES.MaHoaAES("serverclosed", client.getkeyEAS()));
                        out.newLine();
                        out.flush();
                        out.close();
                        ss.close();
                 this.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea msgBox;
    private javax.swing.JLabel sStatus;
    // End of variables declaration//GEN-END:variables
}

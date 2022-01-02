/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.miginfocom.swing.MigLayout;


public class ClientGUI extends javax.swing.JFrame {
    String id, clientID="";
    BufferedReader in ;
    BufferedWriter out;
    String userGhepDoi=null;
    /**
     * Creates new form mainChat
     * 
     */
    
    public ClientGUI() {
        //initComponents();
                //tạo key AES
        //JOptionPane.showMessageDialog(rootPane,"tét");  
    }
    ClientGUI(String id, Socket s){
        
        this.id = id;
        try {
            initComponents();
            txtMessage.setVisible(false);
            this.setTitle("Client - "+id);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            new Read().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class Read extends Thread{
        public void run(){
            boxchat.setLayout(new MigLayout("fillx"));
            while (true) {                
                try {
//                    if(userGhepDoi==null){
//                        btnSend.setEnabled(false);
//                    }else{
//                        btnSend.setEnabled(true);
//                    }
                    String m = in.readLine();   
                    System.out.println("rec: "+m);
                    String GiaiMaM=AES.GiaiMaAES(m);
                    
                    if(GiaiMaM!=null){
                        m=GiaiMaM;
                    }
                    System.out.println("m sau giai ma :"+m);
                    if(m.equals("serverclosed")){
                        lbThongBao.setText("Mất kết nối đến Server");
                        btnSend.setEnabled(false);
                        break;
                    }
                    if(m.indexOf("THONGBAOCANCHO")!=-1){
                        String thongbao=m.split("THONGBAOCANCHO")[1];
                        lbThongBao.setText("Đang đợi "+thongbao);
                        buttonOutRoom.setText("Tìm User Khác");
                        txtMessage.setVisible(false);
                        continue;
                    }
                    if(m.indexOf("USERDAJOIN")!=-1){
                        String thongbao=m.split("USERDAJOIN")[1];
                        buttonOutRoom.setText("Out Room");
                        lbThongBao.setText("chat with "+thongbao);
                        userGhepDoi=thongbao;
                        jLabel2.setText("Chat ("+userGhepDoi+")");
                        txtMessage.setVisible(true);
                        lbThongBao.setText(" ");
                        continue;
                    }
                    if(m.indexOf("HUYCHO")!=-1 || m.indexOf("BiTuChoiGhepDoi")!=-1){
                        String thongbao=null;
                        String loinhan="";
                        if(m.indexOf("HUYCHO")!=-1){
                             thongbao=m.split("HUYCHO")[1];
                             loinhan=thongbao+" đã vào Room khác .";
                        }
                        if(m.indexOf("BiTuChoiGhepDoi")!=-1){
                            thongbao=m.split("BiTuChoiGhepDoi")[1];
                            loinhan=thongbao+" đã từ chối chat chung với bạn .";
                        }
                        lbThongBao.setText(" Đã hủy chờ "+thongbao);
                        buttonOutRoom.setText("Tìm User");
                        txtMessage.setVisible(false);
                        JOptionPane.showConfirmDialog(null,loinhan,"("+id+")", JOptionPane.YES_OPTION);
                        jLabel2.setText("Chat");
                        out.write(AES.MaHoaAES("HUYCHO"));
                        out.newLine();
                        out.flush();
                        continue;
                    }
                    if(m.indexOf("Tbserver")!=-1){
                        //người đối thoại đã rời phòng
                         System.out.println("Đối phương đã out !.");
                         userGhepDoi=null;
                         txtMessage.setVisible(false);
                         buttonOutRoom.setText("Tìm User");
                         boxchat.removeAll();
                         jLabel2.setText("Chat");
                         //boxchat.repaint();
                         JOptionPane.showConfirmDialog(null,m.split("Tbserver")[1],"("+id+")", JOptionPane.YES_OPTION);
                         continue;
                    }
                    if(m.equals("TBDAGUI")){
                        System.out.println("Tin đã gửi .");
                        continue;
                        
                    }
                    if(userGhepDoi==null || m.indexOf("Thongbaoghepdoi")!=-1){
                        if(m.indexOf("Thongbaoghepdoi")!=-1){
                            String ketnoiuser="";
                            ketnoiuser=m.split("Thongbaoghepdoi")[1];//lấy được port;username của người sẽ ghép nôi
                           
                            String[] getusernameinmess=ketnoiuser.split(";");
                            String title = id+" Thông báo ghép đôi ";
                            int reply = JOptionPane.showConfirmDialog(null, "Bạn có muốn ghép đôi với "+getusernameinmess[1]+" không ?", title, JOptionPane.YES_NO_OPTION);
                            if (reply == JOptionPane.YES_OPTION)
                            {
                                out.write(AES.MaHoaAES("muonGN"+ketnoiuser));//muonGN1235;cuong
                                out.newLine();
                                out.flush();
                                userGhepDoi=getusernameinmess[1];
                                jLabel2.setText("Chat ("+userGhepDoi+")");
                                lbThongBao.setText("wait minute ...");
                                lbThongBao.setForeground (Color.blue);
                                //txtMessage.setVisible(true);
                                buttonOutRoom.setText("Out Room");
                                sleep(2000);
                                lbThongBao.setText(null);
                                System.out.println("đồng ý kết nối user: "+userGhepDoi);
                                //break;
                            }
                            if (reply == JOptionPane.NO_OPTION){
                                out.write(AES.MaHoaAES("khongmuonGN"+ketnoiuser));//khongmuonGN1235;cuong
                                out.newLine();
                                out.flush();
                                jLabel2.setText("Chat)");
                                System.out.println("không đồng ý kết nối user: "+getusernameinmess[1]);
                            }
                            
                        }
                        
                        continue;
                    
                    }
                            TextItemLeft item = new TextItemLeft(m);
                            boxchat.add(item,"wrap, w 80%");
                            //boxchat.repaint();
                            boxchat.revalidate();     
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    
                }
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbthongbao = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        boxchat = new javax.swing.JPanel();
        txtMessage = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnSend = new javax.swing.JButton();
        lbThongBao = new javax.swing.JLabel();
        buttonOutRoom = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        username = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lbthongbao.setBackground(new java.awt.Color(33, 50, 74));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        boxchat.setBackground(new java.awt.Color(36, 37, 38));

        javax.swing.GroupLayout boxchatLayout = new javax.swing.GroupLayout(boxchat);
        boxchat.setLayout(boxchatLayout);
        boxchatLayout.setHorizontalGroup(
            boxchatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 712, Short.MAX_VALUE)
        );
        boxchatLayout.setVerticalGroup(
            boxchatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 427, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(boxchat);

        txtMessage.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMessage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        txtMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMessageActionPerformed(evt);
            }
        });
        txtMessage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtMessageKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("VNI-Bandit", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/simsimi2.png"))); // NOI18N
        jLabel2.setText("CHAT");

        btnSend.setBackground(new java.awt.Color(33, 50, 74));
        btnSend.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSend.setForeground(new java.awt.Color(255, 212, 1));
        btnSend.setText("GỬI");
        btnSend.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        lbThongBao.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbThongBao.setForeground(new java.awt.Color(255, 0, 51));
        lbThongBao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        buttonOutRoom.setText("Tìm User");
        buttonOutRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOutRoomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lbthongbaoLayout = new javax.swing.GroupLayout(lbthongbao);
        lbthongbao.setLayout(lbthongbaoLayout);
        lbthongbaoLayout.setHorizontalGroup(
            lbthongbaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lbthongbaoLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(txtMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
            .addComponent(lbThongBao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lbthongbaoLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lbthongbaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lbthongbaoLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(buttonOutRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(username))
                .addGap(39, 39, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(lbthongbaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        lbthongbaoLayout.setVerticalGroup(
            lbthongbaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lbthongbaoLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(lbthongbaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(username)
                    .addComponent(buttonOutRoom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbThongBao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lbthongbaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbthongbao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbthongbao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            // TODO add your handling code here:   
            if(btnSend.isEnabled()){
                userGhepDoi=null;
                out.write(AES.MaHoaAES("exit"));
                out.newLine();
                out.flush();
//                out.close();
//                in.close();
                
            }
                this.dispose();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_formWindowClosing

    private void buttonOutRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOutRoomActionPerformed
        // TODO add your handling code here:

        try {
            userGhepDoi=null;
            lbThongBao.setText("Clicked "+buttonOutRoom.getText()+" ...");
            buttonOutRoom.setText("Tìm User");
            txtMessage.setVisible(false);
            boxchat.removeAll();
            
            lbThongBao.setForeground (Color.green);
            //boxchat.repaint();
            userGhepDoi=null;
            out.write(AES.MaHoaAES("thoatRoom"));
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buttonOutRoomActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        // TODO add your handling code here:
        String textMsg = txtMessage.getText().trim();
        if (textMsg != null && !textMsg.isEmpty()) {
            try {
                String messageToBeSendToServer = textMsg;
                TextItemRight item = new TextItemRight(messageToBeSendToServer);
                boxchat.add(item,"wrap, w 80%, al right");
                //boxchat.repaint();
                boxchat.revalidate();

                txtMessage.setText(null);
                txtMessage.requestFocus();

                out.write(AES.MaHoaAES(messageToBeSendToServer));
                out.newLine();
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }//GEN-LAST:event_btnSendActionPerformed

    private void txtMessageKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMessageKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            btnSend.doClick();
        }
    }//GEN-LAST:event_txtMessageKeyPressed

    private void txtMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMessageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMessageActionPerformed
    
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
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.setProperty("file.encoding","utf-8");
                new ClientGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel boxchat;
    private javax.swing.JButton btnSend;
    private javax.swing.JButton buttonOutRoom;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbThongBao;
    private javax.swing.JPanel lbthongbao;
    private javax.swing.JTextField txtMessage;
    private javax.swing.JLabel username;
    // End of variables declaration//GEN-END:variables
}

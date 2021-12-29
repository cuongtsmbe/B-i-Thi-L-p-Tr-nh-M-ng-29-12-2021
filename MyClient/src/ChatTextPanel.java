/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextPane;

/**
 *
 * @author NGOC BAU
 */
public class ChatTextPanel extends JTextPane{
    Color borderColor = Color.BLUE;
    Color bgColor = Color.GREEN;

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }
    
    public ChatTextPanel() {
        setBackground(new Color(0,0,0,0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2=(Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(bgColor);
        g2.fillRoundRect(0,0,getWidth(),getHeight(), 5, 5);
        
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 5, 5);
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
    }
    
}

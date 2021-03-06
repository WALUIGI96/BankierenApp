/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.gui;

import bank.bankieren.Rekening;
import bank.bankieren.IRekening;
import bank.bankieren.Money;
import bank.internettoegang.Bankiersessie;
import bank.internettoegang.IBalie;
import bank.internettoegang.IBankiersessie;
import bankapplicatie.mark.joris.Iovermaak;
import fontys.util.InvalidSessionException;
import fontys.util.NumberDoesntExistException;
import java.beans.PropertyChangeEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author frankcoenen
 */
public class BankierSessieController implements Observer, Initializable{

    @FXML
    private Hyperlink hlLogout;

    @FXML
    private TextField tfNameCity;
    @FXML
    private TextField tfAccountNr;
    @FXML
    private TextField tfBalance;
    @FXML
    private TextField tfAmount;
    @FXML
    private TextField tfToAccountNr;
    @FXML
    private Button btTransfer;
    @FXML

    private TextArea taMessage;

    private BankierClient application;
    private IBalie balie;
    private IBankiersessie sessie;
    private luisteraar ls;

    public void setApp(BankierClient application, IBalie balie, IBankiersessie sessie) {
        try {
            this.balie = balie;
            this.sessie = sessie;
            this.application = application;
            this.ls = new luisteraar(this);
            IRekening rekening = null;
            try {
                rekening = sessie.getRekening();
                Rekening yr = (Rekening)rekening;
                yr.addObserver(this);
                balie.addListener(ls, "rekening");
                sessie.addListener(ls, "rekening");
                tfAccountNr.setText(rekening.getNr() + "");
                tfBalance.setText(rekening.getSaldo() + "");
                String eigenaar = rekening.getEigenaar().getNaam() + " te "
                        + rekening.getEigenaar().getPlaats();
                tfNameCity.setText(eigenaar);
            } catch (InvalidSessionException ex) {
                taMessage.setText("bankiersessie is verlopen");
                Logger.getLogger(BankierSessieController.class.getName()).log(Level.SEVERE, null, ex);
                
            } catch (RemoteException ex) {
                taMessage.setText("verbinding verbroken");
                Logger.getLogger(BankierSessieController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(BankierSessieController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            sessie.logUit();
            application.gotoLogin(balie, "");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void transfer(ActionEvent event) {
        try {
            int from = Integer.parseInt(tfAccountNr.getText());
            int to = Integer.parseInt(tfToAccountNr.getText());
            if (from == to) {
                taMessage.setText("can't transfer money to your own account");
            }
            long centen = (long) (Double.parseDouble(tfAmount.getText()) * 100);
            sessie.maakOver(to, new Money(centen, Money.EURO));
        } catch (RemoteException e1) {
            e1.printStackTrace();
            taMessage.setText("verbinding verbroken");
        } catch (NumberDoesntExistException | InvalidSessionException e1) {
            e1.printStackTrace();
            taMessage.setText(e1.getMessage());
        }
    }

    @Override
    public void update(Observable o, Object arg) 
    {
         IRekening rekening = (IRekening) arg;
         tfBalance.setText(rekening.getSaldo() + "");
    }
    
    public void saldoupdate()
    {
        try {  
            tfBalance.setText(sessie.getRekening().getSaldo() + "");
        }  catch (RemoteException ex) {
            Logger.getLogger(BankierSessieController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidSessionException ex) {
            Logger.getLogger(BankierSessieController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    public IBankiersessie getsessie()
    {
        return sessie;
    }
}

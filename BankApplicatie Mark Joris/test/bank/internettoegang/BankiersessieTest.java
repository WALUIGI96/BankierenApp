/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.internettoegang;

import bank.bankieren.Bank;
import bank.bankieren.IBank;
import bank.bankieren.Money;
import bankapplicatie.mark.joris.Iovermaak;
import bankapplicatie.mark.joris.overmaak;
import fontys.util.InvalidSessionException;
import fontys.util.NumberDoesntExistException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jvdwi
 */
public class BankiersessieTest {

    private static IBankiersessie bankiersessie, bs2;
    private static IBank bank;

    public BankiersessieTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        try {
            Iovermaak OV = new overmaak();
            bank = new Bank("Rabobank", OV);
            bank.openRekening("Joris", "Oploo");
            bank.openRekening("Mark", "Castenray");
        } catch (RemoteException ex) {
            Logger.getLogger(BankiersessieTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws RemoteException {
        bankiersessie = new Bankiersessie(100000001, bank);
        bs2 = new Bankiersessie(100000002, bank);
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testIsGeldig() throws RemoteException, InterruptedException {
        assertTrue("Bankiersessie is onrechtmatig ongeldig", bankiersessie.isGeldig());
        Thread.sleep(IBankiersessie.GELDIGHEIDSDUUR);
        assertFalse("Bankiersessie is onrechtmatig geldig", bankiersessie.isGeldig());
    }

    @Test(expected = InvalidSessionException.class)
    public void testMaakOverInvalidSessionException() throws InterruptedException, NumberDoesntExistException, InvalidSessionException, RemoteException {
        Thread.sleep(IBankiersessie.GELDIGHEIDSDUUR);
        bankiersessie.maakOver(100000002, new Money(1200, Money.EURO));
    }

    @Test(expected = RuntimeException.class)
    public void testMaakOverRuntimeExceptionSourceDestination() throws InterruptedException, NumberDoesntExistException, InvalidSessionException, RemoteException {
        bankiersessie.maakOver(100000001, new Money(1200, Money.EURO));
    }

    @Test(expected = RuntimeException.class)
    public void testMaakOverRuntimeExceptionAmount() throws InterruptedException, NumberDoesntExistException, InvalidSessionException, RemoteException {
        bankiersessie.maakOver(100000002, new Money(-1200, Money.EURO));
    }

    @Test
    public void testMaakOver() throws InterruptedException, NumberDoesntExistException, InvalidSessionException, RemoteException {
        bankiersessie.maakOver(100000001, new Money(1200, Money.EURO));
    }

    @Test
    public void testGetRekening() throws InvalidSessionException, RemoteException {
        assertEquals("Rekening klopt onterecht niet.", bank.getRekening(100000001), bankiersessie.getRekening());
        assertNotSame("Rekening is op een of andere manier toch geldig", bank.getRekening(100000002), bankiersessie.getRekening());
    }

    @Test
    public void testLogUit() throws RemoteException {
        bankiersessie.logUit();
    }
}

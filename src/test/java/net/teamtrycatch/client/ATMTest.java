package net.teamtrycatch.client;

import net.teamtrycatch.server.Bank;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocateRegistry.class, ATM.class})
public class ATMTest {
    private Bank mockBank;

    private static final String host = "mock_server";
    private static final int port = 1111;

    @Before
    public void setUp() throws Exception {
        mockBank = mock(Bank.class);

        Registry mockRegistry = mock(Registry.class);
        when(mockRegistry.lookup(anyString())).thenReturn(mockBank);

        PowerMockito.mockStatic(LocateRegistry.class);
        when(LocateRegistry.getRegistry(anyString(), anyInt())).thenReturn(mockRegistry);
    }

    // Testing System.out method is from: https://stackoverflow.com/a/1119559
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    void callClient(String... args) throws Exception {
        String[] argsPlusHost = new String[args.length + 2];
        argsPlusHost[0] = host;
        argsPlusHost[1] = "" + port;
        int i = 2;
        for (String  arg : args) {
            argsPlusHost[i++] = arg;
        }

        ATM.main(argsPlusHost);
    }

    @Test
    public void loginWritesSessionIdToFile() throws Exception {
        final long sessionId = Math.abs(new Random().nextLong());
        final String username = "user123";
        final String password = "pass123";

        when(mockBank.login(username, password)).thenReturn(sessionId);

        callClient("login", username, password);

        assertEquals(sessionId, Long.parseLong(new BufferedReader(new FileReader(".session")).readLine()));
    }

    @Test
    public void operationsUseSessionFromFile() throws Exception {
        final long sessionId = Math.abs(new Random().nextLong());
        final String username = "user123";
        final String password = "pass123";
        final int accountNum = 100;

        when(mockBank.login(username, password)).thenReturn(sessionId);
        when(mockBank.inquiry(accountNum, sessionId)).thenReturn(12345); // If ATM isn't reading sessionId from file, then this version of the mock will not be called

        callClient("login", username, password);
        callClient("inquiry", ""+accountNum);

        assertThat(outContent.toString(), containsString("12345"));

    }
}

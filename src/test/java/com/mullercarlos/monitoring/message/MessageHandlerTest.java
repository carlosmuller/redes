package com.mullercarlos.monitoring.message;

import com.mullercarlos.monitoring.models.ClientModel;
import com.mullercarlos.monitoring.utils.Reflection;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.*;
import java.util.HashMap;

import static com.mullercarlos.monitoring.CONSTANTS.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageHandlerTest {
    Reflection reflection = new Reflection();
    private Socket socket;

    @BeforeEach
    public void setUp() throws IOException {
        OutputStream outputStream = mock(OutputStream.class);
        InputStream inputStream = mock(InputStream.class);

        socket = mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getInputStream()).thenReturn(inputStream);

    }

    @Test
    void MessageHandler__should_build_correctly() throws IOException {
        MessageHandler messageHandler = Mockito.spy(new MessageHandler(socket));
        verify(socket, times(1)).getOutputStream();
        verify(socket, times(1)).getInputStream();
    }

    @Test
    void sendMessage__should_serialize_message_into_json() throws IOException {
        MessageHandler messageHandler = Mockito.spy(new MessageHandler(socket));
        PrintWriter out = mock(PrintWriter.class);
        reflection.setField(messageHandler, "output", out);
        messageHandler.sendMessage(SIGNIN);
        verify(messageHandler.output, times(1)).println(SIGNINJSON);
    }

    @Test
    void receiveMessage__should_serialize_json_into_message() throws IOException {
        MessageHandler messageHandler = Mockito.spy(new MessageHandler(socket));
        BufferedReader input = mock(BufferedReader.class);
        reflection.setField(messageHandler, "input", input);
        when(input.readLine()).thenReturn(SIGNINJSON);

        Message message = messageHandler.receiveMessage();
        verify(messageHandler.input, atLeastOnce()).readLine();
        assertEquals(SIGNIN, message);
    }

    @Test
    void close__should_call_close_of_input_out_put_socket() throws IOException {
        MessageHandler messageHandler = Mockito.spy(new MessageHandler(socket));
        BufferedReader input = mock(BufferedReader.class);
        reflection.setField(messageHandler, "input", input);
        PrintWriter out = mock(PrintWriter.class);
        reflection.setField(messageHandler, "output", out);
        messageHandler.close();
        verify(messageHandler.input, only()).close();
        verify(messageHandler.output, only()).close();
        verify(messageHandler.socket, times(1)).close();
    }

    /**
     * Testes para verificar comunicação correta do SIGNIN
     * @throws IOException
     */

    @Test
    void handle__should_add_client_to_clients_list() throws IOException {
        Inet4Address inet4Address = (Inet4Address) Inet4Address.getLocalHost();
        when(socket.getInetAddress()).thenReturn(inet4Address);

        HashMap<String, ClientModel> clients = new HashMap<>();
        MessageHandler messageHandler = Mockito.spy(new MessageHandler(socket, clients));
        BufferedReader input = mock(BufferedReader.class);
        reflection.setField(messageHandler, "input", input);
        when(input.readLine()).thenReturn(SIGNINJSON);
        Signin signin = SIGNIN;

        messageHandler.handle();
        assertTrue(clients.containsKey(signin.getAuthKey()));

        ClientModel clientModel = clients.get(signin.getAuthKey());
        assertEquals(clientModel.getPort(), signin.getPortListener());
        assertEquals(clientModel.getServiceList(), signin.getServiceList());
        assertEquals(clientModel.getIp(), clientModel.getIp());

        verify(messageHandler, times(1)).sendMessage(new Ok("Consegui te cadstrar com sucesso", signin.getAuthKey()));
    }

    @Test
    void handle__should_block_if_authKey_is_equal_but_clients_is_not() throws IOException {
        Inet4Address inet4Address = (Inet4Address) Inet4Address.getLocalHost();
        when(socket.getInetAddress()).thenReturn(inet4Address);
        Signin signin = SIGNIN;

        HashMap<String, ClientModel> clients = new HashMap<>();
        ClientModel clientModel1 = ClientModel.builder().ip("127.0.0.2").authKey(signin.getAuthKey()).build();
        clients.put(signin.getAuthKey(), clientModel1);
        MessageHandler messageHandler = Mockito.spy(new MessageHandler(socket, clients));
        BufferedReader input = mock(BufferedReader.class);
        reflection.setField(messageHandler, "input", input);
        when(input.readLine()).thenReturn(SIGNINJSON);


        messageHandler.handle();
        assertTrue(clients.containsKey(signin.getAuthKey()));

        ClientModel clientModel = clients.get(signin.getAuthKey());
        assertEquals(clientModel, clientModel1);
        verify(messageHandler, times(1)).sendMessage(new Failed("not allowed"));
    }

    /**
     * FIM Testes para verificar comunicação correta do SIGNIN
     * @throws IOException
     */

    /**
     * Testes para verificar comunicação correta do HEALTH
     * @throws IOException
     */
    @Test
    @SneakyThrows
    void handle__should_send_ok_when_processed_correctly() {
        Inet4Address inet4Address = (Inet4Address) Inet4Address.getLocalHost();
        when(socket.getInetAddress()).thenReturn(inet4Address);
        HashMap<String, ClientModel> clients = new HashMap<>();
        String authKey = "authKey";
        ClientModel clientModel = ClientModel.builder().ip(inet4Address.getHostAddress()).authKey(authKey).build();
        clients.put(authKey, clientModel);

        MessageHandler messageHandler = Mockito.spy(new MessageHandler(socket, clients));
        BufferedReader input = mock(BufferedReader.class);
        reflection.setField(messageHandler, "input", input);
        when(input.readLine()).thenReturn(HEALTHJSON);
        messageHandler.handle();

        verify(messageHandler, times(1)).sendMessage(new Ok("Health updated", authKey));
    }

    @Test
    @SneakyThrows
    void handle__should_block_if_client_is_not_signed_in() {
        HashMap<String, ClientModel> clients = new HashMap<>();

        MessageHandler messageHandler = Mockito.spy(new MessageHandler(socket, clients));
        BufferedReader input = mock(BufferedReader.class);
        reflection.setField(messageHandler, "input", input);
        when(input.readLine()).thenReturn(HEALTHJSON);
        messageHandler.handle();
        verify(messageHandler, times(1)). sendMessage(new Failed("You should send signin first!"));
    }

    @Test
    @SneakyThrows
    void handle__should_block_if_client_has_diferent_ip_from_table() {
        Inet4Address inet4Address = (Inet4Address) Inet4Address.getLocalHost();
        when(socket.getInetAddress()).thenReturn(inet4Address);
        HashMap<String, ClientModel> clients = new HashMap<>();
        String authKey = "authKey";
        ClientModel clientModel = ClientModel.builder().ip("1.1.1.1").authKey(authKey).build();
        clients.put(authKey, clientModel);

        MessageHandler messageHandler = Mockito.spy(new MessageHandler(socket, clients));
        BufferedReader input = mock(BufferedReader.class);
        reflection.setField(messageHandler, "input", input);
        when(input.readLine()).thenReturn(HEALTHJSON);
        messageHandler.handle();

        verify(messageHandler, times(1)).sendMessage(new Failed("not allowed"));

    }
    /**
     * FIM Testes para verificar comunicação correta do HEALTH
     * @throws IOException
     */

}
package com.mullercarlos.monitoring.message;

import com.mullercarlos.monitoring.utils.Reflection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.Socket;

import static com.mullercarlos.monitoring.CONSTANTS.SIGNIN;
import static com.mullercarlos.monitoring.CONSTANTS.SIGNINJSON;
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
    void sendMessage_should_serialize_message_into_json() throws IOException {
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
}
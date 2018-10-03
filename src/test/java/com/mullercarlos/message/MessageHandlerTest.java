package com.mullercarlos.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.Socket;

import static com.mullercarlos.CONSTANTS.SIGNIN;
import static com.mullercarlos.CONSTANTS.SIGNINJSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageHandlerTest {


    @Test
    void sendMessage() throws IOException {
        OutputStream outputStream = mock(OutputStream.class);
        InputStream mock = mock(InputStream.class);

        Socket socket = mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getInputStream()).thenReturn(mock);

        MessageHandler messageHandler = spy(new MessageHandler(socket));
        verify(socket, times(1)).getOutputStream();
        verify(socket, times(1)).getInputStream();

        messageHandler.sendMessage(SIGNIN);
//        verify(messageHandler.output, times(1)).print(SIGNINJSON);
    }

    @Test
    void receiveMessage() {
    }
}
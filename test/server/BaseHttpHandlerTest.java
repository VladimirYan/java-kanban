package server;

import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BaseHttpHandlerTest {

    private HttpExchange exchange;
    private BaseHttpHandler handler;

    @BeforeEach
    public void setUp() {
        exchange = mock(HttpExchange.class);
        handler = new BaseHttpHandler() {
            @Override
            public void handle(HttpExchange exchange) {
            }
        };
    }

    @Test
    public void testSendText() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);
        when(exchange.getResponseHeaders()).thenReturn(new com.sun.net.httpserver.Headers());

        handler.sendText(exchange, 200, "{\"message\":\"OK\"}");

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        assertEquals("{\"message\":\"OK\"}", os.toString());
        verify(exchange).getResponseBody();
        verify(exchange).getResponseHeaders();
    }

    @Test
    public void testSendNotFound() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);
        when(exchange.getResponseHeaders()).thenReturn(new com.sun.net.httpserver.Headers());

        handler.sendNotFound(exchange);

        verify(exchange).sendResponseHeaders(eq(404), anyLong());
        assertEquals("{\"error\":\"Resource not found\"}", os.toString());

        verify(exchange).getResponseBody();
        verify(exchange).getResponseHeaders();
    }

    @Test
    public void testSendHasInteractions() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);
        when(exchange.getResponseHeaders()).thenReturn(new com.sun.net.httpserver.Headers());

        handler.sendHasInteractions(exchange);

        verify(exchange).sendResponseHeaders(eq(406), anyLong());
        assertEquals("{\"error\":\"Task overlaps with existing tasks\"}", os.toString());

        verify(exchange).getResponseBody();
        verify(exchange).getResponseHeaders();
    }
}


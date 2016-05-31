package server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;

public class Server extends JFrame
{
    private JTextArea areaPantalla;
    private int contador = 1;
    private ServerSocket servidor;

    public Server()
    {
        super("Servidor");
        Container contenedor = getContentPane();
        areaPantalla = new JTextArea();
        contenedor.add( new JScrollPane( areaPantalla ), BorderLayout.CENTER );
        setSize( 300, 150 );
        setVisible( true );
    }

    public void ejecutarServidor()
    {
        try
        {
            // Paso 1: crear un objeto ServerSocket.
            servidor = new ServerSocket(12345,100);
            ExecutorService ejecutorSubProcesos = Executors.newCachedThreadPool();

            while(true)
            {
                mostrarMensaje( "Esperando una conexión\n" );
                Socket cliente = servidor.accept();
                Runnable nuevoCliente = new ServidorHilos(cliente,this);
                ejecutorSubProcesos.execute(nuevoCliente);
                contador++;
                mostrarMensaje("Conexión " + contador + " recibida de: " + cliente.getInetAddress().getHostName());
            }
        }
        catch (IOException excepcionES)
        {
            excepcionES.printStackTrace();
        }
    }



    public void mostrarMensaje(String mensajeAMostrar )
    {
        areaPantalla.append( mensajeAMostrar );
    }

    public static void main( String args[] )
    {
        Server aplicacion = new Server();
        aplicacion.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        aplicacion.ejecutarServidor();
    }
}

package server;

import cliente.Cliente;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Title: Proyecto Final
 * Created by:
 * Jhon Sebastian Cano - 201556156
 * <p>
 * 30/05/2016.
 */
public class ServidorHilos implements Runnable
{
    //private JTextArea areaPantalla;
    private Socket cliente;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private Server servidor;

    public ServidorHilos(Socket cliente, Server servidor)
    {
        this.cliente = cliente;
        this.servidor = servidor;

        //Container contenedor = getContentPane();
        //areaPantalla = new JTextArea();
        //contenedor.add( new JScrollPane( areaPantalla ), BorderLayout.CENTER );
        //setSize( 300, 150 );
        //setVisible( true );
    }

    private void obtenerFlujos()
            throws IOException
    {
        salida = new ObjectOutputStream(cliente.getOutputStream());
        salida.flush();
        entrada = new ObjectInputStream(cliente.getInputStream());
        servidor.mostrarMensaje("\nSe establecieron los Flujos de E/S para el cliente -->"
                + Thread.currentThread().getName() + "\n");
    }

    private void enviarDatos(String mensaje)
    {
        try
        {
            salida.writeObject("Servidor>>>" + mensaje);
            salida.flush();
            servidor.mostrarMensaje("\nSe envio la respuesta\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void procesarConexion()
        throws IOException
    {
        String mensaje = "Orden recibida\n";
        enviarDatos(mensaje);

        do {
            try
            {
                mensaje = (String) entrada.readObject();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(mensaje.equals("casa"))
                enviarDatos("Ud envio casa");
            else
                enviarDatos("Ud envio otra cosa");
        }
        while (!mensaje.equals("CLIENTE>>> TERMINAR"));
        enviarDatos("SERVIDOR>>> TERMINAR");
    }

    private void cerrarConexion()
    {
        servidor.mostrarMensaje("El usuario " + Thread.currentThread().getName() + " Cerró la conexion\n");
        try
        {
            salida.close();
            entrada.close();
            cliente.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void ejecutarServidorTrabajador()
    {
            while (true)
            {
                try
                {
                    obtenerFlujos();
                    procesarConexion();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally {
                    cerrarConexion();
                }
            }
    }

    @Override
    public void run()
    {
        ejecutarServidorTrabajador();
    }
}

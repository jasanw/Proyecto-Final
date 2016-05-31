package cliente;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;

public class Cliente extends JFrame implements Runnable
{
    private JTextField campoOrdenes;
    private JTextArea areaPantalla;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private Socket cliente1;


    public Cliente()
    {
        super("Cliente");

        Container contenedor = getContentPane();
        // crear areaPantalla
        areaPantalla = new JTextArea();
        areaPantalla.setEditable(false);
        campoOrdenes = new JTextField(20);

        campoOrdenes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String orden = campoOrdenes.getText();
                enviarDatos(orden);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarConexion();
            }
        });

        contenedor.add(campoOrdenes);
        contenedor.add(new JScrollPane(areaPantalla), BorderLayout.CENTER);
        contenedor.setLayout(new GridLayout(2,1));
        setSize(300, 150);
        setVisible(true);

    }

    private void mostrarMensaje(String mensajeAMostrar)
    {
        areaPantalla.append(mensajeAMostrar);
    }

    private void enviarDatos(String mensaje)
    {
        try
        {
            salida.writeObject(mensaje);
            salida.flush();
            mostrarMensaje("\nSe envio la orden " + mensaje + "\n");
        }
        catch (IOException e)
        {
           areaPantalla.append("\nError al escribir el objeto");
        }
    }

    private void cerrarConexion()
    {
        try
        {
            salida.close();
            entrada.close();
            cliente1.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void conectarAServidor()
        throws IOException
    {
        mostrarMensaje("Intentando realizar la conexion\n");
        cliente1 = new Socket("localhost", 12345);
    }

    private void obtenerFlujos()
        throws IOException
    {
        salida = new ObjectOutputStream(cliente1.getOutputStream());
        salida.flush();
        entrada = new ObjectInputStream(cliente1.getInputStream());
        mostrarMensaje("\nSe establecieron los flujos de E/S\n");
    }

    private void procesarConexion()
        throws IOException
    {
        String mensaje = "Sin asignar";
        do {
            try {
                mensaje = (String) entrada.readObject();
                mostrarMensaje("\n" + mensaje);
            }
            catch (ClassNotFoundException e)
            {
                mostrarMensaje("\nSe recibio un objeto de tipo desconocido");
            }
        } while (!mensaje.equals("SERVIDOR>>> TERMINAR"));
    }

    private void ejecutarCliente()
    {
        try
        {
            conectarAServidor();
            obtenerFlujos();
            procesarConexion();
        }
        catch (EOFException e)
        {
            System.err.println("El cliente termino la conexion");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            cerrarConexion();
        }
    }


    @Override
    public void run() {
        ejecutarCliente();

    }

    public static void main(String args[])
    {
        //Cliente aplicacion = args.length == 0 ? new Cliente("localhost") : new Cliente(args[0]);
        //aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //aplicacion.ejecutarCliente();
        Runnable cliente = new Cliente();
        ExecutorService ejecutor = Executors.newCachedThreadPool();
        ejecutor.execute(cliente);
    }
}

package Puzzle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Stack;

public class puzzle8 extends JFrame {
    private NodoPila<Integer[]> cimaPila;
    private int tamanoPila = 0;
    private JButton[] botones = new JButton[9];
    private int indiceVacio;
    private Stack<Integer[]> pilaDeshacer = new Stack<>();
    private Stack<Integer[]> pilaRehacer = new Stack<>();

    public puzzle8() {
        Integer[] estadoInicial = barajarTablero();
        indiceVacio = buscarEspacioVacio(estadoInicial);
        apilar(estadoInicial);

        setTitle("Rompecabezas Unificado");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 3));

        for (int i = 0; i < 9; i++) {
            botones[i] = new JButton();
            botones[i].setFont(new Font("Arial", Font.PLAIN, 35));
            botones[i].setFocusPainted(false);
            int index = i;
            botones[i].addActionListener(e -> {
                try {
                    moverPieza(index);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            add(botones[i]);
        }

        JButton deshacerBoton = new JButton("Deshacer");
        deshacerBoton.addActionListener(e -> deshacer());
        add(deshacerBoton);

        JButton rehacerBoton = new JButton("Rehacer");
        rehacerBoton.addActionListener(e -> rehacer());
        add(rehacerBoton);

        actualizarTablero();
    }

    // Método para barajar el tablero
    private Integer[] barajarTablero() {
        Integer[] piezas = new Integer[9];
        ArrayList<Integer> numeros = new ArrayList<>();

        for (int i = 0; i <= 8; i++) {
            numeros.add(i);
        }

        Collections.shuffle(numeros);
        for (int i = 0; i <= 8; i++) {
            piezas[i] = (numeros.get(i) != 0 ? numeros.get(i) : null);
        }
        return piezas;
    }

    // Buscar la posición vacía en el tablero
    private int buscarEspacioVacio(Integer[] tablero) {
        for (int i = 0; i < tablero.length; i++) {
            if (tablero[i] == null) {
                return i;
            }
        }
        return 0;
    }

    // Actualizar el tablero con el estado actual
    private void actualizarTablero() {
        Integer[] estadoActual = obtenerTope();
        for (int i = 0; i < 9; i++) {
            botones[i].setText(estadoActual[i] != null ? estadoActual[i].toString() : "");
        }
    }

    // Método para mover la pieza
    private void moverPieza(int indice) throws Exception {
        Integer[] estadoActual = obtenerTope().clone();
        if (esMovible(indice, indiceVacio)) {
            pilaDeshacer.push(estadoActual.clone());
            pilaRehacer.clear();
            estadoActual[indiceVacio] = estadoActual[indice];
            estadoActual[indice] = null;
            indiceVacio = indice;
            apilar(estadoActual.clone());
            actualizarTablero();

            if (verificarVictoria()) {
                JOptionPane.showMessageDialog(this, "¡Has ganado!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Movimiento no permitido");
        }
    }

    // Verificar si la posición es adyacente y se puede mover
    private boolean esMovible(int indice1, int indice2) {
        return (indice1 == indice2 - 1 && indice1 % 3 != 2) ||
               (indice1 == indice2 + 1 && indice2 % 3 != 2) ||
               (indice1 == indice2 - 3) || (indice1 == indice2 + 3);
    }

    // Verificar si se ha alcanzado la configuración de victoria
    private boolean verificarVictoria() {
        Integer[] estadoFinal = {1, 2, 3, 4, 5, 6, 7, 8, null};
        Integer[] estadoActual = obtenerTope();
        for (int i = 0; i < estadoActual.length; i++) {
            if (!Objects.equals(estadoActual[i], estadoFinal[i])) {
                return false;
            }
        }
        return true;
    }

    // Deshacer
    private void deshacer() {
        if (!pilaDeshacer.isEmpty()) {
            Integer[] estadoActual = obtenerTope();
            pilaRehacer.push(estadoActual.clone());
            desapilar();
            Integer[] estadoAnterior = pilaDeshacer.pop();
            apilar(estadoAnterior.clone());
            indiceVacio = buscarEspacioVacio(estadoAnterior);
            actualizarTablero();
        } else {
            JOptionPane.showMessageDialog(this, "No hay más movimientos para deshacer");
        }
    }

    // Rehacer
    private void rehacer() {
        if (!pilaRehacer.isEmpty()) {
            Integer[] estadoActual = obtenerTope();
            pilaDeshacer.push(estadoActual.clone());
            Integer[] estadoSiguiente = pilaRehacer.pop();
            apilar(estadoSiguiente.clone());
            indiceVacio = buscarEspacioVacio(estadoSiguiente);
            actualizarTablero();
        } else {
            JOptionPane.showMessageDialog(this, "No hay más movimientos para rehacer");
        }
    }

    // Nodo para la pila dinámica
    private class NodoPila<E> {
        private E valor;
        private NodoPila<E> siguiente;

        public NodoPila(E valor) {
            this.valor = valor;
        }
    }

    // Métodos para manejar la pila dinámica
    private void apilar(Integer[] item) {
        NodoPila<Integer[]> nuevoNodo = new NodoPila<>(item);
        nuevoNodo.siguiente = cimaPila;
        cimaPila = nuevoNodo;
        tamanoPila++;
    }

    private Integer[] desapilar() {
        if (estaVacia()) {
            throw new RuntimeException("La pila está vacía");
        }
        Integer[] item = cimaPila.valor;
        cimaPila = cimaPila.siguiente;
        tamanoPila--;
        return item;
    }

    private boolean estaVacia() {
        return cimaPila == null;
    }

    private Integer[] obtenerTope() {
        if (estaVacia()) {
            throw new RuntimeException("La pila está vacía");
        }
        return cimaPila.valor;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            puzzle8 juego = new puzzle8();
            juego.setVisible(true);
        });
    }
}
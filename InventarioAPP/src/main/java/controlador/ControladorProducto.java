/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import modelo.Producto;
import modelo.RepositorioProducto;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import vista.Principal;

/**
 *
 * @author Lucas
 */
public class ControladorProducto implements ActionListener{
    
    RepositorioProducto repoProducto;
    Principal vista;
    DefaultTableModel defaultTableModel;
    
    private int codigo;
    private String nombre;
    private double precio;
    private int inventario;

//---------------------------------------------------------------------------------------------------------------------------    

    public ControladorProducto() {
        super();
    }

    public ControladorProducto(RepositorioProducto repoProducto, Principal vista) {
        super();
        this.repoProducto = repoProducto;
        this.vista = vista;
        vista.setVisible(true);
        agregarEventos();
        listarTabla();
    }
    
    //---------------------------------------------------------------------------------------------------------------------------    

    private void agregarEventos(){
        vista.getBtnAgregar().addActionListener(this);
        vista.getBtnActualizar().addActionListener(this);
        vista.getBtnBorrar().addActionListener(this);
        vista.getBtnInforme().addActionListener(this);
        vista.getTblTabla().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                llenarCampos(e);
            }
        });
    }
    
    public void listarTabla (){
        String[] titulos = new String[]{"id", "Nombre", "Precio", "Inventario"};
        defaultTableModel = new DefaultTableModel(titulos, 0);
        List<Producto> listaProductos = (List<Producto>) repoProducto.findAll();
        for (Producto producto : listaProductos) {
            defaultTableModel.addRow(new Object[]{producto.getCodigo(), producto.getNombre(), producto.getPrecio(), producto.getInventario()});
        }
        vista.getTblTabla().setModel(defaultTableModel);
        vista.getTblTabla().setPreferredSize(new Dimension(350, defaultTableModel.getRowCount()* 16));
    }
    
    private void llenarCampos(MouseEvent e){
        JTable target = (JTable) e.getSource();
        vista.getTxtCodigo().setText(vista.getTblTabla().getModel().getValueAt(target.getSelectedRow(), 0).toString());
        vista.getTxtNombre().setText(vista.getTblTabla().getModel().getValueAt(target.getSelectedRow(), 1).toString());
        vista.getTxtPrecio().setText(vista.getTblTabla().getModel().getValueAt(target.getSelectedRow(), 2).toString());
        vista.getTxtPrecio().setText(vista.getTblTabla().getModel().getValueAt(target.getSelectedRow(), 3).toString());
    }
    
    //---------------------------------------------------------------------------------------------------------------------------  
    
    
    //Validar Campos vacios
    private boolean validarDatos(){
        if ("".equals(vista.getTxtNombre().getText()) || "".equals(vista.getTxtPrecio().getText()) || "".equals(vista.getTxtInventario().getText()) ) {
            JOptionPane.showMessageDialog(null, "Los campos no pueden ser vacios", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    
    //Cargar los datos y para validar si precio e inventario son numericos
    private boolean cargarDatos(){
        try {
            codigo = Integer.parseInt("".equals(vista.getTxtCodigo().getText()) ? "0" : vista.getTxtCodigo().getText());
            nombre = vista.getTxtNombre().getText();
            precio = Double.parseDouble(vista.getTxtPrecio().getText());
            inventario = Integer.parseInt(vista.getTxtInventario().getText());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void limpiarCampos(){
        vista.getTxtCodigo().setText("");
        vista.getTxtNombre().setText("");
        vista.getTxtPrecio().setText("");
        vista.getTxtInventario().setText("");
    }
    
    //---------------------------------------------------------------------------------------------------------------------------
    
    private void agregarProducto(){
        try {
            if (validarDatos()) {
                if (cargarDatos()) {
                    Producto producto = new Producto(nombre, precio, inventario);
                    repoProducto.save(producto);
                    JOptionPane.showMessageDialog(null, "Producto agregado con exito");
                    limpiarCampos();    
                } else {
                    JOptionPane.showMessageDialog(null, "Los campos PRECIO e INVENTARIO deben ser numericos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (DbActionExecutionException e) {
            JOptionPane.showMessageDialog(null, "El producto ya existe");
        } finally {
            listarTabla();
        }
    }
    
    private void actualizarProducto(){
        try {
            if (validarDatos()) {
                if (cargarDatos()) {
                    Producto producto = new Producto(codigo, nombre, precio, inventario);
                    repoProducto.save(producto);
                    JOptionPane.showMessageDialog(null, "Producto actualizado con exito");
                    limpiarCampos();    
                } else {
                    JOptionPane.showMessageDialog(null, "Los campos PRECIO e INVENTARIO deben ser numericos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (DbActionExecutionException e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error, el producto ya existe");
        } finally {
            listarTabla();
        }
    }
    
    private void borrarProducto(){
        try {
            if (validarDatos()) {
                if (cargarDatos()) {
                    Producto producto = new Producto(codigo, nombre, precio, inventario);
                    repoProducto.delete(producto);
                    JOptionPane.showMessageDialog(null, "Producto borrado con exito");
                    limpiarCampos();    
                } else {
                    JOptionPane.showMessageDialog(null, "El producto no existe", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (DbActionExecutionException e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error");
        } finally {
            listarTabla();
        }
    }
    
    //---------------------------------------------------------------------------------------------------------------------------   
    
    private void generarInforme(){
        String nombreMayor = precioMayor();
        String nombreMenor = precioMenor();
        String promedio = precioPromedio();
        String total = totalInventario();
        JOptionPane.showMessageDialog(null, nombreMayor + " " + nombreMenor + " " + promedio + " " + total);
    }
    
    private String precioMayor(){
        String nombre = "";
        double precioAux = 0;
        List<Producto> listaProductos = (List<Producto>) repoProducto.findAll();
        for (Producto producto : listaProductos) {
            if(producto.getPrecio() > precioAux){
                nombre = producto.getNombre();
                precioAux = producto.getPrecio();
            }
        }
        return nombre;
    }
    
    private String precioMenor(){
        String nombre = "";
        double precioAux = 10000000;
        List<Producto> listaProductos = (List<Producto>) repoProducto.findAll();
        for (Producto producto : listaProductos) {
            if(producto.getPrecio() < precioAux){
                nombre = producto.getNombre();
                precioAux = producto.getPrecio();
            }
        }
        return nombre;
    }
    
    private String precioPromedio(){
        double suma = 0;
        double resultado = 0;
        List<Producto> listaProductos = (List<Producto>) repoProducto.findAll();
        for (Producto producto : listaProductos) {
            suma += producto.getPrecio();
        }
        resultado = suma / listaProductos.size();
        return String.format("%.2f", resultado);
    }
        
    private String totalInventario(){
        double suma = 0;
        double resultado = 0;
        List<Producto> listaProductos = (List<Producto>) repoProducto.findAll();
        for (Producto producto : listaProductos) {
            suma = producto.getPrecio() * producto.getInventario();
            resultado += suma;
        }
        return String.format("%.2f", resultado);
    }    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == vista.getBtnAgregar()) {
            agregarProducto();
        }
        if (ae.getSource() == vista.getBtnActualizar()) {
            actualizarProducto();
        }
        if (ae.getSource() == vista.getBtnBorrar()) {
            borrarProducto();
        }
        if (ae.getSource() == vista.getBtnInforme()) {
            generarInforme();
        }
    }
}

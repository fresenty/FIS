/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
/**
 *
 * @author Brayan
 */
@WebServlet(name = "Validacion", urlPatterns = {"/Validacion"})
public class Validacion extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            Usuarios u1 = new Usuarios();
            u1.setIdUsuario(1);
            u1.setUsrName("admin");
            u1.setUsrPass("123");
            UsuariosJpaController dao;
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("orm2PU");
            dao = new ClienteJpaController(emf);
            String user = request.getParameter("username");
            String password = request.getParameter("pass");;
            Cifrador c1 = new Cifrador();
            try {
                if(user.equals("admin")&&c1.hash(password).equals(c1.hash("123"))){
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>SisFin | Panel Admin</title>");    
                    out.println("<link rel=\"Stylesheet\" href=\"css/estilos.css\">");
                    out.println("</head>");
                    out.println("<body>");                   
                    out.println("<h1>Bienvenido Administrador</h1>");
                    out.println("</body>");
                    out.println("</html>");                    
                }else{
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>SisFin | Error</title>");    
                    out.println("<link rel=\"Stylesheet\" href=\"css/estilos.css\">"); 
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<h1>Credenciales erroneas</h1>");
                    out.println("</body>");
                    out.println("</html>");                   
                }
            } catch (Exception ex) {
                Logger.getLogger(Validacion.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

package com.basamadco.opxi.callmanager.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jun 28, 2008
 *         Time: 3:13:36 PM
 */
public class ConvergedHttpServlet extends HttpServlet {

//    private SipFactory _sipFactory;

//    public void init() throws ServletException {
//        _sipFactory = (SipFactory) getServletContext().getAttribute( SipServlet.SIP_FACTORY );
//
//        if (_sipFactory == null) {
//            System.out.println("ConvergedHttpException: No SipFactory in context...");
//        } else {
//            System.out.println( "ConvergedHttpServlet: Loaded SipFactory: " + _sipFactory );
//        }

//    }

    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.getWriter().print("Converged Application Test 1: " + getServletContext().getAttribute( SipServlet.SIP_FACTORY ) );
        resp.getWriter().print("Converged Application Test 2: " + getServletContext().getAttribute( "com.basamadco.opxi.callmanager.ServiceFactory" ) );
    }
}

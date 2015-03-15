package pl.orange.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;


public class QueueServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();


        Map<String, String[]> parameterMap = request.getParameterMap();
        for(Map.Entry<String,String[]> p : parameterMap.entrySet()) {

            out.println(p.getKey());
            for(String s: Arrays.asList(p.getValue())) {
                out.print(s + " ");
            }
        }
        out.println("HelloWorld");
        out.close();

    }
}

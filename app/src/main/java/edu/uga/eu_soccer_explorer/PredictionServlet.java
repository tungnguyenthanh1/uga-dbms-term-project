package edu.uga.eu_soccer_explorer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Entry point of the servlet Handles all server side requests
 * 
 * @author Piyush Subedi
 * @extended-by Andrew
 */
@WebServlet(name = "PredictionServlet", urlPatterns = { "/predictor" }, loadOnStartup = 1)
public class PredictionServlet extends HttpServlet {

    /**
     * Serialization identifier
     */
    private static final long serialVersionUID = 1L;

    final String MAIN_PAGE = "predictor.jsp"; 

    private PageParams pageParams = null;

    @Override
    public void init() throws ServletException {
        super.init();

        pageParams = new PageParams();
        try {
            pageParams.fetch();
        } catch (ClassNotFoundException | NoSuchElementException | SQLException e) {
            e.printStackTrace();
            super.destroy();
        }
    }

    /**
     * Handles initial page load request Directs the user to index.jsp after setting
     * up the necessary bootstrap parameters
     * 
     * @author Andrew
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // send params as request parameter
        request.setAttribute("params", pageParams);

        request.getRequestDispatcher(MAIN_PAGE).forward(request, response);
    }

    /**
     * Method handles post request (search) from the server
     * 
     * @author Andrew
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String result = "0";
        try {

            DBResult results = SoccerDB.makePrediction(request.getParameterMap());

            if (results != null) {
                if (results.getRows().size() > 0) {
                    if (results.getRows().get(0).get(0).toString().equalsIgnoreCase("Team 1 victory")) {
                        result = "1";
                    } else if (results.getRows().get(0).get(0).toString().equalsIgnoreCase("Team 2 victory")) {
                        result = "2";
                    }
                }
            }

            request.setAttribute("result", result);

        } catch (SQLException | ClassNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
        }

        response.getWriter().write(result); // ajax request from jsp, therefore not forwarding
        // request.getRequestDispatcher(MAIN_PAGE).forward(request, response);
    }

}
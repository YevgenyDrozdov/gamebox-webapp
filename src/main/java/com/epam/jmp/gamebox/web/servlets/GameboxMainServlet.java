package com.epam.jmp.gamebox.web.servlets;

import com.epam.jmp.gamebox.*;
import com.epam.jmp.gamebox.deploy.*;
import com.epam.jmp.gamebox.impl.*;
import com.epam.jmp.gamebox.war.deploy.WarGameDeployAssistant;
import com.epam.jmp.gamebox.war.deploy.WarXmlDeploymentDescriptorLocator;
import com.epam.jmp.gamebox.war.loader.WarGameLoader;
import com.epam.jmp.gamebox.web.action.dispatcher.ActionDispatcher;
import com.epam.jmp.gamebox.web.action.handler.ActionHandler;
import com.epam.jmp.gamebox.web.action.dispatcher.RESTActionDispatcher;
import com.epam.jmp.gamebox.web.action.handler.GameActionRouterActionHandler;
import com.epam.jmp.gamebox.web.action.handler.GetGameMiniatureActionHandler;
import com.epam.jmp.gamebox.web.action.handler.StartGameActionHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "GameboxMainServlet", urlPatterns = "/rest/*")
public class GameboxMainServlet extends HttpServlet {

    private ActionDispatcher dispatcher;

    public GameboxMainServlet() {
        initializeActionDispatcher();
        GameboxContext.getInstance().getGameService().refreshDeployments();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatcher.dispatch(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    private void initializeActionDispatcher() {
        RESTActionDispatcher restDispatcher = new RESTActionDispatcher();
        restDispatcher.mapAction("/", new ActionHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response) {
                Map<String, Game> games = GameboxContext.getInstance().getGameService().getAllDeployedGames();

                RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsp/gameList.jsp");
                try {
                    request.setAttribute("games", games.entrySet());
                    requestDispatcher.forward(request, response);

                } catch (ServletException e) {
                } catch (IOException e) {
                }
            }
        });

        restDispatcher.mapAction("/action/startGame/{" + StartGameActionHandler.GAME_ID_PARAMETER_NAME + "}",
                new StartGameActionHandler(GameboxContext.getInstance().getGameService()));

        restDispatcher.mapAction("/{" + GameActionRouterActionHandler.GAME_ID_PARAMETER_NAME + "}/action/{" +
                GameActionRouterActionHandler.ACTION_ID_PARAMETER_NAME + "}", new GameActionRouterActionHandler());

        restDispatcher.mapAction("/miniature/{" + GetGameMiniatureActionHandler.GAME_ID_PARAMETER_NAME + "}",
                new GetGameMiniatureActionHandler());


        dispatcher = restDispatcher;
    }


    /*
        Map<String, GameDescriptor> gameDescriptors = repository.getAllGames();

        PrintWriter out = resp.getWriter();

        out.print("<ul>");
        for (Map.Entry<String, GameDescriptor> entry : gameDescriptors.entrySet()) {

            String gameId = entry.getKey();
            GameDescriptor descriptor = entry.getValue();

            out.print("<li>");
            out.print(gameId + ": " + descriptor.getGameName() + "[" + descriptor.getControllerClass() + "]");
            out.print("</li>");
        }
        out.print("</ul>");

        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
    * */
}

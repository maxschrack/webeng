
<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:useBean id="game" class="at.ac.tuwien.big.we14.lab2.api.impl.SimpleGame" scope="session"/>
<jsp:useBean id="player1" class="at.ac.tuwien.big.we14.lab2.api.impl.SimplePlayer" scope="session"/>
<jsp:useBean id="player2" class="at.ac.tuwien.big.we14.lab2.api.impl.SimpleGameComputer" scope="session"/>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de">
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Business Informatics Group Quiz - Zwischenstand</title>
        <link rel="stylesheet" type="text/css" href="style/screen.css" />
        <script src="js/jquery.js" type="text/javascript"></script>
        <script src="js/framework.js" type="text/javascript"></script>
    </head>
    <body id="winnerpage">
        <a class="accessibility" href="#roundwinner">Zur Rundenwertung springen</a>
        <header role="banner" aria-labelledby="mainheading"><h1 id="mainheading"><span class="accessibility">Business Informatics Group</span> Quiz</h1></header>
        <nav role="navigation" aria-labelledby="navheading">
            <h2 id="navheading" class="accessibility">Navigation</h2>
            <ul>
                <li><a id="logoutlink" title="Klicke hier um dich abzumelden" href="#" accesskey="l">Abmelden</a></li>
            </ul>
        </nav>
        
        <section role="main">
            <!-- winner message -->
            <section id="roundwinner" aria-labelledby="roundwinnerheading">
                <h2 id="roundwinnerheading" class="accessibility">Rundenzwischenstand</h2>
                <p class="roundwinnermessage"><%=request.getAttribute("winnerMessage")%></p>
            </section>
        
            <!-- round info -->    
            <section id="roundinfo" aria-labelledby="roundinfoheading">
                <h2 id="roundinfoheading" class="accessibility">Spielerinformationen</h2>
                <% int questionNr = game.getQuestionNr(); %>
                <div id="player1info" class="playerinfo">
                    <span id="player1name" class="playername"><%=player1.getName()%></span>
                    <ul class="playerroundsummary">
                    <%for(int i=0; i<3;i++){ %>
                    	<% if(i>=questionNr){ %>
                    		<li><span class="accessibility">Frage <%=i%>:</span><span id="player1answer<%=i%>" class="unknown">Unbekannt</span></li>
                    	<%}else if(game.isQuestionCorrectPlayer1(i)==true){ %>
                        	<li><span class="accessibility">Frage <%=i%>:</span><span id="player1answer<%=i%>" class="correct">Richtig</span></li>
                        <%}else{ %>
                        	<li><span class="accessibility">Frage <%=i%>:</span><span id="player1answer<%=i%>" class="incorrect">Falsch</span></li>
                    	<%} %>
                    <%} %>
                    </ul>
                     <p id="player1roundcounter" class="playerroundcounter">Gewonnene Runden: <span id="player1wonrounds" class="playerwonrounds"><%=game.getPlayer1Rounds()%></span></p>
                </div>
                <div id="player2info" class="playerinfo">
                    <span id="player2name" class="playername"><%=player2.getName()%></span>
                    <ul class="playerroundsummary">
                       <%for(int i=0; i<3;i++){ %>
                    	<% if(i>=questionNr){ %>
                    		<li><span class="accessibility">Frage <%=i%>:</span><span id="player2answer<%=i%>" class="unknown">Unbekannt</span></li>
                    	<%}else if(game.isQuestionCorrectPlayer2(i)==true){ %>
                        	<li><span class="accessibility">Frage <%=i%>:</span><span id="player2answer<%=i%>" class="correct">Richtig</span></li>
                        <%}else{ %>
                        	<li><span class="accessibility">Frage <%=i%>:</span><span id="player2answer<%=i%>" class="incorrect">Falsch</span></li>
                    	<%} %>
                    <%} %>
                    </ul>
                     <p id="player2roundcounter" class="playerroundcounter">Gewonnene Runden: <span id="player2wonrounds" class="playerwonrounds"><%=game.getPlayer2Rounds()%></span></p>
                </div>
                <a id="next" href="BigQuizServlet?action=roundcomplete">Weiter</a>
            </section>
        </section>

        <!-- footer -->
        <footer role="contentinfo">© 2014 BIG Quiz</footer>
    </body>
</html>

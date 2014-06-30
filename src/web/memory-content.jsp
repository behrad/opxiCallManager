            <table>
                <tr>
                    <td valign="top">
                        <ul>
                            <%
                                String gcMode = request.getParameter( "gc" );
                                if( gcMode != null ) {
                                    Runtime.getRuntime().gc();
                                }
                                String appServerName = application.getServerInfo();
                                boolean isWSAS = false;
                                if (appServerName.indexOf("IBM") > -1) {
                                    isWSAS = true;
                                }
                            %>
                            <%
                                if (isWSAS) {
                            %>
                            <br>
                            <li><a href="services.jsp">Service Internals Monitor</a></li>
                            <br>
                            <li><a href="locks.jsp">Lock Manager Monitor</a></li>
                            <br>
                            <li><a href="callRepository.jsp">Call Factory Monitor</a></li>
                            <br>
                            <li><a href="sipSessions.jsp">SipSession Manager Monitor</a></li>
                            <br>
                            <br>
                            <br>
                            <li class="mainText">Current JVM Memory: <%= Runtime.getRuntime().totalMemory()/1024/1024 %>Mb</li>
                            <li class="mainText">Free JVM Memory: <%= Runtime.getRuntime().freeMemory()/1024/1024 %>Mb</li>
                            <li class="mainText">Maximum JVM Memory: <%= Runtime.getRuntime().maxMemory()/1024/1024 %>Mb</li>
                            <br>
                            <li><a href="memory.jsp?gc=on">Run Garbage Collector</a></li>
                            <br>
                            <%
                            } else {
                            %>
                            <%
                                }
                            %>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td class="mainText">
                        <b>Deployed Application Name:</b> <%= application.getServletContextName() %>
                    </td>
                </tr>
                <tr>
                    <td class="mainText">
                        <b>Hosted On:</b> <%=application.getServerInfo() %>
                    </td>
                </tr>
            </table>
package com.server.webduino.servlet;

import com.quartz.QuartzListener;
import com.server.webduino.core.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
//@WebServlet(name = "SensorServlet")
public class ActuatorServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ActuatorServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String id = request.getParameter("id");

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        Core core = (Core)getServletContext().getAttribute(QuartzListener.CoreClass);

        if (id != null) {

            Actuator actuator = core.getActuatorFromId(Integer.valueOf(id));
            JSONObject json = actuator.getJson();
            out.print(json.toString());

        } else {

            ArrayList<Actuator> list = core.getActuators();
            //create Json Object
            JSONArray jsonarray = new JSONArray();
            Iterator<Actuator> iterator = list.iterator();
            while (iterator.hasNext()) {
                Actuator actuator = iterator.next();
                JSONObject json = actuator.getJson();
                jsonarray.put(json);
            }
            out.print(jsonarray.toString());
        }

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //questa servlet riceve command dalla app, dalle pagine wed e riceve status update dagli actuator diorettamente

        StringBuffer jb = new StringBuffer();
        String line = null;
        int id;

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Core core = (Core)getServletContext().getAttribute(QuartzListener.CoreClass);

        try {
            JSONObject json = new JSONObject(jb.toString());

            id = json.getInt("id");
            String command = json.getString("command");


            if (command.equals("status")) { // receive status update

                out.print(json.toString());

                updateActuator(id,json);

                return;


            } else if (command.equals("start")) {
                int duration = json.getInt("duration");
                double temperature = json.getDouble("target");
                int sensor = json.getInt("sensor");
                boolean localSensor;
                if (sensor == 0)
                    localSensor = true;
                else
                    localSensor = false;
                HeaterActuator actuator = (HeaterActuator) core.getActuatorFromId(id);
                //actuator.setActiveSensorID(sensor);
                boolean res = actuator.sendCommand(Actuator.Command_Manual_Start, duration, temperature, localSensor, 0, 0, sensor,0);

                /*if (res == true) {
                    response.setStatus(HttpServletResponse.SC_OK);

                    JSONObject jsn = actuator.getJson();
                    //out.print(jsn.toString());
                } else {

                }*/


            } else if (command.equals("stop")) {
                HeaterActuator actuator = (HeaterActuator) core.getActuatorFromId(id);
                actuator.sendCommand(Actuator.Command_Manual_Stop, 0, 0, false, 0, 0, 0, 0);
            } else {
                LOGGER.severe("command not found");
            }
            //Program program = getProgramFromJson(jb);
            //lastid = Core.updatePrograms(program);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            LOGGER.severe("BAD REQUEST");
            return;
        }



        JSONObject json = new JSONObject();

        try {
            response.setStatus(HttpServletResponse.SC_OK);

            json.put("answer", "success");
            json.put("id", id);

            Actuator actuator = core.getActuatorFromId(id);
            JSONObject actuatorJson = actuator.getJson();

            json.put("actuator", actuatorJson.toString());

            // finally output the json string
            out.print(json.toString());

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    private void updateActuator(int id, JSONObject json) throws JSONException {

        LOGGER.info("SensorServlet:updateActuator - start");

        /*double avtemperature = json.getDouble("avtemperature");
        String status = json.getString("status");
        Boolean relestatus = json.getBoolean("relestatus");*/

        new UpdateActuatorThread(getServletContext(),id,json/*,status,relestatus,avtemperature*/).start();

        LOGGER.info("SensorServlet:updateActuator - end");
    }
}
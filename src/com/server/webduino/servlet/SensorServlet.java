package com.server.webduino.servlet;

import com.quartz.QuartzListener;
import com.server.webduino.core.*;
//import com.server.webduino.core.SensorData;
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
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
//@WebServlet(name = "SensorServlet")
public class SensorServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SensorServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LOGGER.info("SensorServlet:doPost");
        //String registershieldParam = request.getParameter("registershield");

        StringBuffer jb = new StringBuffer();
        String line = null;

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        //create Json Response Object
        JSONObject jsonResponse = new JSONObject();

        try {
            JSONObject jsonObj = new JSONObject(jb.toString());

            LOGGER.info("SensorServlet:doPost" + jb.toString());

            updateShieldSensorsStatus(jsonObj/*, url*/);

        } catch (JSONException e) {
            try {
                jsonResponse.put("result", "error");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        // put some value pairs into the JSON object .
        try {
            jsonResponse.put("result", "success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // finally output the json string
        out.print(jsonResponse.toString());
    }

    private void updateShieldSensorsStatus(JSONObject jsonObj/*, URL url*/) throws JSONException {
        Date lastupdate = Core.getDate();

        if (jsonObj.has("id")) {
            int shieldid = jsonObj.getInt("id");
            if (jsonObj.has("sensors")) {
                JSONArray jsonArray = jsonObj.getJSONArray("sensors");
                updateSensors(shieldid, lastupdate, jsonArray);
            }
        }

    }

    /*private void registerShield(JSONObject jsonObj, URL url) throws JSONException {

        int id;

        String MACAddress = "";
        String boardName = "";
        Date lastupdate = Core.getDate();

        Shield shield = new Shield(jsonObj);

        new RegisterShieldThread(shield).start();
    }*/


    private void updateSensors(int shieldid, Date lastupdate, JSONArray jsonArray) {

        LOGGER.info("SensorServlet:updateSensor - start");
        new UpdateSensorsThread(getServletContext(), shieldid, lastupdate, jsonArray).start();


        LOGGER.info("SensorServlet:updateSensor - end");
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");
        String shieldParam = request.getParameter("shield");

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        Core core = (Core) getServletContext().getAttribute(QuartzListener.CoreClass);

        //create Json Object
        JSONArray jsonarray = new JSONArray();

        if (id != null) {

            SensorBase sensor = core.getSensorFromId(Integer.valueOf(id));
            JSONObject json = sensor.getJson();
            out.print(json.toString());

        } else if (shieldParam != null) { // todo eliminare

            Shields shields = new Shields();
            List<Shield> list = shields.getShields();

            JSONArray jarray = core.getShieldsJsonArray();

            JSONObject jshields = new JSONObject();
            try {
                jshields.put("shields", jarray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            out.print(jshields.toString());

        } else {

            List<SensorBase> list = core.getLastSensorData();


            for (SensorBase sensor : list) {
                JSONObject json = sensor.getJson();
                jsonarray.put(json);
            }

            // finally output the json string
            out.print(jsonarray.toString());
        }
    }
}

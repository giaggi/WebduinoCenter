package com.server.webduino.servlet;

import com.server.webduino.core.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * Created by Giacomo Span� on 08/11/2015.
 */
public class DataLogServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        String idParamValue = request.getParameter("id");
        String activeParamValue = request.getParameter("active");

        String endDateStr = request.getParameter("enddate");
        String elapsedStr = request.getParameter("elapsed");
        int elapsed = 0;

        if (idParamValue != null && endDateStr != null && elapsedStr != null) {

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date end;
            try {
                end = df.parse(endDateStr);
                elapsed = Integer.valueOf(elapsedStr);

            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            //Date end = Core.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(end);
            //calendar.add(Calendar.HOUR, -3);
            calendar.add(Calendar.MINUTE, -1  * elapsed);
            Date start = calendar.getTime();

            ArrayList<PointDouble> serie = new ArrayList<PointDouble>();
            HeaterDataLog dl = new HeaterDataLog();
            ArrayList<DataLog> list = dl.getDataLog(Integer.valueOf(idParamValue), start, end);

            LinearInterpolator interpolator = new LinearInterpolator();
            //ArrayList<DataLog> interpolateData = interpolator.getInterpolatedData(list, start, end, Duration.ofSeconds(60*600/elapsed));

            JSONArray jsonarray = new JSONArray();
            for (DataLog data : list/*interpolateData*/) {
                    JSONObject json = ((HeaterDataLog) data).getJson();
                    jsonarray.put(json);
            }
            out.print(jsonarray.toString());
        }
    }

    /*private JSONObject getJsonFromSensorData(TemperatureSensorDataLog dataLog) throws JSONException {

        JSONObject json = new JSONObject();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy ", Locale.ENGLISH);
        String dateStr = df.format(dataLog.date);
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss");
        //dateStr += dt.format(dataLog.time);

        json.put("date", dateStr);
        json.put("temperature", dataLog.temperature);
        json.put("avtemperature", dataLog.avTemperature);

        return json;
    }
*/
    /*private JSONObject getJsonFromHeaterData(HeaterDataLog dataLog) throws JSONException {

        JSONObject json = new JSONObject();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy ", Locale.ENGLISH);
        String dateStr = df.format(dataLog.date);
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss");
        dateStr += dt.format(dataLog.time);

        json.put("date", dateStr);
        json.put("local", dataLog.localTemperature);
        json.put("remote", dataLog.remoteTemperature);
        json.put("target", dataLog.targetTemperature);
        json.put("relestatus", dataLog.releStatus);
        json.put("status", dataLog.status);
        json.put("program", dataLog.activeProgram);
        json.put("timerange", dataLog.activeTimerange);

        return json;
    }*/
}

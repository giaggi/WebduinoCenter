var sensorServletPath = "../sensor";
var actuatorServletPath = "../actuator";
var programServletPath = "../program";
var $mSensorPanel;
var $mSensorRow;
var $actuators;
var $activeProgram;

function onCommandButtonClicked() {

    //url = $(this).parent().parent().find('td[name="url"]').text();
    url = actuatorServletPath;
    //url += '?id=1';



    var command = {
        id: 1,
        command: 'start',
        duration: 30,
        sensor: 0,
        target: 22.0
    };

    //var json = JSON.stringify(command);

    $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(command),
            //contentType: "application/json; charset=utf-8",
            dataType: "json"
        })
        .done(function( msg ) {
            alert( "Data Saved: " + msg );
            loadActuators()
        });

    /*$.ajax({
        type: "POST",
        url: url,
        data: JSON.stringify(text),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        processData: true,
        success: function (data, status, jqXHR) {

            //callback(data, form);
        },
        error: function (xhr) {
            alert(xhr.responseText);
        }
    });*/

}

function loadSensors() {
    $.getJSON(sensorServletPath, function (data) {
            console.log("success");

            tr = $mSensorPanel.find('tr[name="sensor"]');
            while (tr.size() > 1) {
                tr.last().remove();
            }

            a = data;
            $.each(a, function (idx, elem) {

                tr = $mSensorPanel.find('tr[name="sensor"]');
                //trh = $mSensorPanel.find('tr[name="sensorheader"]');
                //$mSensorPanel.find('tr[name="sensor"]').remove();
                //if (idx > 0) {
                    //newtr = tr.clone();
                    newtr = $mSensorRow.clone();
                //} else {
                    //newtr = tr;
                //}
                newtr.find('td[name="id"]').text(elem.id);
                newtr.find('td[name="date"]').text(elem.lastupdate);
                newtr.find('td[name="temperature"]').text(elem.temperature + "°C");
                newtr.find('td[name="avtemperature"]').text(elem.avtemperature + "°C");
                newtr.find('td[name="url"]').text(elem.url);
                newtr.find('td[name="name"]').text(elem.name);

                tr.last().after(newtr);
                //trh.last().after(newtr);
            });
        })
        .done(function () {
            console.log("succes");
        })
        .fail(function () {
            console.log("error1");
            alert("error1");
        })
        .always(function () {
            console.log("error2");
        });
}
function loadActuators() {
    $.getJSON(actuatorServletPath, function (data) {
            console.log("success");

            a = data;
            $.each(a, function (idx, elem) {

                tr = $actuators.find('tr[name="actuator"]');
                if (idx > 0) {
                    newtr = tr.clone();
                } else {
                    newtr = tr;
                }
                newtr.find('td[name="id"]').text(elem.id);
                newtr.find('td[name="date"]').text(elem.lastupdate);
                newtr.find('td[name="temperature"]').text(elem.temperature + "°C");
                newtr.find('td[name="avtemperature"]').text(elem.avtemperature + "°C");

                newtr.find('td[name="remote"]').text(elem.remotetemperature + "°C");
                newtr.find('td[name="target"]').text(elem.target + "°C");
                if(elem.localsensor)
                    newtr.find('td[name="sensor"]').text("Locale");
                else
                    newtr.find('td[name="sensor"]').text("Remoto");

                newtr.find('td[name="url"]').text(elem.url);
                newtr.find('td[name="name"]').text(elem.name);
                if(elem.relestatus)
                    newtr.find('td[name="relestatus"]').text("Acceso");
                else
                    newtr.find('td[name="relestatus"]').text("Spento");

                if (elem.status == "program")
                    newtr.find('td[name="status"]').text(elem.status + " " + elem.program + "." + elem.timerange);
                else
                    newtr.find('td[name="status"]').text(elem.status);


                newtr.find('td[name="duration"]').text(elem.duration);
                newtr.find('td[name="remaining"]').text(elem.remaining);
                newtr.find('button[name="commandbutton"]').text("on");
                newtr.find('button[name="commandbutton"]').click(onCommandButtonClicked);
                tr.last().after(newtr);
            });
        })
        .done(function () {
            console.log("succes");
        })
        .fail(function () {
            console.log("error1");
            alert("error1");
        })
        .always(function () {
            console.log("error2");
        });
}
function loadActiveProgramList() {

    loadActiveProgram();

    $.getJSON(programServletPath + '?next=true', function (data) {
            console.log("success");

            tr = $activeProgram.find('tr[name="activeprogram"]');
            a = data;
            $.each(a, function (idx, elem) {


                //newtr = tr.clone();
                if (idx > 0) {
                    last = newtr;
                    newtr = tr.clone();
                    last.last().after(newtr);
                } else {
                    newtr = tr;
                    //tr.last().before(newtr);
                }

                newtr.find('td[name="lastupdate"]').text(idx);

                newtr.find('td[name="id"]').text(elem.id);
                newtr.find('td[name="name"]').text(elem.name);
                newtr.find('td[name="timerange"]').text(elem.timerangeid + " " + elem.timerangename);
                newtr.find('td[name="start"]').text(elem.startdate);
                newtr.find('td[name="end"]').text(elem.enddate);
                newtr.find('td[name="temperature"]').text(elem.temperature);
                newtr.find('td[name="sensor"]').text("#" + elem.sensor );
                //newtr.find('td[name="sensor"]').text("#" + elem.sensor + " " + elem.sensorname + " (" + elem.sensortemperature + "°C)");
                //newtr.find('td[name="endtime"]').text(elem.startdate);
                //newtr.find('td[name="temperature"]').text(elem.temperature);
                //newtr.find('td[name="sensor"]').text("#" + elem.sensor + " " + elem.sensorname + " (" + elem.sensortemperature + "°C)");

                //tr.last().before(newtr);
            });
        })
        .done(function () {
            console.log("succes");
        })
        .fail(function () {
            console.log("error1");
            alert("error1");
        })
        .always(function () {
            console.log("error2");
        });
}
function loadActiveProgram() {
    $.getJSON(programServletPath + '?active=true', function (data) {
            console.log("success");

            tr = $("#lastprogramupdate");
            tr.text(data.lastupdate);

        })
        .done(function () {
            console.log("succes");
        })
        .fail(function () {
            //console.log("error1");
        })
        .always(function () {
            console.log("error2");
        });
}




function load() {

    $mSensorPanel = $(this).find('div[id="sensorpanel"]');
    $mSensorRow = $mSensorPanel.find('tr[name="sensor"]');

    $actuators = $(this).find('div[id="actuatorpanel"]');
    $activeProgram = $(this).find('div[id="activeprogrampanel"]');

    loadSensors();
    loadActuators();
    loadActiveProgramList();
    //loadActiveProgram();

    //setTimeout(getSensors, 20000);

    //setTimeout(loadActuators, 20000);
    //setTimeout(loadActiveProgram, 20000);


}
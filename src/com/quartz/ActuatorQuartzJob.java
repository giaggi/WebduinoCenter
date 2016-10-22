/**
 * Created by Giacomo Spanò on 14/02/2016.
 */
package com.quartz;

import com.server.webduino.core.Core;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.servlet.ServletContext;
import java.util.logging.Logger;

public class ActuatorQuartzJob implements Job {

    private static final Logger LOGGER = Logger.getLogger(ActuatorQuartzJob.class.getName());

    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        try {
            LOGGER.info("ActuatorQuartzJob START");
            update(context);
            //core.mPrograms.checkProgram();


        } catch (Exception e) {

            LOGGER.info("--- Error in job!");
            JobExecutionException e2 =
                    new JobExecutionException(e);
            // this job will refire immediately
            e2.refireImmediately();
            throw e2;
        }
        LOGGER.info("ActuatorQuartzJob END");
    }

    private volatile boolean flag_locked = false;
    private /*synchronized*/ void update(JobExecutionContext context) {
        //Date date = Core.getDate();
        LOGGER.info("ActuatorQuartzJob:update");
        if (flag_locked) {
            LOGGER.info("ActuatorQuartzJob:update - LOCKED");
            return;
        }
        flag_locked = true;

        //Core core = (Core)getServletContext().getAttribute(QuartzListener.CoreClass);
        ServletContext servletContext = (ServletContext) context.getMergedJobDataMap().get("servletContext");
        Core core = (Core)servletContext.getAttribute(QuartzListener.CoreClass);
        LOGGER.info("ActuatorQuartzJob:update  start");

        //core.mActuators.update();

        flag_locked = false;
        LOGGER.info("ActuatorQuartzJob:update  end");

    }
}
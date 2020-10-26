package c8y.rpi;

import java.lang.Thread.UncaughtExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.pi4.agent.core.configuration.ConfigurationManager;
import c8y.pi4.agent.core.driver.impl.ServiceLocatorDriversLoader;
import c8y.rpi.agent.Agent;


/**
 * @author Ivelin Yanev
 * @since 01.09.2020
 *
 */
public class AgentLauncher {

	private static final Logger logger = LoggerFactory.getLogger(AgentLauncher.class);

	private static final String STOP = "stop";
	private static final String START = "start";

	public static void main(String[] args) {
		
		System.out.println(System.getProperty("os.name").toLowerCase());
		
		if (START.equals(args[0])) {
			start(args);
		} else if (STOP.equals(args[0])) {
			stop(args);
		}

	}

	private static void stop(String[] args) {
		// TODO Auto-generated method stub

	}

	private static void start(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.error("Unrecoverable error, exiting: {}", e);
				return;

			}

		});

		// change the name of the thread
		Thread.currentThread().setName("C8Y DEVTEAMS Agent");
		final Agent agent = Agent.getInstance();

		agent.init(ConfigurationManager.defaultCredentialsManager(), new ServiceLocatorDriversLoader());
	}

}


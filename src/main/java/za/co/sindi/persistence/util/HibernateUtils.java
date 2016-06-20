/**
 * 
 */
package za.co.sindi.persistence.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * This is a Hibernate utility class that strictly uses the Hibernate 4.x library.
 * 
 * @author Bienfait Sindi
 * @since 26 November 2012
 *
 */
public final class HibernateUtils {

	private static final Logger logger = Logger.getLogger(HibernateUtils.class.getName());
	private static Configuration configuration;
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	private static final ThreadLocal<Session> sessionThread = new ThreadLocal<Session>();
	private static final ThreadLocal<Interceptor> interceptorThread = new ThreadLocal<Interceptor>();
	
	static {
		try {
			configuration = new Configuration();
			serviceRegistry = new StandardServiceRegistryBuilder().build();
			sessionFactory = configuration.configure().buildSessionFactory(serviceRegistry);
		} catch (HibernateException e) {
			logger.log(Level.SEVERE, "Error intializing SessionFactory.", e.getLocalizedMessage());
			throw new ExceptionInInitializerError(e);
		}
	}
	
	/**
	 * Private constructor
	 */
	private HibernateUtils() {}

	/**
	 * @return the sessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * Retrieves the current session local to the thread.
	 * 
	 * @return Hibernate {@link Session} for current thread.
	 * @throws HibernateException when Hibernate has a problem opening a new session.
	 */
	public static Session getSession() {
		Session session = sessionThread.get();
		if (session == null) {
			Interceptor interceptor = getInterceptor();
			if (interceptor != null) {
				session = getSessionFactory().withOptions().interceptor(interceptor).openSession();
			} else {
				session = getSessionFactory().openSession();
			}
			
			if (session != null) {
				sessionThread.set(session);
			}
		}
		
		return session;
	}
	
	/**
	 * Closes the Hibernate Session (created from the <code>getSession()</code> session.
	 */
	public static void closeSession() {
		Session session = sessionThread.get();
		sessionThread.set(null);
		if (session != null && session.isOpen()) {
			session.close();
		}
	}
	
	/**
	 * Registers a Hibernate {@link Interceptor}.
	 * @param interceptor
	 */
	public static void registerInterceptor(Interceptor interceptor) {
		interceptorThread.set(interceptor);
	}
	
	/**
	 * Get the registered Hibernate Interceptor.
	 * @return
	 */
	public static Interceptor getInterceptor() {
		return interceptorThread.get();
	}
}

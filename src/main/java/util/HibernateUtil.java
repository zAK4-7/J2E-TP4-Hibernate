package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Builds the SessionFactory once for the whole application lifecycle and
 * exposes it to the DAOs.
 */
public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            // Tests point "hibernate.config" at their own XML file (H2 in-memory database)
            String configResource = System.getProperty("hibernate.config");
            if (configResource == null || configResource.isBlank()) {
                configuration.configure();
            } else {
                configuration.configure(configResource);
            }

            SessionFactory factory = configuration.buildSessionFactory();
            // Released automatically so a shared factory is never closed by a single test class
            Runtime.getRuntime().addShutdownHook(new Thread(factory::close));
            return factory;
        } catch (Throwable ex) {
            System.err.println("SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}

package dao;

import common.GlobalConstants;
import org.springframework.context.ApplicationContext;
import play.Play;

/** A factory object that creates DAO objects from the spring configuration and initializes them. */
public class DaoFactory {
    private static final String environment = Play.application().configuration().getString("environment.name");
    private static IContestDao contestDao;
    private static ISportsDao sportsDao;
    private static IUserDao userDao;
    private static IStatsDao statsDao;
    private static IWalletDao walletDao;
    private static IAffiliateDao affiliateDao;

    public DaoFactory(ApplicationContext context) {
        contestDao = context.getBean("contestDao", IContestDao.class);
        ((AbstractDao) contestDao).setContext(context);
        sportsDao = context.getBean("sportsDao", ISportsDao.class);
        ((AbstractDao) sportsDao).setContext(context);
        userDao = context.getBean("userDao", IUserDao.class);
        ((AbstractDao) userDao).setContext(context);
        statsDao = context.getBean("statsDao", IStatsDao.class);
        ((AbstractDao) statsDao).setContext(context);
        walletDao = context.getBean("walletDao", IWalletDao.class);
        ((AbstractDao) walletDao).setContext(context);
        affiliateDao = context.getBean("affiliateDao", IAffiliateDao.class);
        ((AbstractDao) affiliateDao).setContext(context);
    }

    public static IContestDao getContestDao() {
        return contestDao;
    }

    public static ISportsDao getSportsDao() {
        return sportsDao;
    }

    public static IUserDao getUserDao() {
        return userDao;
    }

    public static IStatsDao getStatsDao() {
        return statsDao;
    }

    public static IWalletDao getWalletDao() {
        return walletDao;
    }

    public static IAffiliateDao getAffiliateDao() { return affiliateDao; }

    public static String getEnvironment() {
        return environment;
    }

    public static boolean isLocal() {
        return environment.equals(GlobalConstants.ENVIRONMENT_LOCAL);
    }

    public static boolean isDev() {
        return environment.equals(GlobalConstants.ENVIRONMENT_DEV);
    }

    public static boolean isLive() {
        return environment.equals(GlobalConstants.ENVIRONMENT_LIVE);
    }

    public static boolean isMaster() {
        return environment.equals(GlobalConstants.ENVIRONMENT_LIVE);
    }

    public static boolean isQa() {
        return environment.equals(GlobalConstants.ENVIRONMENT_QA);
    }

    public static boolean isRecording() {
        return environment.equals(GlobalConstants.ENVIRONMENT_RECORDING);
    }

    public static boolean isStaging() {
        return environment.equals(GlobalConstants.ENVIRONMENT_STAGING);
    }
}

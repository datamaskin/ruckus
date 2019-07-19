package distributed.topics.mlb;

import dao.IContestDao;
import dao.ISportsDao;
import distributed.topics.BaseStatsUpdateDistributedTopic;
import stats.translator.IFantasyPointTranslator;
import stats.updateprocessor.IUpdateProcessor;

/**
 * The topic to which all MLB fantasy point updates are published.
 */
public class MLBDistributedTopic extends BaseStatsUpdateDistributedTopic {
    /**
     * Default constructor.
     *
     * @param uniqueTopic The unique topic label that will be used..
     */
    public MLBDistributedTopic(String uniqueTopic, IFantasyPointTranslator translator, IUpdateProcessor updateProcessor,
                               ISportsDao sportsDao, IContestDao contestDao, String socketXmlRootNodeName) {
        super(uniqueTopic, translator, updateProcessor, sportsDao, contestDao, socketXmlRootNodeName);
    }
}

package utils.projectiongraph;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for the helper classes that are responsible for computing lineup projection graphs.
 */
public interface IProjectionGraphHelper {
    /**
     * Perform the calculations to determine what the array of existing points and projected points
     * will look like.
     *
     * @param currentData           List of current points values.
     * @param projectedData         List of projected points values.
     * @param fantasyPointUpdate    The number of fantasy points earned on the play.
     * @param unitOfTime            The unit of time that the play occurred at.
     * @param graphCapacity         The number of buckets in the graph
     */
    void updatePerformanceData(List<BigDecimal> currentData, List<BigDecimal> projectedData, BigDecimal fantasyPointUpdate, int unitOfTime, int graphCapacity);
}

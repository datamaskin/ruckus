package models.stats.nfl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;

/**
 * Created by mgiles on 8/12/14.
 */
@DynamoDBTable(tableName = "StatsNflDepthChartRaw")
public class DynamoStatsNflDepthChartRaw {
    private String id;
    private S3Link s3Link;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public S3Link getS3Link() {
        return s3Link;
    }

    public void setS3Link(S3Link s3Link) {
        this.s3Link = s3Link;
    }

}

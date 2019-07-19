package models.user;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created by mwalsh on 8/1/14.
 */
@DynamoDBTable(tableName = "UserAction")
public class DynamoUserAction {

    private Long userId;
    private Long timestamp;
    private String type;
    private String data;

    public DynamoUserAction(UserAction userAction) {
        this.userId = userAction.getUser().getId();
        this.timestamp = userAction.getStartTimestamp().getTime();
        this.type = userAction.getType().name();
        this.data = userAction.getData();
    }

    @DynamoDBHashKey(attributeName = "userId")
    public Long getUserId() {
        return userId;
    }

    @DynamoDBRangeKey(attributeName = "timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setData(String data) {
        this.data = data;
    }
}

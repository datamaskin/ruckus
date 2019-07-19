package dao;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.avaje.ebean.Ebean;
import org.springframework.context.ApplicationContext;
import play.Play;

import java.io.Serializable;

/**
 * Created by mgiles on 7/16/14.
 */
public abstract class AbstractDao implements Serializable {
    static AmazonDynamoDBClient dynamoDB;
    static AmazonS3 s3Client;
    static DynamoDBMapper dynamoDBMapper;

    static {
        final AWSCredentials credentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return Play.application().configuration().getString("aws.key");
            }

            @Override
            public String getAWSSecretKey() {
                return Play.application().configuration().getString("aws.secret");
            }
        };
        final AWSCredentialsProvider provider = new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return credentials;
            }

            @Override
            public void refresh() {

            }
        };
        dynamoDB = new AmazonDynamoDBClient(credentials);
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        dynamoDB.setRegion(usWest2);
        dynamoDBMapper = new DynamoDBMapper(dynamoDB, provider);
        s3Client = new AmazonS3Client(provider);
    }

    protected ApplicationContext context;

    protected void save(Object bean) {
        Ebean.save(bean);
    }

    protected void update(Object bean) {
        Ebean.update(bean);
    }

    protected void delete(Object bean) {
        Ebean.delete(bean);
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    protected String toInString(Object[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "()";

        StringBuilder b = new StringBuilder();
        b.append('(');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append(')').toString();
            b.append(", ");
        }
    }
}

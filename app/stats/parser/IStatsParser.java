package stats.parser;

import java.util.List;

public interface IStatsParser<T> {

  List<T> parse(String results);

}

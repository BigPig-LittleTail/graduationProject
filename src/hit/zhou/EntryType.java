package hit.zhou;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public enum EntryType implements Label, RelationshipType {
    司法,法律,行政法规,包含
}

package kikkoman;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 */
public class graph {

  private static enum RelTypes implements RelationshipType
  {
    KNOWS
  }

  public static void main( String[] args ) {

    GraphDatabaseService graphDb = new GraphDatabaseFactory().
        newEmbeddedDatabaseBuilder("graph/db_files").
        newGraphDatabase();

    Transaction tx = graphDb.beginTx();

    Node firstNode;
    Node secondNode;
    Relationship relationship;

    try
    {
      firstNode = graphDb.createNode();
      firstNode.setProperty( "message", "Hello, " );
      secondNode = graphDb.createNode();
      secondNode.setProperty( "message", "World!" );

      relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
      relationship.setProperty( "message", "brave Neo4j " );


      // Mutating operations go here
      tx.success();
    }
    finally
    {
      tx.finish();
    }


    System.out.print( firstNode.getProperty( "message" ) );
    System.out.print( relationship.getProperty( "message" ) );
    System.out.print( secondNode.getProperty( "message" ) );

    tx = graphDb.beginTx();
    try
    {
      // let's remove the data
      firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
      firstNode.delete();
      secondNode.delete();

      tx.success();
    }
    finally {
      tx.finish();
    }

    graphDb.shutdown();
  }
}

package kikkoman;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.*;

/**
 *
 */
public class graph {

  private static GraphDatabaseService graphDb;
  private static Index<Node> nodeIndex;

  private static String DB_PATH = "graph/db_files";
  private static String PROPERTY_NAME = "propertyName";

  private static void registerShutdownHook()
  {
    // Registers a shutdown hook for the Neo4j and index service instances
    // so that it shuts down nicely when the VM exits (even if you
    // "Ctrl-C" the running example before it's completed)
    Runtime.getRuntime().addShutdownHook( new Thread()
    {
      @Override
      public void run()
      {
        shutdown();
      }
    } );
  }

  private static void shutdown()
  {
    graphDb.shutdown();
  }

  private static enum RelTypes implements RelationshipType
  {
    DEPENDS
  }

  private static Node createAndIndexPropertyName( final String propertyName, final int ruleNumber )
  {
    Node propertyNode = nodeIndex.get( PROPERTY_NAME, propertyName).getSingle();

    if ( propertyNode != null ) {
      System.out.println( "Found propertyName " + propertyName );
      return propertyNode;
    }

    Node node = graphDb.createNode();
    node.setProperty( PROPERTY_NAME, propertyName );
    nodeIndex.add( node, PROPERTY_NAME, propertyName );

    node.setProperty( "ruleNumber", ruleNumber );
    return node;
  }

  public static void main( String[] args ) {

    // START SNIPPET: startDb
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
    nodeIndex = graphDb.index().forNodes( "nodes" );
    registerShutdownHook();
    // END SNIPPET: startDb

    List<Set<String>> rulesFrom = new ArrayList<Set<String>>(6);
    List<Set<String>> rulesTo   = new ArrayList<Set<String>>(6);

    rulesFrom.add(0, new HashSet<String>() );
    rulesFrom.get(0).addAll( Arrays.<String>asList( "citem.clientid", "citem.type", "citem.externalid" ) );
    rulesTo.add(0, new HashSet<String>() );
    rulesTo.get(0).addAll( Arrays.<String>asList( "citem.id" ) );

    rulesFrom.add(1, new HashSet<String>() );
    rulesFrom.get(1).addAll( Arrays.<String>asList("citem.parents") );
    rulesTo.add(1, new HashSet<String>() );
    rulesTo.get(1).addAll( Arrays.<String>asList("citem[type=product].ancestorSet", "citem[type=category].ancestorSet") );

    rulesFrom.add(2, new HashSet<String>() );
    rulesFrom.get(2).addAll( Arrays.<String>asList("citem[type=product].ancestorSet") );
    rulesTo.add(2, new HashSet<String>() );
    rulesTo.get(2).addAll( Arrays.<String>asList("citem[type=category].numProducts") );

    rulesFrom.add(3, new HashSet<String>() );
    rulesFrom.get(3).addAll( Arrays.<String>asList("review.syn_about") );
    rulesTo.add(3, new HashSet<String>() );
    rulesTo.get(3).addAll( Arrays.<String>asList("citem[type=product].numReviews") );

    rulesFrom.add(4, new HashSet<String>() );
    rulesFrom.get(4).addAll( Arrays.<String>asList("review.syn_about", "review.rating") );
    rulesTo.add(4, new HashSet<String>() );
    rulesTo.get(4).addAll( Arrays.<String>asList("citem[type=product].avgRating") );

    rulesFrom.add(5, new HashSet<String>() );
    rulesFrom.get(5).addAll( Arrays.<String>asList("citem[type=product].ancestorSet", "citem[type=product].numReviews") );
    rulesTo.add(5, new HashSet<String>());
    rulesTo.get(5).addAll( Arrays.<String>asList("citem[type=category].avgRating") );

    Set<Node> allNodes = new HashSet<Node>();
    Set<Relationship> allRelationships = new HashSet<Relationship>();

    //// CREATE
    Transaction tx = graphDb.beginTx();
    try
    {
      for( int ruleNumber = 0; ruleNumber < rulesTo.size(); ruleNumber++ ) {

        Set<String> from = rulesFrom.get(ruleNumber);

        Set<Node> toNodes   = new HashSet<Node>();
        Set<String> to   = rulesTo.get(ruleNumber);

        for( String toProperty : to ) {
          Node node = createAndIndexPropertyName(toProperty, ruleNumber);
          toNodes.add( node );
          allNodes.add( node );
        }

        for( String fromProperty : from ) {
          Node fromNode = createAndIndexPropertyName( fromProperty, ruleNumber);

//          System.out.println( "fromNode" + " : propertyName=" + fromNode.getProperty( PROPERTY_NAME )
//              + " rule number=" +  fromNode.getProperty( "ruleNumber" ) );

          allNodes.add( fromNode );

          for( Node toNode : toNodes ) {
            allRelationships.add(toNode.createRelationshipTo(fromNode, RelTypes.DEPENDS));
          }
        }
      }

      // Mutating operations go here
      tx.success();
    }
    finally
    {
      tx.finish();
    }

    //// DELETE
    boolean foundSome = true;
    int level = 0;

    while( foundSome ) {
      foundSome = false;

      tx = graphDb.beginTx();
      try
      {
        Iterable<Node> iter = GlobalGraphOperations.at( graphDb ).getAllNodes();
        Set<Node> toDelete = new HashSet<Node>();

        for ( Node node : iter ) {

          if ( ! node.hasRelationship( Direction.INCOMING ) && node.hasProperty( PROPERTY_NAME ) ) {

            System.out.println( "Level = " + level + " : propertyName = " + node.getProperty( PROPERTY_NAME )
                + " ruleNumber = " +  node.getProperty( "ruleNumber" ) );

            toDelete.add( node );

            foundSome = true;
          }
        }

        for( Node node : toDelete ) {
          Iterable<Relationship> outgoingRels = node.getRelationships( Direction.OUTGOING );
          for( Relationship rel : outgoingRels ) {
            rel.delete();
          }

          node.delete();
        }

        level++;

      tx.success();
    }
    finally {
      tx.finish();
    }
  }



    //// DELETE
//    tx = graphDb.beginTx();
//    try
//    {
//      for( Relationship rel : allRelationships ) {
//        rel.delete();
//      }
//
//      for( Node node : allNodes ) {
//        node.delete();
//      }
//
//      tx.success();
//    }
//    finally {
//      tx.finish();
//    }

    // graphDb.shutdown();
  }
}
